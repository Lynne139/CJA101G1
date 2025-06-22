package com.member.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.member.model.MemberService;
import com.member.model.MemberVO;

import jakarta.validation.Valid;
 
 
@Controller
@RequestMapping("/member")
public class MemberController {
	
	@Autowired
	MemberService memberSvc;
	
	/*
	 * This method will serve as addMember.html handler.
	 */
	@GetMapping("addMember")
	public String addMember(ModelMap model) {
		MemberVO memberVO = new MemberVO();
		model.addAttribute("memberVO", memberVO);
		return "back-end/member/addMember";
	}
	
	/*
	 * This method will be called on addMember.html form submission, handling POST request It also validates the user input
	 */
	@PostMapping("insert")
	public String insert(@Valid MemberVO memberVO, BindingResult result, ModelMap model,
			@RequestParam("memberPic") MultipartFile[] parts) throws IOException{
		
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
			
		if (result.hasErrors()) {
			return "back-end/member/addMember";
		}else {
			for (MultipartFile multipartFile : parts) {
				if (!multipartFile.isEmpty()) {
					byte[] buf = multipartFile.getBytes();
					memberVO.setMemberPic(buf);
				}
			}
		}
		/*************************** 2.開始新增資料 *****************************************/
		// MemberService memberSvc = new MemberService();
		memberSvc.addMember(memberVO);
		/*************************** 3.新增完成,準備轉交(Send the Success view) **************/
		List<MemberVO> list = memberSvc.getAll();
		model.addAttribute("memberListData", list); // for listAllMember.html
		model.addAttribute("success", "- (新增成功)");
		return "redirect:/member/listAllMember"; // 新增成功後重導至IndexController_inSpringBoot.java的@GetMapping("/member/listAllMember")
	}
	
	/*
	 * This method will be called on listAllMember.html form submission, handling POST request
	 */
	@PostMapping("getOne_For_Update")
	public String getOne_For_Update(@RequestParam("memberId") String memberId, ModelMap model) {
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		/*************************** 2.開始查詢資料 *****************************************/
		// MemberService memberSvc = new MemberService();
		MemberVO memberVO = memberSvc.getOneMember(Integer.valueOf(memberId));

		/*************************** 3.查詢完成,準備轉交(Send the Success view) **************/
		model.addAttribute("memberVO", memberVO);
		return "back-end/member/update_member_input"; // 查詢完成後轉交update_member_input.html
	}
	
	/*
	 * This method will be called on update_member_input.html form submission, handling POST request It also validates the user input
	 */
	@PostMapping("update")
	public String update(@Valid MemberVO memberVO, BindingResult result, ModelMap model,
			@RequestParam("memberPic") MultipartFile[] parts) throws IOException{
		
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/	
		if (result.hasErrors()) {
			return "back-end/member/update_member_input";
		}else {
			for (MultipartFile multipartFile : parts) {
				if (!multipartFile.isEmpty()) {
					byte[] buf = multipartFile.getBytes();
					memberVO.setMemberPic(buf);
				}
			}
		}
		/*************************** 2.開始修改資料 *****************************************/
		// MemberService memberSvc = new MemberService();
		memberSvc.updateMember(memberVO);
		/*************************** 3.修改完成,準備轉交(Send the Success view) **************/
		model.addAttribute("success", "- (修改成功)");
		memberVO = memberSvc.getOneMember(Integer.valueOf(memberVO.getMemberId()));
		model.addAttribute("memberVO", memberVO);
		return "back-end/member/listOneMember"; // 修改成功後轉交listOneMember.html
	}
	
	/*
	 * This method will be called on listAllMember.html form submission, handling POST request
	 */
	@PostMapping("delete")
	public String delete(@RequestParam("memberId") String memberId, ModelMap model) {
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		/*************************** 2.開始刪除資料 *****************************************/
		// MemberService memberSvc = new MemberService();
		memberSvc.deleteMember(Integer.valueOf(memberId));
		/*************************** 3.刪除完成,準備轉交(Send the Success view) **************/
		List<MemberVO> list = memberSvc.getAll();
		model.addAttribute("memberListData", list); // for listAllMember.html
		model.addAttribute("success", "- (刪除成功)");
		return "back-end/member/listAllMember"; // 刪除完成後轉交listAllMember.html
	}
	
	

}
