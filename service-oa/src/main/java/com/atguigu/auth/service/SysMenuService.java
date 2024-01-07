package com.atguigu.auth.service;


import com.atguigu.model.system.SysMenu;
import com.atguigu.vo.system.AssginMenuVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author zyl
 * @since 2024-01-04
 */
public interface SysMenuService extends IService<SysMenu> {

    // 菜单列表接口
    List<SysMenu> findNodes();

    // 删除菜单
    void removeMenuById(Long id);

    // 查询所有菜单和角色分配的菜单
    List<SysMenu> findMenuByRoleId(Long roleId);

    // 给角色分配权限（菜单）
    void doAssign(AssginMenuVo assginMenuVo);
}
