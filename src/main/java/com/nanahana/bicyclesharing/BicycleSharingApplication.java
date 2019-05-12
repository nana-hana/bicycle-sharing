package com.nanahana.bicyclesharing;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Author nana
 * @Date 2019/5/8 10:29
 * @Description SpringBoot主函数
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.nanahana.**")
@PropertySource(value = "classpath:parameter.properties")
public class BicycleSharingApplication {
    public static void main(String[] args) {
        SpringApplication.run(BicycleSharingApplication.class, args);
    }

    /**
     * 替换自带的jackson使用fastJson
     *
     * @return HttpMessageConverters
     */
    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        HttpMessageConverter<?> httpMessageConverter = new FastJsonHttpMessageConverter();
        return new HttpMessageConverters(httpMessageConverter);
    }

    /**
     * 用于解析properties文件的占位符
     *
     * @return 返回解析对象
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
