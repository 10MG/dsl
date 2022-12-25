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

	private People father;

	public People() {
		super();
	}

	public People(String name, int age) {
		super();
		this.name = name;
		this.age = age;
	}

	public People(String name, int age, People father) {
		super();
		this.name = name;
		this.age = age;
		this.father = father;
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

	public People getFather() {
		return father;
	}

	public void setFather(People father) {
		this.father = father;
	}

}
