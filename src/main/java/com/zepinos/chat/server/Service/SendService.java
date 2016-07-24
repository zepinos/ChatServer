package com.zepinos.chat.server.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zepinos.chat.server.Domain.Repository.UserRepository;
import com.zepinos.chat.server.Netty.Server.Repository.ChannelIdRepository;
import com.zepinos.chat.server.Netty.Server.Repository.UserIdRepository;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SendService {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private ChannelIdRepository channelIdRepository;
	@Autowired
	private UserIdRepository userIdRepository;
	@Autowired
	private UserRepository userRepository;

	/**
	 * 모든 접속된 사용자에게 메세지 전송
	 *
	 * @param channel
	 * @param method
	 * @param data
	 * @param result
	 * @throws Exception
	 */
	public void send(Channel channel,
	                 String method,
	                 Map<String, Object> data,
	                 Map<String, Object> result) throws Exception {

		String userId = channelIdRepository.getChannelIdUserIdMap().get(channel.id());

		result.put("method", method);
		result.put("userId", userId);
		result.put("userName", userRepository.findOne(userId).getUserName());
		result.put("content", data.get("content"));

		String resultMessage = objectMapper.writeValueAsString(result);

		userIdRepository.writeAndFlush(resultMessage);

	}

}
