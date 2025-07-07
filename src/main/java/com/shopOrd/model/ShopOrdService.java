package com.shopOrd.model;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coupon.entity.Coupon;
import com.coupon.entity.MemberCoupon;
import com.coupon.entity.MemberCouponId;
import com.coupon.repository.MemberCouponRepository;
import com.coupon.repository.CouponRepository;
import com.member.model.MemberVO;
import com.prod.model.ProdRepository;
import com.prod.model.ProdVO;
import com.prodCart.model.ProdCartRepository;
import com.prodCart.model.ProdCartVO;
import com.shopOrdDet.model.ShopOrdDetIdVO;
import com.shopOrdDet.model.ShopOrdDetRepository;
import com.shopOrdDet.model.ShopOrdDetVO;
import com.member.model.MemberService;
import com.notification.service.NotificationService;

@Service("shopOrdService")
public class ShopOrdService {
	
	@Autowired
	ShopOrdRepository repository;
	
	@Autowired
	ShopOrdDetRepository shopOrdDetRepository;
	
	@Autowired
    MemberCouponRepository memberCouponRepository;
	
	@Autowired
    CouponRepository couponRepository;
	
	@Autowired
    ProdCartRepository prodCartRepository;
	
	@Autowired
    private SessionFactory sessionFactory;

	@Autowired
	private MemberService memberService;
	
	@Autowired
	private NotificationService notificationService;

