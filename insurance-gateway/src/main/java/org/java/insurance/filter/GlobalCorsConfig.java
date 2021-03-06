package org.java.insurance.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 *
 *
 * 处理跨域请求的过滤器
 */
@Configuration
public class GlobalCorsConfig {
    @Bean
    public CorsFilter corsFilter() {

        //1.添加CORS配置信息
        CorsConfiguration config = new CorsConfiguration();

        //1) 允许的域,不要写*，否则cookie就无法使用了-------------在此处，配置允许跨域的请求
        config.addAllowedOrigin("http://api.insurance.com");
        config.addAllowedOrigin("http://manage.insurance.com");
        config.addAllowedOrigin("http://www.insurance.com");



        //2) 是否发送Cookie信息
        config.setAllowCredentials(true);  //该配置表示， 允许跨域时，传递cookie
        //3) 允许的请求方式
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");
        // 4）允许的头信息
        config.addAllowedHeader("*");

        //5、允许时间
        config.setMaxAge(3600L);//3600秒有效

        //2.添加映射路径，我们拦截一切请求
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);

        //3.返回新的CorsFilter.
        return new CorsFilter(configSource);
    }
}