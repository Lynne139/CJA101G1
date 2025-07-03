package com.resto.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.resto.dto.PeriodCodeResponse;
import com.resto.dto.RestoPeriodTimeslotDTO;
import com.resto.dto.TimeslotSimpleDTO;
import com.resto.entity.PeriodVO;
import com.resto.integration.room.RestoPeriodCode;

@Service
public class RestoApiService {

	@Autowired
	PeriodRepository periodRepo;

    @Transactional(readOnly = true)
    public PeriodCodeResponse getInfoByCode(RestoPeriodCode code) {

        List<PeriodVO> list = periodRepo.findByPeriodCodeWithRestoAndTimeslots(code);

        List<RestoPeriodTimeslotDTO> dtoList = list.stream().map(p -> {
            List<TimeslotSimpleDTO> ts = p.getTimeslots().stream()
                    .filter(t -> !Boolean.TRUE.equals(t.getIsDeleted()))
                    .map(t -> new TimeslotSimpleDTO(t.getTimeslotId(), t.getTimeslotName()))
                    .toList();

            return new RestoPeriodTimeslotDTO(
                    p.getRestoVO().getRestoId(),
                    p.getRestoVO().getRestoName(),
                    p.getPeriodId(),
                    p.getPeriodName(),
                    ts
            );
        }).toList();

        return new PeriodCodeResponse(code.getLabel(), code.getCssClass(), dtoList);
    }
}
