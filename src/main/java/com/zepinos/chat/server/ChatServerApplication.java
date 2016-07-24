package com.zepinos.chat.server;

import com.zepinos.chat.server.Domain.Repository.UserRepository;
import com.zepinos.chat.server.Domain.User;
import com.zepinos.chat.server.Netty.Server.NettyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatServerApplication implements CommandLineRunner {

	@Autowired
	NettyServer nettyServer;
	@Autowired
	private UserRepository userRepository;

	/**
	 * 채팅 서버 프로그램 시작 지점입니다. Spring Boot 의 시작점이기도 합니다.
	 * run() 에서 실제 프로그램이 시작됩니다.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(ChatServerApplication.class, args);
	}

	/**
	 * CommandLineRunner 에 의해서 자동 실행되는 부분입니다.
	 * 여기서 로그인에 사용할 테스트 데이터를 DB 에 입력하고 Netty Server 을 시작합니다.
	 * Netty.Server.NettyServer 에서 start() 을 이용해서 서버를 시동합니다.
	 *
	 * @param strings
	 * @throws Exception
	 */
	@Override
	public void run(String... strings) throws Exception {

		// 테스트용 사용자 계정 입력
		User user = new User();
		user.setUserId("test1");
		user.setUserName("테스트1");
		user.setPassword("123456");

		userRepository.save(user);

		user = new User();
		user.setUserId("test2");
		user.setUserName("테스트2");
		user.setPassword("098765");

		userRepository.save(user);

		// Netty Server 시작
		nettyServer.start();

	}

}
