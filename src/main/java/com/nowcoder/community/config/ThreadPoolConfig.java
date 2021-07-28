package com.nowcoder.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 配置spring线程池的Scheduler.
 *
 * @Author: 许昊天
 * @Date: 2021/07/10/17:32
 * @Description:
 */
@Configuration
@EnableScheduling
@EnableAsync
public class ThreadPoolConfig {
}
