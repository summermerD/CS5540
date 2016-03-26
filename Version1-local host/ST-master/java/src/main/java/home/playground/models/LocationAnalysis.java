package home.playground.models;

import java.io.Serializable;

public class LocationAnalysis implements Serializable{
	private String code;
	private Integer value;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
}
