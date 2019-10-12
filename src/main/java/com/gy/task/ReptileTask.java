package com.gy.task;

import com.gy.service.AliZhiShu;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * created by yangyu on 2019-09-29
 */
@Configurable
@Component
@EnableScheduling
//@Order(value = 3)
public class ReptileTask implements SchedulingConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(ReptileTask.class);

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        String cron = "0 */1 * * * ?";

        try {
            Properties props = PropertiesLoaderUtils.loadAllProperties("mysql.properties");
            cron = String.valueOf(props.get("jdbc.cron"));
        } catch (IOException ex){
            logger.error("ReptileTask configureTasks get peroid failed :",ex);
        }

        taskRegistrar.setTaskScheduler(taskScheduler());

        /**
         * Trigger接口用于计算任务的下次执行时间
         * 1. CronTrigger通过 Cron表达式来生成调度计划
         * 2. PeriodicTrigger 用于定期执行：它有两种模式:
         *   fixedRate: 两次任务开始时间之间 间隔 指定时长
         *   fixedDelay: 上一次任务的结束时间与下一次任务开始时间 间隔指定时长
         */
        /*PeriodicTrigger trigger = new PeriodicTrigger(period);
        trigger.setFixedRate(true);*/
        CronTrigger cronTrigger = new CronTrigger(cron);
        /** 返回任务应再次运行的时间 **/
//        trigger.nextExecutionTime(new SimpleTriggerContext());
        /**
         * 提交任务
         * new StatusTask(assetStatus) 要提交的任务
         * trigger 要指定的任务调度规则
         */
        taskRegistrar.getScheduler().schedule(new AliZhiShu(), cronTrigger);
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("reptile-task-");
        /**
         * 对拒绝task的处理策略:  有以下4种
         *
         * ThreadPoolExecutor.AbortPolicy:  处理程序遭到拒绝，将丢弃任务并抛出RejectedExecutionException异常(默认使用)
         * ThreadPoolExecutor.DiscardPolicy: 不能执行的任务将被丢弃
         * ThreadPoolExecutor.DiscardOldestPolicy: 丢弃队列最前面的任务,然后重新尝试执行，
         *          如果执行程序尚未关闭，则位于工作队列头部的任务将被删除，然后重新执行程序(如果再次失败，则重复此过程)
         * ThreadPoolExecutor.CallerRunsPolicy: 调用者的线程会执行该任务, 如果执行器已关闭，则丢弃
         *
         **/
        scheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        /**
         * 设置是否等待计划任务在关闭时完成，不中断正在运行的任务，并执行队列中的所有任务，默认false
         */
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        /**
         * 设置此执行程序在关闭时应阻止的最大秒数,以便在容器的其余部分继续关闭之前等待剩余任务完成执行
         */
        scheduler.setAwaitTerminationSeconds(60);
        /** 初始化任务 **/
        scheduler.initialize();
        return scheduler;
    }

}
