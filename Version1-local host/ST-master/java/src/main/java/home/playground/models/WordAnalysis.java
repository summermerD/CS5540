package home.playground.models;

import java.io.Serializable;

import org.apache.hadoop.classification.InterfaceAudience.Private;

public class WordAnalysis implements Serializable{
	
	private String keyword;
	private Integer count;
	
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	

}
