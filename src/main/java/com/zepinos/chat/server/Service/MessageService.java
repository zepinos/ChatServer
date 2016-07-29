package com.zepinos.chat.server.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MessageService {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Value("${netty.server.transfer.type}")
	private String transferType;

	@Autowired
	private LoginService loginService;
	@Autowired
	private SendService sendService;
	@Autowired
	private RoomService roomService;

	/**
	 * 메세지 분기
	 *
	 * @param channel Netty 채널
	 * @param data    전송받은 데이터
	 * @param result  전송할 데이터
	 * @throws Exception
	 */
	public void execute(Channel channel, Map<String, Object> data, Map<String, Object> result) throws Exception {

		String method = (String) data.getOrDefault("method", "");

		switch (method) {

			case "login":

				// 사용자 인증 처리
				loginService.login(channel, method, data, result);
				break;

			case "send":

				// 메세지 전송
				sendService.send(channel, method, data, result);
				break;

			case "create_room":

				// 룸 생성
				roomService.create(channel, method, result);
				break;

			case "enter_room":

				// 룸 입장
				roomService.enter(channel, method, data, result);
				break;

			case "exit_room":

				// 룸 퇴장
				roomService.exit(channel, method, result);
				break;

			case "send_room":

				// 룸에 메세지 전송
				roomService.send(channel, method, data, result);
				break;

			default:

				returnMessage(channel, result, new Exception("메세지 구분이 정확하지 않습니다."), "1005");
				return;

		}

	}

	/**
	 * 예외 발생 메세지 전송
	 *
	 * @param channel   Netty 채널
	 * @param result    전송할 데이터
	 * @param throwable 예외
	 * @param status    상태코드
	 * @throws Exception 예외
	 */
	public void returnMessage(Channel channel, Map<String, Object> result, Throwable throwable, String status) throws Exception {

		result.put("status", status);
		result.put("message", ExceptionUtils.getStackTrace(throwable));

		channel.writeAndFlush(returnMessage(result));

	}

	/**
	 * 메세지 전송
	 *
	 * @param channel Netty 채널
	 * @param result  전송할 데이터
	 * @param method  data method
	 * @throws Exception 예외
	 */
	void returnMessage(Channel channel, Map<String, Object> result, String method) {

		result.put("status", "0");
		result.put("method", method);

		try {

			channel.writeAndFlush(returnMessage(result));

		} catch (Exception e) {

			e.printStackTrace();

		}


	}

	Object returnMessage(Map<String, Object> result) throws Exception {

		switch (transferType) {

			case "websocket":

				return new TextWebSocketFrame(objectMapper.writeValueAsString(result));

			case "tcp":
			default:

				return objectMapper.writeValueAsString(result) + System.lineSeparator();

		}

	}

	public Object returnMessage(String message) {

		switch (transferType) {

			case "websocket":

				return new TextWebSocketFrame(message);

			case "tcp":
			default:

				return message + System.lineSeparator();

		}

	}

}
