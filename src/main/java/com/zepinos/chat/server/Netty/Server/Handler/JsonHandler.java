package com.zepinos.chat.server.Netty.Server.Handler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zepinos.chat.server.Domain.Repository.UserRepository;
import com.zepinos.chat.server.Domain.User;
import com.zepinos.chat.server.Netty.Server.Repository.ChannelIdRepository;
import com.zepinos.chat.server.Netty.Server.Repository.UserIdRepository;
import io.netty.channel.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Qualifier("jsonHandler")
@ChannelHandler.Sharable
public class JsonHandler extends SimpleChannelInboundHandler<String> {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ChannelIdRepository channelIdRepository;
	@Autowired
	private UserIdRepository userIdRepository;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {

		Map<String, Object> result = new HashMap<>();

		// 접속자 채널 정보(연결 정보)
		Channel channel = ctx.channel();
		ChannelId channelId = channel.id();

		// 전송된 내용을 JSON 개체로 변환
		Map<String, Object> data;
		try {

			data = objectMapper.readValue(s, new TypeReference<Map<String, Object>>() {
			});

		} catch (JsonParseException | JsonMappingException e) {

			e.printStackTrace();

			returnMessage(channel, result, e, "1001");
			return;

		}
		
		String method = (String) data.getOrDefault("method", "");

		switch (method) {

			case "login":

				// 사용자 인증 처리
				String userId = (String) data.get("userId");
				String password = (String) data.get("password");

				if (userId == null || password == null) {

					returnMessage(channel, result, new Exception("사용자 아이디 혹은 비밀번호가 비어있습니다."), "1002");
					return;

				}

				User user = userRepository.findOne(userId);

				if (user == null) {

					returnMessage(channel, result, new Exception("사용자 아이디가 존재하지 않습니다."), "1003");
					return;

				} else if (!password.equals(user.getPassword())) {

					returnMessage(channel, result, new Exception("비밀번호가 일치하지 않습니다."), "1004");
					return;

				}

				// 사용자 정보 입력
				channelIdRepository.getChannelIdUserIdMap().put(channelId, userId);
				userIdRepository.getUerIdChannelMap().put(userId, channel);

				returnMessage(channel, result, method);

				break;

			case "send":

				userId = channelIdRepository.getChannelIdUserIdMap().get(channelId);

				result.put("method", method);
				result.put("userId", userId);
				result.put("userName", userRepository.findOne(userId).getUserName());
				result.put("content", data.get("content"));

				String resultMessage = objectMapper.writeValueAsString(result);

				userIdRepository.writeAndFlush(resultMessage);

				break;

			default:

				returnMessage(channel, result, new Exception("메세지 구분이 정확하지 않습니다."), "1005");
				return;

		}

	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {

		ChannelId channelId = ctx.channel().id();

		// 사용자 정보 제거
		Map<ChannelId, String> channelIdUserIdMap = channelIdRepository.getChannelIdUserIdMap();

		String userId = channelIdUserIdMap.get(channelId);
		userIdRepository.getUerIdChannelMap().remove(userId);
		channelIdUserIdMap.remove(channelId);

		ctx.close();

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
	}

	private void returnMessage(Channel channel, Map<String, Object> result, Throwable throwable, String status) throws Exception {

		result.put("status", status);
		result.put("message", ExceptionUtils.getStackTrace(throwable));

		channel.writeAndFlush(objectMapper.writeValueAsString(result) + System.lineSeparator());

	}

	private void returnMessage(Channel channel, Map<String, Object> result, String method) throws Exception {

		result.put("status", "0");
		result.put("method", method);

		channel.writeAndFlush(objectMapper.writeValueAsString(result) + System.lineSeparator());

	}

}
