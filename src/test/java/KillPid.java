
/**
 * created by yangyu on 2019-10-08
 */
public class KillPid {

    public static void main(String...args) throws Exception{
        String[] pidCmd = {"/bin/sh", "-c","ps -ef|grep chrome | grep -v grep | awk '{print $2}'"};
        String pid = cmdInvoke(pidCmd);
        String[] killCmd = {"/bin/sh", "-c",String.format("kill -9 %s",pid.trim())};
        cmdInvoke(killCmd);
    }

    private static String cmdInvoke(String[] cmd) {
        StringBuffer cmdOut = new StringBuffer();
        java.io.BufferedReader br = null;

        try {
            Process p = Runtime.getRuntime().exec(cmd);
            int exec = p.waitFor();
            System.out.println("PidUtil cmdInvoke 执行结果: " + exec);
            br = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = br.readLine()) != null) {
                cmdOut.append(line);
            }
        } catch (Exception e) {
            System.out.println("PidUtil cmdInvoke failed, cmd :");
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    System.out.println("PidUtil cmdInvoke BufferedReader close Failed : ");
                }
            }
        }
        return cmdOut.toString();
    }



}
