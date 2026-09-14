
방화벽, 공유기, 라우터 같은 많은 네트워크 제품들은 리눅스 커널의 넷 필터 프레임워크를 사용하여 개발한다. 다음 그림은 넷 필터 구조이다.

![](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2\&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FbOmntZ%2FbtrQDwVGiNh%2FAAAAAAAAAAAAAAAAAAAAAPkDKk8zfaLF0NSDfnlDn2XwISI6cNF4gVhbPv9V12qA%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1790780399%26allow_ip%3D%26allow_referer%3D%26signature%3DnP0XT4Ggnkh9Yg6%252BHrk%252BK1UHnsU%253D)

커널에 패킷이 들어오고 나가는 과정에서 5개(Prerouting, Input, Forward, Output, Postrouting) 의 후킹 지점을 거치게 된다. 이들 각각의 후킹 지점에는 룰들을 추가 할 수 있는 테이블이 있다. 그 룰들에 따라 패킷을 제어할 수 있다.
클라이언트, 서버 같은 호스트들의 패킷은 수신될 때 Prerouting, Input을 거쳐서 User 영역으로 올라간다. 반대로 패킷이 송신될 때는 User 영역에서 Output, Postrouting을 거쳐서 나가게 된다. 방화벽 같은 네트워크 장비에서는 패킷이 수신되면 Prerouting, Forward, Postrouting 을 거쳐서 다시 나가게 된다. 패킷 필터는 보통 Forward에 Filter table을 설정하여 패킷을 통과, 차단 등의 제어를 한다.
