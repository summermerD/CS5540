package home.playground;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;

import java.net.URL;
import java.security.ProtectionDomain;


public class Startup
{
    public static void main(String[] args)
    {
        final Server server = new Server(8080);
        server.setHandler(configHandler(server));

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static WebAppContext configHandler(Server server)
    {
        final Configuration.ClassList classList = Configuration.ClassList.setServerDefault(server);
        classList.addAfter(
            FragmentConfiguration.class.getName(),
            EnvConfiguration.class.getName(),
            PlusConfiguration.class.getName()
        );
        classList.addBefore(
            JettyWebXmlConfiguration.class.getName(),
            AnnotationConfiguration.class.getName()
        );

        final ProtectionDomain domain = Startup.class.getProtectionDomain();
        final URL locationUrl = domain.getCodeSource().getLocation();

        final WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.setWar(locationUrl.toExternalForm());
        context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/[^/]*\\.class$");

        return context;
    }
}
