package com.atguigu.auth.controller;

import com.atguigu.auth.service.SysUserService;
import com.atguigu.common.config.exception.GuiguException;
import com.atguigu.common.jwt.JwtHelper;
import com.atguigu.common.result.Result;
import com.atguigu.common.utils.MD5;
import com.atguigu.model.system.SysUser;
import com.atguigu.vo.system.LoginVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Api(tags = "后台登录管理")
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 登录
     * @return
     */
    //login
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo) {
        // {"code":200,"data":{"token":"admin-token"}}
        // 这些是登录时返回固定的值
//        Map<String, Object> map = new HashMap<>();
//        map.put("token","admin-token");
//        return Result.ok(map);


        // 1、获取输入用户名和密码
        String username = loginVo.getUsername();
        // 2、根据用户名查询数据库
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername,username); // 设置查询条件为用户名等于传入的 username
        SysUser sysUser = sysUserService.getOne(wrapper); // getOne()返回满足查询条件的一条用户记录，如果查询到多条记录则返回第一条，如果未查询到则返回 null

        // 3、用户信息是否存在
        if(sysUser == null) {
            throw  new GuiguException(201,"用户不存在");
        }

        // 4、判断密码
        // 数据库中存的密码（MD5加密后的）
        String password_db = sysUser.getPassword();
        // 获取输入密码(把输入的密码也进行加密）
        String password_input = MD5.encrypt(loginVo.getPassword());

        if(!password_db.equals(password_db)){
            // 如果2个密码不相同，则抛出异常
            throw new GuiguException(201,"密码错误");
        }

        // 5、判断用户是否被禁用(1可用，0禁用）
        if(sysUser.getStatus().intValue() == 0) {
            throw new GuiguException(201,"用户已经被禁用");
        }

        // 6、使用jwt根据用户id和用户名称生成token字符串
        String token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());

        // 7、返回
        Map<String,Object> map = new HashMap<>();
        map.put("token",token);
        return Result.ok(map);
    }

    /**
     * 获取用户信息
     * @return
     */
    //info
    @GetMapping("info")
    public Result info() {
        Map<String, Object> map = new HashMap<>();
        map.put("roles","[admin]");
        map.put("name","admin");
        map.put("avatar","https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
        return Result.ok(map);
    }

    /**
     * 退出
     * @return
     */
    @PostMapping("logout")
    public Result logout(){
        return Result.ok();
    }
}
