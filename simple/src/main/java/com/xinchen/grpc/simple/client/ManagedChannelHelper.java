package com.xinchen.grpc.simple.client;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;

/**
 * 帮助获取{@link ManagedChannel}
 *
 * @since 2022-02-24 15:05
 */
public final class ManagedChannelHelper{

  ManagedChannelHelper(){}

  /**
   * "localhost"
   * "127.0.0.1"
   * "localhost:8080"
   * "foo.googleapis.com:8080"
   * "127.0.0.1:8080"
   * "[2001:db8:85a3:8d3:1319:8a2e:370:7348]"
   * "[2001:db8:85a3:8d3:1319:8a2e:370:7348]:443"
   *
   * @param target target
   * @return ManagedChannel
   */
  public static ManagedChannel resolveFrom(String target){
    return ManagedChannelBuilder.forTarget(target)
        // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
        // needing certificates.
        .usePlaintext()
        .build();
  }

  public static ManagedChannel resolveFrom(String host,int port){
    return ManagedChannelBuilder.forAddress(host, port)
        // 使用明文连接到服务器。
        .usePlaintext()
        .build();
  }

  public static ManagedChannel resolveFrom(String host,int port, Map<String, ?> serviceConfig ){
    System.out.printf("Load Service Config: %s%n",serviceConfig);
    return ManagedChannelBuilder.forAddress(host, port)
        .usePlaintext()
        .defaultServiceConfig(serviceConfig).enableRetry()
        .build();
  }

  public static ManagedChannel resolveFrom(String host,int port, String serviceConfigPath ){
    return resolveFrom(host, port, getRetryingServiceConfig(serviceConfigPath));
  }

  static Map<String, ?> getRetryingServiceConfig(String filePath) {
    return new Gson()
        .fromJson(
            new JsonReader(
                new InputStreamReader(
                    Objects.requireNonNull(ManagedChannelHelper.class.getClassLoader().getResourceAsStream(
                        filePath)),
                    UTF_8)),
            Map.class);
  }
}
