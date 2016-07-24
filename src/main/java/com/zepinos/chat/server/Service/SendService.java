package com.zepinos.chat.server.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zepinos.chat.server.Domain.Repository.UserRepository;
import com.zepinos.chat.server.Repository.ChannelIdUserIdRepository;
import com.zepinos.chat.server.Repository.UserIdChannelRepository;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SendService {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private ChannelIdUserIdRepository channelIdUserIdRepository;
	@Autowired
	private UserIdChannelRepository userIdChannelRepository;
	@Autowired
	private UserRepository userRepository;

	/**
	 * 모든 접속된 사용자에게 메세지 전송
	 *
	 * @param channel Netty 채널
	 * @param method  data method
	 * @param data    전송받은 데이터
	 * @param result  전송할 데이터
	 * @throws Exception 예외
	 */
	public void send(Channel channel,
	                 String method,
	                 Map<String, Object> data,
	                 Map<String, Object> result) throws Exception {

		String userId = channelIdUserIdRepository.getChannelIdUserIdMap().get(channel.id());

		result.put("method", method);
		result.put("userId", userId);
		result.put("userName", userRepository.findOne(userId).getUserName());
		result.put("content", data.get("content"));

		String resultMessage = objectMapper.writeValueAsString(result);

		userIdChannelRepository.writeAndFlush(resultMessage);

	}

}
