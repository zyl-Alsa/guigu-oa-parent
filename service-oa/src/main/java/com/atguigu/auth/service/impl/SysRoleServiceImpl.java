package com.atguigu.auth.service.impl;

import com.atguigu.auth.mapper.SysRoleMapper;
import com.atguigu.auth.service.SysRoleService;
import com.atguigu.auth.service.SysUserRoleService;
import com.atguigu.model.system.SysRole;
import com.atguigu.model.system.SysUserRole;
import com.atguigu.vo.system.AssginRoleVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private SysUserRoleService sysUserRoleService;
    // 一、查询所有角色 和 当前用户所属角色
    @Override
    public Map<String, Object> findRoleDataByUserId(Long userId) {
        // 1、查询所有角色，返回list集合
        List<SysRole> allRoleList = baseMapper.selectList(null);

        // 2、根据userid查询角色用户关系表，查询userid对应所有角色id
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();  // 条件查询使用LambdaQueryWrapper
        wrapper.eq(SysUserRole::getUserId,userId);  // 根据userid查询
        List<SysUserRole> existUserRoleList = sysUserRoleService.list(wrapper); // 当前用户角色所有的角色id：existUserRoleList

        // 从查询出来的用户id对应角色list集合，获取所有角色id
        List<Long> existUserIdRoleList = existUserRoleList.stream().map(c ->c.getRoleId()).collect(Collectors.toList());

        // 3、根据查询所有角色id，找到对应角色信息。根据角色id到所有的角色的list集合进行比较
        List<SysRole> assignRoleList = new ArrayList<>();
        for(SysRole sysRole : allRoleList) {
            // 比较
            if(existUserIdRoleList.contains(sysRole.getId())) {
                assignRoleList.add(sysRole);
            }
        }

        // 4、把得到两部分数据封装到map集合，返回
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("assginRoleList", assignRoleList);
        roleMap.put("allRolesList", allRoleList);
        return roleMap;

    }

    // 二、为用户分配角色(要为用户id分配多个角色id）
    @Override
    public void doAssgin(AssginRoleVo assginRoleVo) {
        // 把用户之前分配角色数据删除，用户角色关系表里面，根据userid删除
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId,assginRoleVo.getRoleIdList());
        sysUserRoleService.remove((wrapper));

        // 重新进行分配
        List<Long> roleIdList = assginRoleVo.getRoleIdList();
        // 循环遍历
        for(Long roleId:roleIdList){
            // 如果角色id为空，就跳出当前循环
            if(StringUtils.isEmpty(roleId)) {
                continue;
            } else {
                // 否则就添加
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setUserId(assginRoleVo.getUserId()); // 获取了一个叫做assginRoleVo的对象中的userId属性的值，并将其赋给了sysUserRole对象的userId属性
                sysUserRole.setRoleId(roleId);
                sysUserRoleService.save(sysUserRole);
            }
        }


    }
}

