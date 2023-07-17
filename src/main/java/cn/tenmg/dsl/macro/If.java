package cn.tenmg.dsl.macro;

import java.util.Map;

import cn.tenmg.dsl.DSLContext;
import cn.tenmg.dsl.EvalEngine;

/**
 * if判断宏
 * 
 * @author June wjzhao@aliyun.com
 *
 * @since 1.0.0
 */
public class If extends EvalableMacro {

	@Override
	boolean excute(EvalEngine evalEngine, DSLContext context, Map<String, Object> attributes, String logic,
			StringBuilder dslf) throws Exception {
		Object result = evalEngine.eval(toEvalable(logic));
		attributes.put("if", result);
		if (!Boolean.TRUE.equals(result)) {
			dslf.setLength(0);
		}
		return false;
	}
}
