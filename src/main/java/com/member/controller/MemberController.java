package com.member.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.member.model.MemberService;
import com.member.model.MemberVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/member")
public class MemberController {
 
	@Autowired
	MemberService memberSvc;
	
	// 查詢頁面
	 @GetMapping("/select_page")
	 public String showSelectPage(Model model, HttpServletRequest request) {
	     List<MemberVO> memberList = memberSvc.getAll();
	     model.addAttribute("memberListData", memberList);
	     model.addAttribute("currentURI", request.getRequestURI());
	     model.addAttribute("mainFragment", "admin/fragments/member/select_page");
	     return "admin/index_admin";
	 }
	
	 // 查詢 by ID
	 @PostMapping("/searchById")
	 public String searchById(@RequestParam("memberId") String memberId,
	                          Model model, HttpServletRequest request) {
	     List<MemberVO> memberList = new ArrayList<>();
	     try {
	         MemberVO memberVO = memberSvc.getOneMember(Integer.valueOf(memberId));
	         if (memberVO != null) memberList.add(memberVO);
	     } catch (NumberFormatException e) {
	         model.addAttribute("error", "會員編號格式錯誤");
	     }
	     model.addAttribute("memberListData", memberList);
	     model.addAttribute("currentURI", request.getRequestURI());
	     model.addAttribute("mainFragment", "admin/fragments/member/listAllMember");
	     return "admin/index_admin";
	 }
	
	 // 查詢 by 姓名
	 @PostMapping("/searchByName")
	 public String searchByName(@RequestParam("memberName") String memberName,
	                            Model model, HttpServletRequest request) {
	     List<MemberVO> memberList = memberSvc.findByNameLike(memberName);
	     model.addAttribute("memberListData", memberList);
	     model.addAttribute("currentURI", request.getRequestURI());
	     model.addAttribute("mainFragment", "admin/fragments/member/listAllMember");
	     return "admin/index_admin";
	 }
	
	 // 新增會員畫面
	 @GetMapping("/addMember")
	 public String addMember(Model model, HttpServletRequest request) {
	     model.addAttribute("memberVO", new MemberVO());
	     model.addAttribute("memberListData", memberSvc.getAll());
	     model.addAttribute("currentURI", request.getRequestURI());
	     model.addAttribute("mainFragment", "admin/fragments/member/addMember");
	     return "admin/index_admin";
	 }
	
	 // 新增會員功能
	 @PostMapping("/insert")
	 public String insert(@ModelAttribute("memberVO") @Valid MemberVO memberVO,
	                      BindingResult result,
	                      @RequestParam("uploadPic") MultipartFile file,
	                      Model model) {
	     if (result.hasErrors()) {
	         model.addAttribute("memberVO", memberVO);
	         return "admin/fragments/member/addMember"; // 注意：這裡是 fragment，不走 layout
	     }
	
	     try {
	         if (!file.isEmpty()) {
	             memberVO.setMemberPic(file.getBytes());
	         }
	     } catch (IOException e) {
	         e.printStackTrace();
	     }
	
	     memberSvc.addMember(memberVO);
	     return "redirect:/admin/listAllMember";
	 }
	
	 // 會員列表
	 @GetMapping("/listAllMember")
	 public String listAllMember(Model model, HttpServletRequest request) {
	     model.addAttribute("memberListData", memberSvc.getAll());
	     model.addAttribute("currentURI", request.getRequestURI());
	     model.addAttribute("mainFragment", "admin/fragments/member/listAllMember");
	     return "admin/index_admin";
	 }
	
	 // 修改會員畫面
	 @GetMapping("/updateMember/{memberId}")
	 public String getUpdateMemberPage(@PathVariable("memberId") Integer memberId,
	                                   Model model, HttpServletRequest request) {
	     model.addAttribute("memberVO", memberSvc.getOneMember(memberId));
	     model.addAttribute("currentURI", request.getRequestURI());
	     model.addAttribute("mainFragment", "admin/fragments/member/update_member_input");
	     return "admin/index_admin";
	 }
	
	 // 更新會員功能
	 @PostMapping("/update")
	 public String update(@Valid @ModelAttribute("memberVO") MemberVO memberVO,
	                      BindingResult result,
	                      @RequestParam("uploadPic") MultipartFile uploadPic,
	                      Model model, HttpServletRequest request) {
	     if (result.hasErrors()) {
	         model.addAttribute("memberVO", memberVO);
	         model.addAttribute("currentURI", request.getRequestURI());
	         model.addAttribute("mainFragment", "admin/fragments/member/update_member_input");
	         return "admin/index_admin";
	     }
	
	     try {
	         MemberVO existingMember = memberSvc.getOneMember(memberVO.getMemberId());
	
	         if (uploadPic != null && !uploadPic.isEmpty()) {
	             memberVO.setMemberPic(uploadPic.getBytes());
	         } else {
	             memberVO.setMemberPic(existingMember.getMemberPic());
	         }
	
	         memberSvc.updateMember(memberVO);
	
	     } catch (IOException e) {
	         model.addAttribute("errorMessage", "圖片上傳失敗：" + e.getMessage());
	         model.addAttribute("memberVO", memberVO);
	         model.addAttribute("currentURI", request.getRequestURI());
	         model.addAttribute("mainFragment", "admin/fragments/member/update_member_input");
	         return "admin/index_admin";
	     }
	
	     return "redirect:/admin/listAllMember";
	 }
	
	 // 刪除會員功能
	 @PostMapping("/delete")
	 public String delete(@RequestParam("memberId") String memberId,
	                      Model model) {
	     memberSvc.deleteMember(Integer.valueOf(memberId));
	     model.addAttribute("success", "- (刪除成功)");
	     return "redirect:/admin/listAllMember";
	 }

	 // getOne_For_Update
	 @PostMapping("/getOne_For_Update")
	 public String getOneForUpdate(@RequestParam("memberId") String memberId,
	                               Model model, HttpServletRequest request) {
	     MemberVO memberVO = memberSvc.getOneMember(Integer.valueOf(memberId));
	     model.addAttribute("memberVO", memberVO);
	     model.addAttribute("currentURI", request.getRequestURI());
	     model.addAttribute("mainFragment", "admin/fragments/member/update_member_input");
	     return "admin/index_admin";
	 }
    
    
    
}
