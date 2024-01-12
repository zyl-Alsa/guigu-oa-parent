package com.atguigu.auth.service.impl;

import com.atguigu.auth.service.SysRoleMenuService;
import com.atguigu.auth.utils.MenuHelper;
import com.atguigu.common.config.exception.GuiguException;
import com.atguigu.model.system.SysMenu;
import com.atguigu.auth.mapper.SysMenuMapper;
import com.atguigu.auth.service.SysMenuService;
import com.atguigu.model.system.SysRoleMenu;
import com.atguigu.vo.system.AssginMenuVo;
import com.atguigu.vo.system.MetaVo;
import com.atguigu.vo.system.RouterVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private SysRoleMenuService sysRoleMenuService;
    //菜单列表接口
    @Override
    public List<SysMenu> findNodes() {

        //1 查询所有菜单数据
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
        if(count > 0) {
            // count > 0表示里面有子菜单，不能删除
            throw new GuiguException(201,"菜单不能删除");
        }
        // 否则count <= 0 表示里面没有子菜单，可删除
        baseMapper.deleteById(id);
    }

    //查询所有菜单和角色分配的菜单
    @Override
    public List<SysMenu> findMenuByRoleId(Long roleId) {
        //1 全部权限列表：查询所有菜单 - 添加条件 status=1（表示可用）
        LambdaQueryWrapper<SysMenu> wrapperSysMenu = new LambdaQueryWrapper<>();
        wrapperSysMenu.eq(SysMenu::getStatus,1);
        List<SysMenu> allSysMenuList = baseMapper.selectList(wrapperSysMenu);

        //2 根据角色id获取角色权限：根据 roleId 查询角色菜单关系表里面：角色id对应所有的菜单id
        LambdaQueryWrapper<SysRoleMenu> wrapperSysRoleMenu = new LambdaQueryWrapper<>();
        wrapperSysRoleMenu.eq(SysRoleMenu::getRoleId,roleId);
        List<SysRoleMenu> sysRoleMenuList = sysRoleMenuService.list(wrapperSysRoleMenu);

        //3 转换给角色id与角色权限对应Map对象：根据菜单id，获取对应菜单对象
        List<Long> menuIdList = sysRoleMenuList.stream().map(c -> c.getMenuId()).collect(Collectors.toList());

        //3.1 拿着菜单id 和所有菜单集合里面id进行比较，如果相同封装
        allSysMenuList.stream().forEach(item -> {
            if(menuIdList.contains(item.getId())) {
                item.setSelect(true);
            } else {
                item.setSelect(false);
            }
        });

        //4 返回规定树形显示格式菜单列表
        List<SysMenu> sysMenuList = MenuHelper.buildTree(allSysMenuList);
        return sysMenuList;
    }

    //为角色分配菜单（先删除已经存在的角色，再分配角色）
    @Override
    public void doAssign(AssginMenuVo assginMenuVo) {
        //1 根据 角色id 来删除 菜单角色表 中之前 分配的数据
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId,assginMenuVo.getRoleId());
        sysRoleMenuService.remove(wrapper);

        //2 从参数里面获取角色新分配菜单id列表，
        // 进行遍历，把每个id数据添加菜单角色表
        List<Long> menuIdList = assginMenuVo.getMenuIdList();
        for(Long menuId:menuIdList) {
            // 判断当前菜单ID是否为空
            if(StringUtils.isEmpty(menuId)) {
                continue;
            }
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setMenuId(menuId); // 设置 sysRoleMenu 的 menuId 为当前菜单ID
            sysRoleMenu.setRoleId(assginMenuVo.getRoleId()); // 从 assginMenuVo 中获取角色ID，并设置 sysRoleMenu 的 roleId 为该角色ID
            sysRoleMenuService.save(sysRoleMenu);
        }
    }

    //4 根据用户id获取用户可以操作菜单列表
    @Override
    public List<RouterVo> findUserMenuListByUserId(Long userId) {
        List<SysMenu> sysMenuList = null;

        //1 判断当前用户是否是管理员   现在设置userId=1是管理员
        //1.1 如果是管理员，可以查询所有菜单列表
        if(userId.longValue() == 1) {
            //查询所有菜单列表
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus,1);
            wrapper.orderByAsc(SysMenu::getSortValue);  // orderByAsc:升序  orderByDesc：降序
            sysMenuList = baseMapper.selectList(wrapper);
        } else {
            //1.2 如果不是管理员，根据userId查询可以操作菜单列表
            //多表关联查询：用户角色关系表 、 角色菜单关系表、 菜单表
            sysMenuList = baseMapper.findMenuListByUserId(userId);
        }

        //2 把查询出来数据列表-构建成框架要求的路由结构
        //使用菜单操作工具类构建树形结构
        List<SysMenu> sysMenuTreeList = MenuHelper.buildTree(sysMenuList);
        //构建成框架要求的路由结构
        List<RouterVo> routerList = this.buildRouter(sysMenuTreeList);
        return routerList;
    }

    //构建成框架要求的路由结构
    private List<RouterVo> buildRouter(List<SysMenu> menus) {
        //创建list集合，存储最终数据
        List<RouterVo> routers = new ArrayList<>();
        //menus遍历
        for(SysMenu menu : menus) {
            RouterVo router = new RouterVo();
            router.setHidden(false);
            router.setAlwaysShow(false);
            router.setPath(getRouterPath(menu));
            router.setComponent(menu.getComponent());
            router.setMeta(new MetaVo(menu.getName(), menu.getIcon()));
            //下一层数据部分
            List<SysMenu> children = menu.getChildren();
            if(menu.getType().intValue() == 1) {
                //加载出来下面隐藏路由 filter:过滤
                List<SysMenu> hiddenMenuList = children.stream()
                        .filter(item -> !StringUtils.isEmpty(item.getComponent()))
                        .collect(Collectors.toList());
                for(SysMenu hiddenMenu : hiddenMenuList) {
                    RouterVo hiddenRouter = new RouterVo();
                    //true 隐藏路由
                    hiddenRouter.setHidden(true);
                    hiddenRouter.setAlwaysShow(false);
                    hiddenRouter.setPath(getRouterPath(hiddenMenu));
                    hiddenRouter.setComponent(hiddenMenu.getComponent());
                    hiddenRouter.setMeta(new MetaVo(hiddenMenu.getName(), hiddenMenu.getIcon()));

                    routers.add(hiddenRouter);
                }

            } else {
                if(!CollectionUtils.isEmpty(children)) {
                    if(children.size() > 0) {
                        router.setAlwaysShow(true);
                    }
                    //递归
                    router.setChildren(buildRouter(children));
                }
            }
            routers.add(router);
        }

//        System.out.println("******************");
//        System.out.println(routers);

        return routers;
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysMenu menu) {
        String routerPath = "/" + menu.getPath();
        if(menu.getParentId().intValue() != 0) {
            routerPath = menu.getPath();
        }
        return routerPath;
    }

    //5 根据用户id获取用户可以操作按钮列表
    @Override
    public List<String> findUserPermsByUserId(Long userId) {
        //1 判断是否是管理员，如果是管理员，查询所有按钮列表
//        System.out.println("******************");
        List<SysMenu> sysMenuList = null;
        if(userId.longValue() == 1) {
            //查询所有菜单列表
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus,1);
            sysMenuList = baseMapper.selectList(wrapper);
        } else {
            //2 如果不是管理员，根据userId查询可以操作按钮列表
            //多表关联查询：用户角色关系表 、 角色菜单关系表、 菜单表

            sysMenuList = baseMapper.findMenuListByUserId(userId);
        }

        //3 从查询出来的数据里面，获取可以操作按钮值的list集合，返回
        // type = 2 的时候才有perms（按钮）
        List<String> permsList = sysMenuList.stream()
                .filter(item -> item.getType() == 2)
                .map(item -> item.getPerms())
                .collect(Collectors.toList());

        return permsList;
    }
}
