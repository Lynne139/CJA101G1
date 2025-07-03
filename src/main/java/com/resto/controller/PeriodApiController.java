package com.resto.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.resto.dto.PeriodCodeResponse;
import com.resto.integration.room.RestoPeriodCode;
import com.resto.model.RestoApiService;


@RestController
@RequestMapping("/api/resto/period")
public class PeriodApiController {

	@Autowired
    RestoApiService queryService;

    public PeriodApiController(RestoApiService queryService) {
        this.queryService = queryService;
    }

    @PostMapping("/byCode")
    public ResponseEntity<?> getByCode(@RequestParam String periodCode) {

        RestoPeriodCode codeEnum;
        try {
            codeEnum = RestoPeriodCode.valueOf(periodCode);
        } catch (IllegalArgumentException ex) {
            try {
                int codeInt = Integer.parseInt(periodCode);
                codeEnum = RestoPeriodCode.fromCode(codeInt);
            } catch (Exception e) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "無效的 periodCode：" + periodCode));
            }
        }

        PeriodCodeResponse resp = queryService.getInfoByCode(codeEnum);
        return ResponseEntity.ok(resp);
    }
}