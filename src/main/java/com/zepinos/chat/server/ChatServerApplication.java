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

	public static void main(String[] args) {
		SpringApplication.run(ChatServerApplication.class, args);
	}

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

		nettyServer.start();

	}

}
