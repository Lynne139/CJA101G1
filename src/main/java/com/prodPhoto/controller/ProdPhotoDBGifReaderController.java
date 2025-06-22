package com.prodPhoto.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.prodPhoto.model.ProdPhotoService;
import com.prodPhoto.model.ProdPhotoVO;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ProdPhotoDBGifReaderController {
	
//	 @Autowired
//	    private ProdPhotoService prodPhotoService;
//
//	    @GetMapping("/prodPhoto/DBGifReader")
//	    public void getPhoto(@RequestParam("prodPhotoId") Integer prodPhotoId, HttpServletResponse response) throws IOException {
//	        ProdPhotoVO vo = prodPhotoService.getOneProdPhoto(prodPhotoId);
//	        byte[] photo = vo != null ? vo.getProdPhoto() : null;
//	        if (photo != null) {
//	            response.setContentType("image/jpeg"); // 根據實際圖片格式調整
//	            response.getOutputStream().write(photo);
//	        }
//	    }

}
