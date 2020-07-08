package com.alany.spider.bean;

import java.util.Date;

public class HouseBean extends BaseBean{

//    private String objectId;//系统字段，不要set
//
//    private String updatedAt;//系统字段，不要set
//
//    private String createdAt;//系统字段，不要set

    private long id;

    private String sourceName;

    private String itemId;

    private String city;//城市

    private String location;//地区

    private String address;//详细地址

    private float areaSize;//面积

    private float price;//单价

    private float marketTotalPrice;//市场总价

    private float sellTotalPrice;//成交总价

    private String sellStatus;//成交状态

    private Date sellDate;//交易日期

    private String itemUrl;//详情地址

    private Date createdTime;

    private Date updatedTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getAreaSize() {
        return areaSize;
    }

    public void setAreaSize(float areaSize) {
        this.areaSize = areaSize;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getMarketTotalPrice() {
        return marketTotalPrice;
    }

    public void setMarketTotalPrice(float marketTotalPrice) {
        this.marketTotalPrice = marketTotalPrice;
    }

    public float getSellTotalPrice() {
        return sellTotalPrice;
    }

    public void setSellTotalPrice(float sellTotalPrice) {
        this.sellTotalPrice = sellTotalPrice;
    }

    public String getSellStatus() {
        return sellStatus;
    }

    public void setSellStatus(String sellStatus) {
        this.sellStatus = sellStatus;
    }

    public Date getSellDate() {
        return sellDate;
    }

    public void setSellDate(Date sellDate) {
        this.sellDate = sellDate;
    }

    public String getItemUrl() {
        return itemUrl;
    }

    public void setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }
}
