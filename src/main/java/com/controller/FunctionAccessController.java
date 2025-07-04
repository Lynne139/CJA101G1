//package com.controller;
// 
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import jakarta.servlet.http.HttpSession;
//
//@Controller
//public class FunctionAccessController {
//
//	
//	@GetMapping("/front-end/room")
//	public String roomIndex(HttpSession session, Model model, String keyword, String typeId, RedirectAttributes redirectAttributes) {
//		 if (session.getAttribute("loggedInMember") == null) {
//            redirectAttributes.addFlashAttribute("forceLogin", true);
//            return "redirect:/home";
//        }
//        return "front-end/room";
//    }
//
//	
//
//
//
//
//	
//	
//	
//}
