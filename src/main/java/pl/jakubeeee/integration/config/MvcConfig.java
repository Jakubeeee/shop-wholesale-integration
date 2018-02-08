package pl.jakubeeee.integration.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import pl.jakubeeee.integration.interceptors.RedirectInterceptor;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

    @Autowired
    RedirectInterceptor redirectInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(redirectInterceptor);
    }
}
