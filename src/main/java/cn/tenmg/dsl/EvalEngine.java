package cn.tenmg.dsl;

/**
 * 代码执行引擎
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.4.0
 */
public interface EvalEngine {

	/**
	 * 向代码执行对象存入参数
	 * 
	 * @param key
	 *            参数键
	 * @param value
	 *            参数值
	 */
	void put(String key, Object value) throws Exception;

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

}
