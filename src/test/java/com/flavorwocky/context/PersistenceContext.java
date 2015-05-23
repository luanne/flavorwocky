/*
 * Copyright (c) LUANNE 2015. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.flavorwocky.context;

import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.server.InProcessServer;
import org.springframework.data.neo4j.server.Neo4jServer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableNeo4jRepositories("com.flavorwocky.repository")
@EnableTransactionManagement
@ComponentScan("com.flavorwocky")
public class PersistenceContext extends Neo4jConfiguration {

    public static final int NEO4J_PORT = 7479;

    public PersistenceContext() {
        System.setProperty("username", "neo4j");
        System.setProperty("password", "neo");
    }

    @Override
    public SessionFactory getSessionFactory() {
        return new SessionFactory("com.flavorwocky.domain");
    }

    @Bean
    public Neo4jServer neo4jServer() {
        //   return new RemoteServer("http://localhost:7474");
        return new InProcessServer();
    }

    @Override
    @Bean
    public Session getSession() throws Exception {
        return super.getSession();
    }
}
