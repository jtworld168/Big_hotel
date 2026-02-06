package org.zeushotel.fastmart.nucleus.dbgateway;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.zeushotel.fastmart.nucleus.dataschema.ShoppingBasketItem;

@Mapper
public interface ShoppingBasketGateway extends BaseMapper<ShoppingBasketItem> {
}
