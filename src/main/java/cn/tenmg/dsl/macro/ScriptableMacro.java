package cn.tenmg.dsl.macro;

import java.util.Map;
import java.util.Map.Entry;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import cn.tenmg.dsl.Macro;

/**
 * 可运行脚本的宏
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.2.3
 */
public abstract class ScriptableMacro extends AbstractMacro implements Macro {

	/**
	 * 脚本引擎管理器
	 */
	protected static final ScriptEngineManager SCRIPT_ENGINE_MANAGER = new ScriptEngineManager();

	@Override
	public StringBuilder execute(String logic, StringBuilder dslf, Map<String, Object> context,
			Map<String, Object> params) throws Exception {
		ScriptEngine scriptEngine = SCRIPT_ENGINE_MANAGER.getEngineByName("JavaScript");
		if (params != null && !params.isEmpty()) {
			for (Entry<String, Object> entry : params.entrySet()) {
				scriptEngine.put(entry.getKey(), entry.getValue());
			}
		}
		return this.excute(scriptEngine, logic, dslf, context);
	}

	/**
	 * 执行宏代码。并返回执行结果
	 * 
	 * @param scriptEngine
	 *            脚本引擎
	 * @param logic
	 *            宏逻辑代码
	 * @param context
	 *            宏运行的上下文
	 * @return
	 * @throws ScriptException
	 */
	abstract StringBuilder excute(ScriptEngine scriptEngine, String logic, StringBuilder dslf,
			Map<String, Object> context) throws Exception;
}
