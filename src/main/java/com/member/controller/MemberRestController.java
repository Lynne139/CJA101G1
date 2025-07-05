package com.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.member.model.MemberService;
import com.member.model.MemberVO;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/member")
public class MemberRestController {

    @Autowired
    MemberService memberSvc;

    @GetMapping("/points")
    public ResponseEntity<?> getMemberPoints(HttpSession session) {
    	MemberVO loggedInMember = (MemberVO) session.getAttribute("loggedInMember");
    	Integer memberId = loggedInMember.getMemberId();
    	Integer points = memberSvc.getMemberPointsById(memberId);
        if (points != null) {
            return ResponseEntity.ok().body(points);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("會員不存在或查無資料");
        }
    }
}
