# ChatServer

## 이 프로그램의 기능
Netty 을 기반으로 TCP 프로토콜을 이용하고 데이터는 JSON 을 이용한 싱글 서버용 채팅 서버입니다.
Spring Boot 에서 작동하기 때문에 Injection 을 이용할 수 있습니다.

## 이 프로그램을 만들게 된 동기
이 프로그램은 Netty 기반 UDT 프로토콜을 이용하고 데이터는 Protobuf 을 이용한 멀티 서버용 채팅 서버를 개발하면서 터득한 몇 가지 노하우를 공개하기 위해 만든 일종의 예제입니다.

## 개발환경
IntelliJ 에서 개발하였습니다.

## 라이센스

The Artistic License 2.0 입니다. 제약이 많은 라이센스이니 주의하여 주십시오.

## 간단한 테스트

git clone 을 한 뒤 maven 으로 package 를 만들어서 jar 파일을 실행하거나, IDE 에서 Spring boot 로 Run 하면 됩니다.
(스프링 부트를 잘 모르시는 분을 위해 설명드리면) 이 때 실행되는 경로(Working Directory)는 프로젝트(모듈) 디렉토리로 설정해야 config/ 의 application.yml 설정 파일을 읽습니다.

서비스 실행 뒤, 두 개의 클라이언트(윈도우즈 내장 telnet 으로도 충분합니다)로 접속 후 아래와 같이 로그인-메세지 발송을 테스트 해봅니다.

### 1번 클라이언트
<pre>
> telnet localhost 9812

{"method": "login", "userId": "test1", "password": "123456"}
</pre>

### 2번 클라이언트
<pre>
> telnet localhost 9812

{"method": "login", "userId": "test2", "password": "098765"}
{"method": "send", "content": "Send Message"}
</pre>

