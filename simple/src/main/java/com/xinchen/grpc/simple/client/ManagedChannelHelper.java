package com.xinchen.grpc.simple.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.Map;

/**
 * 帮助获取{@link ManagedChannel}
 *
 * @since 2022-02-24 15:05
 */
public final class ManagedChannelHelper{

  ManagedChannelHelper(){}

  public static ManagedChannel resolveFrom(String host,int port){
    return ManagedChannelBuilder.forAddress(host, port)
        .usePlaintext()
        .build();
  }

  public static ManagedChannel resolveFrom(String host,int port, Map<String, ?> serviceConfig ){
    System.out.println(String.format("Load Service Config: %s",serviceConfig));
    return ManagedChannelBuilder.forAddress(host, port)
        .usePlaintext()
        .defaultServiceConfig(serviceConfig)
        .build();
  }
}
