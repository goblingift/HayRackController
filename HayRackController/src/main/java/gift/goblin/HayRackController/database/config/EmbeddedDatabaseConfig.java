/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.database.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "embeddedEntityManagerFactory",
    transactionManagerRef = "embeddedTransactionManager", basePackages = {"gift.goblin.HayRackController.database.embedded.repo"})
public class EmbeddedDatabaseConfig {

  @Primary
  @Bean(name = "embeddedDataSource")
  @ConfigurationProperties(prefix = "embedded.datasource")
  public DataSource dataSource() {
    return DataSourceBuilder.create().build();
  }

  @Primary
  @Bean(name = "embeddedEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean embeddedEntityManagerFactory(
      EntityManagerFactoryBuilder builder, @Qualifier("embeddedDataSource") DataSource dataSource) {
    return builder.dataSource(dataSource).packages("gift.goblin.HayRackController.database.model")
        .build();
  }

  @Primary
  @Bean(name = "embeddedTransactionManager")
  public PlatformTransactionManager embeddedTransactionManager(
      @Qualifier("embeddedEntityManagerFactory") EntityManagerFactory embeddedEntityManagerFactory) {
    return new JpaTransactionManager(embeddedEntityManagerFactory);
  }
}
