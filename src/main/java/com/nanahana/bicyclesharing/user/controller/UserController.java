package com.nanahana.bicyclesharing.user.controller;

import com.nanahana.bicyclesharing.common.constant.Constant;
import com.nanahana.bicyclesharing.common.exception.BadDataException;
import com.nanahana.bicyclesharing.common.response.ApiResult;
import com.nanahana.bicyclesharing.common.rest.BaseController;
import com.nanahana.bicyclesharing.user.entity.LoginInfo;
import com.nanahana.bicyclesharing.user.entity.User;
import com.nanahana.bicyclesharing.user.entity.UserElement;
import com.nanahana.bicyclesharing.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author nana
 * @Date 2019/5/8 10:41
 * @Description 用户Controller类
 */
@RestController
@RequestMapping("user")
@Slf4j
public class UserController extends BaseController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户登录
     *
     * @param loginInfo 加密过的登陆信息
     * @return 返回登录结果
     */
    @RequestMapping(value = "login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResult<String> login(@RequestBody LoginInfo loginInfo) {
        ApiResult<String> apiResult = new ApiResult<>();
        try {
            String data = loginInfo.getData();
            String key = loginInfo.getKey();
            if (StringUtils.isBlank(data) || StringUtils.isBlank(key)) {
                throw new BadDataException("参数校验失败。");
            }
            String token = userService.login(data, key);
            apiResult.setData(token);
        } catch (BadDataException badDataException) {
            apiResult.setCode(Constant.RESP_STATUS_BADREQUEST);
            apiResult.setMessage(badDataException.getMessage());
        } catch (Exception e) {
            log.error("登录失败", e);
            apiResult.setCode(Constant.RESP_STATUS_INTERNAL_ERROR);
            apiResult.setMessage("服务器内部错误。");
        }
        return apiResult;
    }

    /**
     * 修改用户昵称
     *
     * @param user 需要修改的用户信息
     * @return 返回修改结果
     */
    @RequestMapping(value = "modifyNickname", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResult<String> modifyNickname(@RequestBody User user) {
        ApiResult<String> apiResult = new ApiResult<>();
        try {
            UserElement currentUser = getCurrentUser();
            user.setId(currentUser.getUserId());
            userService.modifyNickname(user);
        } catch (Exception e) {
            log.error("登录失败", e);
            apiResult.setCode(Constant.RESP_STATUS_INTERNAL_ERROR);
            apiResult.setMessage("服务器内部错误。");
        }
        return apiResult;
    }

    /**
     * 发送验证码
     *
     * @param user               需要发送的用户
     * @param httpServletRequest request
     * @return 返回发送结果
     */
    @RequestMapping("sendVercode")
    public ApiResult<String> sendVercode(@RequestBody User user, HttpServletRequest httpServletRequest) {
        ApiResult<String> apiResult = new ApiResult<>();
        try {
            userService.sendVercode(user.getMobile(), getIpFromRequest(httpServletRequest));
        } catch (Exception e) {
            log.error("发送验证码失败", e);
            apiResult.setCode(Constant.RESP_STATUS_INTERNAL_ERROR);
            apiResult.setMessage("服务器内部错误。");
        }
        return apiResult;
    }

    /**
     * 修改头像
     *
     * @param httpServletRequest 请求的用户request
     * @param file               需要修改的头像
     * @return 返回修改结果
     */
    @ApiOperation(value = "上传头像", notes = "用户上传头像 file", httpMethod = "POST")
    @RequestMapping(value = "/uploadHeadImg", method = RequestMethod.POST)
    public ApiResult<String> uploadHeadImg(HttpServletRequest httpServletRequest,
                                           @RequestParam(required = false) MultipartFile file) {
        ApiResult<String> resp = new ApiResult<>();
        try {
            UserElement userElement = getCurrentUser();
            userService.uploadHeadImg(file, userElement.getUserId());
            resp.setMessage("上传成功");
        } catch (BadDataException e) {
            resp.setCode(e.getStatusCode());
            resp.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Fail to update user info", e);
            resp.setCode(Constant.RESP_STATUS_INTERNAL_ERROR);
            resp.setMessage("内部错误");
        }
        return resp;
    }
}
