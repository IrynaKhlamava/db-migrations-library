package com.innowise.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * Wraps a real JDBC Connection using a dynamic proxy.
 * Logs method calls (prepareStatement, createStatement ) and SQL statements
 * before delegating to the underlying connection.
 * Obtain a proxied Connection via createProxy(connection).
 */

public class ConnectionProxy implements InvocationHandler {

    private static final Logger logger = LogManager.getLogger(ConnectionProxy.class);

    private final Connection originalConnection;

    public ConnectionProxy(Connection connection) {
        this.originalConnection = connection;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        logger.info("Method called: " + method.getName());

        if ("prepareStatement".equals(method.getName()) || "createStatement".equals(method.getName())) {
            if (args != null && args.length > 0) {
                logger.info("Executing SQL Query: " + args[0]);
            }
        }

        return (args == null) ? method.invoke(originalConnection) : method.invoke(originalConnection, args);
    }

    public static Connection createProxy(Connection connection) {
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class<?>[]{Connection.class},
                new ConnectionProxy(connection)
        );
    }
}