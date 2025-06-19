package com.resto.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.resto.model.RestoService;
import com.resto.model.RestoVO;

import jakarta.persistence.OptimisticLockException;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class RestoController {
	
	@Autowired
	RestoService restoService;
	
	// ===== 刪除 =====
	@GetMapping("/resto_info/delete")
	public String deleteResto(@RequestParam("restoId") Integer id, RedirectAttributes redirectAttributes) {
	    restoService.softDelete(id); // isDeleted = true
	    redirectAttributes.addFlashAttribute("message", "刪除成功！");
	    return "redirect:/admin/resto_info";
	}

	// ===== 細項檢視 =====
	@GetMapping("/resto_info/view")
	public String viewResto(@RequestParam("restoId") Integer id, Model model) {

	    RestoVO resto = restoService.getById(id);
	    model.addAttribute("resto", resto);

	    return "admin/fragments/resto/modals/resto_view :: viewModalContent";
	}
	
	//取資料庫圖片
	@GetMapping("/resto_info/img/{id}")
	public ResponseEntity<byte[]> getImage(@PathVariable Integer id) {
	    RestoVO resto = restoService.getById(id);
	    byte[] imageBytes = resto.getRestoImg();
	    


	    if (imageBytes == null || imageBytes.length == 0) {
	    	// 回傳no_img.svg bytes
	    	try (InputStream is = new ClassPathResource("static/images/admin/no_img.svg").getInputStream()) {
	            byte[] defaultImg = is.readAllBytes();
	            return ResponseEntity
	                    .ok()
	                    .header(HttpHeaders.CONTENT_TYPE, "image/svg+xml")
	                    .body(defaultImg);
	    	} catch (IOException e) {
	            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	        }
	    }

	    // 自動判斷圖片格式
	    String contentType = detectImageMimeType(imageBytes);
	    if (contentType == null) {
	        contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // fallback
	    }

	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.parseMediaType(contentType));
	    return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
	}

	private String detectImageMimeType(byte[] imageBytes) {
	    try (InputStream is = new ByteArrayInputStream(imageBytes)) {
	        ImageInputStream iis = ImageIO.createImageInputStream(is);
	        Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
	        if (readers.hasNext()) {
	            ImageReader reader = readers.next();
	            String formatName = reader.getFormatName().toLowerCase();
	            switch (formatName) {
	                case "jpeg": return "image/jpeg";
	                case "png":  return "image/png";
	                case "gif":  return "image/gif";
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	
	
	// ===== 新增 =====
	//取得add modal
	@GetMapping("/resto_info/add")
	public String showAddModal(Model model) {
	    model.addAttribute("resto", new RestoVO()); // 傳入一個空的RestoVO
	    return "admin/fragments/resto/modals/resto_add :: addModalContent";
	}

	//寫入新增內容到資料庫
	@PostMapping("/resto_info/insert")
	public String insertResto(
	        @Valid @ModelAttribute("resto") RestoVO resto,
	        BindingResult result,
	        @RequestParam(value = "uploadImg", required = false) MultipartFile imageFile,
	        @RequestParam(value = "clearImgFlag", required = false) String clearImgFlag,
	        RedirectAttributes redirectAttributes,
	        Model model
	) {
		
//	    System.out.println("表單資料：" + resto);
		
		
		// 預設值補齊
	    resto.setIsDeleted(false);
	    if (resto.getIsEnabled() == null) resto.setIsEnabled(false);
		
	 // 圖片錯誤 flag（初始 false）
	    boolean hasImageError = false;

	    // 檢查圖片格式與大小
	    if (imageFile != null && !imageFile.isEmpty()) {
	        String contentType = imageFile.getContentType();
	        long maxSize = 16 * 1024 * 1024; // 16MB

	        if (!isValidImageType(contentType)) {
	            model.addAttribute("imageError", "只接受 PNG / JPEG / GIF 格式圖片");
	            hasImageError = true;
	        } else if (imageFile.getSize() > maxSize) {
	            model.addAttribute("imageError", "圖片大小不得超過 16MB");
	            hasImageError = true;
	        } else {
	            try {
	                resto.setRestoImg(imageFile.getBytes());
	            } catch (IOException e) {
	                model.addAttribute("imageError", "圖片處理失敗");
	                hasImageError = true;
	            }
	        }
	    }

	    // 若欄位驗證有錯，或圖片錯誤，回填 modal
	    if (result.hasErrors() || hasImageError) {
	        model.addAttribute("resto", resto);
	        return "admin/fragments/resto/modals/resto_add :: addModalContent";
	    }
	    

//	    System.out.println("準備儲存資料");

	    // 儲存
	    try {
		    restoService.saveWithImage(resto, imageFile , clearImgFlag);
	    } catch (DataIntegrityViolationException e) {
	        model.addAttribute("errorMsg", "餐廳名稱已存在，請重新輸入！");
	        return "admin/fragments/resto/modals/resto_add :: addModalContent";
	    } 
	    
//	    System.out.println("儲存完成");

	    redirectAttributes.addFlashAttribute("message", "新增成功！");
	    return "redirect:/admin/resto_info";
	}

	// 支援格式判斷
	private boolean isValidImageType(String contentType) {
	    return contentType != null && (
	        contentType.equalsIgnoreCase("image/png") ||
	        contentType.equalsIgnoreCase("image/jpeg") ||
	        contentType.equalsIgnoreCase("image/gif")
	    );
	}

	
	// ===== 修改 =====
	//取得edit modal
	@GetMapping("/resto_info/edit")
	public String showEditModal(@RequestParam("restoId") Integer id, Model model) {
	    RestoVO resto = restoService.getById(id);

	    model.addAttribute("resto", resto);
	    return "admin/fragments/resto/modals/resto_edit :: editModalContent";
	}
	
	//寫入新增內容到資料庫
	@PostMapping("/resto_info/update")
	public String updateResto(
	        @Valid @ModelAttribute("resto") RestoVO resto,
	        BindingResult result,
	        @RequestParam(value = "uploadImg", required = false) MultipartFile imageFile,
	        @RequestParam(value = "clearImgFlag", required = false) String clearImgFlag,
	        RedirectAttributes redirectAttributes,
	        Model model
	) {
		
				
	    boolean hasImageError = false;

	    // 處理圖片格式與大小
	    if (imageFile != null && !imageFile.isEmpty()) {
	        String contentType = imageFile.getContentType();
	        long maxSize = 16 * 1024 * 1024;

	        if (!isValidImageType(contentType)) {
	            model.addAttribute("imageError", "只接受 PNG / JPEG / GIF 格式圖片");
	            hasImageError = true;
	        } else if (imageFile.getSize() > maxSize) {
	            model.addAttribute("imageError", "圖片大小不得超過 16MB");
	            hasImageError = true;
	        } else {
	            try {
	                resto.setRestoImg(imageFile.getBytes());
	            } catch (IOException e) {
	                model.addAttribute("imageError", "圖片處理失敗");
	                hasImageError = true;
	            }
	        }
	    }

	    // 驗證錯誤回填modal
	    if (result.hasErrors() || hasImageError) {
	        model.addAttribute("resto", resto);
	        return "admin/fragments/resto/modals/resto_edit :: editModalContent";
	    }
	    
	    
	    try {
		    restoService.saveWithImage(resto, imageFile, clearImgFlag);
	    } catch (DataIntegrityViolationException e) {
	        model.addAttribute("errorMsg", "餐廳名稱已存在，請重新輸入！");
	        return "admin/fragments/resto/modals/resto_edit :: editModalContent";
	    } catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
	        model.addAttribute("errorMsg", "此餐廳資料已被他人修改，請重新整理頁面");
	        model.addAttribute("resto", resto); // 回填原輸入
	        return "admin/fragments/resto/modals/resto_edit :: editModalContent";
	    }
	    redirectAttributes.addFlashAttribute("message", "編輯成功！");
	    return "redirect:/admin/resto_info";
	}

	



	
	
	
	

	
}
