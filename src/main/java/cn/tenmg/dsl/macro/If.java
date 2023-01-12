package cn.tenmg.dsl.macro;

import java.util.Map;

import javax.script.ScriptEngine;

import cn.tenmg.dsl.DSLContext;

/**
 * if判断宏
 * 
 * @author June wjzhao@aliyun.com
 *
 * @since 1.0.0
 */
public class If extends ScriptableMacro {
	
	@Override
	boolean excute(ScriptEngine scriptEngine, DSLContext context, Map<String, Object> attributes, String logic,
			StringBuilder dslf) throws Exception {
		Object result = scriptEngine.eval(toExecutable(logic));
		attributes.put("if", result);
		if (!Boolean.TRUE.equals(result)) {
			dslf.setLength(0);
		}
		return false;
	}
}
