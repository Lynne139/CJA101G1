package com.prodCate.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.prod.model.ProdVO;
import com.prodCate.model.*;

@Controller
@Validated
@RequestMapping("/prodCate")
public class ProdCateController {

	@Autowired
	ProdCateService prodCateSvc;
	
	@GetMapping("listAllProdCate")
	public String listAllProdCate(Model model) {
	    List<ProdCateVO> list = prodCateSvc.getAll();
	    model.addAttribute("prodCateListData", list);
	    return "admin/fragments/shop/prodCate/listAllProdCate";
	}

	@ModelAttribute("prodCateListData")  // for select_page.html 第97 109行用 // for listAllEmp.html 第85行用
	protected List<ProdCateVO> referenceListData(Model model) {
		
    	List<ProdCateVO> list = prodCateSvc.getAll();
		return list;
	}


	/*
	 * This method will serve as addEmp.html handler.
	 */
	@GetMapping("addProdCate")
	public String addProdCate(ModelMap model) {
		ProdCateVO prodCateVO = new ProdCateVO();
		model.addAttribute("prodCateVO", prodCateVO);
		return "admin/fragments/shop/prodCate/addProdCate";
	}

	/*
	 * This method will be called on addEmp.html form submission, handling POST request It also validates the user input
	 */
	@PostMapping("insert")
	public String insert(@Valid ProdCateVO prodCateVO, BindingResult result, ModelMap model) throws IOException {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		// 去除BindingResult中upFiles欄位的FieldError紀錄 --> 見第172行
		if (result.hasErrors()) {
	        return "admin/fragments/shop/prodCate/addProdCate"; // 修改成對應的商品分類新增頁面
	    }
		/*************************** 2.開始新增資料 *****************************************/
		// EmpService empSvc = new EmpService();
		prodCateSvc.addProdCate(prodCateVO);
		/*************************** 3.新增完成,準備轉交(Send the Success view) **************/
		// 取得新增後的商品分類ID，然後redirect到select_page並顯示該商品分類
		Integer newProdCateId = prodCateVO.getProdCateId();
		return "redirect:/admin/prodCate/select_page?prodCateId=" + newProdCateId + "&success=true";
	}

	/*
	 * This method will be called on listAllEmp.html form submission, handling POST request
	 */
	@PostMapping("getOne_For_Update")
	public String getOne_For_Update(@RequestParam("prodCateId") String prodCateId, ModelMap model) {
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		/*************************** 2.開始查詢資料 *****************************************/
		// EmpService empSvc = new EmpService();
		ProdCateVO prodCateVO = prodCateSvc.getOneProdCate(Integer.valueOf(prodCateId));

		/*************************** 3.查詢完成,準備轉交(Send the Success view) **************/
		model.addAttribute("prodCateVO", prodCateVO);
		return "admin/fragments/shop/prodCate/update_prodCate_input"; // 查詢完成後轉交update_emp_input.html
	}

	/*
	 * This method will be called on update_emp_input.html form submission, handling POST request It also validates the user input
	 */
	@PostMapping("update")
	public String update(@Valid ProdCateVO prodCateVO, BindingResult result, ModelMap model) throws IOException {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		// 去除BindingResult中upFiles欄位的FieldError紀錄 --> 見第172行

		if (result.hasErrors()) {
	        return "admin/fragments/shop/prodCate/update_prodCate_input"; // 修改成對應的商品分類修改頁面
	    }
		/*************************** 2.開始修改資料 *****************************************/
		// EmpService empSvc = new EmpService();
		prodCateSvc.updateProdCate(prodCateVO);

		/*************************** 3.修改完成,準備轉交(Send the Success view) **************/
		// 取得修改後的商品分類ID，然後redirect到select_page並顯示該商品分類
		Integer prodCateId = prodCateVO.getProdCateId();
		return "redirect:/admin/prodCate/select_page?prodCateId=" + prodCateId + "&updateSuccess=true";
	}

	// 去除BindingResult中某個欄位的FieldError紀錄
	public BindingResult removeFieldError(ProdCateVO prodCateVO, BindingResult result, String removedFieldname) {
		List<FieldError> errorsListToKeep = result.getFieldErrors().stream()
				.filter(fieldname -> !fieldname.getField().equals(removedFieldname))
				.collect(Collectors.toList());
		result = new BeanPropertyBindingResult(prodCateVO, "prodCateVO");
		for (FieldError fieldError : errorsListToKeep) {
			result.addError(fieldError);
		}
		return result;
	}
	
	/*
	 * This method will be called on select_page.html form submission, handling POST request
	 */
//	@PostMapping("listEmps_ByCompositeQuery")
//	public String listAllEmp(HttpServletRequest req, Model model) {
//		Map<String, String[]> map = req.getParameterMap();
//		List<EmpVO> list = empSvc.getAll(map);
//		model.addAttribute("empListData", list); // for listAllEmp.html 第85行用
//		return "back-end/emp/listAllEmp";
//	}

	/*
	 * This method will be called on select_page.html form submission, handling POST
	 * request It also validates the user input
	 */
	@ExceptionHandler(value = { ConstraintViolationException.class })
	public String handleError(HttpServletRequest req, ConstraintViolationException e, Model model) {
		Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
		StringBuilder strBuilder = new StringBuilder();
		for (ConstraintViolation<?> violation : violations) {
			strBuilder.append(violation.getMessage()).append(" ");
		}
		String message = strBuilder.toString();
		return "redirect:/admin/prodCate/select_page?errorMessage=" + java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8);
	}

}