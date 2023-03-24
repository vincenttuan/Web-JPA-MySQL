package ajax.listener;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class MyServletContextListener implements ServletContextListener {

    private EntityManagerFactory entityManagerFactory;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        // 在此處建立實體管理器工廠
        entityManagerFactory = Persistence.createEntityManagerFactory("demo");
        event.getServletContext().setAttribute("entityManagerFactory", entityManagerFactory);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // 當應用程式停止時，關閉實體管理器工廠和其他資源
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }

}

