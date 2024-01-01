package com.atguigu.auth;

import com.atguigu.auth.mapper.SysRoleMapper;
import com.atguigu.auth.service.SysRoleService;
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
public class TestMpDemo2 {

    // 注入
    @Autowired
    private SysRoleService service; // 定义一个SysRoleService类型的成员变量service，并使用@Autowired注解进行注入

    // 查询所有记录
    @Test
    public void getAll(){
        // 调用service的list（）方法来实现查询所有记录的功能，将查询的结果保存到list变量中
        List<SysRole> list = service.list();
        System.out.println(list);
    }


}
