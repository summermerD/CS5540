package home.playground.configs;

import home.playground.filters.CORSFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;


public class WebXML implements WebApplicationInitializer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(WebXML.class);

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException
    {
        LOGGER.info("Set up servlet context.");

        final WebApplicationContext rootContext = createRootContext(servletContext);

        createWebMvcContext(servletContext, rootContext);
    }

    private WebApplicationContext createRootContext(ServletContext servletContext)
    {
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(ApplicationConfig.class);

        servletContext.addListener(new ContextLoaderListener(rootContext));

        servletContext.addFilter("CORSFilter", CORSFilter.class).addMappingForUrlPatterns(null, false, "/*");

        return rootContext;
    }

    private void createWebMvcContext(ServletContext servletContext, WebApplicationContext rootContext)
    {
        AnnotationConfigWebApplicationContext webMvcContext = new AnnotationConfigWebApplicationContext();
        webMvcContext.register(WebMvcConfig.class);
        webMvcContext.setParent(rootContext);

        ServletRegistration.Dynamic dispatcherServlet = servletContext.addServlet(
            "dispatcher",
            new DispatcherServlet(webMvcContext)
        );

        dispatcherServlet.addMapping("/");
        dispatcherServlet.setLoadOnStartup(1);
    }
}
