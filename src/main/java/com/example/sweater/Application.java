package com.example.sweater;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
//        String res = Stream.of("online,Jedi,Master".split(",")).map(s -> s + "_").collect(Collectors.joining(","));
        SpringApplication.run(Application.class, args);
    }

}