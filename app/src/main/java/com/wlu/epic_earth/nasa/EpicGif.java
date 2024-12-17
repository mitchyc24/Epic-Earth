package com.wlu.epic_earth.nasa;

import java.util.Date;
import java.util.logging.Logger;

public class EpicGif {
    private static final Logger logger = Logger.getLogger(EpicGif.class.getName());

    private Date date;
    private byte[] data;

    public EpicGif() {
        this(new Date(), new byte[0]);
        logger.info("Created new EpicGif");
    }

    public EpicGif(Date date, byte[] data) {
        this.date = date;
        this.data = data;
        logger.info("Created new EpicGif with date " + date);
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