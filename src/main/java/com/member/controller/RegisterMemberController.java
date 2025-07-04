package com.member.controller;
 
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.member.model.MemberService;
import com.member.model.MemberVO;


@Controller
@RequestMapping("/front-end/member")
public class RegisterMemberController {

    @Autowired
    private MemberService memberSvc;

    @GetMapping("/registerMember")
    public String showRegisterPage(Model model) {
        model.addAttribute("memberVO", new MemberVO());
        return "front-end/member/registerMember";
    }

    @PostMapping("/register")
    public String registerMember(
            @ModelAttribute("memberVO") MemberVO memberVO,
            BindingResult result,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {
    	
    	memberVO.setMemberLevel("普通會員");
        memberVO.setMemberStatus(0);
        memberVO.setMemberPoints(0);
        memberVO.setMemberAccumulativeConsumption(0);
        memberVO.setMemberPic(null);
    	
        if (!memberVO.getMemberPassword().equals(confirmPassword)) {         
            model.addAttribute("confirmPwdError", "密碼與確認密碼不一致");
            return "front-end/member/registerMember";
        }

        if (memberSvc.findByEmail(memberVO.getMemberEmail()) != null) {
            result.rejectValue("memberEmail", null, "電子信箱已註冊，請使用其他信箱");
        }
        
        if (memberVO.getMemberBirthday() != null && memberVO.getMemberBirthday().isAfter(LocalDate.now())) {
            model.addAttribute("birthdayError", "生日日期錯誤");
            return "front-end/member/registerMember";
        }

        if (result.hasErrors()) {
            System.out.println(">>> 表單驗證未通過，錯誤如下：");
            result.getAllErrors().forEach(error -> System.out.println(">>> " + error));
            return "front-end/member/registerMember";
        }


        memberSvc.addMember(memberVO);

        return "redirect:/home";
    }
}