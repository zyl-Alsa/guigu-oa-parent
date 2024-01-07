package com.atguigu.auth.service.impl;


import com.atguigu.auth.mapper.SysMenuMapper;
import com.atguigu.auth.service.SysMenuService;
import com.atguigu.auth.utils.MenuHelper;
import com.atguigu.common.config.exception.GuiguException;
import com.atguigu.model.system.SysMenu;
import com.atguigu.vo.system.AssginMenuVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author zyl
 * @since 2024-01-04
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    // 菜单列表接口
    @Override
    public List<SysMenu> findNodes() {
        // 1、查询所有菜单数据
        List<SysMenu> sysMenuList = baseMapper.selectList(null);

        // 2、构建树形结构（递归）
//        {
//            第一层
//              children1:[
//                            {
//                                第二层
//                                        children2：[
//                                                    {
//                                                        第三层
//                                                        .....
//                                                    }
//                                                ]
//
//                            }
//                        ]
//
//        }

        List<SysMenu> resultList =  MenuHelper.buildTree(sysMenuList);
        return resultList;
    }

    // 删除菜单
    @Override
    public void removeMenuById(Long id) {
        // 判断当前菜单是否有子菜单(下一层菜单）
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        // 判断ParentId是否等于id,
        // 等于：wrapper=1， 不等于：wrapper=0
        wrapper.eq(SysMenu::getParentId,id);
        Integer count = baseMapper.selectCount(wrapper);
        if(count > 0){
            // count > 0表示里面有子菜单，不能删除
            throw new GuiguException(201,"菜单不能删除");
        }
        // 否则count <= 0 表示里面没有子菜单，可删除
        baseMapper.deleteById(id);
    }

    // 查询所有菜单和角色分配的菜单
    @Override
    public List<SysMenu> findMenuByRoleId(Long roleId) {
        return null;
    }

    // 给角色分配权限（菜单）
    @Override
    public void doAssign(AssginMenuVo assginMenuVo) {

    }
}
