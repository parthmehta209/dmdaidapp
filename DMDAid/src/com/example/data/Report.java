package com.example.data;

import java.util.List;

public class Report {
	
	private long reportId; 
	private String category; 
	private String reportName; 
	private String reportDate; 
	private String reportType;
	private String pdfPath; 
	private int dirty;
	private List<String> images;
	
	
	public Report(long reportId, String category, String reportName, String reportDate,String reportType,int dirty) {
		this.reportId = reportId; 
		this.category = category; 
		this.reportName = reportName; 
		this.reportDate = reportDate; 
		this.reportType = reportType;
		this.dirty = dirty; 
	}
	
	public Report() {
		
	}

	public long getReportId() {
		return reportId;
	}
	public void setReportId(long reportId) {
		this.reportId = reportId;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getReportName() {
		return reportName;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	public String getReportDate() {
		return reportDate;
	}
	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}
	
	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public String getPdfPath() {
		return pdfPath;
	}

	public void setPdfPath(String pdfPath) {
		this.pdfPath = pdfPath;
	}


	public int getDirty() {
		return dirty;
	}

	public void setDirty(int dirty) {
		this.dirty = dirty;
	}

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

	public String toString() {
		
		return new String(reportId +":"+category+ ":" + reportName + ":" + reportDate);
	}
}
