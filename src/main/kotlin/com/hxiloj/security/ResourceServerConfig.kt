package com.hxiloj.security

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler
import org.springframework.web.cors.CorsConfiguration
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


@Configuration
@EnableResourceServer
class ResourceServerConfig : ResourceServerConfigurerAdapter() {
    override fun configure(resources: ResourceServerSecurityConfigurer) {
        resources.resourceId(RESOURCE_ID).stateless(false)
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.anonymous().disable()
                .authorizeRequests()
                .antMatchers("/ws/**").access("hasRole('ADMIN')")
                .and().cors()
                .configurationSource(corsConfigurationSource())
                .and().exceptionHandling().accessDeniedHandler(OAuth2AccessDeniedHandler())
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource? {
        val config = CorsConfiguration()
        config.allowedOrigins = listOf("*")
        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        config.allowCredentials = true
        config.allowedHeaders = listOf("Content-Type", "Authorization")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }

    @Bean
    fun corsFilter(): FilterRegistrationBean<CorsFilter>? {
        val bean: FilterRegistrationBean<CorsFilter> = FilterRegistrationBean<CorsFilter>(
                corsConfigurationSource()?.let { CorsFilter(it) })
        bean.order = Ordered.HIGHEST_PRECEDENCE
        return bean
    }

    companion object {
        private const val RESOURCE_ID = "resource_id"
    }
}