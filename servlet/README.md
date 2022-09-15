
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


## 2단계 - 필터 학습 테스트
- 필터는 서비스 메소드를 호출하기 전에 실행된다.
  - 생각해보면, `Servlet#init` 메소드와 `Servlet#destroy` 메소드는 톰캣 전반의 실행 직후와 종료 직전에 실행된다.
  - 따라서 `Filter#doFilter` 메소드는 `Servlet#service` 를 하기 이전과 이후를 감싼다.
- charset은 기본적으로 `ISO-8859-1` 을 사용한다.
  - `If no charset is specified, ISO-8859-1 will be used.`
- charset에 대한 설정은 `getWriter` 를 호출하기 이전에 호출되어야 한다.
  - ```
    The setCharacterEncoding, setContentType, or setLocale method must be called
    before getWriter and before committing the response for the character encoding to be used.
    ```
 
