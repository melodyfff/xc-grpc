package com.xinchen.grpc.simple.server;

import io.grpc.BindableService;
import io.grpc.ServerServiceDefinition;
import java.io.IOException;

/**
 * @date 2022-02-24 13:48
 */
interface BaseGrpcServer {

  void start() throws IOException, InterruptedException;

  void stop() throws InterruptedException;

  void addService(ServerServiceDefinition serverServiceDefinition);

  void addService(BindableService serverServiceDefinition);

}
