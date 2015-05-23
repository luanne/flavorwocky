package com.flavorwocky;

import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.server.Neo4jServer;
import org.springframework.data.neo4j.server.RemoteServer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan("com.flavorwocky")
@EnableAutoConfiguration
@EnableTransactionManagement
@EnableNeo4jRepositories("com.flavorwocky.repository")
public class Application extends Neo4jConfiguration {

    public Application() {
        System.setProperty("username", "neo4j");
        System.setProperty("password", "neo");
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    //
    @Override
    public Neo4jServer neo4jServer() {
        return new RemoteServer("http://localhost:7474");
    }


    @Override
    public SessionFactory getSessionFactory() {
        return new SessionFactory("com.flavorwocky.domain");
    }

    @Override
    @Bean
    @Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public Session getSession() throws Exception {
        return super.getSession();
    }
}
