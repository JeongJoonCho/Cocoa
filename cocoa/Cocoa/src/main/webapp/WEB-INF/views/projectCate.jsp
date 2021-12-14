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
<title>CoCoa</title>
</head>
<body style="background-color: #FFEBCD">

	<!-- 상단바 -->
	<jsp:include page="header.jsp"></jsp:include>

	<!-- 카테고리 구간 -->
	<header class="py-5"
		style="background-color: #663333; text-align: center;">
		<div class="row">

			<!-- 분류 -->
			<div class="col-sm-4">
				<div id="class" class="d gap-3">
					<div style="color: #CFFFE5; font-size: 25px;">분류</div>
					<br> <input id="project" type="button"
						class="btn btn-primary btn-lg px-4"
						onClick="location.href='/cocoa/view_projectCate'" value="Project"><br>
					<br> <input id="project" type="button"
						class="btn btn-primary btn-lg px-4"
						onClick="location.href='/cocoa/view_coachCate'" value="Coaching">
				</div>
			</div>

			<!-- 영역 -->
			<div class="col-sm-4">
				<div id="cFields" class="d gap-3">
					<div style="color: #CFFFE5; font-size: 25px; text-align: center;">영역
					</div>
					<br> <input id="Web" type="button"
						class="btn btn-primary btn-lg px-4"
						onClick="location.href='/cocoa/view_projectCate?pField=pField1'"
						value="Web"> <br> <br> <input id="Mobile App"
						type="button" class="btn btn-primary btn-lg px-4"
						onClick="location.href='/cocoa/view_projectCate?pField=pField2'"
						value="Mobile App"> <br> <br> <input
						id="Embedded" type="button" class="btn btn-primary btn-lg px-4"
						onClick="location.href='/cocoa/view_projectCate?pField=pField3'"
						value="Embedded">
				</div>
			</div>

			<!-- 등급 -->
			<div class="col-sm-4">
				<div id="levels" class="d gap-3">
					<div style="color: #CFFFE5; font-size: 25px; text-align: center;">등급</div>
					<br> <input id="Basic" type="button"
						class="btn btn-primary btn-lg px-4"
						onClick="location.href='/cocoa/view_projectCate?level=level1'"
						value="Basic" /><br> <br> <input id="Intermediate"
						type="button" class="btn btn-primary btn-lg px-4"
						onClick="location.href='/cocoa/view_projectCate?level=level2'"
						value="Intemediate" /><br> <br> <input id="Advanced"
						type="button" class="btn btn-primary btn-lg px-4"
						onClick="location.href='/cocoa/view_projectCate?level=level3'"
						value="Advanced" />
				</div>
			</div>

		</div>
	</header>

	<!-- 프로젝트 카탈로그 구간 -->
	<section class="py-5">

		<!-- 선택된 카테고리 표시 -->
		<div style="text-align: center;">
			<c:choose>
				<c:when test="${cri.pField == 'pField' && cri.level == 'level'}">
					<hr>
					<hr>
					<h3 style="color: black; font-weight: 1000; font-size: 40px;">PROJECT</h3>
					<hr>
					<hr>
				</c:when>
				<c:when test="${cri.pField == 'pField1'}">
					<hr>
					<hr>
					<h3 style="color: black; font-weight: 1000; font-size: 40px;">WEB</h3>
					<hr>
					<hr>
				</c:when>
				<c:when test="${cri.pField == 'pField2'}">
					<hr>
					<hr>
					<h3 style="color: black; font-weight: 1000; font-size: 40px;">MOBILE
						APP</h3>
					<hr>
					<hr>
				</c:when>
				<c:when test="${cri.pField == 'pField3'}">
					<hr>
					<hr>
					<h3 style="color: black; font-weight: 1000; font-size: 40px;">EMBEDDED</h3>
					<hr>
					<hr>
				</c:when>
				<c:when test="${cri.level == 'level1'}">
					<hr>
					<hr>
					<h3 style="color: black; font-weight: 1000; font-size: 40px;">BASIC</h3>
					<hr>
					<hr>
				</c:when>
				<c:when test="${cri.level == 'level2'}">
					<hr>
					<hr>
					<h3 style="color: black; font-weight: 1000; font-size: 40px;">INTEMEDIATE</h3>
					<hr>
					<hr>
				</c:when>
				<c:when test="${cri.level == 'level3'}">
					<hr>
					<hr>
					<h3 style="color: black; font-weight: 1000; font-size: 40px;">ADVANCED</h3>
					<hr>
					<hr>
				</c:when>
			</c:choose>
		</div>

		<div class="container px-4 px-lg-5 mt-5">

			<!-- 글 주제별 이동 -->
			<!-- 로그인 여부에따라 숨김 / 표시 -->
			<c:choose>
				<c:when test="${isLogOn == true && member != null}">
					<a id="cwrite_btn" class="btn btn-primary px-4 me-sm-3"
						href="/cocoa/view_projectWrite" style="float: right">프로젝트 등록</a>
				</c:when>
			</c:choose>
			<br> <br> <br>

			<!-- 생성된 코칭 카탈로그 표시 -->
			<div
				class="row gx-4 gx-lg-5 row-cols-2 row-cols-md-3 row-cols-xl-4 justify-content-center">

				<!-- 반복문 시작 컨트롤러에서 addObject한 coachList를 가져와서 coach라고 저장 -->
				<c:forEach var="project" items="${projectList}">

					<!-- 카탈로그 틀 -->
					<div class="col mb-5">
						<div class="card h-100">

							<!-- 개발툴 표시 -->
							<div class="badge bg-dark text-white position-absolute"
								style="top: 0.5rem; right: 0.5rem">
								<c:choose>
									<c:when test="${project.level == 'level1'}">Basic</c:when>
									<c:when test="${project.level == 'level2'}">Intermediate</c:when>
									<c:when test="${project.level == 'level3'}">Advanced</c:when>
								</c:choose>
							</div>

							<!-- 간판 이미지 (src=경로) thumbnails로 보내면서 쿼리스트링을 사용 -->
							<img class="card-img-top" alt="" height=200
								onerror=" this.src='resources/image/sample.png'"
								src="${contextPath}/pthumbnails?leader=${project.leader}&pImg=${project.pImg}&projectNO=${project.projectNO}" />

							<!-- 간판이미지 아래 정보 -->
							<div class="card-body p-4">
								<div class="text-center">
									<!-- 제목 -->
									<h5 class="fw-bolder">${project.pTitle}</h5>
									<!-- 리더 -->
									${project.leader}<br>
									<!-- 인원수 -->
									${project.memberCount}명
								</div>
							</div>

							<!-- coachInfo 이동 -->
							<div class="card-footer p-4 pt-0 border-top-0 bg-transparent">
								<div class="text-center">
									<a class="btn btn-outline-dark mt-auto"
										href="${contextPath}/view_projectInfo?id=${project.leader}&projectNO=${project.projectNO}">Get
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
				href='<c:url value="/view_projectCate?pField=${cri.pField }&level=${cri.level }&page=${pageMaker.startPage-1 }"/>'><i
				class=""></i></a>
		</c:if>

		<c:forEach begin="${pageMaker.startPage }" end="${pageMaker.endPage }"
			var="pageNum">
			<a
				href='<c:url value="/view_projectCate?pField=${cri.pField }&level=${cri.level }&page=${pageNum }"/>'><i
				class="">${pageNum }</i></a>
		</c:forEach>

		<c:if test="${pageMaker.next && pageMaker.endPage >0 }">
			<a
				href='<c:url value="/view_projectCate?pField=${cri.pField }&level=${cri.level }&page=${pageMaker.endPage+1 }"/>'><i
				class=""></i></a>
		</c:if>

	</div>

	<!-- 하단바 -->
	<jsp:include page="footer.jsp"></jsp:include>

</body>
</html>