package com.memberLevelType.controller;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.memberLevelType.model.MemberLevelType;
import com.memberLevelType.model.MemberLevelTypeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/memberLevelType")
public class MemberLevelTypeController {
	 
	@Autowired
    private MemberLevelTypeService memberLevelTypeSvc;

	// 顯示新增表單
    @GetMapping("/addMemberLevelType")
    public String addMemberLevelType(Model model, HttpServletRequest request) {
    	model.addAttribute("memberLevelType", new MemberLevelType());
    	model.addAttribute("memberLevelTypeList", memberLevelTypeSvc.getAll());
	    model.addAttribute("currentURI", request.getRequestURI());
	    model.addAttribute("mainFragment", "admin/fragments/memberLevelType/addMemberLevelType");
	    return "admin/index_admin";
    }

    // 處理新增
    @PostMapping("/insertL")
    public String insertMemberLevelType(@ModelAttribute("memberLevelType") @Valid MemberLevelType memberLevelType,
    					BindingResult result,
    					Model model) {
        if (result.hasErrors()) {
        	model.addAttribute("memberLevelType", memberLevelType);
            return "back-end/memberLevelType/addMemberLevelType";
        }
        memberLevelTypeSvc.addMemberLevelType(memberLevelType);
        return "redirect:/admin/listAllMemberLevelType";
    }

    // 顯示全部資料
    @GetMapping("/listAllMemberLevelType")
    public String listAllMemberLevelType(Model model, HttpServletRequest request) {
    	model.addAttribute("memberLevelTypeList", memberLevelTypeSvc.getAll());
	    model.addAttribute("currentURI", request.getRequestURI());
	    model.addAttribute("mainFragment", "admin/fragments/memberLevelType/listAllMemberLevelType");
	    return "admin/index_admin";
    }

    // 修改資料畫面
    @GetMapping("/updateMemberLevelType/{memberLevel}")
    public String getUpdateMemberLevelTypePage(@PathVariable("memberLevel") String memberLevel,
                                               Model model,
                                               HttpServletRequest request) {
        MemberLevelType memberLevelTypeList = memberLevelTypeSvc.getOneMemberLevelType(memberLevel);
        model.addAttribute("memberLevelType", memberLevelTypeList);
        model.addAttribute("currentURI", request.getRequestURI());
        model.addAttribute("mainFragment", "admin/fragments/memberLevelType/update_memberLevelType_input");
        return "admin/index_admin";
    }

    
    // 更新資料
    @PostMapping("/updateL")
    public String updateMmemberLevel(@Valid @ModelAttribute("memberLevelType") MemberLevelType memberLevelType, 
    					BindingResult result,
    					Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
        	model.addAttribute("memberLevelType", memberLevelType);
    	    model.addAttribute("currentURI", request.getRequestURI());
    	    model.addAttribute("mainFragment", "admin/fragments/memberLevelType/update_memberLevelType_input");
    	    return "admin/index_admin";
        }
        memberLevelTypeSvc.updateMemberLevelType(memberLevelType);
        return "redirect:/admin/listAllMemberLevelType";
    }
    
}
