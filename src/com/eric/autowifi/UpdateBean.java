package com.eric.autowifi;

public class UpdateBean {
	private String serverVersion;
	private String updateUrl;
	private boolean needUpdate;

	public UpdateBean() {
	}

	public UpdateBean(boolean needUpdate, String serverVersion, String updateUrl) {
		this.needUpdate = needUpdate;
		this.serverVersion = serverVersion;
		this.updateUrl = updateUrl;
	}

	public String getServerVersion() {
		return serverVersion;
	}

	public void setServerVersion(String serverVersion) {
		this.serverVersion = serverVersion;
	}

	public String getUpdateUrl() {
		return updateUrl;
	}

	public void setUpdateUrl(String updateUrl) {
		this.updateUrl = updateUrl;
	}

	public boolean isNeedUpdate() {
		return needUpdate;
	}

	public void setNeedUpdate(boolean needUpdate) {
		this.needUpdate = needUpdate;
	}

}
