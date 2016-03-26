package home.playground.models;

import java.io.Serializable;

public class LanguageAnalysis implements Serializable{
	private String lang;
	private Integer count;

	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
}
