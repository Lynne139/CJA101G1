package com.memberLevelType.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "member_level_type")
public class MemberLevelType {
	 
	private String memberLevel;
	private Integer memberRank;
	
	
	
	public MemberLevelType() {
		
	}
	
	
	@Id //@Id代表這個屬性是這個Entity的唯一識別屬性，並且對映到Table的主鍵
	@Column(name = "member_level")  //@Column指這個屬性是對應到資料庫Table的哪一個欄位   //【非必要，但當欄位名稱與屬性名稱不同時則一定要用】
	@NotEmpty(message="會員等級: 請勿空白")
	@Pattern(regexp = "^[\\u4e00-\\u9fa5]{1,30}$", message = "會員等級: 只能是中文")
	public String getMemberLevel() {
		return memberLevel;
	}
	
	public void setMemberLevel(String memberLevel) {
		this.memberLevel = memberLevel;
	}
	
	@Column(name = "level_rank")
	@NotNull(message="等級排序: 請勿空白")
	@Min(value = 1, message = "等級排序: 不能小於{value}")
	public Integer getMemberRank() {
		return memberRank;
	}
	
	public void setMemberRank(Integer memberRank) {
		this.memberRank = memberRank;
	}

}
