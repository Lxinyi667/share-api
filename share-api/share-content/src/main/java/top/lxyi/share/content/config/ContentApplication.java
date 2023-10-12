package top.lxyi.share.content.config;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import java.util.logging.Logger;

@SpringBootApplication
@ComponentScan("top.lxyi")
@MapperScan("top.lxyi.share.*.mapper")
//添加Feign的扫包注解
@EnableFeignClients(basePackages = {"top.lxyi"})
@Slf4j
public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication app=new SpringApplication(ContentApplication.class);
        Environment env=app.run(args).getEnvironment();
        log.info("内容服务启动成功！！");
        log.info("测试地址：http:///127.0.0.1:{}{}",env.getProperty("server.port"),env.getProperty("server.servlet.context-path"));
    }
//    @Bean
//    public Logger.Level level(){
//        return Logger.Level.FULL;
//    }
}

