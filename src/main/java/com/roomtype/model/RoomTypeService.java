package com.roomtype.model;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.room.model.RoomRepository;
import com.room.utils.RoomTypeCriteriaHelper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service("roomTypeService")
public class RoomTypeService {

	private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    public RoomTypeService(RoomTypeRepository roomTypeRepository, RoomRepository roomRepository) {
        this.roomTypeRepository = roomTypeRepository;
        this.roomRepository = roomRepository;
    }

	public void addRoomType(RoomTypeVO roomTypeVO) {
		roomTypeRepository.save(roomTypeVO);
	}

	public void updateRoomType(RoomTypeVO roomTypeVO) {
		roomTypeRepository.save(roomTypeVO);
	}

	public RoomTypeVO getOneRoomType(Integer roomTypeId) {
		Optional<RoomTypeVO> optional = roomTypeRepository.findById(roomTypeId);
		return optional.orElse(null);
	}

	public List<RoomTypeVO> getAll() {
		return roomTypeRepository.findAll();
	}

	// 用於新增時（單一參數）
	public boolean existsDuplicateName(String name) {
		return roomTypeRepository.findByRoomTypeName(name).isPresent();
	}

	// 用於編輯時（排除自己）
	public boolean isDuplicateName(String name, Integer excludeId) {
		Optional<RoomTypeVO> existing = roomTypeRepository.findByRoomTypeName(name);
		return existing.isPresent() && !existing.get().getRoomTypeId().equals(excludeId);
	}

	// 儲存資料和圖片
	@Transactional
	public void saveWithImage(RoomTypeVO roomTypeVO, MultipartFile img, String clearImgFlag) {
		// 傳入前端送出的表單內容、圖片、以及是否清除圖片
		RoomTypeVO newData;

		if (roomTypeVO.getRoomTypeId() == null) {// 傳入的物件沒有ID
			// 新增
			newData = roomTypeVO;// 將收到的物件指定給newData
		} else {
			// 編輯：抓出原始資料（含 version）
			newData = roomTypeRepository.findById(roomTypeVO.getRoomTypeId())
					.orElseThrow(() -> new IllegalArgumentException("查無資料"));
			// 將符合傳入物件ID的資料指定給newData，如無相符資料丟出錯誤訊息(使用Lambda 表達式)

			// 檢查版本一致性
//            if (!newData.getVersion().equals(roomTypeVO.getVersion())) {
//                throw new OptimisticLockException("資料已被他人修改，請重新載入");
//            }

			// 更新欄位(圖片除外)
			newData.setRoomTypeName(roomTypeVO.getRoomTypeName());
			newData.setRoomTypeAmount(roomTypeVO.getRoomTypeAmount());
			newData.setRoomTypeContent(roomTypeVO.getRoomTypeContent());
			newData.setRoomSaleStatus(roomTypeVO.getRoomSaleStatus());
			newData.setRoomTypePrice(roomTypeVO.getRoomTypePrice());
		}

		// 處理圖片
		if ("true".equalsIgnoreCase(clearImgFlag)) {// 清除圖片
			newData.setRoomTypePic(null);
		} else if (img != null && !img.isEmpty()) {// 不清除圖片
			try {
				newData.setRoomTypePic(img.getBytes());
			} catch (IOException e) {
				throw new RuntimeException("圖片處理錯誤", e);
			}
		}

		// 儲存
		roomTypeRepository.save(newData);
	}

	// 這是處理圖片轉換為 Base64 字串的邏輯
//	public String convertImageToBase64(byte[] imageBytes) {
//		if (imageBytes != null) {
//			return Base64.getEncoder().encodeToString(imageBytes);
//		}
//		return ""; // 如果圖片為 null，返回空字串
//	}
	
//	public List<RoomTypeVO> getAll() {
//        List<RoomTypeVO> roomTypeVOList = roomTypeRepository.findAll();  // 假設從資料庫取得所有資料
//
//        // 對每個 RoomTypeVO 處理圖片
//        for (RoomTypeVO roomTypeVO : roomTypeVOList) {
//            if (roomTypeVO.getRoomTypePic() != null) {
//                // 轉換圖片為 Base64 並設定到 VO 上
//                String base64Image = convertImageToBase64(roomTypeVO.getRoomTypePic());
//                String imageType = "image/png"; // 假設圖片格式是 PNG，根據你的需求可以變動
//                roomTypeVO.setBase64Image(base64Image);
//                roomTypeVO.setImageType(imageType); // 假設 RoomTypeVO 有這個屬性來儲存圖片類型
//            } else {
//                roomTypeVO.setBase64Image(""); // 沒有圖片時設為空
//                roomTypeVO.setImageType(""); // 沒有圖片時設為空
//            }
//        }
//
//        return roomTypeVOList;
//    }
	
	// 複合查詢（Criteria 結構）
    public List<RoomTypeVO> compositeQuery(Map<String, String[]> map) {
    	// 1. 先用 CriteriaHelper 查出符合條件的資料（不含間數過濾）
    	List<RoomTypeVO> resultList = RoomTypeCriteriaHelper.getRoomTypeCriteria(map, em);
     // 2. 動態補上每筆房型的房間數量
        for (RoomTypeVO rt : resultList) {
            int count = roomRepository.countByRoomTypeVOAndRoomSaleStatus(rt, (byte) 1); // 計算上架房間數
            rt.setRoomTypeAmount(count); // 設定進 transient 欄位
        }
     // 3. 解析 minAmount / maxAmount
        Integer minAmount = getInt(map, "minAmount");
        Integer maxAmount = getInt(map, "maxAmount");

        // 4. 手動過濾
        if (minAmount != null) {
            resultList = resultList.stream()
                .filter(rt -> rt.getRoomTypeAmount() >= minAmount)
                .collect(Collectors.toList());
        }
        if (maxAmount != null) {
            resultList = resultList.stream()
                .filter(rt -> rt.getRoomTypeAmount() <= maxAmount)
                .collect(Collectors.toList());
        }

        return resultList;
    }
    
 // 工具方法：字串轉 Integer，避免 try-catch 重複
    private Integer getInt(Map<String, String[]> map, String key) {
        try {
            String[] values = map.get(key);
            return (values != null && values.length > 0 && !values[0].isEmpty())
                ? Integer.valueOf(values[0])
                : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    //計算房型數量
    public void updateRoomTypeAmount(RoomTypeVO roomTypeVO) {
        int amount = roomRepository.countByRoomTypeVOAndRoomSaleStatus(roomTypeVO, (byte)1);
        roomTypeVO.setRoomTypeAmount(amount); // 這是 Transient 欄位，不會寫入 DB
    }
}	
	