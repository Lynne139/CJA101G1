package com.resto.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import com.member.model.MemberRepository;
import com.member.model.MemberService;
import com.member.model.MemberVO;
import com.notification.service.NotificationService;
import com.resto.dto.PreBookingDTO;
import com.resto.entity.PeriodVO;
import com.resto.entity.RestoOrderVO;
import com.resto.entity.RestoVO;
import com.resto.entity.TimeslotVO;
import com.resto.model.PeriodService;
import com.resto.model.ReservationService;
import com.resto.model.RestoOrderRepository;
import com.resto.model.RestoOrderService;
import com.resto.model.RestoService;
import com.resto.model.TimeslotService;
import com.resto.utils.RestoEmailService;
import com.resto.utils.RestoOrderSource;
import com.resto.utils.exceptions.OverbookingException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class FrontRestoController {
	
	@Autowired
	RestoService restoSvc;
	@Autowired
	TimeslotService timeslotSvc;
	@Autowired
	RestoOrderService restoOrderSvc;
	@Autowired
	ReservationService reservationSvc;
	@Autowired
	PeriodService periodSvc;
	@Autowired
	MemberService memberSvc;
	@Autowired
	MemberRepository memberRepo;	
	@Autowired
	RestoOrderRepository restoOrderRepository;
	
	@Autowired
	private NotificationService notificationSvc;
	
	@Autowired
	RestoEmailService restoEmailSvc;
	
	
	//  ====餐廳列表====	
	@GetMapping("/restaurants")
	public String showRestoList(HttpServletRequest request, 
								HttpServletResponse response,
								Model model) {
		List<RestoVO> restoList = restoSvc.getAllEnabled();
	    model.addAttribute("restoList", restoList);
	    
	    return "front-end/resto/restoList";
	}
	
	
	// ===== 顯示圖片 =====
		@GetMapping("/restaurants/img/{id}")
		public ResponseEntity<byte[]> getImage(@PathVariable Integer id) {
		    RestoVO resto = restoSvc.getById(id);
		    byte[] imageBytes = resto.getRestoImg();

		    if (imageBytes == null || imageBytes.length == 0) {
		    	// 回傳no_img.svg bytes
		        try (InputStream is = getClass().getResourceAsStream("/static/images/admin/no_img.svg")) {
		            if (is != null) {
		                byte[] defaultImg = is.readAllBytes();
		                return ResponseEntity
		                    .ok()
		                    .header(HttpHeaders.CONTENT_TYPE, "image/svg+xml")
		                    .body(defaultImg);
		            }
		        } catch (IOException e) {
		            // optional: log
		        }
		        return ResponseEntity
		            .status(HttpStatus.NO_CONTENT)
		            .build();
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
	

	
    //  ====顯示餐廳分頁====	
		@GetMapping("/restaurants/{id}")
		public String intro(@PathVariable Integer id, 
									Model model,
									@RequestParam(required = false) LocalDate date) {


			prepareIntroPage(model, id, 1, LocalDate.now());
			if (!model.containsAttribute("preBooking")) {
	            model.addAttribute("preBooking", new PreBookingDTO(id, null, null, null));
	        }

		    return "front-end/resto/restoIntro";
		}
		
		
	    //  ====給前端JS timeslot 可訂狀態去enable 按鈕(非超時與非滿額)====	
		@GetMapping("/restaurants/{restoId}/available")
		@ResponseBody
		public Map<Integer, Boolean> checkAvailable(
		        @PathVariable Integer restoId,
		        @RequestParam Integer seats,
		        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
		) {

		    LocalTime now = LocalTime.now();
			
		    // 回傳 key = timeslotId, value = true(鎖) / false(可訂)
		    return timeslotSvc.findByResto(restoId).stream()
		            .collect(Collectors.toMap(
		                TimeslotVO::getTimeslotId,
		                ts -> {
		                    // 滿位？
		                    boolean full = reservationSvc.getRemaining(restoId, ts.getTimeslotId(), date) < seats;
		                    // 今天且已過開始時間？
		                    boolean passed = date.equals(LocalDate.now()) && 
		                    				 ts.getLocalTime().isBefore(now);
		                    return full || passed;
		                }
		            ));
		}

	
	//  ====intro 按「填表預訂」(POST)====
	@PostMapping("/restaurants/booking-pre")
	public String handlePreBooking(
	        @Validated @ModelAttribute("preBooking") PreBookingDTO dto,
	        BindingResult br,
	        RedirectAttributes ra
	) {
		
	    if (br.hasErrors()) {
	    	keep(dto, br, ra);
//	    	ra.addFlashAttribute("preBooking", dto);
//            ra.addFlashAttribute("org.springframework.validation.BindingResult.preBooking", br);
            return "redirect:/restaurants/" + dto.getRestoId();
	    }
	    
	    // ========== pre名額不足驗證 ==========
	    int remaining = reservationSvc.getRemaining(
	        dto.getRestoId(), dto.getTimeslotId(), dto.getRegiDate());

	    if (remaining < dto.getRegiSeats()) {
	        br.rejectValue("timeslotId", "full", "訂位名額產生異動，該時段已無足夠座位，請選擇其他日期、時段");
	        
	        keep(dto, br, ra);
//	        ra.addFlashAttribute("preBooking", dto);
//            ra.addFlashAttribute("org.springframework.validation.BindingResult.preBooking", br);
            return "redirect:/restaurants/" + dto.getRestoId();
	        
	    }
	        
	    
	    // ========== 成功，進入下一階段填表頁 ==========
	    
//	    ra.addFlashAttribute("preBooking", dto);

//        ra.addFlashAttribute("org.springframework.validation.BindingResult.preBooking", br);
//        return "redirect:/restaurants/booking/confirm/" + dto.getRestoId();

	    // 用 query string 傳遞參數
	    return "redirect:/restaurants/booking/confirm/" + dto.getRestoId()
	    + "?regiDate=" + dto.getRegiDate()
	    + "&regiSeats=" + dto.getRegiSeats()
	    + "&timeslotId=" + dto.getTimeslotId();
	
	}
//
	

    //  ====填聯絡資料頁====
	@GetMapping("/restaurants/booking/confirm/{restoId}")
	public String showBookingForm(@PathVariable Integer restoId,
						          @RequestParam LocalDate regiDate,
						          @RequestParam Integer regiSeats,
						          @RequestParam Integer timeslotId,
						          Model model
	) {
		
		/* ---- 餐廳必須是上線狀態 ---- */
	    RestoVO resto = restoSvc.findOnline(restoId)                       // 只撈 enabled
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

	    /* ---- 時段必須存在，且隸屬同一間餐廳 ---- */
	    TimeslotVO ts = timeslotSvc.findNotDeleted(timeslotId)                // 只撈 enabled
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		
	    if (!ts.getPeriodVO().getRestoVO().getRestoId().equals(restoId)) {
	        throw new ResponseStatusException(HttpStatus.NOT_FOUND);       // timeslot 不屬於該餐廳
	    }

		

//		RestoVO resto = restoSvc.getById(restoId);
//	    TimeslotVO ts = timeslotSvc.getById(timeslotId);
		

        // summary 需要的靜態資料
	    model.addAttribute("resto",    resto);
	    model.addAttribute("timeslot", ts);
	    model.addAttribute("seats",    regiSeats);
	    model.addAttribute("regiDate", regiDate);
		
        /* 第一次進來才建立空的 restoOrder 讓 <form> 綁定  */
        if (!model.containsAttribute("restoOrder")) {
            RestoOrderVO order = new RestoOrderVO();
        	order.setRestoVO(resto);
            order.setTimeslotVO(ts);
            order.setRegiDate(regiDate);
            order.setRegiSeats(regiSeats);
            model.addAttribute("restoOrder", order);
        }
        
	    return "front-end/resto/restoBooking";
	}
	
	//  ====送出聯絡資料====	
	@PostMapping("/restaurants/booking/confirm/{restoId}")
    public String createOrder(@PathVariable Integer restoId,
    						  @Validated @ModelAttribute("restoOrder") RestoOrderVO order,
					          BindingResult br,
					          HttpSession session,
                              RedirectAttributes ra){
		
		
		
		/* ---- 餐廳必須 online ---- */
	    RestoVO resto = restoSvc.findOnline(restoId)
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

	    /* ---- 時段 id 來自 hidden，必須同餐廳 & enabled ---- */
	    TimeslotVO ts = timeslotSvc.findNotDeleted(order.getTimeslotVO().getTimeslotId())
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

	    if (!ts.getPeriodVO().getRestoVO().getRestoId().equals(restoId)) {
	        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
	    }
	    
	    /* ---- ③ 將受管實體塞回訂單 (避免 TransientObjectException) ---- */
	    order.setRestoVO(resto);   // 受管實體
	    order.setTimeslotVO(ts);   // 受管實體
	    
		
	
		
		/* ---------- 從 session 取會員 ---------- */
	    MemberVO loggedIn = (MemberVO) session.getAttribute("loggedInMember");
	    if (loggedIn != null) {
	        // 用 managed reference，避免「TransientObjectException」
	        MemberVO managed = memberRepo.getReferenceById(loggedIn.getMemberId());
	        order.setMemberVO(managed);
	        order.setOrderSource(RestoOrderSource.MEMBER);
	    }


		
        if (br.hasErrors()){
        	
//        	System.out.println("==== 檢查 br 的所有錯誤 ====");
//        	System.out.println("==== Validation Errors ====");
//            br.getFieldErrors().forEach(fe -> {
//                System.out.printf("[欄位錯誤] field=%s, message=%s%n", fe.getField(), fe.getDefaultMessage());
//            });
//
//            br.getGlobalErrors().forEach(ge -> {
//                System.out.printf("[全域錯誤] object=%s, message=%s%n", ge.getObjectName(), ge.getDefaultMessage());
//
//            });
        	
            keepOrder(order, br, ra);
            return "redirect:/restaurants/booking/confirm/" + restoId 
            		+ "?regiDate="  + order.getRegiDate()
                    + "&regiSeats=" + order.getRegiSeats()
                    + "&timeslotId="+ order.getTimeslotVO().getTimeslotId();
        }
                
        
        /* -------- 填表送出時再次名額驗證 ＋ 原子插單 -------- */
        try {
            restoOrderSvc.tryCreateOrder(order);   // 寫入並回填 orderId
            
            /* === 2. 若是會員 → 建立通知 === */
            if (order.getMemberVO() != null) {
                Integer memberId = order.getMemberVO().getMemberId();

                String title   = "餐廳訂位成功";
                String content = String.format("您已成功預約 %s %s（%s人）。",
                                  order.getRestoVO().getRestoName(),
                                  order.getRegiDate() + " " + order.getTimeslotVO().getTimeslotName(),
                                  order.getRegiSeats());

            notificationSvc.createNotification(memberId, title, content);
            }
            
            /* 3. 寄出 Email － 目前只寄會員訂單 */
            String guestEmail = order.getOrderGuestEmail();  
            try {
                restoEmailSvc.sendRestoRegiEmail(guestEmail, order);
            } catch (Exception e) {
                br.reject("error","送餐廳訂位確認信失敗");
            }


        } catch (OverbookingException e){  // 名額被搶
            br.reject("full","很抱歉，名額剛好被訂完，請重新選擇時段");
            keepOrder(order, br, ra);
            
            String errorMsg = br.getGlobalError() != null
            	    ? br.getGlobalError().getDefaultMessage()
            	    : "很抱歉，名額已滿，請重試";

            	ra.addFlashAttribute("globalError", errorMsg);
            
            return "redirect:/restaurants/" + restoId;   // 回第一步
        }

        ra.addFlashAttribute("orderId", order.getRestoOrderId());
        ra.addFlashAttribute("toast",   "已完成訂位！");
        return "redirect:/restaurants/BookSuccess";
    }


	
    //  ====結果頁====	
	@GetMapping("/restaurants/BookSuccess")
	public String showBookingResult(@ModelAttribute("orderId") Integer id,
									Model model
						        
						        
	) {
		if (id == null){ // 防直接刷新/手動輸入URL進入
            return "redirect:/restaurants";
        }
        model.addAttribute("order", restoOrderSvc.getById(id));
        return "front-end/resto/restoBookSuccess";
	}
	
	

	
	// 將 dto + errors 放入 flash
	private void keep(PreBookingDTO dto, BindingResult br, RedirectAttributes ra){
        ra.addFlashAttribute("preBooking", dto);
        ra.addFlashAttribute("org.springframework.validation.BindingResult.preBooking", br);
    }
    private void keepOrder(RestoOrderVO o, BindingResult br, RedirectAttributes ra){
        ra.addFlashAttribute("restoOrder", o);
        ra.addFlashAttribute("org.springframework.validation.BindingResult.restoOrder", br);
    }
	

    //  ====intro 共用資料====
	private void prepareIntroPage(Model model,
                              Integer restoId,
                              int seatsForFullMap,       // 要用多少人去算滿額
                              LocalDate dateForFullMap
    ) {

	    RestoVO resto = restoSvc.findOnline(restoId)
	                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	
	    model.addAttribute("selectedResto", resto);
	    model.addAttribute("restoSeats",    resto.getRestoSeatsTotal());
	    model.addAttribute("today",         LocalDate.now());
	    LocalDate today   = LocalDate.now();
	    LocalDate maxDate = today.plusMonths(1);
	    model.addAttribute("maxDate",maxDate);
	    
	    
	    List<PeriodVO> periodList = periodSvc.findEnabledByRestoIdWithSlots(restoId);
	    model.addAttribute("periodList", periodList);
	
	    
	    // ==== timeslot 是否滿額 ====
	    Map<Integer, Boolean> fullSlotMap = new HashMap<>();
	    for (PeriodVO p : periodList) {
	        for (TimeslotVO ts : p.getTimeslots()) {
	            boolean full = reservationSvc.getRemaining(restoId, ts.getTimeslotId(), dateForFullMap)
	                         < seatsForFullMap;
	            fullSlotMap.put(ts.getTimeslotId(), full);
	        }
	    }
	    model.addAttribute("fullSlotMap", fullSlotMap);
	}

	
	// ===== 會員中心餐廳訂單頁 =====
	   // 列表：type=current or history
	    @GetMapping("/member/order/restaurant")
	    public String list(
	    		@RequestParam(defaultValue = "all") String type,
	            Model model,
	            HttpSession session) {

	        // 從 session 拿 memberId
	    	 // 登入檢查
	    	MemberVO loggedIn = (MemberVO) session.getAttribute("loggedInMember");
	    	if (loggedIn == null) {
	    	    String redirectUrl = "/member/order/restaurant?type=" + type;
	    	    String encoded     = UriUtils.encode(redirectUrl, StandardCharsets.UTF_8);
	    	    return "redirect:/member/login?redirectUrl=" + encoded;
	    	}
	        Integer memberId = loggedIn.getMemberId();
	        // 根據 type 決定要抓哪一種清單
	        List<RestoOrderVO> list;
	        switch (type) {
	            case "current":
	                list = restoOrderSvc.getActiveOrders(memberId);
	                break;
	            case "history":
	                list = restoOrderSvc.getHistoryOrders(memberId);
	                break;
	            default:  // "all"
	                list = restoOrderSvc.getAllOrders(memberId);
	        }

	        // 放 model、回模板
	        model.addAttribute("restoOrderList", list);
	        model.addAttribute("type", type);
	        return "front-end/resto/restoMemOrder";
	    }

	     

	    // 取消訂單
	    @PostMapping("/member/order/restaurant/cancel")
	    public String cancel(
	            @RequestParam Integer id,
	            @RequestParam String type,
	            RedirectAttributes ra,
	            HttpSession session) {

	    	// 先檢查登入、再取消
	        MemberVO m = (MemberVO) session.getAttribute("loggedInMember");
	        if (m == null) {
	            // 帶上 redirectUrl，或直接重導去登入
	            return "redirect:/member/login";
	        }
	        restoOrderSvc.cancelOrder(id);
	       // 用 flash 標示一次性顯示「取消成功」
	        ra.addFlashAttribute("cancelSuccess", true);
	        ra.addAttribute("type", type);
	        return "redirect:/member/order/restaurant";
	    }
		


	
	
	
	
	
	
	
	
	
	
	
	
	

}
