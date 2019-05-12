package com.nanahana.bicyclesharing.record.entity;

import com.nanahana.bicyclesharing.bicycle.entity.Point;
import lombok.Data;

import java.util.List;

/**
 * @Author nana
 * @Date 2019/5/12 19:17
 * @Description 骑行轨迹
 */
@Data
public class RideContrail {
    /**
     * 订单编号
     */
    private String rideRecordNo;
    /**
     * 单车编号
     */
    private Long bicycleNo;
    /**
     *
     */
    private List<Point> contrail;

}