	public void updateShopOrd(ShopOrdVO shopOrdVO) {
		ShopOrdVO dbOrder = repository.findById(shopOrdVO.getProdOrdId()).orElse(null);
		if (dbOrder == null) return;

		// 取得原本 couponCode
		String oldCouponCode = dbOrder.getCoupon() != null ? dbOrder.getCoupon().getCouponCode() : null;
		String newCouponCode = (shopOrdVO.getCoupon() != null && shopOrdVO.getCoupon().getCouponCode() != null && !shopOrdVO.getCoupon().getCouponCode().isBlank())
			? shopOrdVO.getCoupon().getCouponCode() : null;
		Integer memberId = dbOrder.getMemberVO() != null ? dbOrder.getMemberVO().getMemberId() : null;

		// 1. 取得原本的實際付款金額
		int oldActualPayment = dbOrder.getActualPaymentAmount() != null ? dbOrder.getActualPaymentAmount() : 0;

		// 1. 若原本有 coupon，且換成新 coupon 或取消，則將原本的 MemberCoupon 設為 isUsed=false
		if (oldCouponCode != null && memberId != null && !oldCouponCode.equals(newCouponCode)) {
			MemberCouponId oldId = new MemberCouponId(oldCouponCode, memberId);
			memberCouponRepository.findById(oldId).ifPresent(mc -> {
				mc.setIsUsed(false);
				mc.setUsedTime(null);
				memberCouponRepository.save(mc);
			});
		}
		// 2. 若新 coupon 不為空，則將新 coupon 的 MemberCoupon 設為 isUsed=true
		if (newCouponCode != null && memberId != null && !newCouponCode.equals(oldCouponCode)) {
			MemberCouponId newId = new MemberCouponId(newCouponCode, memberId);
			memberCouponRepository.findById(newId).ifPresent(mc -> {
				mc.setIsUsed(true);
				mc.setUsedTime(java.time.LocalDateTime.now());
				memberCouponRepository.save(mc);
			});
		}

		// 折價券處理：如果 couponCode 為空，設為 null
		if (shopOrdVO.getCoupon() == null || 
			shopOrdVO.getCoupon().getCouponCode() == null || 
			shopOrdVO.getCoupon().getCouponCode().isBlank()) {
			dbOrder.setCoupon(null);
			dbOrder.setDiscountAmount(0);
		} else {
			// 以資料庫 coupon 的 discountValue 為主
			String couponCode = shopOrdVO.getCoupon().getCouponCode();
			Coupon coupon = couponRepository.findById(couponCode).orElse(null);
			dbOrder.setCoupon(coupon);
			int discount = (coupon != null) ? coupon.getDiscountValue() : 0;
			dbOrder.setDiscountAmount(discount);
		}

		// 取得原本狀態
		Integer oldOrdStat = dbOrder.getOrdStat();
		dbOrder.setOrdStat(shopOrdVO.getOrdStat());
		dbOrder.setPayMethod(shopOrdVO.getPayMethod());

		Integer prodAmount = dbOrder.getProdAmount() != null ? dbOrder.getProdAmount() : 0;
		Integer discountAmount = dbOrder.getDiscountAmount() != null ? dbOrder.getDiscountAmount() : 0;
		int newActualPayment = prodAmount - discountAmount;
		dbOrder.setActualPaymentAmount(newActualPayment);

		// 新增：狀態切換點數補扣/補回
		if (memberId != null && oldOrdStat != null && shopOrdVO.getOrdStat() != null) {
			// 1. 非取消→取消，扣回點數
			if (oldOrdStat != 2 && shopOrdVO.getOrdStat() == 2) {
				int minus = -oldActualPayment;
				memberService.updateConsumptionAndLevelAndPoints(memberId, minus);
			// 2. 取消→非取消，補回點數
			} else if (oldOrdStat == 2 && shopOrdVO.getOrdStat() != 2) {
				int plus = newActualPayment;
				memberService.updateConsumptionAndLevelAndPoints(memberId, plus);
			} else {
				// 其他情境，維持原本金額差異邏輯
				int priceChange = newActualPayment - oldActualPayment;
				if (priceChange != 0) {
					memberService.updateConsumptionAndLevelAndPoints(memberId, priceChange);
				}
			}
		}

		repository.save(dbOrder);
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

		// 4. 折價券處理和驗證
		int discount = 0;
		if (couponCode != null && !couponCode.trim().isEmpty()) {
			// 先檢查折價券是否存在
			Optional<Coupon> couponOpt = couponRepository.findById(couponCode);
			if (couponOpt.isPresent()) {
				Coupon coupon = couponOpt.get();
				
				// 檢查會員是否擁有這張折價券且未使用
				MemberCouponId memberCouponId = new MemberCouponId(couponCode, memberId);
				Optional<MemberCoupon> memberCouponOpt = memberCouponRepository.findById(memberCouponId);
				
				if (!memberCouponOpt.isPresent()) {
					throw new RuntimeException("您尚未領取此折價券");
				}
				
				MemberCoupon memberCoupon = memberCouponOpt.get();
				if (Boolean.TRUE.equals(memberCoupon.getIsUsed())) {
					throw new RuntimeException("此折價券已使用過");
				}
				
				// 驗證折價券
				validateCoupon(coupon, total, memberId);
				
				// 計算折扣金額（不能超過總金額）
				discount = Math.min(coupon.getDiscountValue(), total);
				order.setCoupon(coupon);
				
				// 標記折價券為已使用
				memberCoupon.setIsUsed(true);
				memberCoupon.setUsedTime(LocalDateTime.now());
				memberCouponRepository.save(memberCoupon);
			} else {
				throw new RuntimeException("折價券不存在");
			}
		}

		order.setProdAmount(total);
		order.setDiscountAmount(discount);
		order.setActualPaymentAmount(total - discount);

		// 5. 儲存主檔與明細
		ShopOrdVO savedOrder = repository.save(order);
		for (ShopOrdDetVO detail : detailList) {
			detail.getPpid().setProdOrdId(savedOrder.getProdOrdId());
			shopOrdDetRepository.save(detail);
		}

		// 6. 刪除購物車項目
		prodCartRepository.deleteAll(cartItems);
		// 新增：更新會員消費與點數
		if (memberId != null && (total - discount) > 0) {
			memberService.updateConsumptionAndLevelAndPoints(memberId, total - discount);
		}
		
		// 7. 建立訂單通知
		String notificationTitle = "訂單建立成功";
		String notificationContent = String.format("您的訂單 #%d 已成功建立，總金額：NT$ %d", 
			savedOrder.getProdOrdId(), total - discount);
		notificationService.createNotification(memberId, notificationTitle, notificationContent);
	}

