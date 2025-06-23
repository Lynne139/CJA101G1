package com.memberLevelType.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.memberLevelType.model.MemberLevelType;
import com.memberLevelType.model.MemberLevelTypeService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/memberLevelType")
public class MemberLevelTypeController {
	
	@Autowired
    private MemberLevelTypeService memberLevelTypeService;

    // 顯示新增表單
    @GetMapping("/addMemberLevelType")
    public String addMemberLevelType(ModelMap model) {
    	MemberLevelType memberLevelType = new MemberLevelType();
        model.addAttribute("memberLevelType", memberLevelType);
        return "back-end/memberLevelType/addMemberLevelType";
    }

    // 處理新增
    @PostMapping("/insert")
    public String insert(@Valid MemberLevelType memberLevelType, BindingResult result, ModelMap model) {
        if (result.hasErrors()) {
            return "back-end/memberLevelType/addMemberLevelType";
        }
        memberLevelTypeService.addMemberLevelType(memberLevelType);
        return "redirect:/memberLevelType/listAllMemberLevelType";
    }

    // 顯示全部資料
    @GetMapping("/listAllMemberLevelType")
    public String listAllMemberLevelType(ModelMap model) {
        List<MemberLevelType> list = memberLevelTypeService.getAll();
        model.addAttribute("memberLevelTypeList", list);
        return "back-end/memberLevelType/listAllMemberLevelType";
    }

    // 顯示修改表單
    @PostMapping("/getOneForUpdate")
    public String getOneForUpdate(@RequestParam("memberLevel") String memberLevel, ModelMap model) {
        MemberLevelType memberLevelType = memberLevelTypeService.getOneMemberLevelType(memberLevel);
        model.addAttribute("memberLevelType", memberLevelType);
        return "back-end/memberLevelType/listOneMemberLevelType";
    }

    // 處理更新
    @PostMapping("/update")
    public String update(@Valid MemberLevelType memberLevelType, BindingResult result, ModelMap model) {
        if (result.hasErrors()) {
            return "back-end/memberLevelType/update_memberLevelType_input";
        }
        memberLevelTypeService.updateMemberLevelType(memberLevelType);
        return "redirect:/memberLevelType/listOneMemberLevelType";
    }

    // 處理刪除
    @PostMapping("/delete")
    public String delete(@RequestParam("memberLevel") String memberLevel, ModelMap model) {
        memberLevelTypeService.deleteMemberLevelType(memberLevel);
        return "redirect:/memberLevelType/listAllMemberLevelType";
    }
}
