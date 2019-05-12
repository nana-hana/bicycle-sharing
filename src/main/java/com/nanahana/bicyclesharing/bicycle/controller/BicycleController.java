package com.nanahana.bicyclesharing.bicycle.controller;

import com.nanahana.bicyclesharing.bicycle.entity.Bicycle;
import com.nanahana.bicyclesharing.bicycle.entity.BicycleLocation;
import com.nanahana.bicyclesharing.bicycle.entity.Point;
import com.nanahana.bicyclesharing.bicycle.service.BicycleGeoService;
import com.nanahana.bicyclesharing.bicycle.service.BicycleService;
import com.nanahana.bicyclesharing.common.constant.Constant;
import com.nanahana.bicyclesharing.common.exception.BadCredentialException;
import com.nanahana.bicyclesharing.common.exception.BadDataException;
import com.nanahana.bicyclesharing.common.exception.SearchException;
import com.nanahana.bicyclesharing.common.response.ApiResult;
import com.nanahana.bicyclesharing.common.rest.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @Author nana
 * @Date 2019/5/12 10:13
 * @Description 单车控制类
 */
@RestController
@RequestMapping("bicycle")
@Slf4j
public class BicycleController extends BaseController {

    private final BicycleService bicycleService;
    private final BicycleGeoService bicycleGeoService;

    @Autowired
    public BicycleController(BicycleGeoService bicycleGeoService,
                             @Qualifier("bicycleServiceImpl") BicycleService bicycleService) {
        this.bicycleGeoService = bicycleGeoService;
        this.bicycleService = bicycleService;
    }

    /**
     * 生成单车
     *
     * @return 返回生成结果
     */
    @ApiIgnore
    @RequestMapping("/generateBicycle")
    public ApiResult generateBicycle() {
        ApiResult<String> resp = new ApiResult<>();
        try {
            bicycleService.generateBicycle();
            resp.setMessage("创建单车成功");
        } catch (BadDataException e) {
            resp.setCode(e.getStatusCode());
            resp.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Fail to update bicycle info", e);
            resp.setCode(Constant.RESP_STATUS_INTERNAL_ERROR);
            resp.setMessage("内部错误");
        }
        return resp;
    }

    /**
     * 查找某坐标附近单车
     *
     * @param point 所在坐标位置
     * @return 返回坐标位置附近的单车
     */
//    @ApiOperation(value = "查找附近单车", notes = "根据用户APP定位坐标来查找附近单车", httpMethod = "POST")
//    @ApiImplicitParam(name = "point", value = "用户定位坐标", required = true, dataType = "Point")
    @RequestMapping("/findAroundBicycle")
    public ApiResult findAroundBicycle(@RequestBody Point point) {
        ApiResult<List<BicycleLocation>> resp = new ApiResult<>();
        try {
            List<BicycleLocation> bicycleList = bicycleGeoService.geoNear("bicycle-position", null, point, 10, 50);
            resp.setMessage("查询附近单车成功");
            resp.setData(bicycleList);
        } catch (SearchException e) {
            resp.setCode(e.getStatusCode());
            resp.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Fail to find around bicycle info", e);
            resp.setCode(Constant.RESP_STATUS_INTERNAL_ERROR);
            resp.setMessage("内部错误");
        }
        return resp;
    }

    /**
     * 解锁单车 准备骑行
     *
     * @param bicycle 需要解锁的单车
     * @return 解锁结果
     */
//    @ApiOperation(value = "解锁单车", notes = "根据单车编号解锁单车", httpMethod = "POST")
//    @ApiImplicitParam(name = "bicycle", value = "单车编号", required = true, dataType = "Bicycle")
    @RequestMapping("/unLockBicycle")
    public ApiResult unLockBicycle(@RequestBody Bicycle bicycle) {
        ApiResult<List<BicycleLocation>> resp = new ApiResult<>();
        try {
            bicycleService.unLockBicycle(getCurrentUser(), bicycle.getNumber());
            resp.setMessage("等待单车解锁");
        } catch (BadCredentialException e) {
            resp.setCode(e.getStatusCode());
            resp.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Fail to unlock bicycle ", e);
            resp.setCode(Constant.RESP_STATUS_INTERNAL_ERROR);
            resp.setMessage("内部错误");
        }
        return resp;
    }

    /**
     * 锁车 骑行结束
     *
     * @param bicycleLocation 单车坐标
     * @return 返回锁车结果
     */
//    @ApiOperation(value = "锁定单车", notes = "骑行结束锁定单车（需要上传锁定时候定位坐标）", httpMethod = "POST")
//    @ApiImplicitParam(name = "bicycleLocation", value = "单车编号", required = true, dataType = "BicycleLocation")
    @RequestMapping("/lockBicycle")
    public ApiResult lockBicycle(@RequestBody BicycleLocation bicycleLocation) {

        ApiResult<List<BicycleLocation>> resp = new ApiResult<>();
        try {
            bicycleService.lockBicycle(bicycleLocation);
            resp.setMessage("锁车成功");
        } catch (BadDataException e) {
            resp.setCode(e.getStatusCode());
            resp.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Fail to lock bicycle", e);
            resp.setCode(Constant.RESP_STATUS_INTERNAL_ERROR);
            resp.setMessage("内部错误");
        }

        return resp;
    }

    /**
     * 单车上报坐标
     *
     * @param bicycleLocation 单车坐标
     * @return 返回上报结果
     */
//    @ApiOperation(value = "骑行轨迹上报", notes = "骑行中上报单车位置 轨迹手机卡", httpMethod = "POST")
//    @ApiImplicitParam(name = "bicycleLocation", value = "单车编号", required = true, dataType = "BicycleLocation")
    @RequestMapping("/reportLocation")
    public ApiResult reportLocation(@RequestBody BicycleLocation bicycleLocation) {
        ApiResult<List<BicycleLocation>> resp = new ApiResult<>();
        try {
            bicycleService.reportLocation(bicycleLocation);
            resp.setMessage("上报坐标成功");
        } catch (BadDataException e) {
            resp.setCode(e.getStatusCode());
            resp.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Fail to report location", e);
            resp.setCode(Constant.RESP_STATUS_INTERNAL_ERROR);
            resp.setMessage("内部错误");
        }
        return resp;
    }
}
