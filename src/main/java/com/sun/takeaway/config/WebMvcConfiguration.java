package com.sun.takeaway.config;

import com.sun.takeaway.utils.JacksonObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author sun
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    /**
     * 设置静态资源映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 访问 /backend、/frontend 开头的任意路径，都会被映射到类路径下的 backend 和 frontend 目录
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/frontend/**").addResourceLocations("classpath:/frontend/");
    }

    /**
     * 添加自定义的消息转换器
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 创建消息转换器对象
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        // 设置消息转换器
        converter.setObjectMapper(new JacksonObjectMapper());
        // 将该转换器添加到 MVC 中的转换器集合中
        converters.add(0, converter);
    }
}
