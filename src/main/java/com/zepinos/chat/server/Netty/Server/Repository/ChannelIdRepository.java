package com.zepinos.chat.server.Netty.Server.Repository;

import io.netty.channel.ChannelId;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChannelIdRepository {

	private final Map<ChannelId, String> channelIdUserIdMap = new ConcurrentHashMap<>();

	public Map<ChannelId, String> getChannelIdUserIdMap() {
		return channelIdUserIdMap;
	}

}
