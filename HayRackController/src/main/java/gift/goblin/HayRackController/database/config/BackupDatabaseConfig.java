package gift.goblin.HayRackController.database.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "backupEntityManagerFactory",
    transactionManagerRef = "backupTransactionManager", basePackages = {"gift.goblin.HayRackController.database.backup.repo"})
public class BackupDatabaseConfig {

  @Bean(name = "backupDataSource")
  @ConfigurationProperties(prefix = "backup.datasource")
  public DataSource dataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean(name = "backupEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean backupEntityManagerFactory(
      EntityManagerFactoryBuilder builder, @Qualifier("backupDataSource") DataSource dataSource) {
    return builder.dataSource(dataSource).packages("gift.goblin.HayRackController.database.model")
        .build();
  }

  @Bean(name = "backupTransactionManager")
  public PlatformTransactionManager backupTransactionManager(
      @Qualifier("backupEntityManagerFactory") EntityManagerFactory backupEntityManagerFactory) {
    return new JpaTransactionManager(backupEntityManagerFactory);
  }

}
