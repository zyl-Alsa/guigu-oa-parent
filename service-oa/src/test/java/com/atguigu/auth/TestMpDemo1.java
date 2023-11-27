package com.atguigu.auth;

import com.atguigu.auth.mapper.SysRoleMapper;
import com.atguigu.model.system.SysRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class TestMpDemo1 {

    // 注入
    @Autowired
    private SysRoleMapper mapper;

    // 查询所有记录
    @Test
    public void getAll(){
        List<SysRole> list = mapper.selectList(null);
        System.out.println(list);
    }

    // 添加操作
    @Test
    public void add() {
        SysRole sysRole = new SysRole();
        // 新增的内容
        sysRole.setRoleName("角色管理员1");
        sysRole.setRoleCode("role1");
        sysRole.setDescription("角色管理员1");
        // 数据库操作，提交新增的东西。如果新增一条记录了，就返回1，新增2条记录，就返回2
        int rows = mapper.insert(sysRole);
        System.out.println(rows);
        System.out.println(sysRole.getId());


    }

    // 修改操作
    @Test
    public void update(){
        // 根据id查询
        SysRole role = mapper.selectById(1);
        // 设置修改值
        role.setRoleName("atguigu角色管理员");
        // 调用方法实现最终修改
        int rows = mapper.updateById(role);
        System.out.println(rows);
    }

    // 删除操作
    @Test
    public void deleteId(){
        int rows = mapper.deleteById(2);
    }
}
