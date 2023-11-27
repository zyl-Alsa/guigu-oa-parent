package com.atguigu.auth;

import com.atguigu.auth.mapper.SysRoleMapper;
import com.atguigu.model.system.SysRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

//增删改查

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

    // 批量删除
    @Test
    public void testDeleteBatchIds() {
        // 删除的记录ID为1和2。
        int result = mapper.deleteBatchIds(Arrays.asList(1, 2));
        System.out.println(result);
    }

    // 条件查询(MyBatis-Plus条件构造器)
    @Test
    public void testQuery1() {
        // 创建QueryWrapper对象，调用方法封装条件
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        // eq表示等于，表示查询role_name字段值为"总经理"的记录
        wrapper.eq("role_name","总经理");
        // 调用mapper对象的selectList方法，将wrapper对象作为参数传递给该方法。该方法会根据提供的查询条件，从数据库中查询符合条件的记录，并将结果返回给list变量。
        List<SysRole> list = mapper.selectList(wrapper);
        System.out.println(wrapper);
    }

    @Test
    public void testQuery2() {
        // 创建LambdaQueryWrapper 对象，调用方法封装条件
        LambdaQueryWrapper <SysRole> wrapper = new LambdaQueryWrapper<>();
        // eq表示等于，SysRole::getRoleName是Lambda表达式，表示获取SysRole对象的roleName字段的值，"总经理"是要匹配的值
        wrapper.eq(SysRole::getRoleName,"总经理");
        // 调用mapper对象的selectList方法，将wrapper对象作为参数传递给该方法。该方法会根据提供的查询条件，从数据库中查询符合条件的记录，并将结果返回给list变量。
        List<SysRole> list = mapper.selectList(wrapper);
        System.out.println(wrapper);
    }
}
