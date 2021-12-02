package cn.tenmg.dsl.macro;

import java.util.Map;

import javax.script.ScriptEngine;

/**
 * if判断宏
 * 
 * @author June wjzhao@aliyun.com
 *
 * @since 1.0.0
 */
public class If extends ScriptableMacro {

	@Override
	StringBuilder excute(ScriptEngine scriptEngine, String logic, StringBuilder dslf, Map<String, Object> context)
			throws Exception {
		Object result = scriptEngine.eval(logic);
		context.put("if", result);
		return Boolean.TRUE.equals(result) ? dslf : emptyStringBuilder();
	}

}
