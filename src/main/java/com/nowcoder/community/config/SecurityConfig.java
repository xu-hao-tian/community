package com.nowcoder.community.config;

import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 权限配置.
 *
 * @Author: 许昊天
 * @Date: 2021/07/08/8:39
 * @Description:
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 授权
        http.authorizeRequests()
                .antMatchers(
                        "/user/setting",
                        "/user/upload",
                        "/discuss/add",
                        "/comment/add/**",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "/unfollow"
                )
                // 设置权限为所有人
                .hasAnyAuthority(
                        AUTHORITY_USER,
                        AUTHORITY_ADMIN,
                        AUTHORITY_MODERATOR
                )
                // 只有贴主能访问到的权限
                .antMatchers(
                        "/discuss/top",
                        "/discuss/wonderful"
                )
                // 设置权限为贴主
                .hasAnyAuthority(
                        AUTHORITY_MODERATOR
                )
                // 只有管理员能访问到的权限
                .antMatchers(
                        "/discuss/delete",
                        "/data/**",
                        "/actuator/**"
                )
                // 设置权限为管理员
                .hasAnyAuthority(
                        AUTHORITY_ADMIN
                )
                .anyRequest().permitAll()
                // 生成页面，不生成凭证
                .and().csrf().disable();

        // 权限不够时的处理
        http.exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    // 没有登录
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                        // 获取请求消息头
                        String xRequestedWith = request.getHeader("x-requested-with");
                        // 判断请求是否为异步请求
                        if ("XMLHttpRequest".equals(xRequestedWith)) {
                            // 声明返回的字符类型
                            response.setContentType("application/plain;charset=utf-8");
                            // 字符流
                            PrintWriter writer = response.getWriter();
                            // 没有登录，无访问权限
                            writer.write(CommunityUtil.getJSONString(403,"您还没有登录!"));
                        } else {
                            // 同步请求，直接跳转到登录页面
                            response.sendRedirect(request.getContextPath() + "/login");
                        }
                    }
                })
                .accessDeniedHandler(new AccessDeniedHandler() {
                    // 权限不足
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                        // 获取请求消息头
                        String xRequestedWith = request.getHeader("x-requested-with");
                        // 判断请求是否为异步请求
                        if ("XMLHttpRequest".equals(xRequestedWith)) {
                            // 声明返回的字符类型
                            response.setContentType("application/plain;charset=utf-8");
                            // 字符流
                            PrintWriter writer = response.getWriter();
                            // 没有登录，无访问权限
                            writer.write(CommunityUtil.getJSONString(403,"您没有访问此功能的权限!"));
                        } else {
                            // 同步请求，直接跳转到登陆页面
                            response.sendRedirect(request.getContextPath() + "/denied");
                        }
                    }
                });

        // Security底层默认会拦截/logout请求，进行退出处理.
        // 覆盖默认的逻辑，才能执行我们自己的退出代码
        http.logout().logoutUrl("/securitylogout");
    }
}
