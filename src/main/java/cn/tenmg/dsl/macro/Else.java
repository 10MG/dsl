package cn.tenmg.dsl.macro;

import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import cn.tenmg.dsl.Macro;

/**
 * else判断宏
 * 
 * @author 赵伟均 wjzhao@aliyun.com
 *
 * @since 1.0.0
 */
public class Else extends AbstractMacro implements Macro {

	@Override
	Object excute(ScriptEngine scriptEngine, String code, Map<String, Object> context) throws ScriptException {
		Object ifValue = context.get("if");
		if (ifValue == null) {
			return null;
		} else {
			if (ifValue instanceof Boolean) {
				if (((Boolean) ifValue).booleanValue()) {
					return false;
				} else {
					return true;
				}
			}
			return null;
		}
	}

}
