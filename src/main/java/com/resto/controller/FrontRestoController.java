package com.resto.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.server.ResponseStatusException;

import com.resto.dto.BookingFormDTO;
import com.resto.dto.PreBookingDTO;
import com.resto.entity.RestoOrderVO;
import com.resto.entity.RestoVO;
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
		public String viewRestaurant(@PathVariable Integer id, Model model) {

		    // 找出「上架」餐廳，若找不到就 404
		    RestoVO selectedResto = restoSvc.findOnline(id)
		            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		    model.addAttribute("selectedResto", selectedResto);

		    // 找出區段與時段（包含 isDeleted = false）
		    model.addAttribute("periodList",
		            periodSvc.findEnabledByRestoIdWithSlots(id));

		    // 今天日期
		    model.addAttribute("today", LocalDate.now());

		    // 給 <form th:object="${restoOrder}">
		    model.addAttribute("restoOrder", new RestoOrderVO());

		    return "front-end/resto/restoIntro";
		}

	
	//  ====第一階段送出表單（POST）====
	@PostMapping("/restaurants/booking-pre")
	public String handlePreBooking(
	        @Validated @ModelAttribute("preBooking") PreBookingDTO dto,
	        BindingResult br,
	        Model model) {

	    if (br.hasErrors()) {
	        // 回傳原畫面並保留原輸入
	        model.addAttribute("today", LocalDate.now());
	        return "front-end/resto/restoIntro";
	    }

	    return "redirect:/restaurants/booking"
	            + "?restoId=" + dto.getRestoId()
	            + "&date=" + dto.getRegiDate()
	            + "&slotId=" + dto.getTimeslotId()
	            + "&seats=" + dto.getRegiSeats();
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
