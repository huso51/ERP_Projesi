/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.filter;

import com.erprest.connection.DBConnectionHelper;
import com.erprest.service.FileSystem;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.glassfish.jersey.servlet.WebComponent;

/**
 *
 * @author msi_ge72
 */
public class FirstStartListener implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(FirstStartListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.log(Level.INFO, "###ErpRest Starting up!");
        String osType = System.getProperty("os.name");
        if (osType.toLowerCase().contains("linux")) {
            FileSystem fileSystem = new FileSystem();
            //check wkhtmltopdf
            if (fileSystem.checkWkhtmltopdf()) {
                logger.log(Level.INFO, "###wkhtmltopdf working fine");
            } else {
                logger.log(Level.SEVERE, "###wkhtmltopdf not found. Pdf generate won't work. Please setup manually!");
            }
            //check configFile
            if (fileSystem.checkConfigFile()) {
                logger.log(Level.INFO, "###configFile OK");
            } else {
                logger.log(Level.INFO, "###configFile not found");
            }
        }
        //Stop unnecessary Log
        Logger jerseyLogger = Logger.getLogger(WebComponent.class.getName());
        jerseyLogger.setFilter(new Filter() {
            @Override
            public boolean isLoggable(LogRecord lr) {
                boolean isLoggable = true;
                if (lr.getMessage().contains("Only resource methods using @FormParam")) {
                    isLoggable = false;
                }
                return isLoggable;
            }
        });
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DBConnectionHelper.getInstance().destroyConnnection();
        logger.log(Level.INFO, "###ErpRest Shutting down!");
    }

}
