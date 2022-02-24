package com.xinchen.grpc.simple;

import io.grpc.BindableService;
import io.grpc.ServerServiceDefinition;

/**
 * @date 2022-02-24 14:20
 */
public class DefaultGrpcServer extends AbstractGrpcServer {

  DefaultGrpcServer(){}

  DefaultGrpcServer(int port) {
    super(port);
  }

  @Override
  public void addService(ServerServiceDefinition serverServiceDefinition) {
    serverServiceDefinitionList.add(serverServiceDefinition);
  }

  @Override
  public void addService(BindableService bindAbleService) {
    bindAbleServiceList.add(bindAbleService);
  }

}
