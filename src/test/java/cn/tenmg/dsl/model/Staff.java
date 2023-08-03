package cn.tenmg.dsl.model;

/**
 * 员工
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.4.0
 */
public class Staff {

	private Integer staffId;

	private String staffName;

	private String position;

	public Integer getStaffId() {
		return staffId;
	}

	public void setStaffId(Integer staffId) {
		this.staffId = staffId;
	}

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public Staff() {
		super();
	}

	public Staff(Integer staffId) {
		super();
		this.staffId = staffId;
	}

}
