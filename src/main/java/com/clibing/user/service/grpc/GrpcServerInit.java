package com.clibing.user.service.grpc;

import com.clibing.user.service.impl.UserServiceImpl;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class GrpcServerInit {
    private Server server;

    @Async
    @PostConstruct
    public void init() throws IOException {
        log.info("spirng init: thread id: {}, thread name: {}", Thread.currentThread().getId(),
                Thread.currentThread().getName());
        grpcInit();
    }

    public void grpcInit() throws IOException {
        log.info("grpc init: thread id: {}, thread name: {}", Thread.currentThread().getId(),
                Thread.currentThread().getName());

        server = Grpc.newServerBuilderForPort(8888, InsecureServerCredentials.create())
                .addService(new UserServiceImpl())
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
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(15, TimeUnit.SECONDS);
        }
    }
}
