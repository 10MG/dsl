package cn.tenmg.dsl.eval;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import cn.tenmg.dsl.EvalEngine;

/**
 * JavaScript 代码执行引擎
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.4.0
 */
public class JavaScriptEngine implements EvalEngine {
	/**
	 * 脚本引擎管理器
	 */
	private static final ScriptEngineManager SCRIPT_ENGINE_MANAGER = new ScriptEngineManager();

	private ScriptEngine scriptEngine;

	public JavaScriptEngine() {
		scriptEngine = SCRIPT_ENGINE_MANAGER.getEngineByName("JavaScript");
	}

	@Override
	public void put(String key, Object value) {
		scriptEngine.put(key, value);
	}

	@Override
	public Object eval(String code) throws Exception {
		return scriptEngine.eval(code);
	}

}
