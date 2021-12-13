<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link href="resources/css/styles.css" rel="stylesheet" />
<script type="text/javascript" src="resources/js/jquery-3.6.0.min.js"></script>
<title>CoCoa's Coaching</title>
</head>
<body style="background-color: #FFEBCD">

	<!-- 상단바 -->
	<jsp:include page="header.jsp"></jsp:include>



	<!-- 카테고리 구간 -->
	<header class="py-5" style="background-color: #663333">
		<div class="row">
			<div class='col-sm-2'></div>
			<div class='col-sm-8' style="align:left;">
				<!-- 코칭 / 프로젝트 cate -->
				<div class="d-grid gap-3 d-sm-flex">
					<h3 style="color:#FFF;" class="my-auto">분류</h3> 
					<input id="project" type="button"
						class="btn btn-primary btn-lg px-4 me-sm-3"
						onClick="location.href='/cocoa/view_projectCate'" value="Project"> <input
						id="project" type="button"
						class="btn btn-primary btn-lg px-4 me-sm-3"
						onClick="location.href='/cocoa/view_coachCate'" value="Coaching">
				</div>
				<br>
				<!-- 코칭 Field -->
				<div id="cFields"
					class="d-grid gap-3 d-sm-flex">
					<h3 style="color:#FFF;" class="my-auto">영역</h3>
					<input id="Web" type="button"
						class="btn btn-primary btn-lg px-4 me-sm-3"
						onClick="location.href='/cocoa/view_coachCate?cField=cField1'" value="Web">
					<input id="Mobile App" type="button"
						class="btn btn-primary btn-lg px-4 me-sm-3"
						onClick="location.href='/cocoa/view_coachCate?cField=cField2'" value="Mobile App">
					<input id="Embedded" type="button"
						class="btn btn-primary btn-lg px-4 me-sm-3"
						onClick="location.href='/cocoa/view_coachCate?cField=cField3'" value="Embedded">
				</div>
				<br>
				<div id="tools"
					class="d-grid gap-3 d-sm-flex">
					<h3 style="color:#FFF;" class="my-auto">도구</h3>
					<input id="Spring" type="button"
						class="btn btn-primary btn-lg px-4 me-sm-3"
						onClick="location.href='/cocoa/view_coachCate?cField=${cri.cField }&tool=tool1'" value="Spring"/>
					<input id="Django" type="button"
						class="btn btn-primary btn-lg px-4 me-sm-3"
						onClick="location.href='/cocoa/view_coachCate?cField=${cri.cField }&tool=tool2'" value="Django"/>
					<input id="AndroidStudio" type="button"
						class="btn btn-primary btn-lg px-4 me-sm-3"
						onClick="location.href='/cocoa/view_coachCate?cField=${cri.cField }&tool=tool3'" value="Android Studio"/>
					<input id="Xcode" type="button"
						class="btn btn-primary btn-lg px-4 me-sm-3"
						onClick="location.href='/cocoa/view_coachCate?cField=${cri.cField }&tool=tool4'" value="Xcode"/>
					<input id="Arduino" type="button"
						class="btn btn-primary btn-lg px-4 me-sm-3"
						onClick="location.href='/cocoa/view_coachCate?cField=${cri.cField }&tool=tool5'" value="Arduino"/>
					<input id="RaspberryPi" type="button"
						class="btn btn-primary btn-lg px-4 me-sm-3"
						onClick="location.href='/cocoa/view_coachCate?cField=${cri.cField }&tool=tool6'" value="Rasberry Pi"/>
						
				</div>
			</div>
			<div class='col-sm-2'></div>
		</div>
	</header>

	<!-- 코칭 카탈로그 구간 -->
	<section class="py-5">
		<div class="container px-4 px-lg-5 mt-5">

			<!-- 글 주제별 이동 -->
			<!-- 로그인 여부에따라 숨김 / 표시 -->
			<c:choose>
				<c:when test="${isLogOn == true && member != null}">
					<a id="cwrite_btn" class="btn btn-primary px-4 me-sm-3"
						href="/cocoa/view_coachWrite" style="float: right">코치 등록</a>
				</c:when>
			</c:choose>
			<br> <br> <br>

			<!-- 생성된 코칭 카탈로그 표시 -->
			<div
				class="row gx-4 gx-lg-5 row-cols-2 row-cols-md-3 row-cols-xl-4 justify-content-center">

				<!-- 반복문 시작 컨트롤러에서 addObject한 coachList를 가져와서 coach라고 저장 -->
				<c:forEach var="coach" items="${coachesList}">

					<!-- 카탈로그 틀 -->
					<div class="col mb-5">
						<div class="card h-100">

							<!-- 개발툴 표시 -->
							<div class="badge bg-dark text-white position-absolute"
								style="top: 0.5rem; right: 0.5rem">
								<c:choose>
									<c:when test="${coach.tool == 'tool1'}">Spring</c:when>
									<c:when test="${coach.tool == 'tool2'}">Django</c:when>
									<c:when test="${coach.tool == 'tool3'}">Android Studio</c:when>
									<c:when test="${coach.tool == 'tool4'}">Xcode</c:when>
									<c:when test="${coach.tool == 'tool5'}">Arduino</c:when>
									<c:when test="${coach.tool == 'tool6'}">Raspberry Pi</c:when>
								</c:choose>
							</div>

							<!-- 간판 이미지 (src=경로) thumbnails로 보내면서 쿼리스트링을 사용 -->
							<img class="card-img-top" alt="" height=200
								onerror=" this.src='resources/image/sample.png'"
								src="${contextPath}/cthumbnails?coach=${coach.coach}&cImg=${coach.cImg}&coachNO=${coach.coachNO}" />

							<!-- 간판이미지 아래 정보 -->
							<div class="card-body p-4">
								<div class="text-center">
									<!-- 제목 -->
									<h5 class="fw-bolder">${coach.cTitle}</h5>
									<!-- 코치 -->
									${coach.coach}<br>
									<!-- 기본 요금 -->
									${coach.basicPrice} 원
								</div>
							</div>

							<!-- coachInfo 이동 -->
							<div class="card-footer p-4 pt-0 border-top-0 bg-transparent">
								<div class="text-center">
									<a class="btn btn-outline-dark mt-auto"
										href="${contextPath}/view_coachInfo?coachNO=${coach.coachNO}">Get
										in</a>
								</div>
							</div>
						</div>
					</div>
				</c:forEach>
				<!-- Coach 반복문 끝 -->

			</div>
		</div>
		<!-- 위의 카탈로그 틀이 반복 생성 및 표시 -->
	</section>
	
	<!-- 쪽 번호 구간 -->
	<div style="margin: 0 auto; font-size: 30px;">

		<c:if test="${pageMaker.prev }">
			<a
				href='<c:url value="/view_coachCate?cField=${cri.cField }&tool=${cri.tool }&page=${pageMaker.startPage-1 }"/>'><i
				class=""></i></a>
		</c:if>

		<c:forEach begin="${pageMaker.startPage }" end="${pageMaker.endPage }"
			var="pageNum">
			<a href='<c:url value="/view_coachCate?cField=${cri.cField }&tool=${cri.tool }&page=${pageNum }"/>'><i
				class="">${pageNum }</i></a>
		</c:forEach>

		<c:if test="${pageMaker.next && pageMaker.endPage >0 }">
			<a
				href='<c:url value="/view_coachCate?cField=${cri.cField }&tool=${cri.tool }&page=${pageMaker.endPage+1 }"/>'><i
				class=""></i></a>
		</c:if>

	</div>

	<!-- 하단바 -->
	<jsp:include page="footer.jsp"></jsp:include>

</body>
</html>