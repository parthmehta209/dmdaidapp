package com.example.data;

import android.R.integer;

public class Medication {
	
	public Integer _id; 
	public String medicationName; 
	public int dose; 
	public String units;
	public int times;
	public String timesPer;
	public String startMonth;
	public String endMonth;
	public String type;
	public int dirty;
	
	public Integer get_id() {
		return _id;
	}
	public void set_id(Integer _id) {
		this._id = _id;
	}
	public String getMedicationNameString() {
		return medicationName;
	}
	public void setMedicationNameString(String medicationNameString) {
		this.medicationName = medicationNameString;
	}
	public int getDose() {
		return dose;
	}
	public void setDose(int dose) {
		this.dose = dose;
	}
	public String getUnits() {
		return units;
	}
	public void setUnits(String units) {
		this.units = units;
	}
	public int getTimes() {
		return times;
	}
	public void setTimes(int times) {
		this.times = times;
	}
	public String getTimesPer() {
		return timesPer;
	}
	public void setTimesPer(String timesPer) {
		this.timesPer = timesPer;
	}
	public String getStartMonth() {
		return startMonth;
	}
	public void setStartMonth(String startMonth) {
		this.startMonth = startMonth;
	}
	public String getEndMonth() {
		return endMonth;
	}
	public void setEndMonth(String endMonth) {
		this.endMonth = endMonth;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getDirty() {
		return dirty;
	}
	public void setDirty(int dirty) {
		this.dirty = dirty;
	}
	@Override
	public String toString() {
		return medicationName + " "
				+ dose +" "
				+ units +" "
				+ times +" "
				+ timesPer +" "
				+ startMonth +" "
				+ endMonth;

	}
}
