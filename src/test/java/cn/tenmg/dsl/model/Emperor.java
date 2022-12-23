package cn.tenmg.dsl.model;

/**
 * 皇帝
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.2.10
 */
public class Emperor extends People {

	private String alias;
	
	public Emperor() {
		super();
	}

	public Emperor(String name, int age, String alias) {
		super(name, age);
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

}
