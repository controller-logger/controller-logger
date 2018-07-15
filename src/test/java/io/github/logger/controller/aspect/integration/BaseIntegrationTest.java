package io.github.logger.controller.aspect.integration;

import io.github.logger.controller.aspect.integration.spring_boot_application.ApiSecurityConfig;
import io.github.logger.controller.aspect.integration.spring_boot_application.BeanConfig;
import io.github.logger.controller.aspect.integration.spring_boot_application.ControllerLoggerConsumerApplication;
import io.github.logger.controller.aspect.integration.spring_boot_application.UserController;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = {
                ApiSecurityConfig.class,
                UserController.class,
                BeanConfig.class,
                ControllerLoggerConsumerApplication.class
        })
@AutoConfigureMockMvc
@EnableConfigurationProperties
abstract class BaseIntegrationTest {
}


