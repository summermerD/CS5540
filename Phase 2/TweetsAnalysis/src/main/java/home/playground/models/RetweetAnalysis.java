package home.playground.models;

import java.io.Serializable;


public class RetweetAnalysis implements Serializable{
	private String text;
	private Integer retweetCount;
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Integer getRetweetCount() {
		return retweetCount;
	}
	public void setRetweetCount(Integer retweetCount) {
		this.retweetCount = retweetCount;
	}
	

}
