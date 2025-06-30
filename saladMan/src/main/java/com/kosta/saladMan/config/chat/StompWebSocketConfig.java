package com.kosta.saladMan.config.chat;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    public StompWebSocketConfig(StompHandler stompHandler) {
        this.stompHandler = stompHandler;
    }

    //엔드포인트 설정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/connect")
                .setAllowedOrigins("http://localhost:5173")
                // ws://가 아니라 http:// 엔드포인트를 사용할 수 있게 해주는 sockJs라이브러리를 통한 요청을 허용하는 설정.
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // /publish/1 의 형태로 메시지 발행해야함을 설정
        // /publish로 시작하는 url패턴으로 메세지가 발행되면 @Controller 객체의 @MessageMapping메서드로 라우팅(전달)
        registry.setApplicationDestinationPrefixes("/publish");


        // /topic/1형태로 메세지를 수신(subscribe)해야 함을 설정
        registry.enableSimpleBroker("/topic");
    }


    //웹소켓 요청(connect, subscribe, disconnect)등의 요청시에는 http header등 http메시지를 넣어올수 있고
    //이를 interceptor를 통해 가로채 토큰 등을 검증 할 수 있음
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }

}
