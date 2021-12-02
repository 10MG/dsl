package cn.tenmg.dsl;

import java.util.Map;

/**
 * 宏
 * 
 * @author 赵伟均 wjzhao@aliyun.com
 * 
 * @since 1.0.0
 */
public interface Macro {
	/**
	 * 执行宏并返回计算结果
	 * 
	 * @param logic
	 *            宏逻辑代码
	 * @param dslf
	 *            DSL动态片段
	 * @param context
	 *            宏运行的上下文
	 * @param params
	 *            宏运行的参数
	 * @return 返回可执行脚本语言的片段
	 */
	StringBuilder excute(String logic, StringBuilder dslf, Map<String, Object> context, Map<String, Object> params)
			throws Exception;
}
