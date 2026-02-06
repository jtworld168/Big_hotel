package org.zeushotel.fastmart.nucleus.dbgateway;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.zeushotel.fastmart.nucleus.dataschema.ShopperVoucherClaim;

@Mapper
public interface ShopperVoucherClaimGateway extends BaseMapper<ShopperVoucherClaim> {
}
