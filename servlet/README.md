
# 서블릿 구현하기

## 1단계 - 서블릿 학습 테스트

### `SharedCounterServlet` 과 `LocalCounterServlet` 에는 왜 차이가 발생했는가 ?
- 기본적으로 Servlet 객체는 Tomcat에 의해 단일 객체로 관리된다 (!= 싱글톤)
- `SharedCounterServlet` 객체가 필드로 갖고 있는 `counter` 변수는 heap 영역에 저장된다.
- 멀티 스레딩 환경에서 heap 영역은 공유된다.
- 즉, 여러 스레드가 `counter` 변수를 공유하게 된다.
- 따라서 `SharedCounterServlet` 의 `counter` 는 요청 횟수 만큼의 값을 갖게 된다.
---
- 한편, `LocalCounterServlet` 의 `counter` 변수는 메소드 내에 위치한다.
- 실행중인 메소드는 stack 영역에서 관리되고, 멀티 스레딩 환경에서 stack은 공유되지 않는다.
- 따라서 `LocalCounterServlet`은 `counter` 몇 번의 요청이 실행되더라도 1 의 값을 응답한다.



