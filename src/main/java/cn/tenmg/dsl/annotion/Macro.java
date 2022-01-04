package cn.tenmg.dsl.annotion;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 宏注解
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.2.4
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Macro {
	/**
	 * <p>
	 * 宏名称（可选配置）
	 * </p>
	 * 默认为类名的小写格式。例如ElseIf类的默认名称为elseif
	 */
	String name() default "";
}