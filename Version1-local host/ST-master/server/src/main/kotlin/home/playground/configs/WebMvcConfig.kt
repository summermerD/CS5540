package home.playground.configs

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

/**
 * Created by cuong on 11/14/15.
 */
@EnableWebMvc
@Configuration
@ComponentScan(basePackages = arrayOf("home.playground.resources"))
open class WebMvcConfig : WebMvcConfigurerAdapter()
{
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WebMvcConfig::class.java)
    }

    override public fun addResourceHandlers(registry: ResourceHandlerRegistry)
    {
        LOGGER.info("Configure static resources.")

        registry.addResourceHandler("/views/**").addResourceLocations("/WEB-INF/views/")
        registry.addResourceHandler("/resources/**").addResourceLocations("/WEB-INF/resources/")
    }
}