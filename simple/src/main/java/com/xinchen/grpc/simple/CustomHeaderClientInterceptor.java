package com.xinchen.grpc.simple;

import com.google.common.annotations.VisibleForTesting;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.ForwardingClientCallListener.SimpleForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import java.util.logging.Logger;

/**
 * @date 2022-03-02 09:58
 */
class CustomHeaderClientInterceptor implements ClientInterceptor {
  private static final Logger logger = Logger.getLogger(CustomHeaderClientInterceptor.class.getName());

  @VisibleForTesting
  static final Metadata.Key<String> CUSTOM_HEADER_KEY =
      Metadata.Key.of("custom_client_header_key", Metadata.ASCII_STRING_MARSHALLER);


  @Override
  public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method,
      CallOptions callOptions, Channel next) {
    return new SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)){

      @Override
      public void start(Listener<RespT> responseListener, Metadata headers) {

        /* put custom header */
        headers.put(CUSTOM_HEADER_KEY, "customRequestValue");


        super.start(new SimpleForwardingClientCallListener<RespT>(responseListener) {
          @Override
          public void onHeaders(Metadata headers) {
            /**
             * if you don't need receive header from server,
             * you can use {@link io.grpc.stub.MetadataUtils#attachHeaders}
             * directly to send header
             */
            logger.info(">> 接收到server的header: " + headers);
            super.onHeaders(headers);
          }
        }, headers);
      }
    };
  }
}
