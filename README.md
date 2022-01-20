# CoCoa Updates

****

21.11.29 추가

* 회원가입 (성공시 alert / 중복확인 / 비번일치확인)
* 로그인 (성공시 우측상단 변경 / 실패시 alert)
* 로그아웃 (성공시 우측상단 변경)
* 마이페이지는 아직 버튼만 존재
* home.jsp 중간에 카테고리 버튼 생성 * 테마 아직 적용 x
* loginForm, joinForm 테마 갈색으로 변경

***

21.11.30 추가

* 코치 글, 프로젝트 글 작성 = db에 추가 구현
(아직 validation 부족 & 파일업로드 너무 복잡)
* 코치, 프로젝트 주제별 작성 버튼 show & hide
* coachWriteForm / projectWriteForm 완성

***

21.12.1 추가

* 코드 전체 리뷰 및 리팩토링
* Git으로 협업하는 법 완벽히 익히기
* 로그인 여부에 따라 [글 작성] 버튼 숨김 / 표시
* [/id/coachNO]로 dest 설정 : 같은 파일명 충돌 x
* db에서 임의로 삭제 후 새로 생성시 생기는 충돌은 삭제 로직에서 조절

***

21.12.2 추가

* coachInfo.jsp / projectInfo.jsp 생성
* 전체적인 css 보완
* coachInfo 수정 (본인이면 수정 버튼 활성화, 누르면 수정 가능) 구현 완료 - 타인 완벽 배제
* projectInfo 삭제 (본인이면 삭제 버튼 활성화) 구현 완료 - 문제점 : 타인이 본인글 수정 가능
* home.jsp에서 카탈로그 조회하면 coach랑 project가 겹치는 부분 회의 필요

***

21.12.3 추가

* coachInfo 삭제 + projectInfo 수정 구현 완료 - 본인 검증 문제 해결 - UI에서 공간 벌어지는건 아직
* c글작성 / p글작성 시 뜨는 좌측 프로필의 coach, leader를 member.id로 뜨게 구현
* 본인이면 대화하기 / 요청서 작성 숨김 구현
* profileInfo.jsp / myPageProfile.jsp 생성 = jsp:include로 생성
* 앞의 coachInfo, projectInfo 처럼 수정 누르면 disabled가 풀리고 수정 가능한 형태로 구현
* 수정시 마이페이지에서는 로그아웃했다가 다시 로그인해야 반영 = 해결

***

21.12.7 추가

* myPageInfo.jsp 생성
* 마이페이지 회원정보 수정, 삭제 구현
* 사이드바 클릭 이벤트 구현
* loginForm.jsp / joinForm.jsp / 마이페이지 css 보완
* home.jsp에서 코칭 / 프로젝트 클릭시 조건 조회 구현 = 카탈로그 부분은 쿼리문 필요할듯
* textarea - resize:none; - 스크롤 고정 구현
* 회원 정보 CASCADE로 연계 수정 및 삭제 = 테이블 생성시 쿼리문에 추가
* jsp 파일에 태그 id, name 중복 안되게 조심!!

***

21.12.8 추가

* requestTBL 테이블 생성 = req res FK 없애고, reqNO PK로 추가
* reqWriteForm.jsp / myPageSent.jsp / myPageGot.jsp 생성
* sentReqWait.jsp / gotReqWait.jsp 생성 = jsp:include로 load가 안됌
* 요청서 작성 구현
* 보낸 요청, 받은 요청 조회 구현 = myPageProfile로 정보 전달 방식
* home.jsp 페이징 구현 = 코칭만 가능, 아직 조건 조회가 없어서 뒤섞이는 문제 = Criteria, PageMaker VO로 작성

***

21.12.9 추가

* myPageSent.jsp에서 [대기] 상태의 요청 클릭 시 sentReqWait.jsp 이동 후 해당 요청서 조회 구현
* sentReqWait.jsp 이동 후 하단 3개 버튼(수정, 철회, 목록으로) 구현
* myPageGot.jsp에서 [대기] 상태의 요청 클릭 시 gotReqWait.jsp 이동 후 해당 요청서 조회 구현
* gotReqWait.jsp 이동 후 하단 3개 버튼(수락, 거절, 목록으로) UI만 구현
* jsp:include 처리 방식 수정 = 별개로 나눔
* 회원정보 수정시 유효성 검증 JS 추가
* 모든 jsp 파일 코드 리팩토링 = 쓸모없는 라인 삭제 + 상단 / 하단바 jsp:include 처리
* home.jsp 와 coachCate.jsp로 카테고리별 이동 결정 = 각 화면 페이징까지 구현
* 미완 ) 세부 카테고리 이동 및 페이징은 아직 구현 x
* 미완 ) 보낸 요청 리스트 / 받은 요청 리스트에도 아직 페이징 구현 x
* sentWait.jsp / gotWait.jsp / profile.jsp UI 보완 필요
* 받은 요청 하단 다운로드 링크 구현
* 모든 jsp 파일 폴더로 정리 분담 예정

