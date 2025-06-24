package com.news.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "hot_news_list")
public class HotNews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Hot_news_no")
    private Integer hotNewsNo;

    @Column(name = "Title", nullable = false, length = 100)
    private String title;

    @Column(name = "Content", nullable = false, length = 1000)
    private String content;

    @Column(name = "Created_date", nullable = false)
    private LocalDate createdDate;

    @Column(name = "Is_display", nullable = false)
    private Boolean isDisplay = true;

    @Lob
    @Column(name = "Hotnews_photo")
    private byte[] newsPhoto;

    // getters/setters
    public Integer getHotNewsNo() { return hotNewsNo; }
    public void setHotNewsNo(Integer hotNewsNo) { this.hotNewsNo = hotNewsNo; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }
    public Boolean getIsDisplay() { return isDisplay; }
    public void setIsDisplay(Boolean isDisplay) { this.isDisplay = isDisplay; }
    public byte[] getNewsPhoto() { return newsPhoto; }
    public void setNewsPhoto(byte[] newsPhoto) { this.newsPhoto = newsPhoto; }
}
