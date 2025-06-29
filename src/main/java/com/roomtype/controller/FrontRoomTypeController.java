package com.roomtype.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.roomtype.model.RoomTypeService;
import com.roomtype.model.RoomTypeVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/")
public class FrontRoomTypeController {

	@Autowired
	RoomTypeService roomTypeSvc;

	@ModelAttribute("roomTypeVOListData")
	protected List<RoomTypeVO> referenceListData() {
		List<RoomTypeVO> list = roomTypeSvc.getAll();
		return list;
	}
	
	@GetMapping("/roomtypes")
	public String showRoomTypeCards(
			HttpServletRequest request, HttpServletResponse response,
			 Model model) {
		List<RoomTypeVO> roomTypes = roomTypeSvc.getAllAvailableRoomTypes();
	    model.addAttribute("roomTypes", roomTypes);
	    return "front-end/room/roomCards";

	}
	
//	 ===== 顯示圖片 =====
	@GetMapping("/roomtypes/img/{id}")
	public ResponseEntity<byte[]> getImage(@PathVariable("id") Integer roomTypeId) {
		System.out.println("🔍 查詢圖片的房型ID：" + roomTypeId);
		RoomTypeVO roomTypeVO = roomTypeSvc.getOneRoomType(roomTypeId);
		byte[] imageBytes = roomTypeVO.getRoomTypePic();
		System.out.println("🖼 圖片 byte 數量：" + (imageBytes != null ? imageBytes.length : "null"));
		if (imageBytes == null || imageBytes.length == 0) {
			// 回傳no_img.svg bytes
			try (InputStream is = getClass().getResourceAsStream("/static/images/admin/no_img.svg")) {
				if (is != null) {
					byte[] defaultImg = is.readAllBytes();
					return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/svg+xml").body(defaultImg);
				}
			} catch (IOException e) {
				// optional: log
			}
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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
				case "jpeg":
					return "image/jpeg";
				case "png":
					return "image/png";
				case "gif":
					return "image/gif";
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
