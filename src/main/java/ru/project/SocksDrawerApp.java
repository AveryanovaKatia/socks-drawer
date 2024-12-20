package ru.project;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class SocksDrawerApp {

    public static void main(String[] args) {
        SpringApplication.run(SocksDrawerApp.class, args);

        log.info("Приложение SocksDrawerApp запущено");
    }
}

