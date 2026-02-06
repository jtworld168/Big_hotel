package org.zeushotel.fastmart.nucleus.setupconfig;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiDocumentationSetup {
    
    @Bean
    public OpenAPI customOpenApiConfiguration() {
        return new OpenAPI()
                .info(new Info()
                        .title("宙斯大酒店快购商城后台系统")
                        .version("v2.8.0")
                        .description("酒店大堂无人便利店智能化管理平台API文档")
                        .contact(new Contact()
                                .name("Zeus Hotel Technology Team")
                                .email("tech@zeushotel.org")));
    }
}
