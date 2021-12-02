package cn.tenmg.dsl.macro;

import java.util.Map;

import javax.script.ScriptEngine;

/**
 * else if判断宏
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.0.0
 */
public class ElseIf extends If {

	@Override
	StringBuilder excute(ScriptEngine scriptEngine, String logic, StringBuilder dslf, Map<String, Object> context)
			throws Exception {
		return Boolean.TRUE.equals(context.get("if")) ? emptyStringBuilder()
				: super.excute(scriptEngine, logic, dslf, context);// if成立，则else if不成立；否则，继续当做if处理
	}

}
