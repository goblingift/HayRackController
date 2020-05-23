/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  andre
 * Created: 23.09.2018
 */

CREATE TABLE `role` (
  `id` bigint NOT NULL,
  `name` varchar(255) NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `user` (
  `id` bigint NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `user_role` (
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`)
);


insert into role (id, name)
    values(900,'admin');

insert into role (id, name)
    values(100,'user');


insert into user (id, username, password) 
    values(9000,'admin','$2a$10$ZHEbIfq20s8zVPPE9csG8.poZ3H7JzKrqFz9pRWTUHcrfjrV8xrUS');

insert into user (id, username, password) 
    values(1000,'farmer','$2a$10$ZHEbIfq20s8zVPPE9csG8.poZ3H7JzKrqFz9pRWTUHcrfjrV8xrUS');


insert into user_role (user_id, role_id)
    values(9000, 900);
insert into user_role (user_id, role_id)
    values(9000, 100);

insert into user_role (user_id, role_id)
    values(1000, 100);