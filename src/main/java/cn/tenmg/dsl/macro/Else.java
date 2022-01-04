package cn.tenmg.dsl.macro;

import java.util.Map;

import javax.script.ScriptEngine;

import cn.tenmg.dsl.annotion.Macro;

/**
 * else判断宏
 * 
 * @author June wjzhao@aliyun.com
 *
 * @since 1.0.0
 */
@Macro
public class Else extends ScriptableMacro {

	@Override
	StringBuilder excute(ScriptEngine scriptEngine, String logic, StringBuilder dslf, Map<String, Object> context)
			throws Exception {
		return Boolean.TRUE.equals(context.get("if")) ? emptyStringBuilder() : dslf;
	}

}
