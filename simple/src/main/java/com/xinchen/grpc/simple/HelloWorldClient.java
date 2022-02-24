package com.xinchen.grpc.simple;

import com.xinchen.grpc.simple.client.ManagedChannelHelper;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @date 2022-02-24 14:33
 */
class HelloWorldClient {
  private static final Logger logger = Logger.getLogger(HelloWorldClient.class.getName());


  private final ManagedChannel channel = ManagedChannelHelper.resolveFrom("localhost",50051);
  private final GreeterGrpc.GreeterBlockingStub blockingStub = GreeterGrpc.newBlockingStub(channel);


  ManagedChannel getChannel() {
    return channel;
  }

  /** Say hello to server. */
  public void greet(String name) {
    logger.info("Will try to greet " + name + " ...");
    HelloRequest request = HelloRequest.newBuilder().setName(name).build();
    HelloResponse response;
    try {
      // This enables compression for requests. Independent of this setting, servers choose whether
      // to compress responses.
      response = blockingStub.withCompression("gzip").sayHello(request);
    } catch (StatusRuntimeException e) {
      logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
      return;
    }
    logger.info("Greeting: " + response.getMessage());
  }

  public static void main(String[] args) throws InterruptedException {

    String user = "world";
    final HelloWorldClient client = new HelloWorldClient();
    try {
      client.greet(user);
    } finally {
      client.getChannel().awaitTermination(5, TimeUnit.SECONDS);
    }
  }
}
