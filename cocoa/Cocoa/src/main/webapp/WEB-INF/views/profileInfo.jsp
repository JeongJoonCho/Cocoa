<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.5.0/font/bootstrap-icons.css" />
<link href="resources/css/styles.css" rel="stylesheet" />
<script type="text/javascript" src="resources/js/jquery-3.6.0.min.js"></script>
<script type="text/javascript">
	function readURL(input) {
		if (input.files && input.files[0]) {
			var reader = new FileReader();
			reader.onload = function(e) {
				$('#preview').attr('src', e.target.result);
			}
			reader.readAsDataURL(input.files[0]);
		}
	}
	var cnt = 1;
	function fn_addFile() {
		$("#d_file")
				.append("<br>" + "<input type='file' name='file"+cnt+"' />");
		cnt++;
	}
</script>
<title>CoCoa</title>
</head>
<body style="background-color: #FFEBCD">

	<!-- 상단바 -->
	<nav class="navbar navbar-expand-lg" style="background-color: #663333;">

		<div class="container px-4 px-lg-5">

			<!-- 로고 -->
			<a class="navbar-brand" href="/cocoa/index"
				style="color: #CFFFE5; font-size: 30px;"
				onmouseover="this.style.color='black';"
				onmouseout="this.style.color='#CFFFE5';"><b>CoCoa</b></a>

			<div class="collapse navbar-collapse" id="navbarSupportedContent">

				<!-- 상단 가운데공간 지우면 안됌 -->
				<div class="navbar-nav me-auto mb-2 mb-lg-0 ms-lg-4"></div>

				<!-- 우측 상단 변경 -->
				<c:choose>
					<c:when test="${isLogOn == true && member != null}">
						<form action="/cocoa/logout" method="get" class="d-flex">
							<input name="My Page" class="btn btn-outline-dark" type="button"
								value="My Page"
								onClick="location.href='/cocoa/view_myPageProfile'" /> &nbsp;<input
								name="logout" class="btn btn-outline-dark" type="submit"
								value="logout" />
						</form>
					</c:when>
					<c:otherwise>
						<form action="/cocoa/view_login" method="get" class="d-flex">
							<input name="login" class="btn btn-outline-dark" type="submit"
								value="log in" /> &nbsp;<input name="join"
								class="btn btn-outline-dark" type="button" value="Sign in"
								onClick="location.href='/cocoa/view_join'" />
						</form>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</nav>

	<!-- 프로필 내용 바 -->
	<form name="proFileInfo" method="post"
		action="${contextPath}/proFileInfo" enctype="multipart/form-data">
		<section class="py-5">
			<div class="container main-secction">
				<div class="row" style="flex-wrap: unset;">

					<!-- 좌측 프로필 : proImg / member.id -->
					<div class="col-md-3 col-sm-3 col-xs-12 user-profil-part pull-left">
						<div class="row">
							<div
								class="col-md-12 col-md-12-sm-12 col-xs-12 user-image text-center"
								style="width: 80%; height: 100%; border: 2px solid; background-color: #FFCCCC;">

								<!-- 프로필 조회 이동 -->
								<br> <br> <img name="proImg"
									src="${contextPath}/downProfileImg?id=${profileId.id }"
									onerror="this.src='resources/image/kakao.png'"
									style="border: 1px solid;" width="50%" height="120px"><br>
								<br>


								<!-- 해당 코치나 프로젝트 member.id -->
								<input type="text" name="id" value="${profileId.id }" readonly
									style="text-align: center; border: 0; font-weight: 700; background-color: #FFCCCC; width: 70%;"><br>
								<br>
							</div>
						</div>
					</div>

					<!-- 우측 내용 : pContents -->
					<div class="card"
						style="width: 50rem; border: 1px solid; background-color: #FFCC99;">
						<div class="proFile">

							<!-- pContents 입력 -->
							<!-- textarea 닫아주는거 붙여써야함 -->
							<br> <b> 소개 및 경력 : </b><br> <br>
							<textarea name="proFile" rows="15" cols="20"
								style="border: 1; width: 100%; background-color: #FFCC99; resize: none;">${profileId.proContents}</textarea>
							<br> <br>
						</div>
					</div>
				</div>
			</div>
		</section>
	</form>

	<!-- 하단바 (마지막에 추가) -->
	<footer class="py-5 bg-dark">
		<div class="container">
			<p class="m-0 text-center text-white">Copyright &copy; CoCoa 2021</p>
		</div>
	</footer>
</body>
</html>