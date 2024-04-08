package com.geekplus.demo.api;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.geekplus.demo.api.listener.AfterApplicationStartupListener;

/**
 * @author wanglinlin
 * @version 1.0
 * @date 2024-04-08
 * @since geekplus-api-demo
 **/
@EnableScheduling
@SpringBootApplication
@Slf4j
public class GeekplusDemoApplication {

	public static void main(String[] args) {
		System.setProperty("spring.devtools.restart.enabled", "false");
		SpringApplication springApplication = new SpringApplication(GeekplusDemoApplication.class);
		springApplication.addListeners(new AfterApplicationStartupListener());
		springApplication.run(args);
	}

}
