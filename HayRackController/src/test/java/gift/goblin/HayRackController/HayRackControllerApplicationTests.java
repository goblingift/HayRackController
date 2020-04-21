package gift.goblin.HayRackController;

import gift.goblin.HayRackController.service.tools.NumberConverterUtil;
import gift.goblin.HayRackController.service.tools.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HayRackControllerApplicationTests {

    @Test
    public void testCreateTicksString500() {

        StringUtils stringUtils = new StringUtils();

        String result = stringUtils.createTicksString(500);
        System.out.println("XYZ:" + result);
        Assert.assertEquals("0,50,100,150,200,250,300,350,400,450,500", result);
    }

    @Test
    public void testCreateTicksString2000() {

        StringUtils stringUtils = new StringUtils();

        String result = stringUtils.createTicksString(2000);
        System.out.println("XYZ:" + result);
        Assert.assertEquals("0,200,400,600,800,1000,1200,1400,1600,1800,2000", result);
    }

    @Test
    public void testCreateTicksString3() {

        StringUtils stringUtils = new StringUtils();

        String result = stringUtils.createTicksString(3);
        System.out.println("XYZ:" + result);
        Assert.assertEquals("0,0.3,0.6,0.9,1.2,1.5,1.8,2.1,2.4,2.7,3", result);
    }

    @Test
    public void testNumberConverterUtil() {

        NumberConverterUtil numberConverterUtil = new NumberConverterUtil();

        double result = numberConverterUtil.convertGramsToKg(89_570);
        Assert.assertEquals(89.6d, result, 1e-15);
    }

}
