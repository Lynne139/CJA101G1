package com.member.model;
 
import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service("memberService")
public class MemberService {
	 
	@Autowired
	MemberRepository repository;
	
	@Autowired
    private SessionFactory sessionFactory;
	
	@Autowired
	@Lazy
	private com.util.email.ResetPasswordEmailService resetPasswordEmailService;

	public void addMember(MemberVO memberVO) {
		repository.save(memberVO);
	}

	public void updateMember(MemberVO memberVO) {
		repository.save(memberVO);
	}

	public void deleteMember(Integer memberId) {
		if (repository.existsById(memberId))
			repository.deleteByMemberId(memberId);

	}

	public MemberVO getOneMember(Integer memberId) {
		Optional<MemberVO> optional = repository.findById(memberId);
//		return optional.get();
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	public List<MemberVO> getAll() {
		return repository.findAll();
	}

//	public List<MemberVO> getAll(Map<String, String[]> map) {
//		return HibernateUtil_CompositeQuery_yuko.getAllC(map,sessionFactory.openSession());
//	}
	
	public MemberVO findByEmail(String email) {
	    return repository.findByMemberEmail(email);
	}

	public List<MemberVO> findByNameLike(String memberName) {
		
		return repository.findByMemberName(memberName);
	}
  
	public Integer getMemberPointsById(Integer memberId) {
	    Optional<MemberVO> optional = repository.findById(memberId);
	    return optional.map(MemberVO::getMemberPoints).orElse(null);
	}


	public MemberVO getByEmail(String email) {
	    Optional<MemberVO> optional = Optional.ofNullable(repository.findByMemberEmail(email));
	    return optional.orElse(null);
	}
	
	public boolean sendResetPasswordEmail(String email) {
	    return resetPasswordEmailService.sendResetPasswordEmail(email);
	}

	
	@Transactional
	public void updateConsumptionAndLevelAndPoints(Integer memberId, int priceChange) {
	    Optional<MemberVO> optional = repository.findById(memberId);
	    if (optional.isPresent()) {
	        MemberVO member = optional.get();

	        // 更新累積消費金額
	        int newConsumption = member.getMemberAccumulativeConsumption() + priceChange;
	        if (newConsumption < 0) newConsumption = 0; // 防止累積消費變負
	        member.setMemberAccumulativeConsumption(newConsumption);

	        // 根據新的累積消費金額調整會員等級
	        if (newConsumption <= 9999) {
	            member.setMemberLevel("普通會員");
	        } else if (newConsumption <= 49999) {
	            member.setMemberLevel("銀卡會員");
	        } else if (newConsumption <= 99999) {
	            member.setMemberLevel("金卡會員");
	        } else {
	            member.setMemberLevel("白金卡會員");
	        }
	        
	        // 更新點數（向下取整）
	        int additionalPoints = (int) (priceChange * 0.1);
	        int newPoints = member.getMemberPoints() + additionalPoints;
	        if (newPoints < 0) newPoints = 0; // 防止點數變負
	        member.setMemberPoints(newPoints);
	        
	        // 儲存更新
	        repository.save(member);
	    }
	}


}
