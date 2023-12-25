package com.atguigu.auth.controller;

import com.atguigu.auth.service.SysRoleService;
import com.atguigu.common.config.exception.GuiguException;
import com.atguigu.common.result.Result;
import com.atguigu.model.system.SysRole;
import com.atguigu.vo.system.AssginRoleVo;
import com.atguigu.vo.system.SysRoleQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "角色管理接口")
@RestController   // @RestController 声明了一个RESTful风格的控制器，这意味着控制器的返回值将自动转换为HTTP响应体
@RequestMapping("/admin/system/sysRole")   // 当某个请求到达了这个映射路径时，Spring Framework就会调用与该路径匹配的控制器操作
public class SysRoleController {
    // http://localhost:8800/admin/system/sysRole/findAll
    // 注入service
    @Autowired
    private SysRoleService sysRoleService;

    // 1、查询所有角色 和 当前用户所属角色
    @ApiOperation("获取角色")
    @GetMapping("/toAssign/{userId}")
    public Result toAssign(@PathVariable Long userId) {
        Map<String,Object> map = sysRoleService.findRoleDataByUserId(userId);
        return Result.ok(map);
    }

    // 2、为用户分配角色(要为用户id分配多个角色id）
    @ApiOperation("为用户分配角色")
    @PostMapping("/doAssign")
    public Result doAssign(@RequestBody AssginRoleVo assginRoleVo) {
        sysRoleService.doAssgin(assginRoleVo);
        return  Result.ok();
    }



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

        // 模拟异常效果
        try {
            int i = 10/0;
        } catch (Exception e){
            // 抛出异常
            throw new GuiguException(20001,"执行了自定义异常处理....");
        }

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
        String roleName = sysRoleQueryVo.getRoleName();
        if(!StringUtils.isEmpty(roleName)) {
            // 封装
            wrapper.like(SysRole::getRoleName,roleName);
        }
        // 3、调用方法实现
        IPage<SysRole> pageModel = sysRoleService.page(pageParam,wrapper);
//        pageModel.getRecords()
        return Result.ok(pageModel);
    }

    // 添加角色
    @ApiOperation("添加角色")
    @PostMapping("save")
    public Result save(@RequestBody SysRole role) {
        // 调用service的方法
        boolean is_success = sysRoleService.save(role);
        if(is_success) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    // 修改角色--根据id查询
    @ApiOperation("根据id查询")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        SysRole sysRole = sysRoleService.getById(id);
        return Result.ok(sysRole);
    }

    // 修改角色--最终修改
    @ApiOperation("修改角色")
    @PutMapping("update")
    public Result update(@RequestBody SysRole role) {
        // 调用service的方法
        boolean is_success = sysRoleService.updateById(role);
        if(is_success) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    // 根据id删除
    @ApiOperation("根据id删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        // 调用service的方法
        boolean is_success = sysRoleService.removeById(id);
        if(is_success) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    // 批量删除（根据多个id）
    // 前端数组 [1,2,3]
    @ApiOperation("批量删除")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        boolean is_success = sysRoleService.removeByIds(idList);
        if(is_success) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

}
