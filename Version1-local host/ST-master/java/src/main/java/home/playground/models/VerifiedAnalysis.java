package home.playground.models;

import java.io.Serializable;

public class VerifiedAnalysis implements Serializable {
	private Boolean verified;
	private Integer count;
	
	public Boolean getVerified() {
		return verified;
	}
	public void setVerified(Boolean verified) {
		this.verified = verified;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}

}
