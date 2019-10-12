package com.gy.utils;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Arrays;
import java.util.Objects;

/**
 * <p>执行Linux系统命令</p>
 *
 * created by yangyu on 2019-10-08
 */
@Service
public class PidUtil {

    private static final Logger logger = LoggerFactory.getLogger(PidUtil.class);

    public void startChrome() throws Exception {
        String startChromeCmd = "setsid /bin/sh -c /usr/bin/chromedriver > /dev/null";
        Runtime.getRuntime().exec(startChromeCmd);
        Thread.sleep(5000);
        // kill -2   等同于  Ctrl + C
        String killCmd = "kill -2";
        Runtime.getRuntime().exec(killCmd);
    }

    public void killChromeDriver() throws Exception{
        String[] pidCmd = {"/bin/sh", "-c",  "ps -ef|grep chrome|awk '{print $2}'"};

        java.util.List<String> pidList = getAllPid(pidCmd);
        for (int i = 0,len = pidList.size();i < len; i++){
            String[] killCmd = {"/bin/sh","-c",pidList.get(i)};
            cmdInvoke(killCmd);
        }
    }

    public void killNoGrepChromeDriver() throws Exception {
        String[] pidCmd = {"/bin/sh", "-c","ps -ef|grep chrome | grep -v grep | awk '{print $2}'"};
        String pid = cmdInvoke(pidCmd);
        String[] killCmd = {"/bin/sh", "-c",String.format("kill -9 %s",StringUtils.trim(pid))};
        cmdInvoke(killCmd);
    }

    private List getAllPid(String[] cmd) throws Exception{
        List<String> pidList = Lists.newArrayList();
        Process p = Runtime.getRuntime().exec(cmd);
        int exec = p.waitFor();
        logger.info("PidUtil getAllPid 执行结果: {}" ,exec);
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = null;
        while ((line = br.readLine()) != null) {
            pidList.add("kill -9 " + StringUtils.trim(line));
        }
        return pidList;
    }

    private String cmdInvoke(String[] cmd) {
        StringBuilder cmdOut = new StringBuilder();
        BufferedReader br = null;

        try {
            Process p = Runtime.getRuntime().exec(cmd);
            int exec = p.waitFor();
            logger.info("PidUtil cmdInvoke 执行结果: {}", exec);
            br = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()));
            String line = null;
            while (Objects.nonNull(line = br.readLine())) {
                cmdOut.append(line);
            }
        } catch (Exception e) {
           logger.error("PidUtil cmdInvoke failed, cmd :" + Arrays.toString(cmd),e);
        } finally {
            if (Objects.nonNull(br)) {
                try {
                    br.close();
                } catch (Exception e) {
                    logger.error("PidUtil cmdInvoke BufferedReader close Failed : ",e);
                }
            }
        }
        return cmdOut.toString();
    }
}
