package com.github.bzsy.fishbowl.sqlite.greendao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "HUMIDITY_ENTITY".
 */
public class HumidityEntity {

    private Long id;
    private long time;
    private Float humidity;
    private Integer device_id;

    public HumidityEntity() {
    }

    public HumidityEntity(Long id) {
        this.id = id;
    }

    public HumidityEntity(Long id, long time, Float humidity, Integer device_id) {
        this.id = id;
        this.time = time;
        this.humidity = humidity;
        this.device_id = device_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Float getHumidity() {
        return humidity;
    }

    public void setHumidity(Float humidity) {
        this.humidity = humidity;
    }

    public Integer getDevice_id() {
        return device_id;
    }

    public void setDevice_id(Integer device_id) {
        this.device_id = device_id;
    }

}
