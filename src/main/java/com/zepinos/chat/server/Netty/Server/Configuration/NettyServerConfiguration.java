package com.zepinos.chat.server.Netty.Server.Configuration;

import com.zepinos.chat.server.Netty.Server.Handler.JsonHandler;
import com.zepinos.chat.server.Netty.Server.Initializer.NettyChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@Configuration
public class NettyServerConfiguration {

	@Value("${netty.server.transfer.type}")
	private String transferType;        // 서버의 Transfer Type. tcp, udp, udt 등을 정의하고 이용할 수 있도록 프로그래밍 할 수 있을 것이다.
	@Value("${netty.server.transfer.port}")
	private int transferPort;           // 서버의 Transfer Port. tcp 등이 이용할 포트 번호이다.
	@Value("${netty.server.thread.count.boss}")
	private int threadCountBoss;        // Netty Server 의 Boss Thread 수이다.
	@Value("${netty.server.thread.count.worker}")
	private int threadCountWorker;      // Netty Server 의 Worker Thread 수이다.
	@Value("${netty.server.log.level.bootstrap}")
	private String logLevelBootstrap;   // ServerBootStrap 의 Log Level 설정이다.

	@Autowired
	private NettyChannelInitializer nettyChannelInitializer;

	/**
	 * Netty Server 의 Boss Thread 설정.
	 * 추후 http, udp 등의 설정이 필요할 경우 case 을 추가하여 설정을 변경할 수 있다.
	 *
	 * @return
	 */
	@Bean(destroyMethod = "shutdownGracefully")
	public NioEventLoopGroup bossGroup() {

		switch (transferType) {

			case "tcp":
			default:

				return new NioEventLoopGroup(threadCountBoss);

		}

	}

	/**
	 * Netty Server 의 Worker Thread 설정.
	 * 추후 http, udp 등의 설정이 필요할 경우 case 을 추가하여 설정을 변경할 수 있다.
	 *
	 * @return
	 */
	@Bean(destroyMethod = "shutdownGracefully")
	public NioEventLoopGroup workerGroup() {

		switch (transferType) {

			case "tcp":
			default:

				return new NioEventLoopGroup(threadCountWorker);

		}

	}

	/**
	 * Transfer Port 설정.
	 *
	 * @return
	 */
	@Bean
	public InetSocketAddress port() {
		return new InetSocketAddress(transferPort);
	}

	/**
	 * Netty ServerBootStrap 설정.
	 * LogLevel 을 지정해주고 사용자의 입력을 처리해줄 Handler 을 등록해주는데, Netty.Server.Initializer.NettyChannelInitializer 을 통해 이를 설정해준다.
	 * 그리고 Transfer Type 에 따ㄹ channel 을 등록해준다.
	 *
	 * @return
	 */
	@Bean
	public ServerBootstrap serverBootstrap() {

		ServerBootstrap serverBootstrap = new ServerBootstrap();

		serverBootstrap
				.group(bossGroup(), workerGroup())
				.handler(new LoggingHandler(LogLevel.valueOf(logLevelBootstrap)))
				.childHandler(nettyChannelInitializer);

		switch (transferType) {

			case "websocket":
			case "tcp":
			default:

				serverBootstrap.channel(NioServerSocketChannel.class);

		}

		return serverBootstrap;

	}

	/**
	 * Handler Bean 을 등록한다.
	 * Netty.Server.Initializer.NettyChannelInitializer 에서 이용할 Handler 을 등록해둔다.
	 *
	 * @return
	 */
	@Bean
	public ChannelInboundHandlerAdapter handler() {
		return new JsonHandler();
	}

}
