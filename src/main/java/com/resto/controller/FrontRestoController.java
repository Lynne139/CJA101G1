package com.resto.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

import com.resto.dto.BookingFormDTO;
import com.resto.dto.PreBookingDTO;
import com.resto.entity.PeriodVO;
import com.resto.entity.RestoOrderVO;
import com.resto.entity.RestoVO;
import com.resto.entity.TimeslotVO;
import com.resto.model.PeriodService;
import com.resto.model.ReservationService;
import com.resto.model.RestoOrderService;
import com.resto.model.RestoService;
import com.resto.model.TimeslotService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
		public String viewRestaurant(@PathVariable Integer id, 
									Model model,
									@RequestParam(required = false) LocalDate date) {


//			// 找出「上架」餐廳，若找不到就 404
//		    RestoVO selectedResto = restoSvc.findOnline(id)
//		            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
//		    model.addAttribute("selectedResto", selectedResto);
//		    model.addAttribute("restoSeats", selectedResto.getRestoSeatsTotal());
//
//		    // 找出區段與時段（包含 isDeleted = false）
//		    List<PeriodVO> periodList = periodSvc.findEnabledByRestoIdWithSlots(id);
//		    model.addAttribute("periodList",periodList);
//
//		    // 今天日期
//		    model.addAttribute("today", LocalDate.now());
//
//		    // 抓出timeslot滿額名單
//		    Map<Integer, Boolean> fullSlotMap = new HashMap<>();
//		    int requiredSeats = 1; // 初始顯示畫面時沒選擇人數，預設為1
//
//		    for (PeriodVO period : periodList) {
//		        for (TimeslotVO slot : period.getTimeslots()) {
//		            int usedSeats = reservationSvc.getReserved(id, slot.getTimeslotId(), LocalDate.now());
//		            boolean isFull = (usedSeats + requiredSeats > selectedResto.getRestoSeatsTotal());
//		            fullSlotMap.put(slot.getTimeslotId(), isFull);
//		        }
//		    }
//			model.addAttribute("fullSlotMap", fullSlotMap);

			prepareIntroPage(model, id, 1, LocalDate.now());
		    model.addAttribute("preBooking", new PreBookingDTO(id, null, null, null));



		    // 給 <form th:object="${preBooking}">
		    model.addAttribute("preBooking", new PreBookingDTO(id, null, null, null));
		    
		    
		    
		    return "front-end/resto/restoIntro";
		}
		
	    //  ====給前端JS timeslot 可訂狀態去enable 按鈕(非超時與非滿額)====	
		@GetMapping("/restaurants/{restoId}/available")
		@ResponseBody
		public Map<Integer, Boolean> checkAvailable(
		        @PathVariable Integer restoId,
		        @RequestParam Integer seats,
		        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

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

	
	//  ====第一階段送出表單（POST）====
	@PostMapping("/restaurants/booking-pre")
	public String handlePreBooking(
	        @Validated @ModelAttribute("preBooking") PreBookingDTO dto,
	        BindingResult br,
	        Model model) {
		
	    if (br.hasErrors()) {
	    	
//	        // 回傳原畫面並保留原輸入
//	        model.addAttribute("today", LocalDate.now());
//	        
//	        // 加入必要資料以重新渲染頁面
//	        RestoVO resto = restoSvc.findOnline(dto.getRestoId())
//                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));	        model.addAttribute("preBooking", dto);
//	        model.addAttribute("selectedResto", resto);
//	        model.addAttribute("restoSeats", resto.getRestoSeatsTotal());
//	        model.addAttribute("periodList", periodSvc.findEnabledByRestoIdWithSlots(dto.getRestoId()));
//	        
//	        // 給 timeslot 滿額地圖（預設 seats=1）
//	        // 預設把每顆按鈕鎖起來，前端 JS 會再 call /available 更新
//	        Map<Integer, Boolean> fullSlotMap = new HashMap<>();
//	        for (PeriodVO p : periodSvc.findEnabledByRestoIdWithSlots(dto.getRestoId())) {
//	            for (TimeslotVO slot : p.getTimeslots()) {
//	                boolean isFull = reservationSvc.getReserved(dto.getRestoId(), slot.getTimeslotId(), LocalDate.now()) + 1
//	                                 > resto.getRestoSeatsTotal();
//	                fullSlotMap.put(slot.getTimeslotId(), isFull);
//	            }
//	        }
//	        model.addAttribute("fullSlotMap", fullSlotMap);
//	        
//	        
//	        return "front-end/resto/restoIntro";
	    	
	    	// 用 1 人、今天 來計算預設的滿額 Map
	        prepareIntroPage(model, dto.getRestoId(), 1, LocalDate.now());
	        // preBooking **保持 dto**，不要 new
	        model.addAttribute("preBooking", dto);
	        return "front-end/resto/restoIntro";
	    	
	    	
	    	
	    }
	    
	    // ========== 名額不足驗證 ==========
	    int remaining = reservationSvc.getRemaining(
	        dto.getRestoId(), dto.getTimeslotId(), dto.getRegiDate());

	    if (remaining < dto.getRegiSeats()) {
	        br.rejectValue("timeslotId", "full", "訂位名額產生異動，該時段已無足夠座位，請選擇其他時段");
	        
//	        model.addAttribute("today", LocalDate.now());
//	        RestoVO resto = restoSvc.getById(dto.getRestoId());
//	        model.addAttribute("selectedResto", resto);
//	        model.addAttribute("restoSeats", resto.getRestoSeatsTotal());
//	        model.addAttribute("periodList", periodSvc.findEnabledByRestoIdWithSlots(dto.getRestoId()));
//
//	        // 給 timeslot 滿額地圖
//	        Map<Integer, Boolean> fullSlotMap = new HashMap<>();
//	        for (PeriodVO p : periodSvc.findEnabledByRestoIdWithSlots(dto.getRestoId())) {
//	            for (TimeslotVO slot : p.getTimeslots()) {
//	                boolean isFull = reservationSvc.getRemaining(dto.getRestoId(), slot.getTimeslotId(), dto.getRegiDate())
//	                                 < dto.getRegiSeats();
//	                fullSlotMap.put(slot.getTimeslotId(), isFull);
//	            }
//	        }
//	        model.addAttribute("fullSlotMap", fullSlotMap);
//	        return "front-end/resto/restoIntro";
	        
	     // 用「使用者剛選的人數 / 日期」重算滿額 Map，讓時段按鈕即時鎖起來
	        prepareIntroPage(model,
	                         dto.getRestoId(),
	                         dto.getRegiSeats(),
	                         dto.getRegiDate());

	        model.addAttribute("preBooking", dto);
	        return "front-end/resto/restoIntro";
	        
	        
	    }
	        
	    // ========== 成功 → 進入下一階段填表頁 ==========
	    return "redirect:/restaurants/booking"
	            + "?restoId=" + dto.getRestoId()
	            + "&date=" + dto.getRegiDate()
	            + "&slotId=" + dto.getTimeslotId()
	            + "&seats=" + dto.getRegiSeats();
    
	}

	/** 把 intro 頁面一定要的共用資料全部塞進 model */
	private void prepareIntroPage(Model model,
	                              Integer restoId,
	                              int seatsForFullMap,       // 要用多少人去算滿額
	                              LocalDate dateForFullMap) {

	    RestoVO resto = restoSvc.findOnline(restoId)
	                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

	    model.addAttribute("selectedResto", resto);
	    model.addAttribute("restoSeats",    resto.getRestoSeatsTotal());
	    model.addAttribute("today",         LocalDate.now());

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

	
	
	
	
	
	@GetMapping("/restaurants/booking")
	public String showBookingPage(@RequestParam Integer restoId,
	                              @RequestParam LocalDate date,
	                              @RequestParam Integer slotId,
	                              @RequestParam Integer seats,
	                              Model model) {

	    model.addAttribute("preFilled", new BookingFormDTO(
	        restoId, date, seats, slotId, null, null, null, 0, null
	    ));

	    // 新增預設空的 RestoOrderVO 供 form 綁定
	    model.addAttribute("restoOrder", new RestoOrderVO());

	    return "front-end/resto/restoBooking";
	}
	
	
	
	


	
	
	
	
	
	

}
