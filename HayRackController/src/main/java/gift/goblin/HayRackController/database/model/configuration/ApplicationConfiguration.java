/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.database.model.configuration;

import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity which contains settings for the application.
 * @author andre
 */
@Entity
@Table
public class ApplicationConfiguration {
    
    @Id
    private int configurationId;
    
    /**
     * Description of this field- describes which value it contains.
     */
    private String description;
    
    /**
     * Value of the field, stored as simple String.
     */
    private String value;
    
    /**
     * Last modification date of this entry.
     */
    private LocalDateTime lastModified;

    public ApplicationConfiguration() {
    }

    public ApplicationConfiguration(int configurationId, String description, String value, LocalDateTime lastModified) {
        this.configurationId = configurationId;
        this.description = description;
        this.value = value;
        this.lastModified = lastModified;
    }

    public int getConfigurationId() {
        return configurationId;
    }

    public void setConfigurationId(int configurationId) {
        this.configurationId = configurationId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.description);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ApplicationConfiguration other = (ApplicationConfiguration) obj;
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (!Objects.equals(this.configurationId, other.configurationId)) {
            return false;
        }
        if (!Objects.equals(this.lastModified, other.lastModified)) {
            return false;
        }
        return true;
    }
    
    
    
    @Override
    public String toString() {
        return "ApplicationConfiguration{" + "configurationId=" + configurationId + ", description=" + description + ", value=" + value + ", lastModified=" + lastModified + '}';
    }
    
}
