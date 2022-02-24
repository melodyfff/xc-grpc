package com.xinchen.grpc.simple.server;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @date 2022-02-24 13:49
 */
public abstract class AbstractGrpcServer implements BaseGrpcServer {
  private static final Logger logger = Logger.getLogger(AbstractGrpcServer.class.getName());
  private int port;
  private Server server;

  protected final List<ServerServiceDefinition> serverServiceDefinitionList = new ArrayList<>();
  protected final List<BindableService> bindAbleServiceList = new ArrayList<>();

  AbstractGrpcServer() {
  }

  protected AbstractGrpcServer(int port) {
    this.port = port;
  }


  @Override
  public void start() throws IOException, InterruptedException {

    checkServices();

    internalStart();

    blockUntilShutdown();
  }

  @Override
  public void stop() throws InterruptedException {
    internalStop();
  }

  void checkServices(){
    if (serverServiceDefinitionList.isEmpty() && bindAbleServiceList.isEmpty()){
      throw new RuntimeException("There is no service to deploy.");
    }
  }

  protected void internalStart() throws IOException {
    /* The port on which the server should run */

    final ServerBuilder<?> serverBuilder = ServerBuilder.forPort(0 == port ? 50051 : port);

    // 添加需要发布的服务
    serverBuilder.addServices(serverServiceDefinitionList);
    bindAbleServiceList.forEach(serverBuilder::addService);

    this.server = serverBuilder
        .build()
        .start();

    logger.info("Server started, listening on " + server.getPort());
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        try {
          AbstractGrpcServer.this.stop();
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
        }
        System.err.println("*** server shut down");
      }
    });
  }

  protected void internalStop() throws InterruptedException {
    if (server != null) {
      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }

  /**
   * Await termination on the main thread since the grpc library uses daemon threads.
   */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

}
