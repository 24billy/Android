package com.billy.worksheetsenderapp.vo;
import java.util.Arrays;
import java.util.Date;

/**
 * 「工作表」物件 
 * @author Billy
 *
 */
public class WorkSheetVO {

	private String fileName;  // 檔案名稱
	private Date startDate;  //  開始日期
	private String consultantName;  // 使用者名稱
	private double[] hours;  // 時數 
	private String[] system; // 系統別
	private String[] deliverables;  // 實際完成工作內容
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public String getConsultantName() {
		return consultantName;
	}
	public void setConsultantName(String consultantName) {
		this.consultantName = consultantName;
	}
	public double[] getHours() {
		return hours;
	}
	public void setHours(double[] hours) {
		this.hours = hours;
	}
	public String[] getSystem() {
		return system;
	}
	public void setSystem(String[] system) {
		this.system = system;
	}
	public String[] getDeliverables() {
		return deliverables;
	}
	public void setDeliverables(String[] deliverables) {
		this.deliverables = deliverables;
	}

	@Override
	public String toString() {
		return "WorkSheetVO [fileName=" + fileName + ", startDate=" + startDate
				+ ", consultantName=" + consultantName + ", hours="
				+ Arrays.toString(hours) + ", system="
				+ Arrays.toString(system) + ", deliverables="
				+ Arrays.toString(deliverables) + "]";
	}
	
}