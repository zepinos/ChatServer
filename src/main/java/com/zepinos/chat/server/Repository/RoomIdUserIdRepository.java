package com.zepinos.chat.server.Repository;

import org.apache.commons.collections.map.MultiValueMap;
import org.springframework.stereotype.Component;

@Component
public class RoomIdUserIdRepository {

	private final MultiValueMap roomIdUserIdMap = new MultiValueMap();

	public MultiValueMap getRoomIdUserIdMap() {
		return roomIdUserIdMap;
	}

}
