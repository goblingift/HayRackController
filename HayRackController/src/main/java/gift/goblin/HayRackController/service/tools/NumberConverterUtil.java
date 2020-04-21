/*
 * Copyright (C) 2020 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.tools;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Service;

/**
 * Service bean with different methods to convert numbers from one format or
 * unit into another.
 *
 * @author andre
 */
@Service
public class NumberConverterUtil {

    /**
     * Will convert the given grams into kilograms. By default, this method will
     * round to one decimal places, e.g. "89.9".
     *
     * @return the given value converted to kg.
     */
    public double convertGramsToKg(long grams) {
        BigDecimal bd = new BigDecimal(Long.toString(grams));
        bd = bd.divide(BigDecimal.valueOf(1000));
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
