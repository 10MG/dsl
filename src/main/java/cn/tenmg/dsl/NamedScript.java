package cn.tenmg.dsl;

import java.io.Serializable;
import java.util.Map;

/**
 * 使用命名参数的脚本对象模型
 * 
 * @author June wjzhao@aliyun.com
 *
 * @since 1.0.0
 */
public class NamedScript implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1957193573552275926L;

	/**
	 * SQL
	 */
	private String script;

	/**
	 * 参数
	 */
	private Map<String, Object> params;

	public NamedScript() {
		super();
	}

	public NamedScript(String script) {
		super();
		this.script = script;
	}

	public NamedScript(String script, Map<String, Object> params) {
		super();
		this.script = script;
		this.params = params;
	}

	public String getScript() {
		return script;
	}

	public Map<String, Object> getParams() {
		return params;
	}

}
