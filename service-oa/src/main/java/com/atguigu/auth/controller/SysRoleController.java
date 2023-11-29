package com.atguigu.auth.controller;

import com.atguigu.auth.service.SysRoleService;
import com.atguigu.common.result.Result;
import com.atguigu.model.system.SysRole;
import com.atguigu.vo.system.SysRoleQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "角色管理接口")
@RestController   // @RestController 声明了一个RESTful风格的控制器，这意味着控制器的返回值将自动转换为HTTP响应体
@RequestMapping("/admin/system/sysRole")   // 当某个请求到达了这个映射路径时，Spring Framework就会调用与该路径匹配的控制器操作
public class SysRoleController {
    // http://localhost:8800/admin/system/sysRole/findAll
    // 注入service
    @Autowired
    private SysRoleService sysRoleService;

    // 查询所有角色
//    @GetMapping("/findAll")
//    public List<SysRole> findAll() {
//        // 调用service的方法
//        List<SysRole> list = sysRoleService.list();
//        return list;
//    }


    // 统一返回数据结果
    @ApiOperation("查询所有角色")
    @GetMapping("/findAll")
    public Result findAll() {
        // 调用service的方法
        List<SysRole> list = sysRoleService.list();
        return Result.ok(list);
    }

    // 条件分页查询
    // page：当前页 ，   limit：每页显示记录数
    // SysRoleQueryVo: 条件对象
    @ApiOperation("条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result pageQueryRole(@PathVariable Long page,
                                @PathVariable Long limit,
                                SysRoleQueryVo sysRoleQueryVo) {
    // 调用service的方法实现
        // 1、创建page对象，传递分页相关参数
        Page<SysRole> pageParam = new Page<>(page,limit);

        // 2、封装条件，判断条件是否为空，不为空进行封装
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        String roleName = SysRoleQueryVo.getRoleName();
        if(!StringUtils.isEmpty(roleName)) {
            // 封装
            wrapper.like(SysRole::getRoleName,roleName);
        }
        // 调用方法实现
        IPage<SysRole> pageModel = sysRoleService.page(pageParam,wrapper);
        return Result.ok();
    }
}
