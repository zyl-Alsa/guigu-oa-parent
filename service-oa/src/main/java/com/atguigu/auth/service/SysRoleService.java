package com.atguigu.auth.service;

import com.atguigu.model.system.SysRole;
import com.atguigu.vo.system.AssginRoleVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

// 第一步：创建实体类接口
public interface SysRoleService extends IService<SysRole> {
    // 1、查询所有角色 和 当前用户所属角色
    Map<String, Object> findRoleDataByUserId(Long userId);

    // 2、为用户分配角色(要为用户id分配多个角色id）
    void doAssgin(AssginRoleVo assginRoleVo);


}
