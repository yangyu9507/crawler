/**
 * created by yangyu on 2019-10-11
 */
public class StartChrome {

    public static void main(String...args) throws Exception{
        String startChromeCmd = "setsid /bin/sh -c /usr/bin/chromedriver > /dev/null";
        Runtime.getRuntime().exec(startChromeCmd);
        Thread.sleep(5000);
        String killCmd = "kill -2";
        Runtime.getRuntime().exec(killCmd);
    }

}
