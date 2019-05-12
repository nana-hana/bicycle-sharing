package com.nanahana.bicyclesharing.bicycle.entity;

import lombok.Data;

/**
 * @Author nana
 * @Date 2019/5/12 12:13
 * @Description 坐标轴
 */
@Data
public class Point {

    /**
     * 默认构造器
     */
    public Point() {
    }

    /**
     * 经纬度构造器（数组）
     *
     * @param loc 经纬度数组
     */
    public Point(Double[] loc) {
        this.longitude = loc[0];
        this.latitude = loc[1];
    }

    /**
     * 经纬度构造器
     *
     * @param longitude 精度
     * @param latitude  纬度
     */
    public Point(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * 精度
     */
    private double longitude;

    /**
     * 纬度
     */
    private double latitude;
}
