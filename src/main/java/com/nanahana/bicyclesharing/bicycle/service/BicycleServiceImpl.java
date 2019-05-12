package com.nanahana.bicyclesharing.bicycle.service;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.nanahana.bicyclesharing.bicycle.dao.BicycleMapper;
import com.nanahana.bicyclesharing.bicycle.entity.Bicycle;
import com.nanahana.bicyclesharing.bicycle.entity.BicycleLocation;
import com.nanahana.bicyclesharing.bicycle.entity.BicycleNoGen;
import com.nanahana.bicyclesharing.common.exception.BadCredentialException;
import com.nanahana.bicyclesharing.common.exception.BadDataException;
import com.nanahana.bicyclesharing.common.utils.BaiduPushUtil;
import com.nanahana.bicyclesharing.common.utils.DateUtil;
import com.nanahana.bicyclesharing.common.utils.RandomNumberCode;
import com.nanahana.bicyclesharing.fee.dao.RideFeeMapper;
import com.nanahana.bicyclesharing.fee.entity.RideFee;
import com.nanahana.bicyclesharing.record.dao.RideRecordMapper;
import com.nanahana.bicyclesharing.record.entity.RideRecord;
import com.nanahana.bicyclesharing.user.dao.UserMapper;
import com.nanahana.bicyclesharing.user.entity.User;
import com.nanahana.bicyclesharing.user.entity.UserElement;
import com.nanahana.bicyclesharing.wallet.dao.WalletMapper;
import com.nanahana.bicyclesharing.wallet.entity.Wallet;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author nana
 * @Date 2019/5/12 10:14
 * @Description 单车具体实现类
 */
@Service("bicycleServiceImpl")
@Slf4j
public class BicycleServiceImpl implements BicycleService {

    /**
     * 未认证
     */
    private static final Byte NOT_VERYFY = 1;
    /**
     * 单车解锁
     */
    private static final Object BIKE_UNLOCK = 2;
    /**
     * 单车锁定
     */
    private static final Object BIKE_LOCK = 1;
    /**
     * 骑行结束
     */
    private static final Byte RIDE_END = 2;


    private final BicycleMapper bicycleMapper;
    private final UserMapper userMapper;
    private final RideRecordMapper rideRecordMapper;
    private final WalletMapper walletMapper;
    private final MongoTemplate mongoTemplate;
    private final RideFeeMapper feeMapper;

