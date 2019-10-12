package com.gy.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;

import com.gy.entity.HotSearchInfo;
import com.gy.entity.ItemInfo;
import com.gy.utils.DBHelper;
import com.gy.utils.PidUtil;
import com.gy.utils.SpringContextUtil;
import gy.lib.common.util.FinanceUtil;
import gy.lib.common.util.NumberUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.text.SimpleDateFormat;

/**
 * created by yangyu on 2019-09-27
 */
public class AliZhiShu implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(AliZhiShu.class);

    //日期格式
    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //PHANTOMJS驱动路劲
    private static String PHANTOMJS_DRIVER = String.format("E:%sphantomjs%sphantomjs-2.1.1-windows%sbin%sphantomjs.exe",File.separator,File.separator,File.separator,File.separator);
    //文件位置
    private static String path = null;
    //文件位置
    private static String path2 = null;
    private static WebDriver driver = null;
    private static int companyNameNum = 0;
    //谷歌驱动器
    private static String CHROME_DRIVER = String.format("C:%sUsers%sAdministrator%sAppData%sLocal%sGoogle%sChrome%sApplication%schromedriver.exe",File.separator,File.separator,File.separator,File.separator,File.separator,File.separator,File.separator,File.separator);

    //爬虫时模拟的浏览器请求头
    private static String[] userAgent = {
            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; AcooBrowser; .NET CLR 1.1.4322; .NET CLR 2.0.50727)",
            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; Acoo Browser; SLCC1; .NET CLR 2.0.50727; Media Center PC 5.0; .NET CLR 3.0.04506)",
            "Mozilla/4.0 (compatible; MSIE 7.0; AOL 9.5; AOLBuild 4337.35; Windows NT 5.1; .NET CLR 1.1.4322; .NET CLR 2.0.50727)",
            "Mozilla/5.0 (Windows; U; MSIE 9.0; Windows NT 9.0; en-US)",
            "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET CLR 2.0.50727; Media Center PC 6.0)",
            "Mozilla/5.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET CLR 1.0.3705; .NET CLR 1.1.4322)",
            "Mozilla/4.0 (compatible; MSIE 7.0b; Windows NT 5.2; .NET CLR 1.1.4322; .NET CLR 2.0.50727; InfoPath.2; .NET CLR 3.0.04506.30)",
            "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN) AppleWebKit/523.15 (KHTML, like Gecko, Safari/419.3) Arora/0.3 (Change: 287 c9dfb30)",
            "Mozilla/5.0 (X11; U; Linux; en-US) AppleWebKit/527+ (KHTML, like Gecko, Safari/419.3) Arora/0.6",
            "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.2pre) Gecko/20070215 K-Ninja/2.1.1",
            "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9) Gecko/20080705 Firefox/3.0 Kapiko/3.0",
            "Mozilla/5.0 (X11; Linux i686; U;) Gecko/20070322 Kazehakase/0.4.5",
            "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.8) Gecko Fedora/1.9.0.8-1.fc10 Kazehakase/0.5.6",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/535.20 (KHTML, like Gecko) Chrome/19.0.1036.7 Safari/535.20",
            "Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; fr) Presto/2.9.168 Version/11.52",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.11 (KHTML, like Gecko) Chrome/20.0.1132.11 TaoBrowser/2.0 Safari/536.11",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.71 Safari/537.1 LBBROWSER",
            "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; LBBROWSER)",
            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 732; .NET4.0C; .NET4.0E; LBBROWSER)",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.84 Safari/535.11 LBBROWSER",
            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E)",
            "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; QQBrowser/7.0.3698.400)",
            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 732; .NET4.0C; .NET4.0E)",
            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; SV1; QQDownload 732; .NET4.0C; .NET4.0E; 360SE)",
            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 732; .NET4.0C; .NET4.0E)",
            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E)",
            "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1",
            "Mozilla/5.0 (iPad; U; CPU OS 4_2_1 like Mac OS X; zh-cn) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8C148 Safari/6533.18.5",
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:2.0b13pre) Gecko/20110307 Firefox/4.0b13pre",
            "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:16.0) Gecko/20100101 Firefox/16.0",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11",
            "Mozilla/5.0 (X11; U; Linux x86_64; zh-CN; rv:1.9.2.10) Gecko/20100922 Ubuntu/10.10 (maverick) Firefox/3.6.10",
            "Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; SMJB; rv:11.0) like Gecko",
            "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:36.0) Gecko/20100101 Firefox/36.0"};


    static {
        String CHROME_DRIVER = "";
        if (StringUtils.contains(StringUtils.lowerCase(System.getProperty("os.name")),"windows")){
            CHROME_DRIVER = String.format("C:%sUsers%sAdministrator%sAppData%sLocal%sGoogle%sChrome%sApplication%schromedriver.exe"
            ,File.separator,File.separator,File.separator,File.separator,File.separator,File.separator,File.separator,File.separator);
        }else{
            CHROME_DRIVER = String.format("%susr%sbin%schromedriver",File.separator,File.separator,File.separator);
        }
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER);
    }

    private static DBHelper dbHelper = (DBHelper)SpringContextUtil.getBean(DBHelper.class);
    private static PidUtil pidUtil = (PidUtil)SpringContextUtil.getBean(PidUtil.class);

    private void dealDB() {
        String timeStr = DateTime.now().toString(DateTimeFormat.forPattern("yyyyMMdd"));
        if (StringUtils.isNotBlank(DBHelper.tableName)) {
            String dbStr = StringUtils.substring(DBHelper.tableName, 15);
            if (!StringUtils.equalsIgnoreCase(timeStr,dbStr)){
                DBHelper.isDrop = true;
                DBHelper.dropTableName = DBHelper.tableName;
                DBHelper.tableName = "hot_search_word" + timeStr;
            }
        } else {
            DBHelper.isDrop = false;
            DBHelper.tableName = "hot_search_word" + timeStr;
        }
    }

    private static long totalCount = 0;
    private static int NUMBER_OF_PROCESSORS = Runtime.getRuntime().availableProcessors();

    @Override
    public void run() {
        try {

            boolean isNotWindows = !StringUtils.contains(StringUtils.lowerCase(System.getProperty("os.name")), "windows");
            if (isNotWindows){
                NUMBER_OF_PROCESSORS = 10;
                logger.info("开始清理Chrome...");
                pidUtil.killChromeDriver();
                waitTime(10000);
                pidUtil.killNoGrepChromeDriver();
                waitTime(10000);
                logger.info("清理Chrome完成...");

                logger.info("开始开启Chrome...");
                pidUtil.startChrome();
                logger.info("开启Chrome完成...");
                waitTime(5000);
            }

            logger.info("爬取1688网站热搜榜开始!  ");
            totalCount = 0;
            long start = System.currentTimeMillis();
            dealDB();

            dbHelper.dbChange();

            ExecutorService service = Executors.newFixedThreadPool(NUMBER_OF_PROCESSORS);

            Properties urlProperties = PropertiesLoaderUtils.loadAllProperties("hot-search.properties");
            List<Future<?>> futureList = new ArrayList<>(NUMBER_OF_PROCESSORS);
            List<Object> resultList = new ArrayList<>(NUMBER_OF_PROCESSORS);

            List<ChromeDriver> chromeDrivers = new ArrayList<>(NUMBER_OF_PROCESSORS);
            for (int i = 0 ; i < NUMBER_OF_PROCESSORS ; i++){
                chromeDrivers.add(new ChromeDriver(new ChromeOptions().addArguments("--headless","--no-sandbox","--disable-gpu","--disable-dev-shm-usage")));
            }

            Set<Object> urlKeySets = urlProperties.keySet();
            Iterator<Object> iterator = urlKeySets.iterator();
            int i = 0;
            while (iterator.hasNext()){
                Object next = iterator.next();
                String key = String.valueOf(next);
                if (StringUtils.isEmpty(key)) {
                    continue;
                }
                String url =  String.valueOf(urlProperties.get(key));

                ChromeDriver chromeDriver = chromeDrivers.get(i);
                Future<?> hotSearch = service.submit(() -> this.importHostWord(chromeDriver, url, "hot_search"));
                logger.info("开始爬取 {} : {} 的热搜词! ", key, url);
                futureList.add(hotSearch);
                i++;
                if (i == NUMBER_OF_PROCESSORS){
                    // 执行线程,结束后, 再完成初始化操作后,继续执行
                    logger.info("已经开启所有的子线程....");
                    for (int j = 0,len = futureList.size(); j < len; j++){
                        Object o = futureList.get(j).get();
                        resultList.add(o);
                    }

                    service.shutdown();
                    logger.info("shutdown()：启动一次顺序关闭，执行以前提交的任务，但不接受新任务...");

                    logger.info("开始关闭Chrome...");
                    closeChrome(chromeDrivers);
                    logger.info("关闭Chrome完成...");

                    resultList.clear();
                    resultList  = new ArrayList<>(NUMBER_OF_PROCESSORS);
                    futureList.clear();
                    futureList = new ArrayList<>(NUMBER_OF_PROCESSORS);
                    chromeDrivers.clear();
                    chromeDrivers = new ArrayList<>(NUMBER_OF_PROCESSORS);
                    for (int j = 0 ; j < NUMBER_OF_PROCESSORS ; j++){
                        chromeDrivers.add(new ChromeDriver(new ChromeOptions().addArguments("--headless","--no-sandbox","--disable-gpu","--disable-dev-shm-usage")));
                    }
                    service = Executors.newFixedThreadPool(NUMBER_OF_PROCESSORS);
                    i = 0;
                }
            }

            if (i > 0) {
                // 执行线程,结束后, 再完成初始化操作后,继续执行
                logger.info("已经最后一次开启所有的子线程....");
                for (int j = 0, len = futureList.size(); j < len; j++) {
                    Object o = futureList.get(j).get();
                    resultList.add(o);
                }

                service.shutdown();
                logger.info("shutdown()：最后一次启动顺序关闭，执行以前提交的任务，但不接受新任务...");
                logger.info("最后一次开始关闭Chrome...");
                closeChrome(chromeDrivers);
                logger.info("最后一次关闭Chrome完成...");
            }

            resultList.clear();
            resultList = null;
            futureList.clear();
            futureList = null;
            chromeDrivers.clear();
            chromeDrivers = null;
            service.shutdown();
            service = null;
            String time = getExecuteTime(start);
            logger.info("爬取1688网站热搜榜结束, 共有{}条数据, 耗时:{} ",totalCount,time);

            if (isNotWindows) {
                logger.info("开始最终关闭Chrome...");
                pidUtil.killChromeDriver();
                waitTime(5000);
                pidUtil.killNoGrepChromeDriver();
                logger.info("最终关闭Chrome完成...");
            }

        } catch (Exception ex){
            logger.error("HotWordController get hot search word From1688 Failed: ",ex);
        }
    }

    private void closeChrome(List<ChromeDriver> chromeDrivers) {
        for (int k = 0, len = chromeDrivers.size(); k < len; k++) {
            ChromeDriver chromeItem = chromeDrivers.get(k);
            //关闭并退出浏览器
            chromeItem.close();
            chromeItem.quit();
            // 等待Chrome关闭
            waitTime(2000);
        }
        waitTime(2000);
    }

    private void importHostWord(WebDriver webDriver, String firstUrl, String type) {
        String url = null;
        String urlWhole = null;
        String category2 = null;
        WebElement search = null;
        List<String> arr2 = new ArrayList<>();
        List<String> arrName2 = new ArrayList<>();
        List<String> arrName23 = new ArrayList<>();
        List<String> arr3 = new ArrayList<>();
        List<String> arrName3 = new ArrayList<>();

        Document doc = connect(webDriver,firstUrl);
        if (Objects.isNull(doc)){
            return;
        }
        String firstName = "";
        Elements firstNameElement = doc.select("[class=selector cate-select fd-clr]")
                              .select(String.format("[data-key=%s]",StringUtils.substringAfterLast(firstUrl,"=")));
        if (Objects.nonNull(firstNameElement)){
            int i = 0;
            for (Element e : firstNameElement) {
                if (StringUtils.isNotEmpty(firstName)){
                    break;
                }
                firstName = firstNameElement.select("[class=current fd-clr]").select("a").get(i).text();
                i++;
            }
        }
        //将二级分类文件取出
        Elements categoryTwo = doc.select("[class=selector cate-select fd-clr]").select("[data-key=-1]").select("a[data-key]");
        int i = 0;
        for (Element e : categoryTwo) {
            //将二级分类名称取出
            String categoryTwoName = categoryTwo.get(i).text();
            //将二级分类id取出
            String categoryTwoId = categoryTwo.get(i).attr("data-key");
            url = firstUrl + "," + categoryTwoId;
            arr2.add(url);
            arrName2.add(categoryTwoName);
            i++;
        }

        //将三级分类取出
        for (int j = 0; j < arr2.size(); j++) {
            Document doc1 = connect(webDriver,arr2.get(j));
            Elements categoryThird = doc1.select("[class=selector cate-select fd-clr]").select("[data-key=-1]").select("a[data-key]");
            for (int n = 1; n < categoryThird.size(); n++) {
                String categoryThirdName = categoryThird.get(n).text();
                String categoryThirdId = categoryThird.get(n).attr("data-key");
                urlWhole = arr2.get(j) + "," + categoryThirdId;
                arrName23.add(arrName2.get(j));
                arr3.add(urlWhole);
                arrName3.add(categoryThirdName);
            }
        }
        //输入每一个三级分类标签，爬虫
        for (int j = 0; j < arr3.size(); j++) {
            //搜索'最近30天'按钮，并点击
            webDriver.get(arr3.get(j));
            WebElement element = null;
            try {
                int tried = 5;
                while (tried > 0) {
                    waitTime(200);
                    tried--;
                }
                element = webDriver.findElement(By.xpath(".//*/input[@value='month']"));
                if (Objects.isNull(element)) {
                    continue;
                }
                element.click();
            } catch (Exception ex){
                logger.error("Get hot search word from " + arr3.get(j) + " failed : ", ex);
            }
            //爬虫
            crawler(webDriver,firstName,arr3.get(j), arrName23.get(j), arrName3.get(j),type);
            //wait
        }
    }

    /**
     * 爬虫
     *
     * @param url
     * @param arrName2
     * @param arrName3
     * @throws UnsupportedEncodingException
     */
    private void crawler(WebDriver webDriver,String firstName,String url, String arrName2, String arrName3,String type) {
        Document docDetail = connect(webDriver,url);
        if (docDetail != null) {
            if (StringUtils.equalsIgnoreCase("hot_search",type)){

                List<WebElement> unitHots = webDriver.findElements(By.xpath(".//*/div[@class='unit hot']"));
                if (CollectionUtils.isEmpty(unitHots)){
                    return;
                }
                List<WebElement> paginations = unitHots.get(0).findElements(By.xpath(".//*/div[@class='pagination']"));
                if (CollectionUtils.isEmpty(paginations)){
                    return;
                }

                insert(webDriver,firstName,url,arrName2,arrName3);

                List<WebElement> spans = paginations.get(0).findElements(By.tagName("span"));
                for (int i = 1,len = spans.size() ; i < len; i++){
                     spans.get(i).click();
                    insert(webDriver,firstName,url,arrName2,arrName3);
                }
            } else if (StringUtils.equalsIgnoreCase("product_ranking",type)){
                Elements elementEach = docDetail.select("div[class=unit hot unfolded]").select(".item.fd-clr");
                for (int i = 0; i < elementEach.size(); i++) {
                    String elementUrl = elementEach.select("div[class=detail each]").select("p[class=title]").select("a").get(i).attr("href");
                    String elementName = elementEach.select("div[class=detail each]").select("p[class=title]").select("a").get(i).text();
                    String elementPrice = elementEach.select("div[class=detail each]").select("em[class=money]").get(i).text();
                    String elementCompany = elementEach.select("div[class=detail each]").select("p[class=com]").get(i).text();
                    String elementTrade = elementEach.select("p[class=first each]").get(i).text();
                    String elementEstimate = elementEach.select("p[class=second each]").get(i).text();

                    ItemInfo itemInfo = new ItemInfo();

                    itemInfo.setThird_link(url);
                    itemInfo.setFirst_name(firstName);
                    itemInfo.setSecond_name(arrName2);
                    itemInfo.setThird_name(arrName3);
                    itemInfo.setItem_link(elementUrl);
                    itemInfo.setItem_name(elementName);
                    itemInfo.setPrice(elementPrice);
                    itemInfo.setItem_company(elementCompany);
                    itemInfo.setTrading_index(elementTrade);
                    itemInfo.setEvaluation(elementEstimate);

                    dbHelper.insertMysql(itemInfo);
                }
            }
        }
    }

    private void insert(WebDriver webDriver,String firstName,String url, String arrName2, String arrName3){
        if (Objects.nonNull(webDriver.findElement(By.xpath(".//*/div[@class='unit hot']")))
                && Objects.nonNull(webDriver.findElement(By.xpath(".//*/div[@class='unit hot']"))
                .findElement(By.className("list")))
                && CollectionUtils.isNotEmpty(webDriver.findElement(By.xpath(".//*/div[@class='unit hot']"))
                .findElement(By.className("list")).findElements(By.tagName("a")))) {

            List<WebElement> listElements = webDriver.findElement(By.xpath(".//*/div[@class='unit hot']"))
                    .findElement(By.className("list")).findElements(By.tagName("a"));

            for (int j = 0,length = listElements.size(); j < length; j++){
                String keyword = listElements.get(j).getText();
                HotSearchInfo info = new HotSearchInfo();
                info.setThird_link(url);
                info.setFirst_name(firstName);
                info.setSecond_name(arrName2);
                info.setThird_name(arrName3);
                info.setKeyword(keyword);
                dbHelper.insertMysql(info);
                totalCount++;
            }
        }
    }


    private Document connect(WebDriver webDriver,String url) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        Document doc = null;
        try {
            webDriver.get(url);

            //开始打开网页，等待输入元素出现
            /*WebDriverWait wait = new WebDriverWait(webDriver, 1);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.name("keywords")));*/
            int tried = 5;
            while (tried > 0) {
                waitTime(200);
                tried--;
            }
            if (Objects.nonNull(webDriver)) {
                doc = Jsoup.parse(webDriver.getPageSource());
            }
        } catch (Exception e) {
            logger.error("连接网站 url ["+url+"]超时",e);
        }
        return doc;
    }

    /**
     * 线程等待
     * @param time
     */
    private void waitTime(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("！进程被打断");
        }
    }

    //写文件
    private void exportFile(String content, File file) {
        try {
            if (Objects.isNull(file)){
                return;
            }
            String filePath = file.getAbsolutePath();
            if (StringUtils.isEmpty(filePath)){
                return;
            }
            String dirPath = StringUtils.substringBeforeLast(filePath, File.separator);
            File dir = new File(dirPath);
            if (!dir.exists()){
                dir.mkdirs();
            }

            if (!file.exists()){
                file.createNewFile();
            }

            FileWriter out = new FileWriter(file, true);
            out.write(content + System.lineSeparator());
            out.flush();
            out.close();
        } catch (IOException e) {
            logger.error("！IO异常，写文件异常",e);
        }
    }

    /**
     * 计算执行时间
     *
     * @param start
     * @return
     */
    private String getExecuteTime(long start){
        Double totalTime = FinanceUtil.divide(NumberUtil.toDouble(System.currentTimeMillis() - start), NumberUtil.toDouble(1000));
        int hour  = NumberUtil.toInt(Math.floor(totalTime / 3600));
        totalTime %= 3600;
        int min  = NumberUtil.toInt(Math.floor(totalTime / 60));

        String sec  = String.format("%.3f",totalTime % 60);
        return String.format("%dh %dm %ss",hour,min,sec);
    }

}