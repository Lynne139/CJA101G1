package com.member.model;
 
import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;



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
}
