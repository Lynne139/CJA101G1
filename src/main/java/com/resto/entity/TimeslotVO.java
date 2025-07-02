
package com.resto.entity;

import java.time.LocalTime;
import java.util.Objects;

import com.resto.utils.ValidationGroups;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "resto_timeslot")
public class TimeslotVO{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "timeslot_id", updatable = false)
	private Integer timeslotId;

	@NotBlank(groups = ValidationGroups.First.class)
	@Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "請輸入有效的時間格式（HH:mm）",groups = ValidationGroups.Second.class)
	@Column(name = "timeslot_name")
	private String timeslotName;
	
	@Column(name = "is_deleted") //軟刪除
	private Boolean isDeleted = false;
	
	@ManyToOne
	@JoinColumn(name="resto_id", nullable = false)
	private RestoVO restoVO;
	
	@ManyToOne
	@JoinColumn(name="period_id")
	private PeriodVO periodVO;
	
	
	public TimeslotVO() {
	}


	public TimeslotVO(Integer timeslotId) {
		super();
		this.timeslotId = timeslotId;
	}

	
	
	// 計算開始時間
	public LocalTime getLocalTime() {
	    return LocalTime.parse(this.timeslotName);
	}

	


	public Integer getTimeslotId() {
		return timeslotId;
	}

	public void setTimeslotId(Integer timeslotId) {
		this.timeslotId = timeslotId;
	}

	public RestoVO getRestoVO() {
		return restoVO;
	}

	public void setRestoVO(RestoVO restoVO) {
		this.restoVO = restoVO;
	}

	public PeriodVO getPeriodVO() {
		return periodVO;
	}

	public void setPeriodVO(PeriodVO periodVO) {
		this.periodVO = periodVO;
	}

	public String getTimeslotName() {
		return timeslotName;
	}

	public void setTimeslotName(String timeslotName) {
		this.timeslotName = timeslotName;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	@Override
	public int hashCode() {
		return Objects.hash(isDeleted, timeslotId, timeslotName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimeslotVO other = (TimeslotVO) obj;
		return Objects.equals(isDeleted, other.isDeleted) && Objects.equals(timeslotId, other.timeslotId)
				&& Objects.equals(timeslotName, other.timeslotName);
	}

	@Override
	public String toString() {
		return "TimeslotVO [timeslotId=" + timeslotId + ", timeslotName=" + timeslotName + ", isDeleted=" + isDeleted
				+ "]";
	}
		
	
}
