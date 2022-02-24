package com.xinchen.grpc.simple;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.xinchen.grpc.simple.client.ManagedChannelHelper;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @date 2022-02-24 15:28
 */
class RetryingHelloWorldClient {
  private static final Logger logger = Logger.getLogger(RetryingHelloWorldClient.class.getName());
  private final ManagedChannel channel = ManagedChannelHelper.resolveFrom("localhost",50051,getRetryingServiceConfig());
  private final GreeterGrpc.GreeterBlockingStub blockingStub = GreeterGrpc.newBlockingStub(channel);

  static Map<String, ?> getRetryingServiceConfig() {
    return new Gson()
        .fromJson(
            new JsonReader(
                new InputStreamReader(
                    Objects.requireNonNull(RetryingHelloWorldClient.class.getClassLoader().getResourceAsStream(
                        "retrying_service_config.json")),
                    UTF_8)),
            Map.class);
  }


  private final AtomicInteger totalRpcs = new AtomicInteger();
  private final AtomicInteger failedRpcs = new AtomicInteger();
  /**
   * Say hello to server in a blocking unary call.
   */
  public void greet(String name) {
    HelloRequest request = HelloRequest.newBuilder().setName(name).build();
    HelloResponse response = null;
    StatusRuntimeException statusRuntimeException = null;
    try {
      response = blockingStub.sayHello(request);
    } catch (StatusRuntimeException e) {
      failedRpcs.incrementAndGet();
      statusRuntimeException = e;
    }

    totalRpcs.incrementAndGet();

    if (statusRuntimeException == null) {
      logger.log(Level.INFO,"Greeting: {0}", new Object[]{response.getMessage()});
    } else {
      logger.log(Level.INFO,"RPC failed: {0}", new Object[]{statusRuntimeException.getStatus()});
    }
  }


  private void printSummary() {
    logger.log(
        Level.INFO,
        "\n\nTotal RPCs sent: {0}. Total RPCs failed: {1}\n",
        new Object[]{
            totalRpcs.get(), failedRpcs.get()});
  }

  ManagedChannel getChannel() {
    return channel;
  }

  public static void main(String[] args) throws InterruptedException {
    final RetryingHelloWorldClient client = new RetryingHelloWorldClient();

    ForkJoinPool executor = new ForkJoinPool();
    for (int i = 0; i < 50; i++) {
      final String userId = "user" + i;
      executor.execute(
          new Runnable() {
            @Override
            public void run() {
              client.greet(userId);
            }
          });
    }
    executor.awaitQuiescence(100, TimeUnit.SECONDS);
    executor.shutdown();
    client.printSummary();
    client.getChannel().shutdown().awaitTermination(60, TimeUnit.SECONDS);
  }
}
