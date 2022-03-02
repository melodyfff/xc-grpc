package com.xinchen.grpc.simple;

import com.xinchen.grpc.simple.client.ManagedChannelHelper;
import io.grpc.ClientInterceptors;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @date 2022-03-02 09:52
 */
class CustomHeaderClient {
  private static final Logger logger = Logger.getLogger(CustomHeaderClient.class.getName());
  private final ManagedChannel channel = ManagedChannelHelper.resolveFrom("localhost",8081);

  private final GreeterGrpc.GreeterBlockingStub blockingStub = GreeterGrpc.newBlockingStub(
      // 处理channel 添加自定义拦截器
      ClientInterceptors.intercept(channel, new CustomHeaderClientInterceptor())
  );

  public ManagedChannel getChannel() {
    return channel;
  }

  /**
   * A simple client method that like {@link HelloWorldClient}.
   */
  private void greet(String name) {
    logger.info("Will try to greet " + name + " ...");
    HelloRequest request = HelloRequest.newBuilder().setName(name).build();
    HelloResponse response;
    try {
      response = blockingStub.sayHello(request);
    } catch (StatusRuntimeException e) {
      logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
      return;
    }
    logger.info("Greeting: " + response.getMessage());
  }


  public static void main(String[] args) throws InterruptedException {
    final CustomHeaderClient client = new CustomHeaderClient();
    String user = "world";

    try {
      client.greet(user);
    } finally {
      client.getChannel().shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

  }
}
