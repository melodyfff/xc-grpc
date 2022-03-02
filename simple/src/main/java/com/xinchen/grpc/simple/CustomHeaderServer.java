package com.xinchen.grpc.simple;

import com.xinchen.grpc.simple.server.DefaultGrpcServer;
import io.grpc.ServerInterceptors;
import io.grpc.stub.StreamObserver;
import java.io.IOException;

/**
 * 示例添加HEADER
 */
class CustomHeaderServer {

  public static void main(String[] args) throws IOException, InterruptedException {
    DefaultGrpcServer server = new DefaultGrpcServer(8081);
    server.addService(ServerInterceptors.intercept(new GreeterImpl(),new CustomHeaderServerInterceptor()));
    server.start();
  }

  private static class GreeterImpl extends GreeterGrpc.GreeterImplBase {
    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloResponse> responseObserver) {
      HelloResponse reply = HelloResponse.newBuilder().setMessage("Hello " + req.getName()).build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }
  }
}
