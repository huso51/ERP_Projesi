/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.connection;

import com.erprest.model.Config;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author msi_ge72
 */
public class DBConnectionHelper {

    private static final Logger logger = Logger.getLogger(DBConnectionHelper.class.getName());
    private static DBConnectionHelper dbhelper;
    private static DataSource ds;

    public DBConnectionHelper() {
    }

    public static DBConnectionHelper getInstance() {
        if (dbhelper == null || ds == null) {
            dbhelper = new DBConnectionHelper();
            Config config = ProjectConfig.getInstance().getConfig();
            MysqlConnectionPoolDataSource dsx = new MysqlConnectionPoolDataSource();
            dsx.setUser(config.getDatabase().getUser());
            dsx.setPassword(config.getDatabase().getPassword());
            dsx.setServerName(config.getDatabase().getServerName());
            dsx.setPort(config.getDatabase().getPort());
            dsx.setDatabaseName(config.getDatabase().getDatabase());
            ds = dsx;
        }
        return dbhelper;
    }

    public static DBConnectionHelper getInstance(DataSource dsx) {
        if (dbhelper == null) {
            dbhelper = DBConnectionHelper.getInstance();
            ds = dsx;
        }
        return dbhelper;
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }

    public void destroyConnnection() {
        com.mysql.jdbc.AbandonedConnectionCleanupThread.uncheckedShutdown();
    }
}
