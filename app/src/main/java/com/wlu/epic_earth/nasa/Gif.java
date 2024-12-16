package com.wlu.epic_earth.nasa;

import java.util.Date;

public class Gif {
    private Long id;
    private String name;
    private Date date;
    private byte[] data;

    public Gif() {
    }

    public Gif(Long id, String name, Date date, byte[] data) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.data = data;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}