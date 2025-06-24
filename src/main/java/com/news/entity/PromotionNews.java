package com.news.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "promotion_list")
public class PromotionNews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Promo_no")
    private Integer promoNo;

    @Column(name = "Title", nullable = false, length = 100)
    private String title;

    @Column(name = "Content", nullable = false, length = 1000)
    private String content;

    @Column(name = "Start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "End_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "Is_display", nullable = false)
    private Boolean isDisplay = true;

    @Lob
    @Column(name = "Promo_photo")
    private byte[] promoPhoto;

    // getters/setters
    public Integer getPromoNo() { return promoNo; }
    public void setPromoNo(Integer promoNo) { this.promoNo = promoNo; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Boolean getIsDisplay() { return isDisplay; }
    public void setIsDisplay(Boolean isDisplay) { this.isDisplay = isDisplay; }
    public byte[] getPromoPhoto() { return promoPhoto; }
    public void setPromoPhoto(byte[] promoPhoto) { this.promoPhoto = promoPhoto; }
} 