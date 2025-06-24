package com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.prod.model.ProdService;
import com.prod.model.ProdVO;
import com.prodCate.model.ProdCateService;
import com.prodCate.model.ProdCateVO;
import com.prodPhoto.model.ProdPhotoService;
import com.prodPhoto.model.ProdPhotoVO;

@Controller
@RequestMapping("/front-end")
public class ShopIndexController {
    
    @Autowired
    private ProdService prodService;
    
    @Autowired
    private ProdPhotoService prodPhotoService;
    
    @Autowired
    private ProdCateService prodCateService;
    
    /**
     * 商城首頁
     */
    @GetMapping("/shop")
    public String shopIndex(Model model) {
        // 獲取所有商品
        List<ProdVO> products = prodService.getAll();
        model.addAttribute("products", products);
        
        // 獲取所有商品照片
        List<ProdPhotoVO> productPhotos = prodPhotoService.getAll();
        model.addAttribute("productPhotos", productPhotos);
        
        // 獲取所有商品分類
        List<ProdCateVO> categories = prodCateService.getAll();
        model.addAttribute("categories", categories);
        
        return "front-end/shop/shopIndex";
    }
    
    /**
     * 商品照片圖片服務
     */
    @GetMapping("/shop/product-image/{productId}")
    public ResponseEntity<byte[]> getProductImage(@PathVariable Integer productId) {
        // 根據商品ID獲取第一張照片
        List<ProdPhotoVO> photos = prodPhotoService.getAll();
        for (ProdPhotoVO photo : photos) {
            if (photo.getProdVO().getProductId().equals(productId)) {
                return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(photo.getProdPhoto());
            }
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * 商品詳情頁面
     */
    @GetMapping("/shop/product")
    public String productDetail(Model model) {
        return "front-end/shop/product-detail";
    }
    
    /**
     * 購物車頁面
     */
    @GetMapping("/shop/cart")
    public String cart(Model model) {
        return "front-end/shop/cart";
    }
    
    /**
     * 結帳頁面
     */
    @GetMapping("/shop/checkout")
    public String checkout(Model model) {
        return "front-end/shop/checkout";
    }
} 