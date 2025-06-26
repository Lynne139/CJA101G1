package com.shopOrd.model;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coupon.entity.Coupon;
import com.coupon.repository.CouponRepository;
import com.member.model.MemberVO;
import com.prod.model.ProdRepository;
import com.prod.model.ProdVO;
import com.prodCart.model.ProdCartRepository;
import com.prodCart.model.ProdCartVO;
import com.shopOrdDet.model.ShopOrdDetIdVO;
import com.shopOrdDet.model.ShopOrdDetRepository;
import com.shopOrdDet.model.ShopOrdDetVO;
import java.time.LocalDateTime;

@Service("shopOrdService")
public class ShopOrdService {
	
	@Autowired
	ShopOrdRepository repository;
	
	@Autowired
	ShopOrdDetRepository shopOrdDetRepository;
	
	@Autowired
    CouponRepository couponRepository;
	
	@Autowired
    ProdCartRepository prodCartRepository;
	
	@Autowired
    private SessionFactory sessionFactory;

	public void updateShopOrd(ShopOrdVO shopOrdVO) {
		repository.save(shopOrdVO);
	}

	public ShopOrdVO getOneShopOrd(Integer prodOrdId) {
		Optional<ShopOrdVO> optional = repository.findById(prodOrdId);
		return optional.orElse(null);
	}

	public List<ShopOrdVO> getAll() {
		return repository.findAll();
	}

	/**
	 * 從購物車建立訂單（會員結帳功能）
	 * 這是主要的訂單建立方法，符合真實電商流程
	 */
	@Transactional
	public void createOrderFromCart(Integer memberId, String couponCode, Boolean paymentMethod) {
		// 1. 取得購物車中所有商品
		List<ProdCartVO> cartItems = prodCartRepository.findByMemberVO_MemberId(memberId);
		if (cartItems.isEmpty()) {
			throw new RuntimeException("購物車為空");
		}

		// 2. 建立訂單主檔
		ShopOrdVO order = new ShopOrdVO();
		MemberVO member = new MemberVO();
		member.setMemberId(memberId);
		order.setMemberVO(member);
		order.setProdOrdDate(LocalDateTime.now());
		order.setPayMethod(paymentMethod);
		order.setOrdStat(0); // 已付款

		// 折價券處理
		int discount = 0;
		if (couponCode != null && !couponCode.trim().isEmpty()) {
			Optional<Coupon> couponOpt = couponRepository.findById(couponCode);
			if (couponOpt.isPresent()) {
				Coupon coupon = couponOpt.get();
				discount = coupon.getDiscountValue();
				order.setCoupon(coupon);
			}
		}

		// 3. 建立明細，計算總金額
		List<ShopOrdDetVO> detailList = new ArrayList<>();
		int total = 0;
		for (ProdCartVO cartItem : cartItems) {
			ProdVO product = cartItem.getProdVO();

			ShopOrdDetVO detail = new ShopOrdDetVO();
			ShopOrdDetIdVO detailId = new ShopOrdDetIdVO(null, product.getProductId()); // 先留 null，儲存後再補
			detail.setPpid(detailId);
			detail.setProdVO(product);
			detail.setPurchasePrice(product.getProductPrice());
			detail.setProdQuantity(cartItem.getQuantity());
			detail.setShopOrdVO(order);

			total += product.getProductPrice() * cartItem.getQuantity();
			detailList.add(detail);
		}

		order.setProdAmount(total);
		order.setDiscountAmount(discount);
		order.setActualPaymentAmount(total - discount);

		// 4. 儲存主檔與明細
		ShopOrdVO savedOrder = repository.save(order);
		for (ShopOrdDetVO detail : detailList) {
			detail.getPpid().setProdOrdId(savedOrder.getProdOrdId());
			shopOrdDetRepository.save(detail);
		}

		// 5. 刪除購物車項目
		prodCartRepository.deleteAll(cartItems);
	}

	/**
	 * 手動新增訂單（管理員功能）
	 * 用於管理員手動建立訂單，包含主檔和明細
	 */
	@Transactional
	public void addShopOrd(ShopOrdVO shopOrdVO, List<ShopOrdDetVO> detailList) {
		// 1. 計算訂單總金額
		int totalAmount = detailList.stream()
				.mapToInt(detail -> {
					Integer price = detail.getPurchasePrice() != null ? detail.getPurchasePrice() : 0;
					return price * (detail.getProdQuantity() != null ? detail.getProdQuantity() : 0);
				})
				.sum();
		shopOrdVO.setProdAmount(totalAmount);

		// 2. 套用折價券（如果有）
		int discount = 0;
		if (shopOrdVO.getCoupon() != null && shopOrdVO.getCoupon().getCouponCode() != null) {
			var couponOpt = couponRepository.findById(shopOrdVO.getCoupon().getCouponCode());
			if (couponOpt.isPresent()) {
				discount = couponOpt.get().getDiscountValue();
				shopOrdVO.setCoupon(couponOpt.get()); // 確保關聯正確
			}
		}
		shopOrdVO.setDiscountAmount(discount);

		// 3. 計算實際付款金額
		int actualAmount = totalAmount - discount;
		shopOrdVO.setActualPaymentAmount(actualAmount);

		// 4. 設定訂單時間與狀態
		shopOrdVO.setProdOrdDate(LocalDateTime.now());
		if (shopOrdVO.getOrdStat() == null) {
			shopOrdVO.setOrdStat(0); // 0: 已付款（預設）
		}

		// 5. 儲存訂單主檔
		ShopOrdVO savedOrder = repository.save(shopOrdVO);

		// 6. 儲存訂單明細（設定訂單關聯）
		for (ShopOrdDetVO detail : detailList) {
			detail.setShopOrdVO(savedOrder); // 設定 FK 關聯
			shopOrdDetRepository.save(detail);
		}
	}

	/**
	 * 手動新增訂單主檔（管理員功能 - 簡化版）
	 * 用於管理員手動建立訂單主檔，明細可後續添加
	 */
	@Transactional
	public void addShopOrd(ShopOrdVO shopOrdVO) {
		// 1. 設定預設值
		if (shopOrdVO.getProdAmount() == null) {
			shopOrdVO.setProdAmount(0);
		}
		if (shopOrdVO.getDiscountAmount() == null) {
			shopOrdVO.setDiscountAmount(0);
		}
		if (shopOrdVO.getActualPaymentAmount() == null) {
			shopOrdVO.setActualPaymentAmount(shopOrdVO.getProdAmount() - shopOrdVO.getDiscountAmount());
		}

		// 2. 套用折價券（如果有）
		if (shopOrdVO.getCoupon() != null && shopOrdVO.getCoupon().getCouponCode() != null) {
			var couponOpt = couponRepository.findById(shopOrdVO.getCoupon().getCouponCode());
			if (couponOpt.isPresent()) {
				shopOrdVO.setCoupon(couponOpt.get()); // 確保關聯正確
			}
		}

		// 3. 設定訂單時間與狀態
		shopOrdVO.setProdOrdDate(LocalDateTime.now());
		if (shopOrdVO.getOrdStat() == null) {
			shopOrdVO.setOrdStat(0); // 0: 已付款（預設）
		}

		// 4. 儲存訂單主檔
		repository.save(shopOrdVO);
	}

}
