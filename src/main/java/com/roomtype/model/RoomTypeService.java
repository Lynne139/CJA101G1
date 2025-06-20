package com.roomtype.model;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.resto.model.RestoRepository;
import com.resto.model.RestoVO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;

@Service("roomTypeService")
public class RoomTypeService {
	
	@PersistenceContext
    private EntityManager em;

    private final RoomTypeRepository roomTypeRepository;

    public RoomTypeService(RoomTypeRepository roomTypeRepository) {
        this.roomTypeRepository = roomTypeRepository;
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
    public boolean isDuplicateName(String name) {
        return roomTypeRepository.findByRoomTypeName(name).isPresent();
    }

    // 用於編輯時（排除自己）
    public boolean isDuplicateName(String name, Integer excludeId) {
        Optional<RoomTypeVO> existing = roomTypeRepository.findByRoomTypeName(name);
        return existing.isPresent() && !existing.get().getRoomTypeId().equals(excludeId);
    }
    
    //儲存資料和圖片
    @Transactional
    public void saveWithImage(RoomTypeVO roomTypeVO, MultipartFile img, String clearImgFlag) {
    	//傳入前端送出的表單內容、圖片、以及是否清除圖片
    	RoomTypeVO newData;

        if (roomTypeVO.getRoomTypeId() == null) {//傳入的物件沒有ID
            // 新增
        	newData = roomTypeVO;//將收到的物件指定給newData
        } else {
            // 編輯：抓出原始資料（含 version）
        	newData = roomTypeRepository.findById(roomTypeVO.getRoomTypeId())
                      .orElseThrow(() -> new IllegalArgumentException("查無資料"));
        	//將符合傳入物件ID的資料指定給newData，如無相符資料丟出錯誤訊息(使用Lambda 表達式)

            //檢查版本一致性
            if (!newData.getVersion().equals(roomTypeVO.getVersion())) {
                throw new OptimisticLockException("資料已被他人修改，請重新載入");
            }
            
            // 更新欄位(圖片除外)
            newData.setRoomTypeName(roomTypeVO.getRoomTypeName());
            newData.setRoomTypeAmount(roomTypeVO.getRoomTypeAmount());
            newData.setRoomTypeContent(roomTypeVO.getRoomTypeContent());
            newData.setRoomSaleStatus(roomTypeVO.getRoomSaleStatus());
            newData.setRoomTypePrice(roomTypeVO.getRoomTypePrice());
        }

        // 處理圖片
        if ("true".equalsIgnoreCase(clearImgFlag)) {//清除圖片
        	newData.setRoomTypePic(null);
        } else if (img != null && !img.isEmpty()) {//不清除圖片
            try {
            	newData.setRoomTypePic(img.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("圖片處理錯誤", e);
            }
        }

        // 儲存
        roomTypeRepository.save(newData);
    }
}
