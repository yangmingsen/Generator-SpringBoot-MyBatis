package top.yms.generate.entity;

public class FieldsInfo {
	private boolean isPrimary = false;// 是否为主键
	private String name; //字段名
	private String type; //字段类型
	private String desc; //字段描述
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public boolean isPrimary() {
		return isPrimary;
	}

	public void setPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

	@Override
	public String toString() {
		return "FieldsInfo [isPrimary=" + isPrimary + ", name=" + name + ", type=" + type + ", desc=" + desc + "]";
	}

}
