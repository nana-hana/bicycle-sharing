package com.nanahana.bicyclesharing.bicycle.service;

import com.nanahana.bicyclesharing.bicycle.entity.BicycleLocation;
import com.nanahana.bicyclesharing.common.exception.BadCredentialException;
import com.nanahana.bicyclesharing.common.exception.BadDataException;
import com.nanahana.bicyclesharing.user.entity.UserElement;

/**
 * @Author nana
 * @Date 2019/5/12 10:13
 * @Description 单车Service接口类
 */
public interface BicycleService {

    /**
     * 创建单车
     */
    void generateBicycle() throws BadDataException;

    /**
     * 解锁单车，准备骑行
     *
     * @param currentUser 当前用户
     * @param bicycleNo   单车编号
     * @throws BadCredentialException 认证异常
     */
    void unLockBicycle(UserElement currentUser, Long bicycleNo) throws BadCredentialException;

    /**
     * 锁车
     *
     * @param bikeLocation 自行车坐标
     * @throws BadDataException 数据异常
     */
    void lockBicycle(BicycleLocation bikeLocation) throws BadDataException;

    /**
     * 上传单车坐标
     *
     * @param bikeLocation 自行车坐标
     * @throws BadDataException 数据异常
     */
    void reportLocation(BicycleLocation bikeLocation) throws BadDataException;
}
