package com.room.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.room.model.RoomService;
import com.room.model.RoomVO;
import com.roomtype.model.RoomTypeService;
import com.roomtype.model.RoomTypeVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

@Controller
@Validated
@RequestMapping("/admin")
public class RoomController {

	@Autowired
	RoomService roomSvc;

	@Autowired
	RoomTypeService roomTypeSvc;
	
	// 顯示新增 modal
    @GetMapping("/add")
    public String showAddModal(Model model) {
        RoomVO roomVO = new RoomVO();
        // 預設房客名稱驗證格式
        roomVO.setRoomGuestName("");
        model.addAttribute("roomVO", roomVO);
        model.addAttribute("roomTypeVOList", roomTypeSvc.getAll());
        return "admin/fragments/room/modals/room_add :: addModalContent";
    }
	
	
 // 新增處理
    @PostMapping("/insert")
    public String insertRoom(@Valid @ModelAttribute("roomVO") RoomVO roomVO,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roomTypeVOList", roomTypeSvc.getAll());
            return "admin/fragments/room/modals/room_add :: addModalContent";
        }
        roomSvc.addRoom(roomVO);
        redirectAttributes.addFlashAttribute("message", "新增成功！");
        return "redirect:/admin/room_info";
    }	
	
	
 // 顯示修改 modal
    @GetMapping("/edit")
    public String showEditModal(@RequestParam("roomId") Integer roomId, Model model) {
        RoomVO roomVO = roomSvc.getOneRoom(roomId);
        model.addAttribute("roomVO", roomVO);
        model.addAttribute("roomTypeVOList", roomTypeSvc.getAll());
        return "admin/fragments/room/modals/room_edit :: editModalContent";
    }

    // 修改處理
    @PostMapping("/update")
    public String updateRoom(@Valid @ModelAttribute("roomVO") RoomVO roomVO,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roomTypeVOList", roomTypeSvc.getAll());
            return "admin/fragments/room/modals/room_edit :: editModalContent";
        }
        roomSvc.updateRoom(roomVO);
        redirectAttributes.addFlashAttribute("message", "修改成功！");
        return "redirect:/admin/room_info";
    }

	
 // 查看單筆資料 modal
    @GetMapping("/view")
    public String viewRoom(@RequestParam("roomId") Integer roomId, Model model) {
        RoomVO roomVO = roomSvc.getOneRoom(roomId);
        model.addAttribute("roomVO", roomVO);
        return "admin/fragments/room/modals/room_view :: viewModalContent";
    }

    // 刪除
//    @GetMapping("/delete")
//    public String deleteRoom(@RequestParam("roomId") Integer roomId, RedirectAttributes redirectAttributes) {
//        roomSvc.deleteRoom(roomId);
//        redirectAttributes.addFlashAttribute("message", "刪除成功！");
//        return "redirect:/admin/room_info";
//    }

    // 房型列表供下拉選單用
    @ModelAttribute("roomTypeVOList")
    public List<RoomTypeVO> getRoomTypes() {
        return roomTypeSvc.getAll();
    }
	
}
