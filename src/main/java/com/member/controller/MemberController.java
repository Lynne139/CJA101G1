package com.member.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
	public String addMember(Model model) {
		model.addAttribute("memberVO", new MemberVO());
		model.addAttribute("mainFragment", "admin/fragments/member/addMember");
		return "admin/index_admin";
	}

	/*
	 * This method will serve as listAllMember.html handler.
	 */
	@GetMapping("listAllMember")
	public String listAllMember(Model model) {
		List<MemberVO> list = memberSvc.getAll();
		model.addAttribute("memberListData", list);
		model.addAttribute("mainFragment", "admin/fragments/member/listAllMember");
		return "admin/index_admin";
	}

	/*
	 * This method will be called on addMember.html form submission, handling POST
	 * request It also validates the user input
	 */
	@PostMapping("/insert")
	public String insert(@Valid @ModelAttribute("memberVO") MemberVO memberVO, BindingResult result, ModelMap model) {

		if (result.hasErrors()) {
			model.addAttribute("mainFragment", "admin/fragments/member/addMember");
	        return "admin/index_admin";
		}

		try {
			MultipartFile file = memberVO.getUploadPic();
			if (file != null && !file.isEmpty()) {
				memberVO.setMemberPic(file.getBytes()); // 轉成 byte[] 存入 memberPic
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		memberSvc.addMember(memberVO); // 儲存進資料庫
		return "redirect:/member/listAllMember";	
	}

	/*
	 * This method will be called on listAllMember.html form submission, handling
	 * POST request
	 */
	@PostMapping("getOne_For_Update")
	public String getOne_For_Update(@RequestParam("memberId") String memberId, ModelMap model) {
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		/*************************** 2.開始查詢資料 *****************************************/
		// MemberService memberSvc = new MemberService();
		MemberVO memberVO = memberSvc.getOneMember(Integer.valueOf(memberId));

		/*************************** 3.查詢完成,準備轉交(Send the Success view) **************/
		model.addAttribute("memberVO", memberVO);
		model.addAttribute("mainFragment", "admin/fragments/member/update_member_input");
		return "admin/index_admin";
	}

	/*
	 * This method will be called on update_member_input.html form submission,
	 * handling POST request It also validates the user input
	 */
	@PostMapping("update")
	public String update(@Valid @ModelAttribute("memberVO") MemberVO memberVO,
            BindingResult result, ModelMap model) throws IOException {
		
		if (result.hasErrors()) {
		
			model.addAttribute("mainFragment", "admin/fragments/member/update_member_input");
			return "admin/index_admin";
		}
		MultipartFile file = memberVO.getUploadPic();
		if (file != null && !file.isEmpty()) {
	        memberVO.setMemberPic(file.getBytes()); // 將圖片存入 byte[] 欄位
	    }

		memberSvc.updateMember(memberVO);

		model.addAttribute("success", "- (修改成功)");
		return "redirect:/member/listAllMember";
	}

	/*
	 * This method will be called on listAllMember.html form submission, handling
	 * POST request
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
		return "redirect:/member/listAllMember";
	}

}
