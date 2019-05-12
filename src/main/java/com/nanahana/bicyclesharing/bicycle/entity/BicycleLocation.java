package com.nanahana.bicyclesharing.bicycle.entity;

import lombok.Data;

/**
 * @Author nana
 * @Date 2019/5/12 10:55
 * @Description 单车坐标类
 */
@Data
public class BicycleLocation {
    /**
     * 单车字段id
     */
    private String id;
    /**
     * 单车编号
     */
    private Long bicycleNumber;
    /**
     * 单车状态
     */
    private int status;
    /**
     * 单车坐标
     */
    private Double[] coordinates;
    /**
     * 单车距离你的距离
     */
    private Double distance;
}
