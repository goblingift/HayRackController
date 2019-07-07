/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

/**
 * Starts the spring boot application
 * @author andre
 */
@EntityScan(basePackages = {"gift.goblin.HayRackController.database.*"}, basePackageClasses = {Jsr310JpaConverters.class})
@SpringBootApplication
public class HayRackControllerApplication {

	public static void main(String[] args) {
		SpringApplication.run(HayRackControllerApplication.class, args);
	}
}
