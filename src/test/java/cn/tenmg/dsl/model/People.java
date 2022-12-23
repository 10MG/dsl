package cn.tenmg.dsl.model;

/**
 * 人民
 * 
 * @author June wjzhao@aliyun.com
 *
 * @since 1.2.10
 */
public class People {

	private String name;

	private Integer yearOfbirth;

	private int age;

	private People parent;

	public People() {
		super();
	}

	public People(String name, int age) {
		super();
		this.name = name;
		this.age = age;
	}

	public People(String name, int age, People parent) {
		super();
		this.name = name;
		this.age = age;
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getYearOfbirth() {
		return yearOfbirth;
	}

	public void setYearOfbirth(String yearOfbirth) {
		if (yearOfbirth == null) {
			this.yearOfbirth = null;
		} else {
			this.yearOfbirth = Integer.valueOf(yearOfbirth);
		}
	}

	public void setYearOfbirth(Integer yearOfbirth) {
		this.yearOfbirth = yearOfbirth;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public People getParent() {
		return parent;
	}

	public void setParent(People parent) {
		this.parent = parent;
	}

}
