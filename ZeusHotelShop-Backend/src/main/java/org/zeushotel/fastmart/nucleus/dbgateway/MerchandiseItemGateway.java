package org.zeushotel.fastmart.nucleus.dbgateway;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.zeushotel.fastmart.nucleus.dataschema.MerchandiseItem;

@Mapper
public interface MerchandiseItemGateway extends BaseMapper<MerchandiseItem> {
}
