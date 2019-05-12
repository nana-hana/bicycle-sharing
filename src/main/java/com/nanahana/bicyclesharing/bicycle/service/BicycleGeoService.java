package com.nanahana.bicyclesharing.bicycle.service;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.nanahana.bicyclesharing.bicycle.entity.BicycleLocation;
import com.nanahana.bicyclesharing.bicycle.entity.Point;
import com.nanahana.bicyclesharing.common.exception.SearchException;
import com.nanahana.bicyclesharing.record.entity.RideContrail;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author nana
 * @Date 2019/5/12 10:15
 * @Description 单车定位服务类
 */
@Component
@Slf4j
public class BicycleGeoService {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public BicycleGeoService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * 查找某经坐标点附近某范围内坐标点 由近到远
     *
     * @param collection    mongodb中要查的table
     * @param locationField mongodb中坐标字段名
     * @param center        自身所在经纬度
     * @param minDistance   最近的单车位置
     * @param maxDistance   最远的单车位置
     * @param query         java拼接mongodb查询语句
     * @param fields        限制mongodb搜索的域
     * @param limit         mongodb中搜索几条数据
     * @return 附近所有单车的坐标
     * @throws SearchException 查找异常
     */
    public List<BicycleLocation> geoNearSphere(String collection, String locationField, Point center,
                                               long minDistance, long maxDistance, DBObject query, DBObject fields,
                                               int limit) throws SearchException {
        try {
            if (query == null) {
                query = new BasicDBObject();
            }
            query.put(locationField,
                new BasicDBObject("$nearSphere",
                    new BasicDBObject("$geometry",
                        new BasicDBObject("type", "Point")
                            .append("coordinates", new double[]{center.getLongitude(), center.getLatitude()}))
                        .append("$minDistance", minDistance)
                        .append("$maxDistance", maxDistance)
                ));
            query.put("status", 1);
            FindIterable<Document> objList = mongoTemplate.getCollection(collection).find();
            MongoCursor<Document> mongoCursor = objList.iterator();
            List<BicycleLocation> result = new ArrayList<>();
            while (mongoCursor.hasNext()) {
                Document document = mongoCursor.next();
                BicycleLocation location = new BicycleLocation();
                location.setBicycleNumber(((Integer) document.get("bicycle_no")).longValue());
                location.setStatus((Integer) document.get("status"));
                ArrayList coordinates = document.get("location", Document.class).get("coordinates", ArrayList.class);
                Double[] temp = (Double[]) coordinates.toArray(new Double[0]);
                location.setCoordinates(temp);
                result.add(location);
            }
            return result;
        } catch (Exception e) {
            log.error("fail to find around bike", e);
            throw new SearchException("查找附近单车失败");
        }
    }

    /**
     * 查找某经坐标点附近某范围内坐标点 由近到远 并且计算距离
     *
     * @param collection  mongodb中要查的table
     * @param query       java拼接mongodb查询语句
     * @param point       自身所在经纬度
     * @param limit       mongodb中搜索几条数据
     * @param maxDistance 最远的单车位置
     * @return 返回附近单车集
     * @throws SearchException 查询异常
     */
    public List<BicycleLocation> geoNear(String collection, DBObject query, Point point, int limit, long maxDistance)
        throws SearchException {
        try {
            if (query == null) {
                query = new BasicDBObject();
            }
            List<DBObject> pipeLine = new ArrayList<>();
            DBObject aggregate = new BasicDBObject("$geoNear",
                new BasicDBObject("near", new BasicDBObject("type", "Point").append("coordinates",
                    new double[]{point.getLongitude(), point.getLatitude()}))
                    .append("distanceField", "distance")
                    .append("num", limit)
                    .append("maxDistance", maxDistance)
                    .append("spherical", true)
                    .append("query", new BasicDBObject("status", 1))
            );
            pipeLine.add(aggregate);
//            AggregateIterable output = mongoTemplate.getCollection(collection).aggregate(pipeLine);
//            MongoCursor mongoCursor = output.iterator();
            //此处报错，原因暂时不知道
            Cursor cursor = ((DBCollection) mongoTemplate.getCollection(collection)).aggregate(pipeLine,
                AggregationOptions.builder().build());
            List<BicycleLocation> result = new ArrayList<>();
            while (cursor.hasNext()) {
                Document document = (Document) cursor.next();
                BicycleLocation location = new BicycleLocation();
                location.setBicycleNumber(((Integer) document.get("bike_no")).longValue());
                ArrayList coordinates = document.get("location", Document.class).get("coordinates", ArrayList.class);
                Double[] temp = (Double[]) coordinates.toArray(new Double[0]);
                location.setCoordinates(temp);
                location.setDistance((Double) document.get("distance"));
                result.add(location);
            }
            return result;
        } catch (Exception e) {
            log.error("fail to find around bike", e);
            throw new SearchException("查找附近单车失败");
        }
    }

    /**
     * 查询单车轨迹
     *
     * @param collection 要查询的table
     * @param recordNo   查询的记录
     * @return 返回查询到的结果
     * @throws SearchException 查询异常
     */
    public RideContrail rideContrail(String collection, String recordNo) throws SearchException {

        try {
//            DBObject obj = mongoTemplate.getCollection(collection).findOne(new BasicDBObject("record_no", recordNo));
            FindIterable<Document> findIterable = mongoTemplate.getCollection(collection).find(new BasicDBObject(
                "record_no", recordNo));
            DBObject obj = (DBObject) findIterable.iterator().next();
            RideContrail rideContrail = new RideContrail();
            rideContrail.setRideRecordNo((String) obj.get("record_no"));
            rideContrail.setBicycleNo(((Integer) obj.get("bike_no")).longValue());
            BasicDBList locList = (BasicDBList) obj.get("contrail");
            List<Point> pointList = new ArrayList<>();
            for (Object object : locList) {
                BasicDBList locObj = (BasicDBList) ((BasicDBObject) object).get("loc");
                Double[] temp = new Double[2];
                locObj.toArray(temp);
                Point point = new Point(temp);
                pointList.add(point);
            }
            rideContrail.setContrail(pointList);
            return rideContrail;
        } catch (Exception e) {
            log.error("fail to query ride contrail", e);
            throw new SearchException("查询单车轨迹失败");
        }
    }
}
