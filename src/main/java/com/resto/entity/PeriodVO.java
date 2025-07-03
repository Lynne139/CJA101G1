package com.resto.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.resto.integration.room.RestoPeriodCode;
import com.resto.integration.room.RestoPeriodCodeConverter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "resto_period")
public class PeriodVO{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "period_id", updatable = false)
	private Integer periodId;
	
	@NotBlank
	@Size(max = 10, message = "類別名稱請勿超過10字")
	@Column(name = "period_name")
	private String periodName;
	
	@Column(name = "sort_order")
	private Integer sortOrder;
	
	@ManyToOne
	@JoinColumn(name="resto_id", nullable = false)
	private RestoVO restoVO;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="periodVO", orphanRemoval = true)
	@OrderBy("timeslot_id asc")
	private List<TimeslotVO> timeslots = new ArrayList<>();

	
	@Convert(converter = RestoPeriodCodeConverter.class)
	@Column(name = "period_code")
	private RestoPeriodCode periodCode;
	
	
	public PeriodVO() {
		super();
	}

	public Integer getPeriodId() {
		return periodId;
	}

	public void setPeriodId(Integer periodId) {
		this.periodId = periodId;
	}

	public String getPeriodName() {
		return periodName;
	}

	public void setPeriodName(String periodName) {
		this.periodName = periodName;
	}
	
	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public List<TimeslotVO> getTimeslots() {
		return timeslots;
	}

	public void setTimeslots(List<TimeslotVO> timeslots) {
		this.timeslots = timeslots;
	}

	public RestoVO getRestoVO() {
		return restoVO;
	}

	public void setRestoVO(RestoVO restoVO) {
		this.restoVO = restoVO;
	}

	
	
	public RestoPeriodCode getPeriodCode() {
		return periodCode;
	}

	public void setPeriodCode(RestoPeriodCode periodCode) {
		this.periodCode = periodCode;
	}

	@Override
	public int hashCode() {
		return Objects.hash(periodId);
	}
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PeriodVO other = (PeriodVO) obj;
		return Objects.equals(periodId, other.periodId);
	}

	@Override
	public String toString() {
		return "PeriodVO [periodId=" + periodId + ", periodName=" + periodName + ", sortOrder=" + sortOrder
				+ ", restoVO=" + restoVO + ", timeslots=" + timeslots + ", periodCode=" + periodCode + "]";
	}

	

	
	
	
}