package com.clibing.user.service.impl;

import com.clibing.user.service.LoginRequest;
import com.clibing.user.service.LoginResponse;
import com.clibing.user.service.UserServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {
    @Override
    public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        log.info("Login, username: {}, password: {}", request.getName(), request.getPassword());
        LoginResponse response = LoginResponse.newBuilder()
                .setCode(200)
                .setMessage("success")
                .setData(LoginResponse.Data.newBuilder().setToken(UUID.randomUUID().toString()).build())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
        log.info("Login success!");
    }
}
