package org.zeushotel.fastmart.nucleus.setupconfig;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DatabaseRecordTimeStampHandler implements MetaObjectHandler {
    
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime currentMoment = LocalDateTime.now();
        
        this.strictInsertFill(metaObject, "recordCreatedTime", LocalDateTime.class, currentMoment);
        this.strictInsertFill(metaObject, "recordUpdatedTime", LocalDateTime.class, currentMoment);
    }
    
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "recordUpdatedTime", LocalDateTime.class, LocalDateTime.now());
    }
}
