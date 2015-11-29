package home.playground.models;

import java.io.Serializable;

public class HashtagAnalysis implements Serializable
{
	private String hashTag;
	private Integer count;
	public String getHashTag() {
		return hashTag;
	}
	public void setHashTag(String hashTag) {
		this.hashTag = hashTag;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
}
