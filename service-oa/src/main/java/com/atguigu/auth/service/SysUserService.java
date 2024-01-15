package com.atguigu.auth.service;


import com.atguigu.model.system.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author zyl
 * @since 2023-12-16
 */
public interface SysUserService extends IService<SysUser> {

    // 更新状态
    void updateStatus(Long id, Integer status);

    // 根据用户名进行查询
    SysUser getUserByUsername(String username);
}
