package com.prod.controller;

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


import com.prod.model.*;
import com.prodCate.model.*;

@Controller
@Validated
@RequestMapping("/prod")
public class ProdController {

	@Autowired
	ProdService prodSvc;

	@Autowired
	ProdCateService prodCateSvc;
	
	@GetMapping("select_page")
	public String selectPage() {
		return "admin/fragments/shop/prod/select_page";
	}
	
	@GetMapping("listAllProd")
	public String listAllProd(Model model) {
	    List<ProdVO> list = prodSvc.getAll();
	    model.addAttribute("prodListData", list);
	    return "admin/fragments/shop/prod/listAllProd";
	}

	@ModelAttribute("prodListData")  // for select_page.html 第97 109行用 // for listAllEmp.html 第85行用
	protected List<ProdVO> referenceListData(Model model) {
		
    	List<ProdVO> list = prodSvc.getAll();
		return list;
	}
    
//	@ModelAttribute("prodCateListData") // for select_page.html 第135行用
//	protected List<ProdCateVO> referenceListData_ProdCate(Model model) {
//		model.addAttribute("prodCateVO", new ProdCateVO()); // for select_page.html 第133行用
//		List<ProdCateVO> list = prodCateSvc.getAll();
//		return list;
//	}

	/*
	 * This method will serve as addEmp.html handler.
	 */
	@GetMapping("addProd")
	public String addProd(ModelMap model) {
		ProdVO prodVO = new ProdVO();
		prodVO.setProductStatus(true);
		model.addAttribute("prodVO", prodVO);
		return "admin/fragments/shop/prod/addProd";
	}

	/*
	 * This method will be called on addEmp.html form submission, handling POST request It also validates the user input
	 */
	@PostMapping("insert")
	public String insert(@Valid ProdVO prodVO, BindingResult result, ModelMap model) {

	    /*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
	    if (result.hasErrors()) {
	        return "admin/fragments/shop/prod/addProd"; // 修改成對應的商品分類新增頁面
	    }

	    /*************************** 2.開始新增資料 *****************************************/
	    prodSvc.addProd(prodVO); // 呼叫你的 Service 層方法（這裡記得改成你對應的方法）

	    /*************************** 3.新增完成,準備轉交(Send the Success view) **************/
	    List<ProdVO> list = prodSvc.getAll();
	    model.addAttribute("prodListData", list); // 頁面上顯示用的清單資料
	    model.addAttribute("success", "- (新增成功)");

	    return "redirect:/prod/listAllProd"; // 新增完成後導向商品分類列表
	}

	/*
	 * This method will be called on listAllEmp.html form submission, handling POST request
	 */
	@PostMapping("getOne_For_Update")
	public String getOne_For_Update(@RequestParam("productId") String productId, ModelMap model) {
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		/*************************** 2.開始查詢資料 *****************************************/
		// EmpService empSvc = new EmpService();
		ProdVO prodVO = prodSvc.getOneProd(Integer.valueOf(productId));

		/*************************** 3.查詢完成,準備轉交(Send the Success view) **************/
		model.addAttribute("prodVO", prodVO);
		return "admin/fragments/shop/prod/update_prod_input"; // 查詢完成後轉交update_emp_input.html
	}

	/*
	 * This method will be called on update_emp_input.html form submission, handling POST request It also validates the user input
	 */
	@PostMapping("update")
	public String update(@Valid ProdVO prodVO, BindingResult result, ModelMap model) throws IOException {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/

		if (result.hasErrors()) {
			return "admin/fragments/shop/prod/update_prod_input";
		}
		/*************************** 2.開始修改資料 *****************************************/
		// EmpService empSvc = new EmpService();
		prodSvc.updateProd(prodVO);

		/*************************** 3.修改完成,準備轉交(Send the Success view) **************/
		model.addAttribute("success", "- (修改成功)");
		prodVO = prodSvc.getOneProd(Integer.valueOf(prodVO.getProductId()));
		model.addAttribute("prodVO", prodVO);
		return "admin/fragments/shop/prod/listOneProd"; // 修改成功後轉交listOneEmp.html
	}



	/*
	 * 第一種作法 Method used to populate the List Data in view. 如 : 
	 * <form:select path="deptno" id="deptno" items="${deptListData}" itemValue="deptno" itemLabel="dname" />
	 */
	@ModelAttribute("prodCateListData")
	protected List<ProdCateVO> referenceListData() {
		// DeptService deptSvc = new DeptService();
		List<ProdCateVO> list = prodCateSvc.getAll();
		return list;
	}

	/*
	 * 【 第二種作法 】 Method used to populate the Map Data in view. 如 : 
	 * <form:select path="deptno" id="deptno" items="${depMapData}" />
	 */
//	@ModelAttribute("deptMapData") //
//	protected Map<Integer, String> referenceMapData() {
//		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
//		map.put(10, "財務部");
//		map.put(20, "研發部");
//		map.put(30, "業務部");
//		map.put(40, "生管部");
//		return map;
//	}

	// 去除BindingResult中某個欄位的FieldError紀錄
	public BindingResult removeFieldError(ProdVO prodVO, BindingResult result, String removedFieldname) {
		List<FieldError> errorsListToKeep = result.getFieldErrors().stream()
				.filter(fieldname -> !fieldname.getField().equals(removedFieldname))
				.collect(Collectors.toList());
		result = new BeanPropertyBindingResult(prodVO, "prodVO");
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
	@GetMapping("getOne_For_Display")
	public String getOne_For_Display(@RequestParam(value = "productId", required = false) String productId) {
	    if (productId == null || productId.trim().isEmpty()) {
	        return "redirect:/admin/prod/select_page?errorMessage=商品編號請勿空白";
	    }

	   
	        Integer id = Integer.valueOf(productId);
	        ProdVO prodVO = prodSvc.getOneProd(id);
	        if (prodVO == null) {
	            return "redirect:/admin/prod/select_page?errorMessage=查無資料";
	        } else {
	            return "redirect:/admin/prod/select_page?productId=" + id;
	        }
	    
	}

	@ExceptionHandler(value = { ConstraintViolationException.class })
	public String handleError(HttpServletRequest req, ConstraintViolationException e, Model model) {
		Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
		StringBuilder strBuilder = new StringBuilder();
		for (ConstraintViolation<?> violation : violations ) {
			strBuilder.append(violation.getMessage()).append(" ");
		}
		String message = strBuilder.toString();
		return "redirect:/admin/prod/select_page?errorMessage=" + java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8);
	}

	

}