package com.atguigu.model.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
public class BaseEntity implements Serializable {

    @TableId(type = IdType.AUTO)
    // IdType.AUTO：主键自动增加（AUTO：可能表示自动化或自动处理的选项。ASSIGN_ID：可能表示分配一个唯一的标识符或 ID 的选项。）
    private Long id;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    @TableLogic  //  进行逻辑删除
    @TableField("is_deleted")
    private Integer isDeleted;

    // 表中可以没有指定的字段。例如：isDeleted
    @TableField(exist = false)
    private Map<String,Object> param = new HashMap<>();
}
