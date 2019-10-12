
import gy.lib.common.util.FinanceUtil;
import gy.lib.common.util.NumberUtil;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * created by yangyu on 2019-09-30
 */
public class ConditionTest {

    @Test
    public void test() throws Exception {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime localDate = now.minusDays(1);
        System.out.println(localDate.toString(DateTimeFormat.forPattern("yyyyMMdd")));

        String str = DateTime.now().toString(DateTimeFormat.forPattern("yyyyMMdd"));
        System.out.println(str);

        System.out.println(StringUtils.substring("hot_search_word20190930",15));
    }

    @Test
    public void testPool() throws Exception {
        ExecutorService service = Executors.newFixedThreadPool(10);

        long start = System.currentTimeMillis();

        List<Future<?>> futureList = new ArrayList<>(10);
        List<Object> resultList = new ArrayList<>(10);

        for (int i = 0; i < 10; i++){
            int j = i;
            Future<?> submit1 = service.submit(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + ": " + j);
                    Thread.sleep(5000L);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            futureList.add(submit1);

        }

        System.out.println("已经开启所有的子线程");
        for (int j = 0,len = futureList.size(); j < len; j++){
            Object o = futureList.get(j).get();
            resultList.add(o);
        }

        service.shutdown();
        System.out.println("shutdown()：启动一次顺序关闭，执行以前提交的任务，但不接受新任务。");

        while (true) {
            if (service.isTerminated()) {
                System.out.println("所有的子线程都结束了！耗时: " + (System.currentTimeMillis() - start) + " ms.");
                break;
            }
            Thread.sleep(1000);


        }
    }

    @Test
    public void timeTest() throws Exception {

            long start = System.currentTimeMillis() - 42517836L;
            Double totalTime = FinanceUtil.divide(NumberUtil.toDouble(System.currentTimeMillis() - start), NumberUtil.toDouble(1000));
            int hour  = NumberUtil.toInt(Math.floor(totalTime / 3600));
            totalTime %= 3600;
            int min  = NumberUtil.toInt(Math.floor(totalTime / 60));

            String sec  = String.format("%.3f",totalTime % 60);
            System.out.println(String.format("%dh %dm %ss",hour,min,sec));
    }

    private static int NUMBER_OF_PROCESSORS = NumberUtil.toInt(System.getenv("NUMBER_OF_PROCESSORS"));


    @Test
    public void test1() throws Exception {

        Map<String, String> getenv = System.getenv();
        for (Map.Entry<String,String> entry : getenv.entrySet()){
            System.out.println(entry.getKey()+"\t"+entry.getValue());
        }

    }

    public static void main(String...args){
        java.util.Map<String, String> getenv = System.getenv();
        for (java.util.Map.Entry<String,String> entry : getenv.entrySet()){
            System.out.println(entry.getKey()+"\t"+entry.getValue());
        }
    }

}
