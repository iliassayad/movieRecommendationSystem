package net.ayad.moviebffservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MovieBffServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieBffServiceApplication.class, args);
    }

}
