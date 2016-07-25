package com.zepinos.chat.server.Service;

import com.zepinos.chat.server.Domain.Repository.UserRepository;
import com.zepinos.chat.server.Domain.User;
import com.zepinos.chat.server.Repository.ChannelIdUserIdRepository;
import com.zepinos.chat.server.Repository.RoomIdUserIdRepository;
import com.zepinos.chat.server.Repository.UserIdChannelRepository;
import com.zepinos.chat.server.Repository.UserIdRoomIdRepository;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class RoomService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserIdChannelRepository userIdChannelRepository;
	@Autowired
	private ChannelIdUserIdRepository channelIdUserIdRepository;
	@Autowired
	private UserIdRoomIdRepository userIdRoomIdRepository;
	@Autowired
	private RoomIdUserIdRepository roomIdUserIdRepository;
	@Autowired
	private MessageService messageService;
	@Autowired
	private LoginService loginService;

	/**
	 * 룸 생성
	 *
	 * @param channel Netty 채널
	 * @param method  data method
	 * @param result  전송할 데이터
	 * @throws Exception 예외
	 */
	public void create(Channel channel,
	                   String method,
	                   Map<String, Object> result) throws Exception {

		String userId = channelIdUserIdRepository.getChannelIdUserIdMap().get(channel.id());

		// 이미 룸에 입장해있는지 확인
		if (userIdRoomIdRepository.getUserIdRoomIdMap().containsKey(userId)) {

			messageService.returnMessage(channel, result, new Exception("룸에 입장해있는 사용자입니다."), "1006");
			return;

		}

		// 룸 아이디 생성
		String roomId = UUID.randomUUID().toString();

		// 룸 입장
		roomIdUserIdRepository.getRoomIdUserIdMap().put(roomId, userId);
		userIdRoomIdRepository.getUserIdRoomIdMap().put(userId, roomId);

		result.put("method", method);
		result.put("roomId", roomId);

		messageService.returnMessage(channel, result, method);

	}

	/**
	 * 룸 입장
	 *
	 * @param channel Netty 채널
	 * @param method  data method
	 * @param data    전송받은 데이터
	 * @param result  전송할 데이터
	 * @throws Exception 예외
	 */
	public void enter(Channel channel,
	                  String method,
	                  Map<String, Object> data,
	                  Map<String, Object> result) throws Exception {

		String userId = channelIdUserIdRepository.getChannelIdUserIdMap().get(channel.id());

		// 이미 룸에 입장해있는지 확인
		if (userIdRoomIdRepository.getUserIdRoomIdMap().containsKey(userId)) {

			messageService.returnMessage(channel, result, new Exception("룸에 입장해있는 사용자입니다."), "1006");
			return;

		}

		// 룸 아이디
		String roomId = (String) data.get("roomId");

		if (!roomIdUserIdRepository.getRoomIdUserIdMap().containsKey(roomId)) {

			messageService.returnMessage(channel, result, new Exception("존재하지 않는 룸 아이디입니다.."), "1007");
			return;

		}

		// 룸 입장
		roomIdUserIdRepository.getRoomIdUserIdMap().put(roomId, userId);
		userIdRoomIdRepository.getUserIdRoomIdMap().put(userId, roomId);

		result.put("method", method);
		result.put("roomId", roomId);

		messageService.returnMessage(channel, result, method);

	}

	/**
	 * 룸 퇴장
	 *
	 * @param channel Netty 채널
	 * @param method  data method
	 * @param result  전송할 데이터
	 * @throws Exception 예외
	 */
	public void exit(Channel channel,
	                 String method,
	                 Map<String, Object> result) throws Exception {

		String userId = channelIdUserIdRepository.getChannelIdUserIdMap().get(channel.id());

		// 룸에 입장해있는지 확인
		if (!userIdRoomIdRepository.getUserIdRoomIdMap().containsKey(userId)) {

			messageService.returnMessage(channel, result, new Exception("룸에 존재하지 않습니다."), "1008");
			return;

		}

		// 룸 아이디
		String roomId = userIdRoomIdRepository.getUserIdRoomIdMap().get(userId);

		// 룸 퇴장
		roomIdUserIdRepository.getRoomIdUserIdMap().remove(roomId, userId);
		userIdRoomIdRepository.getUserIdRoomIdMap().remove(userId);

		result.put("method", method);

		messageService.returnMessage(channel, result, method);

	}

	/**
	 * 룸 입장자에게 메세지 전송
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

		// 룸에 입장해있는지 확인
		if (!userIdRoomIdRepository.getUserIdRoomIdMap().containsKey(userId)) {

			messageService.returnMessage(channel, result, new Exception("룸에 존재하지 않습니다."), "1008");
			return;

		}

		User user = userRepository.findOne(userId);

		if (user == null) messageService.returnMessage(channel, result, new Exception("사용자 정보를 조회할 수 없습니다.."), "1009");

		String userName = user.getUserName();

		// 전달할 메세지 내용 생성
		result.put("method", method);
		result.put("userId", userId);
		result.put("userName", userName);
		result.put("content", data.get("content"));

		// 룸 아이디
		String roomId = userIdRoomIdRepository.getUserIdRoomIdMap().get(userId);

		// 룸에 메세지 전송
		roomIdUserIdRepository.getRoomIdUserIdMap().getCollection(roomId).parallelStream().forEach(otherUserId -> {

			// 채널 가져오기
			Channel otherChannel = userIdChannelRepository.getUserIdChannelMap().get(otherUserId);

			// 채널이 활성화 상태가 아니라면 사용자를 제거
			if (!otherChannel.isActive()) {

				loginService.removeUser(otherChannel);
				return;

			}

			messageService.returnMessage(otherChannel, result, method);

		});

	}

}