	/**
	 * 驗證折價券是否可用
	 */
	private void validateCoupon(Coupon coupon, int orderTotal, Integer memberId) {
		// 檢查最低消費
		if (coupon.getMinPurchase() > 0 && orderTotal < coupon.getMinPurchase()) {
			throw new RuntimeException("訂單金額未達折價券最低消費 NT$ " + coupon.getMinPurchase());
		}
		
		// 檢查到期日
		LocalDate today = LocalDate.now();
		if (today.isAfter(coupon.getExpiryDate())) {
			throw new RuntimeException("折價券已過期");
		}
		
		// 注意：不檢查 claim_start_date 和 claim_end_date
		// 因為這些是領取期間，會員已經領取了折價券，只需要檢查是否過期即可
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

		// 7. 更新會員消費與點數
		Integer memberId = savedOrder.getMemberVO() != null ? savedOrder.getMemberVO().getMemberId() : null;
		if (memberId != null && actualAmount > 0) {
			memberService.updateConsumptionAndLevelAndPoints(memberId, actualAmount);
		}
		
		// 8. 建立訂單通知
		if (memberId != null) {
			String notificationTitle = "訂單建立成功";
			String notificationContent = String.format("您的訂單 #%d 已成功建立，總金額：NT$ %d", 
				savedOrder.getProdOrdId(), actualAmount);
			notificationService.createNotification(memberId, notificationTitle, notificationContent);
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
		ShopOrdVO savedOrder = repository.save(shopOrdVO);
		// 新增：更新會員消費與點數
		Integer memberId = shopOrdVO.getMemberVO() != null ? shopOrdVO.getMemberVO().getMemberId() : null;
		if (memberId != null && shopOrdVO.getActualPaymentAmount() != null && shopOrdVO.getActualPaymentAmount() > 0) {
			memberService.updateConsumptionAndLevelAndPoints(memberId, shopOrdVO.getActualPaymentAmount());
		}
		
		// 5. 建立訂單通知
		if (memberId != null && savedOrder.getProdOrdId() != null) {
			String notificationTitle = "訂單建立成功";
			String notificationContent = String.format("您的訂單 #%d 已成功建立，總金額：NT$ %d", 
				savedOrder.getProdOrdId(), savedOrder.getActualPaymentAmount());
			notificationService.createNotification(memberId, notificationTitle, notificationContent);
		}
	}

	/**
	 * 根據 LINE Pay 訂單資料建立商城訂單
	 * 用於 LINE Pay 付款成功後建立訂單
	 */
	@Transactional
	public void createShopOrderFromLinePay(Map<String, Object> linepayOrder) {
		Integer memberId = (Integer) linepayOrder.get("memberId");
		String couponCode = (String) linepayOrder.get("couponCode");
		Boolean paymentMethod = (Boolean) linepayOrder.get("paymentMethod");
		String orderId = (String) linepayOrder.get("orderId");
		Integer amount = (Integer) linepayOrder.get("amount");
		
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

		// 4. 折價券處理和驗證
		int discount = 0;
		if (couponCode != null && !couponCode.trim().isEmpty()) {
			// 先檢查折價券是否存在
			Optional<Coupon> couponOpt = couponRepository.findById(couponCode);
			if (couponOpt.isPresent()) {
				Coupon coupon = couponOpt.get();
				
				// 檢查會員是否擁有這張折價券且未使用
				MemberCouponId memberCouponId = new MemberCouponId(couponCode, memberId);
				Optional<MemberCoupon> memberCouponOpt = memberCouponRepository.findById(memberCouponId);
				
				if (!memberCouponOpt.isPresent()) {
					throw new RuntimeException("您尚未領取此折價券");
				}
				
				MemberCoupon memberCoupon = memberCouponOpt.get();
				if (Boolean.TRUE.equals(memberCoupon.getIsUsed())) {
					throw new RuntimeException("此折價券已使用過");
				}
				
				// 驗證折價券
				validateCoupon(coupon, total, memberId);
				
				// 計算折扣金額（不能超過總金額）
				discount = Math.min(coupon.getDiscountValue(), total);
				order.setCoupon(coupon);
				
				// 標記折價券為已使用
				memberCoupon.setIsUsed(true);
				memberCoupon.setUsedTime(LocalDateTime.now());
				memberCouponRepository.save(memberCoupon);
			} else {
				throw new RuntimeException("折價券不存在");
			}
		}

		order.setProdAmount(total);
		order.setDiscountAmount(discount);
		order.setActualPaymentAmount(total - discount);

		// 5. 儲存主檔與明細
		ShopOrdVO savedOrder = repository.save(order);
		for (ShopOrdDetVO detail : detailList) {
			detail.getPpid().setProdOrdId(savedOrder.getProdOrdId());
			shopOrdDetRepository.save(detail);
		}

		// 6. 刪除購物車項目
		prodCartRepository.deleteAll(cartItems);
		// 新增：更新會員消費與點數
		if (memberId != null && (total - discount) > 0) {
			memberService.updateConsumptionAndLevelAndPoints(memberId, total - discount);
		}
		
		// 7. 建立訂單通知
		String notificationTitle = "LINE Pay 付款成功";
		String notificationContent = String.format("您的訂單 #%d 已透過 LINE Pay 付款成功，總金額：NT$ %d", 
			savedOrder.getProdOrdId(), total - discount);
		notificationService.createNotification(memberId, notificationTitle, notificationContent);
	}

	public List<ShopOrdVO> getOrdersByMemberId(Integer memberId) {
		return repository.findAll().stream()
			.filter(o -> o.getMemberVO() != null && o.getMemberVO().getMemberId().equals(memberId))
			.toList();
	}

}
