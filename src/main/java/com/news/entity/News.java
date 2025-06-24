package com.news.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "news_list")
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "News_no")
    private Integer newsNo;

    @Column(name = "Title", nullable = false, length = 100)
    private String title;

    @Column(name = "Content", nullable = false, length = 1000)
    private String content;

    @Column(name = "Published_date", nullable = false)
    private LocalDate publishedDate;

    @Column(name = "Is_display", nullable = false)
    private Boolean isDisplay = true;

    @Lob
    @Column(name = "News_photo")
    private byte[] newsPhoto;

    // getters/setters
    public Integer getNewsNo() { return newsNo; }
    public void setNewsNo(Integer newsNo) { this.newsNo = newsNo; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDate getPublishedDate() { return publishedDate; }
    public void setPublishedDate(LocalDate publishedDate) { this.publishedDate = publishedDate; }
    public Boolean getIsDisplay() { return isDisplay; }
    public void setIsDisplay(Boolean isDisplay) { this.isDisplay = isDisplay; }
    public byte[] getNewsPhoto() { return newsPhoto; }
    public void setNewsPhoto(byte[] newsPhoto) { this.newsPhoto = newsPhoto; }
} 