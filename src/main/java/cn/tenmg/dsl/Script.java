package cn.tenmg.dsl;

/**
 * 脚本对象模型
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.2.0
 */
public class Script<T> {

	/**
	 * 脚本
	 */
	private String value;

	/**
	 * 参数
	 */
	private T params;

	public Script() {
		super();
	}

	public Script(String value, T params) {
		super();
		this.value = value;
		this.params = params;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public T getParams() {
		return params;
	}

	public void setParams(T params) {
		this.params = params;
	}

}
