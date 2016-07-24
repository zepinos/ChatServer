package com.zepinos.chat.server.Service;

import com.zepinos.chat.server.Domain.Repository.UserRepository;
import com.zepinos.chat.server.Domain.User;
import com.zepinos.chat.server.Netty.Server.Repository.ChannelIdRepository;
import com.zepinos.chat.server.Netty.Server.Repository.UserIdRepository;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LoginService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ChannelIdRepository channelIdRepository;
	@Autowired
	private UserIdRepository userIdRepository;
	@Autowired
	private MessageService messageService;

	/**
	 * 사용자 로그인
	 *
	 * @param channel
	 * @param method
	 * @param data
	 * @param result
	 * @throws Exception
	 */
	public void login(Channel channel,
	                  String method,
	                  Map<String, Object> data,
	                  Map<String, Object> result) throws Exception {

		// 사용자 인증 처리
		String userId = (String) data.get("userId");
		String password = (String) data.get("password");

		if (userId == null || password == null) {

			messageService.returnMessage(channel, result, new Exception("사용자 아이디 혹은 비밀번호가 비어있습니다."), "1002");
			return;

		}

		User user = userRepository.findOne(userId);

		if (user == null) {

			messageService.returnMessage(channel, result, new Exception("사용자 아이디가 존재하지 않습니다."), "1003");
			return;

		} else if (!password.equals(user.getPassword())) {

			messageService.returnMessage(channel, result, new Exception("비밀번호가 일치하지 않습니다."), "1004");
			return;

		}

		// 사용자 정보 입력
		channelIdRepository.getChannelIdUserIdMap().put(channel.id(), userId);
		userIdRepository.getUerIdChannelMap().put(userId, channel);

		messageService.returnMessage(channel, result, method);

	}

	public void removeUser(Channel channel) {

		ChannelId channelId = channel.id();

		// 사용자 정보 제거
		Map<ChannelId, String> channelIdUserIdMap = channelIdRepository.getChannelIdUserIdMap();

		String userId = channelIdUserIdMap.get(channelId);
		userIdRepository.getUerIdChannelMap().remove(userId);
		channelIdUserIdMap.remove(channelId);

	}

}
