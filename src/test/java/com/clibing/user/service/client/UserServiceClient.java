package com.clibing.user.service.client;

import com.clibing.user.service.LoginRequest;
import com.clibing.user.service.LoginResponse;
import com.clibing.user.service.UserServiceGrpc;
import com.google.errorprone.annotations.SuppressPackageLocation;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserServiceClient {
    private final UserServiceGrpc.UserServiceBlockingStub blockingStub;

    public UserServiceClient(String address, int port) {
        ManagedChannel channel = Grpc.newChannelBuilder(String.format("%s:%d", address, port),
                InsecureChannelCredentials.create()).build();
        this.blockingStub = UserServiceGrpc.newBlockingStub(channel);
    }

    public void login(String username, String password) {
        LoginRequest request = LoginRequest.newBuilder().setName(username).setPassword(password).build();
        LoginResponse response = blockingStub.login(request);
        log.info("login token: {}", response.getData().getToken());
    }

    public static void main(String[] args) {
        UserServiceClient client = new UserServiceClient("127.0.0.1", 8888);
        client.login("admin", "123456");
    }


}
