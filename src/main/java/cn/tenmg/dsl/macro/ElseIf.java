package cn.tenmg.dsl.macro;

import java.util.Map;

import cn.tenmg.dsl.DSLContext;
import cn.tenmg.dsl.EvalEngine;

/**
 * else if判断宏
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.0.0
 */
public class ElseIf extends If {

	@Override
	boolean excute(EvalEngine evalEngine, DSLContext context, Map<String, Object> attributes, String logic,
			StringBuilder dslf) throws Exception {
		if (Boolean.TRUE.equals(attributes.get("if"))) {// if成立，则else if不成立
			dslf.setLength(0);
			return false;
		} else {// 否则，继续当做if处理
			return super.excute(evalEngine, context, attributes, logic, dslf);
		}
	}

}
