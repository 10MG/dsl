package cn.tenmg.dsl.macro;

import java.util.Map;

import javax.script.ScriptEngine;

/**
 * else判断宏
 * 
 * @author 赵伟均 wjzhao@aliyun.com
 *
 * @since 1.0.0
 */
public class Else extends ScriptableMacro {

	@Override
	StringBuilder excute(ScriptEngine scriptEngine, String logic, StringBuilder dslf, Map<String, Object> context)
			throws Exception {
		return Boolean.TRUE.equals(context.get("if")) ? emptyStringBuilder() : dslf;
	}

}
