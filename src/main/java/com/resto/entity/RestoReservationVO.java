package com.resto.entity;

import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Table(name = "resto_reservation")
public class RestoReservationVO {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resto_reserve_id")
    private Integer restoReserveId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resto_id")
    private RestoVO restoVO;

    @NotNull
    @Column(name = "reserve_date")
    private LocalDate reserveDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserve_timeslot_id")
    private TimeslotVO reserveTimeslotVO;

    @NotNull
    @Column(name = "resto_seats_total")
    private Integer restoSeatsTotal;

    @NotNull
    @Column(name = "reserve_seats_total")
    private Integer reserveSeatsTotal = 0;

    
    
	public RestoReservationVO() {
		
	}



	public Integer getRestoReserveId() {
		return restoReserveId;
	}



	public void setRestoReserveId(Integer restoReserveId) {
		this.restoReserveId = restoReserveId;
	}



	public RestoVO getRestoVO() {
		return restoVO;
	}



	public void setRestoVO(RestoVO restoVO) {
		this.restoVO = restoVO;
	}



	public LocalDate getReserveDate() {
		return reserveDate;
	}



	public void setReserveDate(LocalDate reserveDate) {
		this.reserveDate = reserveDate;
	}



	public TimeslotVO getTimeslotVO() {
		return reserveTimeslotVO;
	}



	public void setTimeslotVO(TimeslotVO timeslotVO) {
		this.reserveTimeslotVO = reserveTimeslotVO;
	}



	public Integer getRestoSeatsTotal() {
		return restoSeatsTotal;
	}



	public void setRestoSeatsTotal(Integer restoSeatsTotal) {
		this.restoSeatsTotal = restoSeatsTotal;
	}



	public Integer getReserveSeatsTotal() {
		return reserveSeatsTotal;
	}



	public void setReserveSeatsTotal(Integer reserveSeatsTotal) {
		this.reserveSeatsTotal = reserveSeatsTotal;
	}



	@Override
	public int hashCode() {
		return Objects.hash(restoReserveId);
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RestoReservationVO other = (RestoReservationVO) obj;
		return Objects.equals(restoReserveId, other.restoReserveId);
	}


	@Override
	public String toString() {
		return "RestoReservationVO [restoReserveId=" + restoReserveId + ", restoVO=" + restoVO + ", reserveDate="
				+ reserveDate + ", timeslotVO=" + timeslotVO + ", restoSeatsTotal=" + restoSeatsTotal
				+ ", reserveSeatsTotal=" + reserveSeatsTotal + "]";
	}

	
       
    
    
    
    
    
    
    
    
    
}
