package com.nanahana.bicyclesharing.record.controller;

import com.nanahana.bicyclesharing.bicycle.service.BicycleGeoService;
import com.nanahana.bicyclesharing.common.constant.Constant;
import com.nanahana.bicyclesharing.common.exception.BadDataException;
import com.nanahana.bicyclesharing.common.exception.SearchException;
import com.nanahana.bicyclesharing.common.response.ApiResult;
import com.nanahana.bicyclesharing.common.rest.BaseController;
import com.nanahana.bicyclesharing.record.entity.RideContrail;
import com.nanahana.bicyclesharing.record.entity.RideRecord;
import com.nanahana.bicyclesharing.record.service.RideRecordService;
import com.nanahana.bicyclesharing.user.entity.UserElement;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author nana
 * @Date 2019/5/12 19:53
 * @Description 骑行记录Controller类
 */
@RestController
@RequestMapping("rideRecord")
@Slf4j
public class RideRecordController extends BaseController {

    private final RideRecordService rideRecordService;
    private final BicycleGeoService bicycleGeoService;

    @Autowired
    public RideRecordController(@Qualifier("rideRecordServiceImpl") RideRecordService rideRecordService,
                                BicycleGeoService bicycleGeoService) {
        this.rideRecordService = rideRecordService;
        this.bicycleGeoService = bicycleGeoService;
    }

    /**
     * 查询骑行历史
     *
     * @param lastId 最后一条数据id
     * @return 返回查询记录
     */
//    @ApiOperation(value = "骑行历史", notes = "骑行历史分页 每次传递用户界面最后一条数据的ID 下拉时候调用 每次十条数据", httpMethod = "GET")
    @ApiImplicitParam(name = "id", value = "最后一条数据ID", required = true, dataType = "Long")
    @RequestMapping("/list/{id}")
    public ApiResult<List<RideRecord>> listRideRecord(@PathVariable("id") Long lastId) {
        ApiResult<List<RideRecord>> resp = new ApiResult<>();
        try {
            UserElement userElement = getCurrentUser();
            List<RideRecord> list = rideRecordService.listRideRecord(userElement.getUserId(), lastId);
            resp.setData(list);
            resp.setMessage("查询成功");
        } catch (BadDataException e) {
            resp.setCode(e.getStatusCode());
            resp.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Fail to query ride record ", e);
            resp.setCode(Constant.RESP_STATUS_INTERNAL_ERROR);
            resp.setMessage("内部错误");
        }
        return resp;
    }

    /**
     * 查询骑行轨迹
     *
     * @param recordNo 订单号
     * @return 返回骑行轨迹
     */
    @ApiOperation(value = "骑行轨迹查询", notes = "骑行轨迹查询", httpMethod = "GET")
    @ApiImplicitParam(name = "recordNo", value = "骑行历史记录号", required = true, dataType = "Long")
    @RequestMapping("/contrail/{recordNo}")
    public ApiResult<RideContrail> rideContrail(@PathVariable("recordNo") String recordNo) {
        ApiResult<RideContrail> resp = new ApiResult<>();
        try {
            RideContrail contrail = bicycleGeoService.rideContrail("ride_contrail", recordNo);
            resp.setData(contrail);
            resp.setMessage("查询成功");
        } catch (SearchException e) {
            resp.setCode(e.getStatusCode());
            resp.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Fail to query ride record ", e);
            resp.setCode(Constant.RESP_STATUS_INTERNAL_ERROR);
            resp.setMessage("内部错误");
        }
        return resp;
    }
}
