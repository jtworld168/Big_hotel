package org.zeushotel.fastmart.nucleus.webportal;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.zeushotel.fastmart.nucleus.bizcore.MerchandiseCatalogOrchestrator;
import org.zeushotel.fastmart.nucleus.dataschema.MerchandiseItem;
import org.zeushotel.fastmart.nucleus.viewresponse.UnifiedApiResponse;
import org.zeushotel.fastmart.nucleus.viewresponse.MerchandiseDisplayVo;

@Tag(name = "商品目录接入点")
@RestController
@RequestMapping("/gateway/merchandise")
@RequiredArgsConstructor
public class MerchandiseCatalogEndpoint {
    
    private final MerchandiseCatalogOrchestrator orchestrator;
    
    @Operation(summary = "浏览商品目录")
    @GetMapping("/browse-catalog")
    public UnifiedApiResponse<Page<MerchandiseDisplayVo>> browseCatalog(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String categoryLabel) {
        Long shopperId = StpUtil.isLogin() ? StpUtil.getLoginIdAsLong() : null;
        return orchestrator.browseMerchandise(pageNum, pageSize, categoryLabel, shopperId);
    }
    
    @Operation(summary = "查看商品详细信息")
    @GetMapping("/details/{itemId}")
    public UnifiedApiResponse<MerchandiseDisplayVo> viewItemDetails(@PathVariable Long itemId) {
        Long shopperId = StpUtil.isLogin() ? StpUtil.getLoginIdAsLong() : null;
        return orchestrator.fetchMerchandiseDetails(itemId, shopperId);
    }
    
    @Operation(summary = "添加新商品（管理员）")
    @PostMapping("/admin/add-new")
    public UnifiedApiResponse<String> addNewItem(@RequestBody MerchandiseItem item) {
        return orchestrator.addNewMerchandise(item);
    }
    
    @Operation(summary = "修改商品（管理员）")
    @PutMapping("/admin/modify/{itemId}")
    public UnifiedApiResponse<String> modifyItem(@PathVariable Long itemId, @RequestBody MerchandiseItem updateData) {
        return orchestrator.modifyMerchandise(itemId, updateData);
    }
}
