package com.chen.library.DataClass;

public class HistoryProjects {
	private String time;
	private String projectName;
	private String projectCategory;
	private String projectCost;
	private int flag;

	public HistoryProjects() {
		this.flag = 1;
	}

	public HistoryProjects(String time, String projectName,
			String projectCategory, String projectCost) {
		this.time = time;
		this.projectName = projectName;
		this.projectCategory = projectCategory;
		this.projectCost = projectCost;
		this.flag = 1;
	}

	public String getTime() {
		return time;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getProjectCategory() {
		return projectCategory;
	}

	public String getProjectCost() {
		return projectCost;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public int getFlag() {
		return flag;
	}

}
