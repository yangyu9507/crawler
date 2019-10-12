import org.apache.commons.lang3.StringUtils;




/**
 * created by yangyu on 2019-09-29
 */
public class Test {


    @org.testng.annotations.Test
    public void testOs(){
        String property = StringUtils.lowerCase(System.getProperty("os.name"));
        if (StringUtils.contains(property,"windows")){
            System.out.println("windowns");
        } else {
            System.out.println("linux");
        }
    }


}
