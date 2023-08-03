package cn.tenmg.dsl;

import java.util.Map;

/**
 * 代码执行引擎
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.4.0
 */
public interface EvalEngine {

	/**
	 * 代码执行前调用
	 */
	void open();

	/**
	 * 向代码执行对象存入参数
	 * 
	 * @param params
	 *            参数
	 */
	void put(Map<String, Object> params) throws Exception;

	/**
	 * 执行代码
	 * 
	 * @param code
	 *            代码
	 * @return 执行代码的结果
	 * @throws Exception
	 *             发生异常
	 */
	Object eval(String code) throws Exception;

	/**
	 * 代码执行后调用
	 */
	void close();

}
