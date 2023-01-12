package cn.tenmg.dsl;

import java.util.Map;

/**
 * 宏
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.0.0
 */
public interface Macro {

	/**
	 * 执行宏并解析DSL动态片段。如果返回结果为{@code true}，则DSL解析立即终止，并以当前宏解析DSL动态片段的结果为DSL解析的最终结果；否则，将当前宏解析的DSL片段结果拼接到DSL的主解析结果中，并继续后续解析。
	 * 
	 * @param context
	 *            DSL上下文
	 * @param attributes
	 *            属性表。由当前层已运行的宏所存储，供本层后续执行的宏使用
	 * @param logic
	 *            逻辑代码
	 * @param dslf
	 *            DSL动态片段
	 * @param params
	 *            宏运行的参数
	 * @return 如果返回结果为{@code true}，则DSL解析立即终止，并以当前宏解析DSL动态片段的结果为DSL解析的最终结果；否则，将当前宏解析的DSL片段结果拼接到DSL的主解析结果中，并继续后续解析。
	 */
	boolean execute(DSLContext context, Map<String, Object> attributes, String logic, StringBuilder dslf,
			Map<String, Object> params) throws Exception;
}
