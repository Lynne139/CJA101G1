package com.prodPhoto.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.prod.model.ProdService;
import com.prod.model.ProdVO;
import com.prodPhoto.model.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/prodPhoto")
public class ProdPhotoController {
	
	@Autowired
	ProdPhotoService prodPhotoSvc;

	@Autowired
	ProdService prodSvc;
	
	@GetMapping("/admin/prodPhoto/select_page")
	public String selectPage(Model model) {
		model.addAttribute("prodPhotoVO", new ProdPhotoVO());
		model.addAttribute("prodPhotoListData", prodPhotoSvc.getAll());
		model.addAttribute("prodListData", prodSvc.getAll());
		model.addAttribute("mainFragment", "admin/fragments/shop/prodPhoto/select_page :: content");
		return "admin/index_admin";
	}
	
	@GetMapping("listAllProdPhoto")
	public String listAllProdPhoto(Model model) {
	    List<ProdPhotoVO> list = prodPhotoSvc.getAll();
	    model.addAttribute("prodPhotoListData", list);
	    return "admin/fragments/shop/prodPhoto/listAllProdPhoto";
	}

	@ModelAttribute("prodPhotoListData")  // for select_page.html 第97 109行用 // for listAllEmp.html 第85行用
	protected List<ProdPhotoVO> referenceListData(Model model) {
		
    	List<ProdPhotoVO> list = prodPhotoSvc.getAll();
		return list;
	}
	
	
	@GetMapping("addProdPhoto")
	public String addProdPhoto(ModelMap model) {
		ProdPhotoVO prodPhotoVO = new ProdPhotoVO();
		model.addAttribute("prodPhotoVO", prodPhotoVO);
		model.addAttribute("prodListData", prodSvc.getAll());
		return "admin/fragments/shop/prodPhoto/addProdPhoto";
	}
	
	/*
	 * This method will be called on addEmp.html form submission, handling POST request It also validates the user input
	 */
	@PostMapping("insert")
	public String insert(@Valid ProdPhotoVO prodPhotoVO, BindingResult result, ModelMap model,
			@RequestParam("prodPhoto") MultipartFile[] parts) throws IOException {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		// 去除BindingResult中upFiles欄位的FieldError紀錄 --> 見第172行
		result = removeFieldError(prodPhotoVO, result, "prodPhoto");

		if (parts[0].isEmpty()) { // 使用者未選擇要上傳的圖片時
			model.addAttribute("errorMessage", "商品照片: 請上傳照片");
		} else {
			for (MultipartFile multipartFile : parts) {
				byte[] buf = multipartFile.getBytes();
				prodPhotoVO.setProdPhoto(buf);
			}
		}
		if (result.hasErrors() || parts[0].isEmpty()) {
			model.addAttribute("prodListData", prodSvc.getAll());
			return "admin/fragments/shop/prodPhoto/addProdPhoto";
		}
		/*************************** 2.開始新增資料 *****************************************/
		// EmpService empSvc = new EmpService();
		prodPhotoSvc.addProdPhoto(prodPhotoVO);
		/*************************** 3.新增完成,準備轉交(Send the Success view) **************/
		// 取得新增後的商品照片ID，然後redirect到select_page並顯示該商品照片
		Integer newProdPhotoId = prodPhotoVO.getProdPhotoId();
		return "redirect:/admin/prodPhoto/select_page?prodPhotoId=" + newProdPhotoId + "&success=true";
	}
	
	/*
	 * This method will be called on listAllEmp.html form submission, handling POST request
	 */
	@PostMapping("getOne_For_Update")
	public String getOne_For_Update(@RequestParam("prodPhotoId") String prodPhotoId, ModelMap model) {
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		/*************************** 2.開始查詢資料 *****************************************/
		// EmpService empSvc = new EmpService();
		ProdPhotoVO prodPhotoVO = prodPhotoSvc.getOneProdPhoto(Integer.valueOf(prodPhotoId));

		/*************************** 3.查詢完成,準備轉交(Send the Success view) **************/
		model.addAttribute("prodPhotoVO", prodPhotoVO);
		model.addAttribute("prodListData", prodSvc.getAll());
		return "admin/fragments/shop/prodPhoto/update_prodPhoto_input"; // 查詢完成後轉交update_emp_input.html
	}
	
