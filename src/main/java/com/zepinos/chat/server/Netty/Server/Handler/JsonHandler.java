package com.zepinos.chat.server.Netty.Server.Handler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zepinos.chat.server.Service.LoginService;
import com.zepinos.chat.server.Service.MessageService;
import com.zepinos.chat.server.Service.RoomService;
import com.zepinos.chat.server.Service.SendService;
import io.netty.channel.*;
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
	private MessageService messageService;
	@Autowired
	private LoginService loginService;
	@Autowired
	private SendService sendService;
	@Autowired
	private RoomService roomService;

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

			messageService.returnMessage(channel, result, e, "1001");
			return;

		}

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

				messageService.returnMessage(channel, result, new Exception("메세지 구분이 정확하지 않습니다."), "1005");
				return;

		}

	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {

		loginService.removeUser(ctx.channel());

		ctx.close();

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
	}

}
