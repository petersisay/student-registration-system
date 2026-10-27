package servlet;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import util.DatabaseMigration;

/**
 * Application Lifecycle Listener that initializes the database schema,
 * reference data, and default accounts on startup.
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("[AppContextListener] Ethiopian Elementary School Management System initializing...");
        try {
            DatabaseMigration.runMigrations();
        } catch (Exception e) {
            System.err.println("[AppContextListener] Failed to complete database migrations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("[AppContextListener] Ethiopian Elementary School Management System stopping.");
    }
}
