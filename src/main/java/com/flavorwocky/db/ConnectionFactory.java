package com.flavorwocky.db;

import com.flavorwocky.exception.DbException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by luanne on 11/06/14.
 */
public class ConnectionFactory {

    private final static ConnectionFactory instance = new ConnectionFactory();
    private Connection serverConnection = null;


    private ConnectionFactory() {
        String jdbcUrl = System.getenv("GRAPHENEDB_URL").replace("http:", "jdbc:neo4j:");
        try {
            serverConnection = DriverManager.getConnection(jdbcUrl);
        } catch (SQLException sqle) {
            throw new DbException("Could not obtain a connection to " + jdbcUrl, sqle);
        }
    }

    public static ConnectionFactory getInstance() {
        return instance;
    }

    /**
     * Get a JDBC connection to a Neo4j Server
     *
     * @return Connection
     */
    public Connection getServerConnection() throws DbException {
        return serverConnection;
    }


    /**
     * Close the connection to the Neo4j Server
     */
    public void closeServerConnection() {
        try {
            if (serverConnection != null) {
                serverConnection.close();
            }
        } catch (SQLException sqle) {
            throw new DbException("Could not close db connection", sqle);
        }
    }


}
