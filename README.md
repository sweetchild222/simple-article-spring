![](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2\&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FbGoH7s%2FbtrOC5q3caO%2FAAAAAAAAAAAAAAAAAAAAAJz6RbOtSiZI0wlT-MkGz-fggE5u6PkaeWt62pLX3NNU%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1790780399%26allow_ip%3D%26allow_referer%3D%26signature%3DQ1i7D2HbZ7G%252BqaphQiephlnRfno%253D)

|                                                   |
| ------------------------------------------------- |
| ① Copy : 메인 메모리에 있는 데이터를 GPU 메모리 공간으로 복사한다.       |
| ② Instruct : CPU는 GPU에 연산을 요청하는 명령을 보낸다.          |
| ③ Execute : GPU는 메모리의 데이터를 연산하고 결과를 다시 메모리에 저장한다. |
| ④ Copy : GPU 메모리에 저장된 결과를 메인 메모리에 복사한다.           |

이러한 처리 절차를 API로 제공하는 것이 바로 Cuda 이다.
