package gift.goblin.HayRackController;

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
    public void testCreateTicksString3() {

        StringUtils stringUtils = new StringUtils();

        String result = stringUtils.createTicksString(3);
        System.out.println("XYZ:" + result);
        Assert.assertEquals("0,0.3,0.6,0.9,1.2,1.5,1.8,2.1,2.4,2.7,3", result);
    }

}
