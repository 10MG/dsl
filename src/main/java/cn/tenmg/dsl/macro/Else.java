package cn.tenmg.dsl.macro;

import java.util.Map;

import cn.tenmg.dsl.DSLContext;
import cn.tenmg.dsl.EvalEngine;

/**
 * else判断宏
 * 
 * @author June wjzhao@aliyun.com
 *
 * @since 1.0.0
 */
public class Else extends EvalableMacro {

	@Override
	boolean excute(EvalEngine evalEngine, DSLContext context, Map<String, Object> attributes, String logic,
			StringBuilder dslf) throws Exception {
		if (Boolean.TRUE.equals(attributes.get("if"))) {
			dslf.setLength(0);
		}
		return false;
	}

}
