package com.xinchen.grpc.simple;

import com.xinchen.grpc.simple.server.DefaultGrpcServer;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * @date 2022-02-24 15:27
 */
class RetryingHelloWorldServer {
  private static final Logger logger = Logger.getLogger(RetryingHelloWorldServer.class.getName());


  public static void main(String[] args) throws IOException, InterruptedException {
    final DefaultGrpcServer defaultGrpcServer = new DefaultGrpcServer();

    defaultGrpcServer.addService(new RetryingGreeterImpl());

    defaultGrpcServer.start();
  }

  static class RetryingGreeterImpl extends GreeterGrpc.GreeterImplBase{
    AtomicInteger retryCounter = new AtomicInteger(0);
    private static final float UNAVAILABLE_PERCENTAGE = 0.5F;
    private static final Random random = new Random();

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
      int count = retryCounter.incrementAndGet();

      // 随机模拟失败尝试
      if (random.nextFloat() < UNAVAILABLE_PERCENTAGE) {
        logger.info("Returning stubbed UNAVAILABLE error. count: " + count);
        responseObserver.onError(Status.UNAVAILABLE
            .withDescription("Greeter temporarily unavailable...").asRuntimeException());
      } else {
        logger.info("Returning successful Hello response, count: " + count);
        HelloResponse reply = HelloResponse.newBuilder().setMessage("Hello " + request.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
      }
    }
  }

}
