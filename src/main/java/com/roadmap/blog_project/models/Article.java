package com.roadmap.blog_project.models;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Article
{
    private String title;
    private String summary;
    private String content;


    private String image;

    private Date createdAt;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @JsonIgnore
    public String getFormattedCreatedAt ()
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM d, yyyy");

        return simpleDateFormat.format(createdAt);
    }


}

