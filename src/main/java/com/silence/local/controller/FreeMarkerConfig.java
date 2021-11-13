package com.silence.local.controller;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.Properties;

@Configuration
public class FreeMarkerConfig {

    @Bean
    public FreeMarkerConfigurer getConfigurer() {

        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        ///Users/silence/projects/silence-codegenerator/src/main/resources/
        configurer.setTemplateLoaderPath("classpath:/codetemplate");
        configurer.setDefaultEncoding("UTF-8");
        configurer.setFreemarkerSettings(new Properties() {{
            setProperty("classic_compatible", "true");
            setProperty("defaultEncoding", "utf-8");
            setProperty("template_exception_handler", "rethrow");
        }});
        return configurer;
    }

}