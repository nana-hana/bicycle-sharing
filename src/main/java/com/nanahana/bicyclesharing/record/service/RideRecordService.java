package com.nanahana.bicyclesharing.record.service;

import com.nanahana.bicyclesharing.common.exception.BadDataException;
import com.nanahana.bicyclesharing.record.entity.RideRecord;

import java.util.List;

/**
 * @Author nana
 * @Date 2019/5/12 20:11
 * @Description 骑行记录服务类
 */
public interface RideRecordService {
    List<RideRecord> listRideRecord(long userId, Long lastId) throws BadDataException;
}
