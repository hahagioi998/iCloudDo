package io.jnyou.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 代码千万行，注释第一行，
 * 注释不规范，同事泪两行。
 * <p>
 * 访问权限（元注解）
 *
 * @author JnYou
 * @version 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface VisitPermission {

    /**
     * 用于配置具体接口的权限值
     * 在数据库中添加对应的记录
     * 用户登录时，将用户所有的权限列表放入redis中
     * 用户访问接口时，将对应接口的值和redis中的匹配看是否有访问权限
     * 用户退出登录时，清空redis中对应的权限缓存
     */
    String value() default "";

}
