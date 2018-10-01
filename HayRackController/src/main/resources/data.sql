/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  andre
 * Created: 23.09.2018
 */

insert into role (id, name)
    values(100,'admin');

insert into user (id, username, password) 
    values(1000,'user_admin','$2a$10$ZHEbIfq20s8zVPPE9csG8.poZ3H7JzKrqFz9pRWTUHcrfjrV8xrUS');

insert into user_role (user_id, role_id)
    values(1000, 100);