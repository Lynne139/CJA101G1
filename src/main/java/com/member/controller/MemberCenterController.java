package com.member.controller;
 
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.member.model.MemberService;
import com.member.model.MemberVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
 
@Controller
@RequestMapping("/member")
public class MemberCenterController {

    @Autowired
    private MemberService memberSvc;

    // 會員中心頁面
    @GetMapping("/center")
    public String showMemberCenter(HttpSession session, Model model) {
        MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
        if (member == null) {
            return "redirect:/home";
        }
        model.addAttribute("loggedInMember", member);
        return "front-end/member/memberCenter";
    }

    // 顯示修改會員資料表單
    @GetMapping("/edit")
    public String editMember(Model model, HttpSession session) {
        MemberVO loginMember = (MemberVO) session.getAttribute("loggedInMember");
        if (loginMember == null) return "redirect:/home";
        model.addAttribute("memberVO", loginMember);
        return "front-end/member/editMember";
    }

    // 更新會員資料
    @PostMapping("/center/update")
    public String updateMember(@Valid @ModelAttribute("memberVO") MemberVO memberVO,
                               BindingResult result,
                               @RequestParam("uploadPic") MultipartFile uploadPic,
                               Model model,
                               HttpSession session,
                               HttpServletRequest request) {
        MemberVO original = (MemberVO) session.getAttribute("loggedInMember");
        if (original == null) return "redirect:/home";

        if (result.hasErrors()) {
            model.addAttribute("memberVO", memberVO);
            return "front-end/member/editMember"; // 回原本修改畫面
        }

        try {
            if (uploadPic != null && !uploadPic.isEmpty()) {
                memberVO.setMemberPic(uploadPic.getBytes());
            } else {
                memberVO.setMemberPic(original.getMemberPic());
            }

            // 固定不可修改欄位
            memberVO.setMemberId(original.getMemberId());
            memberVO.setMemberEmail(original.getMemberEmail());
            memberVO.setMemberLevel(original.getMemberLevel());
            memberVO.setMemberStatus(original.getMemberStatus());
            memberVO.setMemberPoints(original.getMemberPoints());
            memberVO.setMemberAccumulativeConsumption(original.getMemberAccumulativeConsumption());

            memberSvc.updateMember(memberVO);
            session.setAttribute("loggedInMember", memberVO);

        } catch (IOException e) {
            model.addAttribute("errorMessage", "圖片上傳失敗：" + e.getMessage());
            model.addAttribute("memberVO", memberVO);
            return "front-end/member/editMember";
        }

        return "redirect:/member/center";
    }
}
