package com.resto.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
	
	@NotBlank(message = "區段設置不得為空")
	@Size(max = 10, message = "區段設置請勿超過10字")
	@Column(name = "period_name")
	private String periodName;

	@ManyToOne
	@JoinColumn(name="resto_id")
	private RestoVO restoVO;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, mappedBy="timeslotId")
	@OrderBy("timeslot_id asc")
	private Set<TimeslotVO> timeslots = new HashSet<TimeslotVO>();

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

	@Override
	public int hashCode() {
		return Objects.hash(periodId, periodName);
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
		return Objects.equals(periodId, other.periodId) && Objects.equals(periodName, other.periodName);
	}

	@Override
	public String toString() {
		return "PeriodVO [periodId=" + periodId + ", periodName=" + periodName + "]";
	}
	
	
}