	/*
	 * This method will be called on update_emp_input.html form submission, handling POST request It also validates the user input
	 */
	@PostMapping("update")
	public String update(@Valid ProdPhotoVO prodPhotoVO, BindingResult result, ModelMap model,
			@RequestParam("prodPhoto") MultipartFile[] parts) throws IOException {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		// 去除BindingResult中upFiles欄位的FieldError紀錄 --> 見第172行
		result = removeFieldError(prodPhotoVO, result, "prodPhoto");

		if (parts[0].isEmpty()) { // 使用者未選擇要上傳的新圖片時
			// EmpService empSvc = new EmpService();
			byte[] prodPhoto = prodPhotoSvc.getOneProdPhoto(prodPhotoVO.getProdPhotoId()).getProdPhoto();
			prodPhotoVO.setProdPhoto(prodPhoto);
		} else {
			for (MultipartFile multipartFile : parts) {
				byte[] prodPhoto = multipartFile.getBytes();
				prodPhotoVO.setProdPhoto(prodPhoto);
			}
		}
		if (result.hasErrors()) {
			model.addAttribute("prodListData", prodSvc.getAll());
			return "admin/fragments/shop/prodPhoto/update_prodPhoto_input";
		}
		/*************************** 2.開始修改資料 *****************************************/
		// EmpService empSvc = new EmpService();
		prodPhotoSvc.updateProdPhoto(prodPhotoVO);

		/*************************** 3.修改完成,準備轉交(Send the Success view) **************/
		// 取得修改後的商品照片ID，然後redirect到select_page並顯示該商品照片
		Integer prodPhotoId = prodPhotoVO.getProdPhotoId();
		return "redirect:/admin/prodPhoto/select_page?prodPhotoId=" + prodPhotoId + "&updateSuccess=true";
	}
	
	/*
	 * This method will be called on listAllEmp.html form submission, handling POST request
	 */
	@PostMapping("delete")
	public String delete(@RequestParam("prodPhotoId") String prodPhotoId, ModelMap model) {
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		/*************************** 2.開始刪除資料 *****************************************/
		// EmpService empSvc = new EmpService();
		prodPhotoSvc.deleteProdPhoto(Integer.valueOf(prodPhotoId));
		/*************************** 3.刪除完成,準備轉交(Send the Success view) **************/
		List<ProdPhotoVO> list = prodPhotoSvc.getAll();
		model.addAttribute("prodPhotoListData", list); // for listAllEmp.html 第85行用
		model.addAttribute("success", "- (刪除成功)");
		return "redirect:/admin/prodPhoto/select_page"; // 刪除完成後轉交listAllEmp.html
	}
	
	/*
	 * 第一種作法 Method used to populate the List Data in view. 如 : 
	 * <form:select path="deptno" id="deptno" items="${deptListData}" itemValue="deptno" itemLabel="dname" />
	 */
	@ModelAttribute("prodListData")
	protected List<ProdVO> referenceListData() {
		// DeptService deptSvc = new DeptService();
		List<ProdVO> list = prodSvc.getAll();
		return list;
	}
	
	// 去除BindingResult中某個欄位的FieldError紀錄
	public BindingResult removeFieldError(ProdPhotoVO prodPhotoVO, BindingResult result, String removedFieldname) {
		List<FieldError> errorsListToKeep = result.getFieldErrors().stream()
				.filter(fieldname -> !fieldname.getField().equals(removedFieldname))
				.collect(Collectors.toList());
		result = new BeanPropertyBindingResult(prodPhotoVO, "prodPhotoVO");
		for (FieldError fieldError : errorsListToKeep) {
			result.addError(fieldError);
		}
		return result;
	}
	
//	@PostMapping("listProdPhotos_ByCompositeQuery")
//	public String listAllEmp(HttpServletRequest req, Model model) {
//		Map<String, String[]> map = req.getParameterMap();
//		List<ProdPhotoVO> list = prodPhotoSvc.getAll(map);
//		model.addAttribute("prodPhotoListData", list); // for listAllEmp.html 第85行用
//		return "back-end/prodPhoto/listAllProdPhoto";
//	}
	
	@GetMapping("DBGifReader")
    public void getPhoto(@RequestParam("prodPhotoId") Integer prodPhotoId, HttpServletResponse response) throws IOException {
        ProdPhotoVO vo = prodPhotoSvc.getOneProdPhoto(prodPhotoId);
        byte[] photo = vo != null ? vo.getProdPhoto() : null;
        if (photo != null) {
            response.setContentType("image/jpeg"); // 根據實際圖片格式調整
            response.getOutputStream().write(photo);
        }
    }
	
	

}
