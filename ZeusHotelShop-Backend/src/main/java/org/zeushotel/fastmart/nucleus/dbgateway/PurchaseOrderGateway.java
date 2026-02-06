package org.zeushotel.fastmart.nucleus.dbgateway;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.zeushotel.fastmart.nucleus.dataschema.PurchaseOrderRecord;

@Mapper
public interface PurchaseOrderGateway extends BaseMapper<PurchaseOrderRecord> {
}
