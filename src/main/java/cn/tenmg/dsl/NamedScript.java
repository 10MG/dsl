package cn.tenmg.dsl;

import java.io.Serializable;
import java.util.Map;

/**
 * 使用命名参数的脚本对象模型
 * 
 * @author 赵伟均 wjzhao@aliyun.com
 *
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

	public void setScript(String script) {
		this.script = script;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

}
