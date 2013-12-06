package com.eric.autowifi;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class UpdateBean {
	private String serverVersion;
	private String updateUrl;
	private boolean needUpdate;
	private String desc;

	public UpdateBean() {
	}

	public UpdateBean(boolean needUpdate, String serverVersion,
			String updateUrl, String desc) {
		this.needUpdate = needUpdate;
		this.serverVersion = serverVersion;
		this.updateUrl = updateUrl;
		this.desc = desc;
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

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		try {
			this.desc = URLDecoder.decode(desc, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}
