package com.zepinos.chat.server.Netty.Server.Repository;

import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserIdRepository {

	private final Map<String, Channel> userIdChannelMap = new ConcurrentHashMap<>();

	public Map<String, Channel> getUerIdChannelMap() {
		return userIdChannelMap;
	}

	public void writeAndFlush(String returnMessage) throws Exception {

		userIdChannelMap.values().parallelStream().forEach(channel -> {

			if (!channel.isActive()) channel.close();

			channel.writeAndFlush(returnMessage + System.lineSeparator());

		});

	}

}
