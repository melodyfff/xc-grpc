package com.xinchen.grpc.simple;

import com.xinchen.grpc.simple.server.DefaultGrpcServer;
import io.grpc.stub.StreamObserver;
import java.io.IOException;

/**
 *
 * 普通rpc访问
 *
 * @date 2022-02-24 10:46
 */
class HelloWorldServer {

  public static void main(String[] args) throws IOException, InterruptedException {
    final DefaultGrpcServer server = new DefaultGrpcServer();
    server.addService(new GreeterImpl());
    server.start();
  }


  static class GreeterImpl extends GreeterGrpc.GreeterImplBase{
    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
      // 简单返回
      final HelloResponse response = HelloResponse
          .newBuilder()
          .setMessage(String.format("Hello From [%s].", request.getName()))
          .build();
     responseObserver.onNext(response);
     responseObserver.onCompleted();
    }
  }

}
