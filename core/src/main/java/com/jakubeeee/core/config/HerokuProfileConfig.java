package com.jakubeeee.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * Java spring configuration file for <b>heroku</b> profile.
 */
@Configuration
@Profile("heroku")
@PropertySource("classpath:application-heroku.properties")
public class HerokuProfileConfig {
}
