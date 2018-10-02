/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.service.security.enumerations;

/**
 *
 * @author andre
 */
public enum UserRole {

    USER("user"),
    ADMIN("admin");
        
    // Contains the value for the user-role, exactly how its stored in the database
    private String databaseValue;

    private UserRole(String databaseValue) {
        this.databaseValue = databaseValue;
    }

    @Override
    public String toString() {
        return "UserRole{" + "databaseValue=" + databaseValue + '}';
    }

    public String getDatabaseValue() {
        return databaseValue;
    }


}
