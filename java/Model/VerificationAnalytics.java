package Model;

import java.io.Serializable;

public class VerificationAnalytics implements Serializable{
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
