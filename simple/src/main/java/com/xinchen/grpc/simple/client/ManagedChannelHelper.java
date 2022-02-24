package com.xinchen.grpc.simple.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.AbstractBlockingStub;
import io.grpc.stub.AbstractStub;

/**
 * @date 2022-02-24 15:05
 */
public class ServiceCallerClient <T>{

  private final ManagedChannel channel;
  AbstractStub blockingStub;

  ServiceCallerClient(String host,int port){
    channel = ManagedChannelBuilder.forAddress(host, port)
        .usePlaintext()
        .build();
  }
}
