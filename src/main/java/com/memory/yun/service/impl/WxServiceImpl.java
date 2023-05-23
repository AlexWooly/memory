package com.memory.yun.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.memory.yun.enums.BizCodeEnum;
import com.memory.yun.mapper.UserMapper;
import com.memory.yun.model.*;
import com.memory.yun.request.WxProfileRequest;
import com.memory.yun.service.*;
import com.memory.yun.util.ImageUtils;
import com.memory.yun.util.JWTUtil;
import com.memory.yun.util.JsonData;
import com.memory.yun.vo.RecordVO;
import com.memory.yun.vo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author NJUPT wly
 * @Date 2021/10/5 7:00 下午
 * @Version 1.0
 */

@Service
@Slf4j
public class WxServiceImpl implements WxService {

    public static final String appId="wx46394ebb0d814a8e";

    public static final String secret="c858fc1afaea2fac697a9f829f1c134e";


    @Autowired
    UserService userService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    ImageUtils imageUtils;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // Do any additional configuration here
        return builder.build();
    }

    @Autowired
    RestTemplate restTemplate;

    @Override
    public JsonData getOpenId(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId + "&secret=" + secret + "&js_code=" + code + "&grant_type=authorization_code";
        String result = restTemplate.getForObject(url,String.class);
        if (StringUtils.isEmpty(result)) {
            log.error("result:{}",result);
            return JsonData.buildResult(BizCodeEnum.WX_GET_OPENID_FAIL);
        }

        JSONObject jsonObject = JSONObject.parseObject(result);
        String openid = jsonObject.getString("openid");

        if (openid == null || StringUtils.isEmpty(openid)){
            log.error("result:{}",result);
            return JsonData.buildResult(BizCodeEnum.WX_GET_OPENID_FAIL);
        }

        return JsonData.buildSuccess(jsonObject);
    }

    @Override
    public JsonData login(WxProfileRequest request) {
        if (request.getUserInfo() != null) {
            JsonData jsonData = getOpenId(request.getCode());
            if (jsonData.getCode() != 0) {
                return JsonData.buildResult(BizCodeEnum.WX_GET_OPENID_FAIL);
            } else {

                JSONObject jsonObject = jsonData.getData(new TypeReference<JSONObject>() {});

                String openId = jsonObject.getString("openid");

                UserDO oldDO = userMapper.selectOne(new QueryWrapper<UserDO>().eq("open_id",openId));

                UserInfo userInfo = request.getUserInfo();
                UserDO userDO = new UserDO();
                BeanUtils.copyProperties(userInfo, userDO);
                userDO.setOpenId(openId);

                if (userDO.getCity()==null){
                    userDO.setCity("0");
                }else if (userDO.getCountry()==null){
                    userDO.setCountry("0");
                }else if (userDO.getGender()==null){
                    userDO.setGender(0);
                }else if (userDO.getAvatarUrl()==null){
                    userDO.setAvatarUrl("0");
                }else if (userDO.getLanguage()==null){
                    userDO.setLanguage("0");
                }else if (userDO.getNickName()==null){
                    userDO.setNickName("0");
                }else if (userDO.getPoints()==null){
                    userDO.setPoints(0L);
                }else if (userDO.getProvince()==null){
                    userDO.setProvince("0");
                }

                LoginUser loginUser = new LoginUser();

                if (oldDO == null){
                    userMapper.insert(userDO);
                    userRegisterInitTask(userDO);
                    loginUser.setId(userDO.getId());
                }else {
                    userMapper.update(userDO,new QueryWrapper<UserDO>().eq("id",oldDO.getId()));
                    loginUser.setId(oldDO.getId());
                }

                loginUser.setOpenId(openId);
                loginUser.setHeadImg(userDO.getAvatarUrl());
                loginUser.setName(userDO.getNickName());

                String token = JWTUtil.geneJsonWebToken(loginUser);
                Long userId = loginUser.getId();
                List<String> list = new ArrayList<>();
                list.add(token);
                list.add(userId.toString());
                return JsonData.buildSuccess(list);
            }
        } else {
            return JsonData.buildResult(BizCodeEnum.WX_GET_USERINFO_FAIL);
        }
    }

    @Override
    public JsonData checkImg(MultipartFile file) {

        JsonData jsonData = getAccessToken();

        if ( jsonData.getCode() != 0){
            return JsonData.buildResult(BizCodeEnum.WX_GET_ACCESS_TOKEN);
        }

        String accessToken = getAccessToken().getData(new TypeReference<String>(){});

        String url = "https://api.weixin.qq.com/wxa/img_sec_check?access_token=" + accessToken;

        String str = imageUtils.uploadFile(url,file);

        if (str == null){
            return JsonData.buildResult(BizCodeEnum.WX_CHECK_IMG_FAIL);
        }

        JSONObject jsonObject = JSONObject.parseObject(str);
        int errcode = jsonObject.getIntValue("errcode");

        if (errcode == 0){
            return JsonData.buildSuccess(jsonObject);
        } else {
            return JsonData.buildResult(BizCodeEnum.WX_CHECK_IMG_ERROR);
        }
    }

    private JsonData getAccessToken() {
        String key = appId + ":access_token";
        String accessToken = null;

        if (redisTemplate.opsForValue().get(key) == null || redisTemplate.opsForValue().getOperations().getExpire(key) < 5) {
            String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + secret;
            String str = restTemplate.getForObject(url,String.class);

            JSONObject jsonObject = JSONObject.parseObject(str);

            if (jsonObject == null || jsonObject.getIntValue("errcode") != 0 ) {
                return JsonData.buildResult(BizCodeEnum.WX_GET_ACCESS_TOKEN);
            }

            accessToken = jsonObject.getString("access_token");
            redisTemplate.opsForValue().set(key,accessToken,60, TimeUnit.MINUTES);

        }else {
            accessToken = redisTemplate.opsForValue().get(key);
        }

        return JsonData.buildSuccess(accessToken);
    }
    @Autowired
    TeamService teamService;

    @Autowired
    LabelService labelService;

    @Autowired
    RecordService recordService;

    /**
         * 用户注册，初始化福利信息 TODO
         * @param userDO
         */
        private void userRegisterInitTask(UserDO userDO){
//            NewUserCouponRequest request = new NewUserCouponRequest();
//            request.setName(userDO.getNickName());
//            request.setUserId(userDO.getId());
//            JsonData jsonData = couponFeignService.addNewUserCoupon(request);
//        if (jsonData.getCode()!=0){
//            throw new RuntimeException("发放优惠券异常");
//        }
//            log.info("发放新用户优惠券{}，结果{}",request.toString(),jsonData.toString());
            TeamDO teamDO1 = new TeamDO();
            teamDO1.setUserId(userDO.getId());
            teamDO1.setName("学业");

            TeamDO teamDO2 = new TeamDO();
            teamDO2.setUserId(userDO.getId());
            teamDO2.setName("科研");

            TeamDO teamDO3 = new TeamDO();
            teamDO3.setUserId(userDO.getId());
            teamDO3.setName("学业");


            int res = teamService.innerAddTeam(teamDO1);
            if (res!=1){
                log.error("初始化成就组失败，用户id:{},成就组:{}",userDO.getId(),teamDO1);
            }
            res = teamService.innerAddTeam(teamDO2);
            if (res!=1){
                log.error("初始化成就组失败，用户id:{},成就组:{}",userDO.getId(),teamDO2);
            }

            res = teamService.innerAddTeam(teamDO3);
            if (res!=1){
                log.error("初始化成就组失败，用户id:{},成就组:{}",userDO.getId(),teamDO3);
            }

            LabelDO labelDO1 = new LabelDO();
            labelDO1.setUserId(userDO.getId());
            labelDO1.setColor("#8ABCD1");
            labelDO1.setTeam1(teamDO1.getId());
            labelDO1.setName("参加数学竞赛");

            LabelDO labelDO2 = new LabelDO();
            labelDO2.setUserId(userDO.getId());
            labelDO2.setColor("#8ABCD1");
            labelDO2.setTeam1(teamDO1.getId());
            labelDO2.setName("数学竞赛一等奖");

            LabelDO labelDO3 = new LabelDO();
            labelDO3.setUserId(userDO.getId());
            labelDO3.setColor("#E7C2CA");
            labelDO3.setTeam1(teamDO2.getId());
            labelDO3.setName("SCI发表论文");

            LabelDO labelDO4 = new LabelDO();
            labelDO4.setUserId(userDO.getId());
            labelDO4.setColor("#E7C2CA");
            labelDO4.setTeam1(teamDO2.getId());
            labelDO4.setName("参加计算机科学会议");

            LabelDO labelDO5 = new LabelDO();
            labelDO5.setUserId(userDO.getId());
            labelDO5.setColor("#E7C2CA");
            labelDO5.setTeam1(teamDO2.getId());
            labelDO5.setName("最佳论文奖");

            LabelDO labelDO6 = new LabelDO();
            labelDO6.setUserId(userDO.getId());
            labelDO6.setColor("#E7C2CA");
            labelDO6.setTeam1(teamDO2.getId());
            labelDO6.setName("担任课题组组长");

            LabelDO labelDO7 = new LabelDO();
            labelDO7.setUserId(userDO.getId());
            labelDO7.setColor("#8ABCD1");
            labelDO7.setTeam1(teamDO2.getId());
            labelDO7.setName("高数100分");

            LabelDO labelDO8 = new LabelDO();
            labelDO8.setUserId(userDO.getId());
            labelDO8.setColor("#8ABCD1");
            labelDO8.setTeam1(teamDO3.getId());
            labelDO8.setName("参加数竞培训");
            

            int resl = labelService.inerAddLabel(labelDO1);
            if (resl!=1){
                log.error("初始化标签失败，用户id:{},标签:{}",userDO.getId(),labelDO1);
            }

            resl = labelService.inerAddLabel(labelDO2);
            if (resl!=1){
                log.error("初始化标签失败，用户id:{},标签:{}",userDO.getId(),labelDO2);
            }

            resl = labelService.inerAddLabel(labelDO3);
            if (resl!=1){
                log.error("初始化标签失败，用户id:{},标签:{}",userDO.getId(),labelDO3);
            }

            resl = labelService.inerAddLabel(labelDO4);
            if (resl!=1){
                log.error("初始化标签失败，用户id:{},标签:{}",userDO.getId(),labelDO4);
            }

            resl = labelService.inerAddLabel(labelDO5);
            if (resl!=1){
                log.error("初始化标签失败，用户id:{},标签:{}",userDO.getId(),labelDO5);
            }

            resl = labelService.inerAddLabel(labelDO6);
            if (resl!=1){
                log.error("初始化标签失败，用户id:{},标签:{}",userDO.getId(),labelDO6);
            }

            resl = labelService.inerAddLabel(labelDO7);
            if (resl!=1){
                log.error("初始化标签失败，用户id:{},标签:{}",userDO.getId(),labelDO7);
            }

            resl = labelService.inerAddLabel(labelDO8);
            if (resl!=1){
                log.error("初始化标签失败，用户id:{},标签:{}",userDO.getId(),labelDO8);
            }

            RecordVO recordDO1 = new RecordVO();
            recordDO1.setName("参加全国大学生数学竞赛");
            recordDO1.setTeam1(labelDO1.getTeam1());
            recordDO1.setLabel(labelDO1.getId());
            recordDO1.setIntroduction("参加全国大学生数学竞赛");
            recordDO1.setEndTime(getTime(-120));


            RecordVO recordDO2 = new RecordVO();
            recordDO2.setName("全国大学生数学竞赛一等奖");
            recordDO2.setTeam1(labelDO2.getTeam1());
            recordDO2.setLabel(labelDO2.getId());
            recordDO2.setIntroduction("全国大学生数学竞赛中获得一等奖");
            recordDO2.setEndTime(getTime(-90));


            RecordVO recordDO3 = new RecordVO();
            recordDO3.setName("SCI期刊论文发表");
            recordDO3.setTeam1(labelDO3.getTeam1());
            recordDO3.setLabel(labelDO3.getId());
            recordDO3.setIntroduction("在SCI期刊上发表论文");
            recordDO3.setEndTime(getTime(-60));

            RecordVO recordDO4 = new RecordVO();
            recordDO4.setName("参加国际计算机科学会议");
            recordDO4.setTeam1(labelDO4.getTeam1());
            recordDO4.setLabel(labelDO4.getId());
            recordDO4.setIntroduction("参加国际计算机科学会议");
            recordDO4.setEndTime(getTime(-30));


            RecordVO recordDO5 = new RecordVO();
            recordDO5.setName("获得IEEE最佳论文奖");
            recordDO5.setTeam1(labelDO5.getTeam1());
            recordDO5.setLabel(labelDO5.getId());
            recordDO5.setIntroduction("获得IEEE最佳论文奖");
            recordDO5.setEndTime(getTime(-10));


            RecordVO recordDO6 = new RecordVO();
            recordDO6.setName("担任课题组组长");
            recordDO6.setTeam1(labelDO6.getTeam1());
            recordDO6.setLabel(labelDO6.getId());
            recordDO6.setIntroduction("因发表顶刊论文被学院评选为计算机组成课题组组长");
            recordDO6.setEndTime(getTime(-1));


            RecordVO recordDO7 = new RecordVO();
            recordDO7.setName("高数满分");
            recordDO7.setTeam1(labelDO7.getTeam1());
            recordDO7.setLabel(labelDO7.getId());
            recordDO7.setIntroduction("在期末考试中高数取得满分");
            recordDO7.setEndTime(getTime(30));


            RecordVO recordDO8 = new RecordVO();
            recordDO8.setName("在老师建议下进入数学竞赛队伍");
            recordDO8.setTeam1(labelDO8.getTeam1());
            recordDO8.setLabel(labelDO8.getId());
            recordDO8.setIntroduction("在老师建议下进入数学竞赛队伍");
            recordDO8.setEndTime(getTime(-4));

            int resll = recordService.init_add(recordDO1, userDO.getId());
            if (resll!=1){
                log.error("初始化record，用户id:{},记录:{}",userDO.getId(),recordDO1);
            }

            resll = recordService.init_add(recordDO2, userDO.getId());
            if (resll!=1){
                log.error("初始化record，用户id:{},记录:{}",userDO.getId(),recordDO2);
            }

            resll = recordService.init_add(recordDO3, userDO.getId());
            if (resll!=1){
                log.error("初始化record，用户id:{},记录:{}",userDO.getId(),recordDO3);
            }

            resll = recordService.init_add(recordDO4, userDO.getId());
            if (resll!=1){
                log.error("初始化record，用户id:{},记录:{}",userDO.getId(),recordDO4);
            }

            resll = recordService.init_add(recordDO5, userDO.getId());
            if (resll!=1){
                log.error("初始化record，用户id:{},记录:{}",userDO.getId(),recordDO5);
            }
            resll = recordService.init_add(recordDO6, userDO.getId());
            if (resll!=1){
                log.error("初始化record，用户id:{},记录:{}",userDO.getId(),recordDO6);
            }
            resll = recordService.init_add(recordDO7, userDO.getId());
            if (resll!=1){
                log.error("初始化record，用户id:{},记录:{}",userDO.getId(),recordDO7);
            }
            resll = recordService.init_add(recordDO8, userDO.getId());
            if (resll!=1){
                log.error("初始化record，用户id:{},记录:{}",userDO.getId(),recordDO8);
            }

        }

        private Date getTime(int d){
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, d); //得到前一天
            Date date = calendar.getTime();
            return date;
        }

}
