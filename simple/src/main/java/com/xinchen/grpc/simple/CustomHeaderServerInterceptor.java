package com.xinchen.grpc.simple;

import com.google.common.annotations.VisibleForTesting;
import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import java.util.logging.Logger;

/**
 *
 * {@link VisibleForTesting}修改私有属性，方便在测试的时候使用
 *
 * @date 2022-03-02 09:58
 */
class CustomHeaderServerInterceptor implements ServerInterceptor {
  private static final Logger logger = Logger.getLogger(CustomHeaderServerInterceptor.class.getName());



  @VisibleForTesting
  static final Metadata.Key<String> CUSTOM_HEADER_KEY =
      Metadata.Key.of("custom_server_header_key", Metadata.ASCII_STRING_MARSHALLER);


  @Override
  public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers,
      ServerCallHandler<ReqT, RespT> next) {

    logger.info(">> 接收到client的header: " + headers);

    return next.startCall(new SimpleForwardingServerCall<ReqT, RespT>(call) {
      @Override
      public void sendHeaders(Metadata responseHeaders) {
        responseHeaders.put(CUSTOM_HEADER_KEY, "customRespondValue");
        super.sendHeaders(responseHeaders);
      }
    },headers);
  }
}
