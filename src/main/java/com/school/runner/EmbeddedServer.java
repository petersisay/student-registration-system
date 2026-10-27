package com.school.runner;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;

/**
 * Embedded Tomcat 10 runner providing standalone local execution and
 * free-tier cloud deployment readiness (Render, Railway, Fly.io, etc.).
 * Standard WAR packaging is fully preserved for traditional Tomcat installations.
 */
public class EmbeddedServer {

    public static void main(String[] args) throws Exception {
        // Prefer source webapp directory if it exists (for live updates), otherwise exploded WAR
        File sourceWebapp = new File("src/main/webapp");
        File explodedWar = new File("target/StudentRegistrationApp-1.0-SNAPSHOT");
        String webappDirLocation = sourceWebapp.exists() ? sourceWebapp.getAbsolutePath() : 
                (explodedWar.exists() ? explodedWar.getAbsolutePath() : new File(".").getAbsolutePath());

        Tomcat tomcat = new Tomcat();

        String webPort = System.getenv("PORT");
        if (webPort == null || webPort.trim().isEmpty()) {
            webPort = "8080";
        }
        tomcat.setPort(Integer.parseInt(webPort));
        tomcat.getConnector(); // triggers default HTTP connector initialization

        String contextPath = System.getenv("CONTEXT_PATH");
        if (contextPath == null) {
            contextPath = "";
        }

        StandardContext ctx = (StandardContext) tomcat.addWebapp(contextPath, webappDirLocation);
        ctx.setReloadable(true);
        // Delegate class loading to parent class loader so all compiled classes & libraries are visible
        ctx.setParentClassLoader(EmbeddedServer.class.getClassLoader());

        File additionWebInfClasses = new File("target/classes");
        if (additionWebInfClasses.exists()) {
            WebResourceRoot resources = new StandardRoot(ctx);
            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                    additionWebInfClasses.getAbsolutePath(), "/"));
            ctx.setResources(resources);
        }

        System.out.println("==================================================================");
        System.out.println("  Ethiopian Elementary School Management System");
        System.out.println("  Starting server on: http://localhost:" + webPort + contextPath + "/");
        System.out.println("==================================================================");

        tomcat.start();
        tomcat.getServer().await();
    }
}
