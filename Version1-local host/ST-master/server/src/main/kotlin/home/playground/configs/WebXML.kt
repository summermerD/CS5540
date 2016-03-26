package home.playground.configs

import home.playground.filters.CORSFilter
import org.slf4j.LoggerFactory
import org.springframework.web.WebApplicationInitializer
import org.springframework.web.context.ContextLoaderListener
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext
import org.springframework.web.servlet.DispatcherServlet
import javax.servlet.ServletContext

/**
 * Created by cuong on 11/14/15.
 */
class WebXML : WebApplicationInitializer
{
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WebXML::class.java)
    }

    override fun onStartup(servletContext: ServletContext?)
    {
        LOGGER.info("Set up servlet context.")

        val rootContext = createRootContext(servletContext)

        createWebMvcContext(servletContext, rootContext)
    }

    private fun createRootContext(servletContext: ServletContext?): WebApplicationContext
    {
        val rootContext = AnnotationConfigWebApplicationContext()
        rootContext.register(ApplicationConfig::class.java)

        servletContext?.addListener(ContextLoaderListener(rootContext))

        servletContext?.addFilter("CORSFilter", CORSFilter::class.java)
                      ?.addMappingForUrlPatterns(null, false, "/*");

        return rootContext
    }

    private fun createWebMvcContext(servletContext: ServletContext?, rootContext: WebApplicationContext)
    {
        val webMvcContext = AnnotationConfigWebApplicationContext()
        webMvcContext.register(WebMvcConfig::class.java)
        webMvcContext.parent = rootContext

        val dispatcherServlet = servletContext?.addServlet(
            "dispatcher",
            DispatcherServlet(webMvcContext)
        )

        dispatcherServlet?.addMapping("/")
        dispatcherServlet?.setLoadOnStartup(1)
    }
}