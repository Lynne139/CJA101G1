package com.memberLevelType.model;
 
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("memberLevelTypeService")
public class MemberLevelTypeService {
 
	@Autowired
    MemberLevelTypeRepository repository; 
   
    // 新增
    public void addMemberLevelType(MemberLevelType memberLevelType) {
    	repository.save(memberLevelType);
    }
    
    // 修改
    public void updateMemberLevelType(MemberLevelType memberLevelType) {
    	repository.save(memberLevelType);
    	System.out.println("要更新到 DB 的資料: " + memberLevelType.getMemberLevel());
    }
    
    // 刪除
    public void deleteMemberLevelType(String memberLevel){
		if (repository.existsById(memberLevel)) 
			repository.deleteById(memberLevel);
		
	}
    
    // 用主鍵查詢
    public MemberLevelType getOneMemberLevelType(String memberLevel) {
        Optional<MemberLevelType> optional = repository.findById(memberLevel);
        return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}
    
    
    // 查詢全部
    public List<MemberLevelType> getAll() {
        return repository.findAll();
    }


    
}
