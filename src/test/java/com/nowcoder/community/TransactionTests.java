package com.nowcoder.community;

import com.nowcoder.community.service.AlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 事务回滚测试类.
 *
 * @Author: 许昊天
 * @Date: 2021/06/06/14:59
 * @Description:
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TransactionTests {
    @Autowired
    private AlphaService alphaService;

    @Test
    public void testSavel() {
        Object obj = alphaService.save1();
        System.out.println("obj = " + obj);
    }

    @Test
    public void testSave2() {
        Object obj = alphaService.save2();
        System.out.println("obj = " + obj);
    }

}
