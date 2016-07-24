package com.zepinos.chat.server.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MessageService {

	private final ObjectMapper objectMapper = new ObjectMapper();

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

		channel.writeAndFlush(objectMapper.writeValueAsString(result) + System.lineSeparator());

	}

	/**
	 * 메세지 전송
	 *
	 * @param channel Netty 채널
	 * @param result  전송할 데이터
	 * @param method  data method
	 */
	void returnMessage(Channel channel, Map<String, Object> result, String method) {

		result.put("status", "0");
		result.put("method", method);

		try {

			channel.writeAndFlush(objectMapper.writeValueAsString(result) + System.lineSeparator());

		} catch (JsonProcessingException e) {

			e.printStackTrace();

		}


	}

}
