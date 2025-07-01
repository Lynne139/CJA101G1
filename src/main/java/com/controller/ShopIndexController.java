package com.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.prod.model.ProdService;
import com.prod.model.ProdVO;
import com.prodCate.model.ProdCateService;
import com.prodCate.model.ProdCateVO;
import com.prodPhoto.model.ProdPhotoService;
import com.prodPhoto.model.ProdPhotoVO;
import jakarta.servlet.http.HttpSession;
import com.member.model.MemberService;
import com.member.model.MemberVO;

@Controller
@RequestMapping("/front-end")
public class ShopIndexController {
    
    @Autowired
    private ProdService prodService;
    
    @Autowired
    private ProdPhotoService prodPhotoService;
    
    @Autowired
    private ProdCateService prodCateService;
    
    @Autowired
    private MemberService memberService;
    
    /**
     * 商城首頁（原本版本，已註解）
     */
    /*
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
    */

    /**
     * 商城首頁（本地測試用，含假登入）
     */
    @GetMapping("/shop")
    public String shopIndex(HttpSession session, Model model, 
                           @RequestParam(required = false) String payment,
                           @RequestParam(required = false) String orderId) {
        // 假登入：本地測試用，直接查詢會員編號1號的資料
        if (session.getAttribute("memberVO") == null) {
            MemberVO member = memberService.getOneMember(1);
            if (member != null) {
                session.setAttribute("memberVO", member);
            }
        }
        
        // 處理付款狀態參數
        if (payment != null) {
            if ("success".equals(payment)) {
                model.addAttribute("paymentMessage", "付款成功！");
                model.addAttribute("paymentStatus", "success");
            } else if ("failed".equals(payment)) {
                model.addAttribute("paymentMessage", "付款失敗，請稍後再試。");
                model.addAttribute("paymentStatus", "failed");
            }
        }
        
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
     * 獲取特定商品的所有照片資訊（JSON格式）
     */
    @GetMapping("/shop/product-photos/{productId}")
    @ResponseBody
    public List<Map<String, Object>> getProductPhotos(@PathVariable Integer productId) {
        List<ProdPhotoVO> photos = prodPhotoService.getPhotosByProductId(productId);
        List<Map<String, Object>> photoList = new ArrayList<>();
        
        for (ProdPhotoVO photo : photos) {
            Map<String, Object> photoInfo = new HashMap<>();
            photoInfo.put("photoId", photo.getProdPhotoId());
            photoInfo.put("imageUrl", "/front-end/shop/product-image/" + productId + "/" + photo.getProdPhotoId());
            photoList.add(photoInfo);
        }
        
        return photoList;
    }
    
    /**
     * 商品照片圖片服務（指定照片ID）
     */
    @GetMapping("/shop/product-image/{productId}/{photoId}")
    public ResponseEntity<byte[]> getProductImageById(@PathVariable Integer productId, @PathVariable Integer photoId) {
        ProdPhotoVO photo = prodPhotoService.getOneProdPhoto(photoId);
        if (photo != null && photo.getProdVO().getProductId().equals(productId)) {
            return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(photo.getProdPhoto());
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