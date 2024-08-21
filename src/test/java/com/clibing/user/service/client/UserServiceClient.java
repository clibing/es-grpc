package com.clibing.user.service.client;

import com.clibing.user.service.LoginRequest;
import com.clibing.user.service.LoginResponse;
import com.clibing.user.service.UserServiceGrpc;
import com.clibing.user.service.grpc.interceptor.HeaderClientInterceptor;
import io.grpc.ClientInterceptors;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class UserServiceClient {
    private final UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;
    private final HealthGrpc.HealthBlockingStub healthBlockingStub;
    private ManagedChannel channel = null;

    public UserServiceClient(String address, int port) {
        channel = Grpc.newChannelBuilder(String.format("%s:%d", address, port),
                InsecureChannelCredentials.create()).build();
        this.userServiceBlockingStub = UserServiceGrpc.newBlockingStub(ClientInterceptors.intercept(channel,
                new HeaderClientInterceptor()));
        this.healthBlockingStub = HealthGrpc.newBlockingStub(ClientInterceptors.intercept(channel,
                new HeaderClientInterceptor()));
    }

    public void login(String username, String password) {
        LoginRequest request = LoginRequest.newBuilder().setName(username).setPassword(password).build();
        LoginResponse response = userServiceBlockingStub.login(request);
        log.info("login token: {}", response.getData().getToken());
    }

    public void health() {
        HealthCheckRequest request = HealthCheckRequest.newBuilder().setService("user-service").build();
        StreamObserver<HealthCheckResponse> responseStreamObserver = new StreamObserver<HealthCheckResponse>() {
            @Override
            public void onNext(HealthCheckResponse healthCheckResponse) {
                log.info("HealthCheck: {}", healthCheckResponse.getStatus());
            }

            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onCompleted() {}
        };
        healthBlockingStub.watch(request);
    }

    public static void main(String[] args) throws InterruptedException {
        UserServiceClient client = new UserServiceClient("127.0.0.1", 8888);
        client.login("admin", "123456");

        client.health();

        TimeUnit.SECONDS.sleep(30);
    }


}