    @Autowired
    public BicycleServiceImpl(UserMapper userMapper, BicycleMapper bicycleMapper, RideRecordMapper rideRecordMapper,
                              WalletMapper walletMapper, MongoTemplate mongoTemplate, RideFeeMapper feeMapper) {
        this.userMapper = userMapper;
        this.bicycleMapper = bicycleMapper;
        this.rideRecordMapper = rideRecordMapper;
        this.feeMapper = feeMapper;
        this.walletMapper = walletMapper;
        this.mongoTemplate = mongoTemplate;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void generateBicycle() throws BadDataException {
        BicycleNoGen bicycleNoGen = new BicycleNoGen();
        bicycleMapper.generateBicycleNo(bicycleNoGen);
        Bicycle bike = new Bicycle();
        bike.setType((byte) 2);
        bike.setNumber(bicycleNoGen.getAutoIncNo());
        bicycleMapper.insertSelective(bike);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unLockBicycle(UserElement currentUser, Long bicycleNo) throws BadCredentialException {
        try {
            /* 检查用户是否已经认证（实名认证没  押金交了没 ）*/
            User user = userMapper.selectByPrimaryKey(currentUser.getUserId());
            if (user.getVerifyFlag().equals(NOT_VERYFY)) {
                throw new BadCredentialException("用户尚未认证");
            }
            /* 检查用户有没有未关闭的骑行记录 */
            RideRecord record = rideRecordMapper.selectRecordNotClosed(currentUser.getUserId());
            if (record != null) {
                throw new BadCredentialException("存在未关闭骑行订单");
            }
            /* 检查用户钱包余额是否足够（大于一元）*/
            Wallet wallet = walletMapper.selectByUserId(currentUser.getUserId());
            if (wallet.getRemainSum().compareTo(new BigDecimal(1)) < 0) {
                throw new BadCredentialException("余额不足");
            }
            /* 推送单车进行解锁 */
            JSONObject notification = new JSONObject();
            notification.put("unlock", "unlock");
            BaiduPushUtil.pushMsgToSingleDevice(currentUser, "{\"title\":\"TEST\",\"description\":\"Hello Baidu " +
                "push!\"}");
            /* 推送如果可靠性比较差 可以采用单车端开锁后 主动ACK服务器 再修改相关状态的方式
             * 修改mongoDB中单车状态
             * */
            Query query = Query.query(Criteria.where("bike_no").is(bicycleNo));
            Update update = Update.update("status", BIKE_UNLOCK);
            mongoTemplate.updateFirst(query, update, "bike-position");
            /* 建立订单  记录开始骑行时间  同时骑行轨迹开始上报(另一个接口) */
            RideRecord rideRecord = new RideRecord();
            rideRecord.setBicycleNo(bicycleNo);
            String recordNo = new Date().toString() + System.currentTimeMillis() + RandomNumberCode.randomNumber();
            rideRecord.setRecordNo(recordNo);
            rideRecord.setStartTime(new Date());
            rideRecord.setUserId(currentUser.getUserId());
            rideRecordMapper.insertSelective(rideRecord);
        } catch (Exception e) {
            log.error("fail to un lock bike", e);
            throw new BadCredentialException("解锁单车失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void lockBicycle(BicycleLocation bicycleLocation) throws BadDataException {
        try {
            /* 结束订单 计算骑行时间存订单 */
            RideRecord record = rideRecordMapper.selectBicycleRecordOnGoing(bicycleLocation.getBicycleNumber());
            if (record == null) {
                throw new BadDataException("骑行记录不存在");
            }
            Long userId = record.getUserId();
            /* 查询单车类型 查询计价信息 */
            Bicycle bicycle = bicycleMapper.selectByBicycleNo(bicycleLocation.getBicycleNumber());
            if (bicycle == null) {
                throw new BadDataException("单车不存在");
            }
            RideFee fee = feeMapper.selectBicycleTypeFee(bicycle.getType());
            if (fee == null) {
                throw new BadDataException("计费信息异常");
            }
            BigDecimal cost = BigDecimal.ZERO;
            record.setEndTime(new Date());
            record.setStatus(RIDE_END);
            Long min = DateUtil.getBetweenMin(new Date(), record.getStartTime());
            record.setRideTime(min.intValue());
            int minUnit = fee.getMinUnit();
            int intMin = min.intValue();
            if (intMin / minUnit == 0) {
                /* 不足一个时间单位 按照一个时间单位算 */
                cost = fee.getFee();
            } else if (intMin % minUnit == 0) {
                /* 整除了时间单位 直接计费 */
                cost = fee.getFee().multiply(new BigDecimal(intMin / minUnit));
            } else if (intMin % minUnit != 0) {
                /* 不整除 +1 补足一个时间单位 */
                cost = fee.getFee().multiply(new BigDecimal((intMin / minUnit) + 1));
            }
            record.setRideCost(cost);
            rideRecordMapper.updateByPrimaryKeySelective(record);
            /* 钱包扣费 */
            Wallet wallet = walletMapper.selectByUserId(userId);
            wallet.setRemainSum(wallet.getRemainSum().subtract(cost));
            walletMapper.updateByPrimaryKeySelective(wallet);
            /* 修改mongoDB中单车状态为锁定 */
            Query query = Query.query(Criteria.where("bike_no").is(bicycleLocation.getBicycleNumber()));
            Update update = Update.update("status", BIKE_LOCK)
                .set("location.coordinates", bicycleLocation.getCoordinates());
            mongoTemplate.updateFirst(query, update, "bike-position");
        } catch (Exception e) {
            log.error("fail to lock bike", e);
            throw new BadDataException("锁定单车失败");
        }
    }

    @Override
    public void reportLocation(BicycleLocation bikeLocation) throws BadDataException {
        /* 数据库中查询该单车尚未完结的订单 */
        RideRecord record = rideRecordMapper.selectBicycleRecordOnGoing(bikeLocation.getBicycleNumber());
        if (record == null) {
            throw new BadDataException("骑行记录不存在");
        }
        /* 查询mongo中是否已经有骑行的坐标记录数据 */
        FindIterable<Document> findIterable = mongoTemplate.getCollection("ride_contrail").find(new BasicDBObject(
            "record_no", record.getRecordNo()));
        DBObject obj = (DBObject) findIterable.iterator().next();
//        DBObject obj = mongoTemplate.getCollection("ride_contrail")
//            .findOne(new BasicDBObject("record_no", record.getRecordNo()));
        /* 没有则插入，如果已经存在则添加坐标 */
        if (obj == null) {
            List<BasicDBObject> list = new ArrayList();
            BasicDBObject temp = new BasicDBObject("loc", bikeLocation.getCoordinates());
            list.add(temp);
            BasicDBObject insertObj = new BasicDBObject("record_no", record.getRecordNo())
                .append("bike_no", record.getBicycleNo())
                .append("contrail", list);
            mongoTemplate.insert(insertObj, "ride_contrail");
        } else {
            Query query = new Query(Criteria.where("record_no").is(record.getRecordNo()));
            Update update = new Update().push("contrail", new BasicDBObject("loc", bikeLocation.getCoordinates()));
            mongoTemplate.updateFirst(query, update, "ride_contrail");
        }
    }
}
