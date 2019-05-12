package com.nanahana.bicyclesharing.bicycle.entity;

import lombok.Data;

/**
 * @Author nana
 * @Date 2019/5/12 10:49
 * @Description 单车编号生成类
 */
@Data
public class BicycleNoGen {

    /**
     * 单车唯一编号
     */
    private Long autoIncNo;

    /**
     * 任意byte值都可（为了生成单车唯一编号使用）
     */
    private byte whatEver;
}
