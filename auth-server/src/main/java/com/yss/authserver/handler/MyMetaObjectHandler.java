package com.yss.authserver.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component // 必须加这个注解，把处理器交给 Spring 管理
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入（新增）数据时，自动执行的方法
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 自动将当前时间填充到 createTime 字段中
        // 参数说明：字段名(必须是实体类中的属性名), 填充的值, 元对象
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());

        // 如果你的表有 update_time，也可以在这里一起填充
        // this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

    /**
     * 更新（修改）数据时，自动执行的方法
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 如果你的表有 update_time 字段，就在这里自动填充更新时间
        // this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
