package com.nanahana.bicyclesharing.record.service;

import com.nanahana.bicyclesharing.common.exception.BadDataException;
import com.nanahana.bicyclesharing.record.dao.RideRecordMapper;
import com.nanahana.bicyclesharing.record.entity.RideRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author nana
 * @Date 2019/5/12 19:46
 * @Description 骑行记录实现类
 */
@Service
@Slf4j
public class RideRecordServiceImpl implements RideRecordService {

    private final RideRecordMapper rideRecordMapper;

    @Autowired
    public RideRecordServiceImpl(RideRecordMapper rideRecordMapper) {
        this.rideRecordMapper = rideRecordMapper;
    }

    @Override
    public List<RideRecord> listRideRecord(long userId, Long lastId) throws BadDataException {
        return rideRecordMapper.selectRideRecordPage(userId, lastId);
    }
}
