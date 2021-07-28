package com.nowcoder.community.config;

import com.nowcoder.community.quartz.AlphaJob;
import com.nowcoder.community.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

/**
 * 配置 -> 数据库 -> 调用.
 *
 * @Author: 许昊天
 * @Date: 2021/07/11/8:50
 * @Description:
 */
@Configuration
public class QuartzConfig {
    // FactoryBean可简化Bean的实例化过程：
    // 1.通过FactoryBean封装Bean的实例化过程.
    // 2.将FactoryBean装配到Spring容器里.
    // 3.将FactoryBean注入给其他的Bean.
    // 4.该Bean得到的是FactoryBean所管理的对象实例.

    /**
     * 配置JobDetail
     * @return
     */
    // @Bean
    public JobDetailFactoryBean alphaJobDetail() {
        // 实例化JobDetailFactoryBean
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        // 声明管理的类型
        factoryBean.setJobClass(AlphaJob.class);
        // 声明job的名字
        factoryBean.setName("alphaJob");
        // 声明任务的组，一个组可以有多个任务
        factoryBean.setGroup("alphaJobGroup");
        // 声明任务是否为长久保存
        factoryBean.setDurability(true);
        // 声明任务是不是可恢复的
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    /**
     * 配置Trigger(SimpleTriggerFactoryBean, CronTriggerFactoryBean)
     * @param alphaJobDetail 与alphaJobDetail()保持一致
     * @return
     */
    // @Bean
    public SimpleTriggerFactoryBean alphaTrigger(JobDetail alphaJobDetail) {
        // 实例化SimpleTriggerFactoryBean
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        // 设置Trigger对应哪个JobDetail
        factoryBean.setJobDetail(alphaJobDetail);
        // 声明Trigger的名字
        factoryBean.setName("alphaTrigger");
        // 声明Trigger的组，一个组可以有多个任务
        factoryBean.setGroup("alphaTriggerGroup");
        // 声明频率
        factoryBean.setRepeatInterval(3000);
        // Trigger的底层需要存储job的状态，指定默认存储
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }

    /**
     * 刷新帖子分数任务
     * @return
     */
    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail() {
        // 实例化JobDetailFactoryBean
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        // 声明管理的类型
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        // 声明job的名字
        factoryBean.setName("postScoreRefreshJob");
        // 声明任务的组，一个组可以有多个任务
        factoryBean.setGroup("communityJobGroup");
        // 声明任务是否为长久保存
        factoryBean.setDurability(true);
        // 声明任务是不是可恢复的
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail) {
        // 实例化SimpleTriggerFactoryBean
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        // 设置Trigger对应哪个JobDetail
        factoryBean.setJobDetail(postScoreRefreshJobDetail);
        // 声明Trigger的名字
        factoryBean.setName("postScoreRefreshTrigger");
        // 声明Trigger的组，一个组可以有多个任务
        factoryBean.setGroup("communityTriggerGroup");
        // 声明频率
        factoryBean.setRepeatInterval(1000 * 60 * 5);
        // Trigger的底层需要存储job的状态，指定默认存储
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }
}
