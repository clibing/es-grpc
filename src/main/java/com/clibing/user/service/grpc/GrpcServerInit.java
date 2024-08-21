package com.clibing.user.service.grpc;

import com.clibing.user.service.grpc.interceptor.HeaderClientInterceptor;
import com.clibing.user.service.grpc.interceptor.HeaderServerInterceptor;
import com.clibing.user.service.impl.UserServiceImpl;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.ServerInterceptors;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.protobuf.services.HealthStatusManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

@Slf4j
@Service
public class GrpcServerInit {
    private int port = 8888;
    private Server server;

    // pom.xml: grpc-services
    private HealthStatusManager health;

    @Value("${spring.application.nam:user-service}")
    private String applicationName;

    @Async
    @PostConstruct
    public void init() throws IOException {
        log.info("Application name: {}, init: thread id: {}, thread name: {}", applicationName,
                Thread.currentThread().getId(), Thread.currentThread().getName());
        grpcInit();
    }

    public void grpcInit() throws IOException {
        log.info("grpc init, port: {}, thread id: {}, thread name: {}", port, Thread.currentThread().getId(),
                Thread.currentThread().getName());
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);

        health = new HealthStatusManager();

        server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
                .addService(health.getHealthService())
                .addService(ServerInterceptors.intercept(new UserServiceImpl(), new HeaderServerInterceptor()))
                .build()
                .start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.info("When the system exits, turn off the grpc service and wait for 15 seconds");
                try {
                    GrpcServerInit.this.stop();
                } catch (InterruptedException e) {
                }
            }
        });
        health.setStatus(applicationName, HealthCheckResponse.ServingStatus.SERVING);
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(15, TimeUnit.SECONDS);
        }
    }
}
