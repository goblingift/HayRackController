/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.controller;

import gift.goblin.HayRackController.controller.model.UserSessionInfo;
import gift.goblin.HayRackController.database.model.user.User;
import gift.goblin.HayRackController.service.io.WebcamDeviceService;
import gift.goblin.HayRackController.service.security.SecurityService;
import gift.goblin.HayRackController.service.security.UserService;
import gift.goblin.HayRackController.service.security.enumerations.UserRole;
import gift.goblin.HayRackController.service.validator.LoginValidator;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Controller which offers endpoints for registration and login.
 *
 * @author andre
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private LoginValidator loginValidator;

    @Autowired
    private BuildProperties buildProperties;

    @Autowired
    private WebcamDeviceService webcamService;

    @Autowired
    private SessionRegistry sessionRegistry;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Renders the user-list overview.
     *
     * @param model model dto.
     * @return name of the view.
     */
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String renderUserOverview(Model model) {

        model.addAttribute("webcam_count", webcamService.getWebcamCount());

        SecurityContext context = SecurityContextHolder.getContext();
        boolean isAdmin = context.getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase(UserRole.ADMIN.getDatabaseValue()));

        if (isAdmin) {
            
            List<UserSessionInfo> userSessionInfos = new ArrayList<>();
            
            List<List<SessionInformation>> sessionInformations = sessionRegistry.getAllPrincipals().stream()
                    .map(u -> sessionRegistry.getAllSessions(u, false))
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            
            for (List<SessionInformation> actSessionInfoList : sessionInformations) {
                for (SessionInformation actSessionInfo : actSessionInfoList) {
                    org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) actSessionInfo.getPrincipal();
                    LocalDateTime lastRequest = actSessionInfo.getLastRequest().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    String sessionId = actSessionInfo.getSessionId();
                    
                    UserSessionInfo userSessionInfo = new UserSessionInfo(user.getUsername(), sessionId, lastRequest);
                    userSessionInfos.add(userSessionInfo);
                }
            }
            model.addAttribute("userInfos", userSessionInfos);
            
            return "users";
        } else {
            logger.warn("Current user has no admin-role, redirect to login-page!");
            return "login";
        }
    }

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String registration(Model model) {
        model.addAttribute("userForm", new User());

        return "registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult, Model model) {
        loginValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "registration";
        }

        userService.save(userForm);

        securityService.autoLogin(userForm.getUsername(), userForm.getPassword());

        return "redirect:/welcome";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model, String error, String logout) {
        if (error != null) {
            model.addAttribute("error", "Your username and password is invalid.");
        }

        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        logger.info("External user viewed login-page: {}", request.getRemoteAddr());

        model.addAttribute("build_artifact", buildProperties.getArtifact());
        model.addAttribute("build_version", buildProperties.getVersion());
        model.addAttribute("build_time", buildProperties.getTime().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        return "login";
    }

}
