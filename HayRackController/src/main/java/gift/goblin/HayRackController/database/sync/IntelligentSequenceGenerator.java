/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.database.sync;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author andre
 */
@Component
public class IntelligentSequenceGenerator implements IdentifierGenerator  {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {
        
        if (obj instanceof LongIdentifier) {
            LongIdentifier identifiable = (LongIdentifier) obj;
            Serializable id = identifiable.getId();
            
            if (id != null) {
                return id;
            }
        }
 
        return System.currentTimeMillis();
    }

}
