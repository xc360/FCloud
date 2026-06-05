package com.xc.file.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * <p>http访问资源控制</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Configuration
public class CORSConfig {

    @Value("${spring.profiles.active}")
    private String active;

    /**
     * <p>解决跨域访问问题</p>
     *
     * @return 跨域配置
     */
    @Bean
    public CorsFilter corsFilterPro() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        if ("pro".equals(active)) {
            source.registerCorsConfiguration("/*/upload_file", buildCorsConfiguration());
            source.registerCorsConfiguration("/m3u8/disk/**", buildCorsConfiguration());
            source.registerCorsConfiguration("/cdn/disk/**", buildCorsConfiguration());
            source.registerCorsConfiguration("/open/disk/**", buildCorsConfiguration());
        } else {
            source.registerCorsConfiguration("/**", buildCorsConfiguration());
        }
        return new CorsFilter(source);
    }

    public static CorsConfiguration buildCorsConfiguration() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
//        corsConfiguration.addAllowedMethod(HttpMethod.OPTIONS);
//        corsConfiguration.addAllowedMethod(HttpMethod.POST);
//        corsConfiguration.addAllowedMethod(HttpMethod.GET);
//        corsConfiguration.addAllowedMethod(HttpMethod.PUT);
//        corsConfiguration.addAllowedMethod(HttpMethod.DELETE);
//        corsConfiguration.addAllowedMethod(HttpMethod.PATCH);
        corsConfiguration.addAllowedMethod("*"); // 允许提交请求的方法，*表示全部允许，也可以单独设置GET、PUT等


//        corsConfiguration.addAllowedHeader("Origin");
//        corsConfiguration.addAllowedHeader("X-Requested-With");
//        corsConfiguration.addAllowedHeader("Authorization");
//        corsConfiguration.addAllowedHeader("Content-Type");
//        corsConfiguration.addAllowedHeader("Accept");
//        corsConfiguration.addAllowedHeader("X-CSRF-TOKEN");
//        corsConfiguration.addAllowedHeader("token");
        corsConfiguration.addAllowedHeader("*"); // 允许访问的头信息,*表示全部

        corsConfiguration.addAllowedOriginPattern("*");// 允许向该服务器提交请求的URI，*表示全部允许。。这里尽量限制来源域，比如http://xxxx:8080 ,以降低安全风险。。
        corsConfiguration.setMaxAge(3601L); // 预检请求的缓存时间（秒），即在这个时间段里，对于相同的跨域请求不会再预检了
        corsConfiguration.setAllowCredentials(true); // 允许cookies跨域
        return corsConfiguration;
    }
}
