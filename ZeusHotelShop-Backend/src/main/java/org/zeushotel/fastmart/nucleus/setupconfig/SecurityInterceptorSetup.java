package org.zeushotel.fastmart.nucleus.setupconfig;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityInterceptorSetup implements WebMvcConfigurer {
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/gateway/shopper-accounts/register-new",
                        "/gateway/shopper-accounts/authenticate",
                        "/gateway/merchandise/browse-catalog",
                        "/gateway/merchandise/details/**",
                        "/gateway/vouchers/available-list",
                        "/gateway/payment-channels/**",
                        "/doc.html",
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/webjars/**"
                );
    }
}
