package com.ximen.springbootshiro.config.shiroconfig;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author: 西门小狼狗
 * @Date: 2019/5/14 16:04
 */
@Configuration
public class ShiroConfig {
    /**
     * 创建ShiroFilterFactoryBean
     */
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager")DefaultWebSecurityManager securityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        /**
         * Shiro内置过滤器，可以实现权限相关的拦截器
         * 常用的过滤器：
         *      anon:无需认证(登录)可以访问
         *      authc:必须认证才可以访问
         *      user:如果使用remember的功能可以直接访问
         *      perms:该资源必须得到资源权限才可以访问
         *      role:该资源必须得到角色权限才可以访问
         */
        //设置拦截页面
        Map<String, String> filterMap = new LinkedHashMap<>();
        filterMap.put("/user/doLogin","anon");
        //授权过滤器
        //注意：当前授权拦截后，shiro会自动跳转到位授权页面
        filterMap.put("/user/add", "perms[user:add]");
        //设置登录页面
        shiroFilterFactoryBean.setLoginUrl("/user/toLogin");
        //设置未授权页面
        shiroFilterFactoryBean.setUnauthorizedUrl("/user/noAuth");
        filterMap.put("/**","authc");//放在路径拦截的最后面
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
        //设置安全管理器
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        return shiroFilterFactoryBean;
    }

    /**
     * 创建DefaultWebSecurityManager
     */
    @Bean(name="securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm")UserRealm userRealm){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //关联realm
        securityManager.setRealm(userRealm);
        return securityManager;
    }

    /**
     * 创建Realm
     */
    @Bean(name="userRealm")
    public UserRealm getRealm(){
        return new UserRealm();
    }

    /**
     * 配置ShiroDialect，用于thymeleaf和shiro标签配合使用
     */
    @Bean
    public ShiroDialect getShiroDialect(){
        return new ShiroDialect();
    }
}
