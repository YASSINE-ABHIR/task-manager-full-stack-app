//package com.example.taskmanager;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//@SpringBootApplication
//@EnableJpaRepositories
//@EnableTransactionManagement
//public class TaskManagerApplication {
//
//    public static void main(String[] args) {
//        SpringApplication.run(TaskManagerApplication.class, args);
//    }
//}
//
package com.example.taskmanager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.example.taskmanager.config.DefaultProfileUtil;

@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
public class TaskManagerApplication {

    private static final Logger log = LoggerFactory.getLogger(TaskManagerApplication.class);

    private final Environment env;

    // ➜ Spring injecte Environment ici
    public TaskManagerApplication(Environment env) {
        this.env = env;
    }

    /**
     * Main method, used to run the application.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TaskManagerApplication.class);

        // Ajoute un profil par défaut si aucun n’est défini
        DefaultProfileUtil.addDefaultProfile(app);

        Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);
    }

    /** Log formatted access URLs + profiles after startup */
    private static void logApplicationStartup(Environment env) {
        String protocol = env.getProperty("server.ssl.key-store") != null ? "https" : "http";
        String serverPort = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path");
        if (StringUtils.isBlank(contextPath)) {
            contextPath = "/";
        }

        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }

        log.info(
            "\n----------------------------------------------------------\n\t" +
            "Application '{}' is running! Access URLs:\n\t"   +
            "Local:    \t{}://localhost:{}{}\n\t"              +
            "External: \t{}://{}:{}{}\n\t"                    +
            "Profile(s): \t{}\n----------------------------------------------------------",
            env.getProperty("spring.application.name"),
            protocol, serverPort, contextPath,
            protocol, hostAddress, serverPort, contextPath,
            env.getActiveProfiles()
        );
    }
}