***

21.12.10 추가

* jsp 그룹화 완료
* gotWaitNo.jsp / gotWaitYes.jsp / sentNo.jsp / sentYes.jsp 생성
* 받은 요청 대기글에서 수락 클릭시 수락 전송 화면 이동 구현
* 받은 요청 대기글에서 거절 클릭시 거절 전송 화면 이동 구현
* 보낸 요청 거절글, 수락글 이동 구현
* Coaching / Project 카테고리 하위 3개씩 조건조회 및 페이징 구현
* 상위 주제 디폴트는 Project / 하위 주제 디폴트는 C, Beginner = 표시만 C/C++로 설정
* 보낸 요청 / 받은 요청 제목이 공란이면 링크도 안뜨는 현상 해결 = JS) nullCheck 추가

***

21.12.13 추가

* 계층 3단계로 조건 조회 및 페이징 구현 = 2계층에서 3계층 연계 보완 필요
* 보낸 요청 / 받은 요청 목록 페이징 구현 완료
* UI 검토작업 진행중 = 유효성 검증이나 빠진 요소들 보완 필요
* 카테고리 세분화 추가 작업 복구 완료

***

21.12.14 추가

* 후기작성, 후기조회, 후기삭제 구현
* 후기수정은 문제점이 발견되어 고치는 중
* 거절사유 재전송 기능 삭제 = 수락만 재전송 가능
* reviewTBL 생성
* reviewInfo.jsp / coachRateForm.jsp 생성
* home.jsp - coachCate.jsp / projectCate.jsp 계층별 각개조회 = UI 및 로직 보완
* gotYes.jsp / gotNo.jsp 생성
* 수락 전송에서 코치가 요청자에게 공지사항을 전달할 수 있게 입력란 추가

***

21.12.15 추가

* 카탈로그 UI에 평점 평균 및 후기 개수 표시란 생성 + 정렬 버튼 생성
* 평점 평균 및 후기 개수 계산 조회 로직 구현 - { target : count }
* 마이페이지 UI 보완
* 받은요청 수락 및 거절 글에 요청서 내용 추가
* 후기 로직과 UI 연동 = 2개 이상 수정 안되는 부분 modReview.jsp 추가로 해결
* API 사전 조사 시작 및 실제 정보 준비 진행

***

21.12.16 추가

* 아임포트 API 결제화면 팝업으로 이동 구현 = 실결제 x
* 코칭 글 수정 및 작성 시 영역에 따라서 개발 툴이 표시되게 JS로 이벤트 처리 구현
* 카탈로그 평점순, 최신순, 가격순 정렬 기능 구현 = INNER JOIN 활용
* 네이버 클라우드에 DB 연동, 팀원 공통으로 CRUD 작업 진행 가능 = tomcat은 아직 미연동
* 네이버 클라우드에 실제 정보들 삽입 = 필터링 필요

***

21.12.17 추가

* 실제 정보 재생성
* MAP API 검색 및 삽입 구현 = 삽입된 정보 조회는 아직
* 평점평균, 후기개수 디폴트로 0.0 / 0개 설정
* tomcat 서버 실배포 = [CoCoa](http://49.50.173.116:8080/cocoa/)
* 서버에 이미지 FileZilla - /opt/cocoa/image/~ 경로로 업로드
* 서버가 안될 때는 카타리나 문제 확인

***

21.12.18 추가

* MAP API 삽입된 정보 조회 및 수정 구현 
* 모든 사진 onerror 이미지 설정
* 모든 입력에 대한 유효성 검증 작업 추가 진행중
* 코치 글 수정시 영역에 따른 개발툴 표시 구현 = 코치에 적용된 표시법 프로젝트에도 적용해야함
* 보낸 요청, 받은 요청 리스트 조건 조회 추가 구현
* Cate에서 계층별 상위 하위 조건 조회 개념 추가 = 현재 조회한 조건 중간에 표시
* 다듬는 작업 계속 진행 예정

***

21.12.20 추가

* 전체적인 UI 테마 통일
* home.jsp 컨텐츠 생성
* 유효성 검증 공백은 모두 구현 = 띄어쓰기는 예외
* Cate 화면에 내가 쓴글 조건 조회 기능 구현
* QA 계속하여 진행 중

***

21.12.21 추가

* 사소한 UI 부분 수정
* MemberController 에서 로그인 처리 후 이동하는 경로 재설정
* footer 임의로 하단 고정
* QA 계속하여 진행 중

***

21.12.22 추가

* QA 종료
* 발표자료 준비 완료
* 시연 시나리오 준비 완료

***

21.12.24 마무리

- 프로젝트 발표
- 최우수상 수상
