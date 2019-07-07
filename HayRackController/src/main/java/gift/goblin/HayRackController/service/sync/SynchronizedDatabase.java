/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.sync;

/**
 * Defines methods for synchronizing the data between embedded database
 * and backup database.
 * @author andre
 */
public interface SynchronizedDatabase {
    
    /**
     * Backup the values from the embedded database to the backup database.
     */
    void backupValues();
    
    /**
     * Restore the entries of the backup database into the embedded database.
     */
    void prefillEmbeddedDatabase();
    
}
