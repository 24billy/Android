package com.billy.worksheetsenderapp.vo;
import java.util.Arrays;
import java.util.Date;

/**
 * �u�u�@��v���� 
 * @author Billy
 *
 */
public class WorkSheetVO {

	private String fileName;  // �ɮצW��
	private Date startDate;  //  �}�l���
	private String consultantName;  // �ϥΪ̦W��
	private double[] hours;  // �ɼ� 
	private String[] system; // �t�ΧO
	private String[] deliverables;  // ��ڧ����u�@���e
	
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