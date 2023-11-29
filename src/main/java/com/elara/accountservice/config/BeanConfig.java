package com.elara.accountservice.config;

import com.elara.accountservice.util.PasswordEncoder;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class BeanConfig {

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

  @Bean
  public Gson gson() {
    return new Gson();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new PasswordEncoder();
  }
}
