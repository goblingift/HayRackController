/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController;

import gift.goblin.HayRackController.service.security.enumerations.Pages;
import gift.goblin.HayRackController.service.security.enumerations.UserRole;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.Executor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;

/**
 * Defines which paths are public, and which are private (login required).
 *
 * @author andre
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeRequests()
                .antMatchers("/resources/**", "/css/**", "/js/**").permitAll()
                .anyRequest().access("isAuthenticated() or hasIpAddress('0:0:0:0:0:0:0:1') or hasIpAddress('192.168.2.0/16') or hasIpAddress('127.0.0.1')")
                .and()
                .formLogin()
                    .loginPage("/login")
                    .successHandler(this::loginSuccessHandler)
                    .failureHandler(this::loginFailureHandler)
                    .permitAll()
                .and()
                .logout()
                .permitAll();
        
        // security config for using h2-console
        httpSecurity.csrf().disable();
        httpSecurity.headers().frameOptions().disable();
    }

    private void loginSuccessHandler(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        String redirectAfterLoginUrl = getRedirectAfterLoginUrl(authentication);

        logger.info("Login successful, redirect user to: {}", redirectAfterLoginUrl);

        redirectStrategy.sendRedirect(request, response, redirectAfterLoginUrl);
    }

    private void loginFailureHandler(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException e) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        logger.error("Login failed! Caused by: {}", e.getMessage());
    }

    private String getRedirectAfterLoginUrl(Authentication authentication) {

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority actAuthority : authorities) {
            if (actAuthority.getAuthority().equals(UserRole.ADMIN.getDatabaseValue())) {
                return Pages.ADMIN_DASHBOARD.getUrl();
            } else if (actAuthority.getAuthority().equals(UserRole.USER.getDatabaseValue())) {
                return Pages.DASHBOARD.getUrl();
            }
        }

        return Pages.LOGINPAGE.getUrl();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authManagerBuilder) throws Exception {
        authManagerBuilder.userDetailsService(userDetailsService()).passwordEncoder(bCryptPasswordEncoder());
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder authManagerBuilder) throws Exception {
        authManagerBuilder
                .userDetailsService(userDetailsService);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return super.userDetailsService();
    }
}
