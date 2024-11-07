package cn.tenmg.dsl.macro;

import java.util.Map;

import cn.tenmg.dsl.DSLContext;
import cn.tenmg.dsl.EvalEngine;
import cn.tenmg.dsl.Macro;
import cn.tenmg.dsl.eval.JavaScriptEngine;
import cn.tenmg.dsl.utils.ConfigUtils;
import cn.tenmg.dsl.utils.DSLUtils;
import cn.tenmg.dsl.utils.MapUtils;
import cn.tenmg.dsl.utils.StringUtils;

/**
 * 可运行脚本的宏
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.4.0
 */
public abstract class EvalableMacro implements Macro {

	private static final EvalEngine evalEngine = getEvalEngine();

	private static EvalEngine getEvalEngine() {
		String className = ConfigUtils.getProperty("macro.eval-engine");
		EvalEngine evalEngine;
		if (StringUtils.isBlank(className)) {
			evalEngine = new JavaScriptEngine();
		} else {
			try {
				evalEngine = (EvalEngine) Class.forName(className).getConstructor().newInstance();
			} catch (Exception e) {
				throw new RuntimeException(String.format("Cannot instantiated {} using default constructor", className),
						e);
			}
		}
		return evalEngine;
	}

	@Override
	public boolean execute(DSLContext context, Map<String, Object> attributes, String logic, StringBuilder dslf,
			Map<String, Object> params) throws Exception {
		try {
			evalEngine.open();
			if (MapUtils.isNotEmpty(params)) {
				evalEngine.put(params);
			}
			return this.excute(evalEngine, context, attributes, logic, dslf);
		} finally {
			evalEngine.close();
		}
	}

	/**
	 * 将带参数的字符串代码转换为 {@code ScriptEngine} 可执行的代码
	 * 
	 * @param code 带参数的字符串代码
	 * @return 可执行的代码
	 */
	protected static String toEvalable(String code) {
		int backslashes = 0;
		boolean isString = false;// 是否在字符串区域
		boolean isParam = false;// 是否在参数区域
		char a = DSLUtils.BLANK_SPACE, b = DSLUtils.BLANK_SPACE;
		StringBuilder codeBuilder = new StringBuilder();
		for (int i = 0, len = code.length(); i < len; i++) {
			char c = code.charAt(i);
			if (isString) {
				if (c == DSLUtils.BACKSLASH) {
					backslashes++;
				} else {
					if (DSLUtils.isStringEnd(a, b, c, backslashes)) {// 字符串区域结束
						isString = false;
					}
					backslashes = 0;
				}
				codeBuilder.append(c);
			} else {
				if (isParam) {
					if (!DSLUtils.isParamChar(c)) {
						isParam = false;
					}
					codeBuilder.append(c);
				} else {
					if (DSLUtils.isParamBegin(a, b, c)) {
						isParam = true;
						codeBuilder.setCharAt(codeBuilder.length() - 1, c);
					} else {
						codeBuilder.append(c);
					}
				}
			}
			a = b;
			b = c;
		}
		return codeBuilder.toString();
	}

	/**
	 * 执行宏并解析DSL动态片段。如果返回结果为{@code true}，则DSL解析立即终止，并以当前宏解析DSL动态片段的结果为DSL解析的最终结果；否则，将当前宏解析的DSL片段结果拼接到DSL的主解析结果中，并继续后续解析。
	 * 
	 * @param evalEngine 代码执行引擎
	 * @param context    DSL上下文
	 * @param attributes 属性表。由当前层已运行的宏所存储，供本层后续执行的宏使用
	 * @param logic      宏逻辑代码
	 * @param dslf       DSL片段构建器
	 * @return 如果返回结果为{@code true}，则DSL解析立即终止，并以当前宏解析DSL动态片段的结果为DSL解析的最终结果；否则，将当前宏解析的DSL片段结果拼接到DSL的主解析结果中，并继续后续解析。
	 * @throws Exception
	 */
	abstract boolean excute(EvalEngine evalEngine, DSLContext context, Map<String, Object> attributes, String logic,
			StringBuilder dslf) throws Exception;
}
