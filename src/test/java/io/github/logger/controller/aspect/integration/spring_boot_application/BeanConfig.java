package io.github.logger.controller.aspect.integration.spring_boot_application;

import io.github.logger.controller.aspect.GenericControllerAspect;
import io.github.logger.controller.aspect.integration.IntegrationTest;
import io.github.logger.controller.utils.JsonUtil;
import io.github.logger.controller.utils.RequestUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan("io.github.logger")
public class BeanConfig {
    @Bean
    public GenericControllerAspect genericControllerAspect() {
        return new GenericControllerAspect(IntegrationTest.logger, new JsonUtil(), new RequestUtil());
    }
}
