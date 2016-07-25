package com.zepinos.chat.server.Service;

import com.zepinos.chat.server.Domain.Repository.UserRepository;
import com.zepinos.chat.server.Domain.User;
import com.zepinos.chat.server.Repository.ChannelIdUserIdRepository;
import com.zepinos.chat.server.Repository.RoomIdUserIdRepository;
import com.zepinos.chat.server.Repository.UserIdChannelRepository;
import com.zepinos.chat.server.Repository.UserIdRoomIdRepository;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LoginService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ChannelIdUserIdRepository channelIdUserIdRepository;
	@Autowired
	private UserIdChannelRepository userIdChannelRepository;
	@Autowired
	private UserIdRoomIdRepository userIdRoomIdRepository;
	@Autowired
	private RoomIdUserIdRepository roomIdUserIdRepository;
	@Autowired
	private MessageService messageService;

	/**
	 * 사용자 로그인
	 *
	 * @param channel Netty 채널
	 * @param method  data method
	 * @param data    전송받은 데이터
	 * @param result  전송할 데이터
	 * @throws Exception 예외
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
		channelIdUserIdRepository.getChannelIdUserIdMap().put(channel.id(), userId);
		userIdChannelRepository.getUserIdChannelMap().put(userId, channel);

		messageService.returnMessage(channel, result, method);

	}

	/**
	 * 접속 사용자 정보 제거
	 *
	 * @param channel Netty 채널
	 */
	public void removeUser(Channel channel) {

		ChannelId channelId = channel.id();
		Map<ChannelId, String> channelIdUserIdMap = channelIdUserIdRepository.getChannelIdUserIdMap();
		String userId = channelIdUserIdMap.get(channelId);

		// 사용자 정보 제거
		if (!StringUtils.isEmpty(userId)) {

			userIdChannelRepository.getUserIdChannelMap().remove(userId);

			String roomId = userIdRoomIdRepository.getUserIdRoomIdMap().get(userId);

			// 룸 정보 제거
			if (!StringUtils.isEmpty(roomId)) {

				roomIdUserIdRepository.getRoomIdUserIdMap().remove(roomId, userId);
				userIdRoomIdRepository.getUserIdRoomIdMap().remove(userId);

			}

			channelIdUserIdMap.remove(channelId);

		}

	}

}
