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
<script type="text/javascript"
	src="https://openapi.map.naver.com/openapi/v3/maps.js?ncpClientId=7kuhr3ly8q"></script>
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

	$(document).ready(function() {
		$('#map').hide();
		// 맵 geoCode
		$('#sendMark').click(function() {
			$('#map').show();
			event.preventDefault();
			$.ajax({
				type : "get",
				url : "/cocoa/map",
				contentType : "application/json",
				data : {
					"addr" : $("#addr").val()
				},
				success : function(data, textStatus) {
					resultText = JSON.parse(data);
					var lang1 = resultText.addresses[0].x;
					var lat1 = resultText.addresses[0].y;
					var mapOptions = {
						center : new naver.maps.LatLng(lat1, lang1),
						zoom : 15
					};
					var map = new naver.maps.Map('map', mapOptions);
					var marker = new naver.maps.Marker({
						position : new naver.maps.LatLng(lat1, lang1),
						map : map
					});
				},
				error : function(data, textStatus) {
					alert("에러가! 발생했습니다.");
				},
				complete : function(data, textStatus) {
				}
			});
		});
	});

	function nullCheck() {
		var _kakao = $("#kakao").val();
		var _pTitle = $("#pTitle").val();
		var _memberCount = $("#memberCount").val();
		var _pField = $("#pField").val();
		var _level = $("#level").val();
		var _pContents = $("#pContents").val();
		if (_kakao == "") {
			alert("카카오 오픈채팅 링크를 입력하세요");
			$('#projectWrite').attr('onSubmit', "return false;");
		} else if (_pTitle == "") {
			alert("제목을 입력하세요");
			$('#projectWrite').attr('onSubmit', "return false;");
		} else if (_memberCount == "") {
			alert("인원을 입력하세요");
			$('#projectWrite').attr('onSubmit', "return false;");
		} else if (_pField == "-- 선택 --") {
			alert("영역을 선택해주세요");
			$('#projectWrite').attr('onSubmit', "return false;");
		} else if (level == "-- 선택 --") {
			alert("수준을 선택해주세요");
			$('#projectWrite').attr('onSubmit', "return false;");
		} else if (_pContents == "") {
			alert("내용을 입력하세요");
			$('#projectWrite').attr('onSubmit', "return false;");
		} else {
			$('#projectWrite').removeAttr('onSubmit');
		}
	}
</script>
<title>CoCoa</title>
</head>
<body style="background-color: #FFEBCD">

	<!-- 상단바 -->
	<jsp:include page="../header.jsp"></jsp:include>

	<!-- 프로젝트 글 작성 -->
	<form action="${contextPath}/projectWrite" method="post"
		id="projectWrite" enctype="multipart/form-data">
		<section class="py-5">
			<div class="container main-secction">
				<div class="row" style="flex-wrap: unset;">

					<!-- 좌측 프로필 : leader~proImg / leader~name -->
					<div class="col-md-3 col-sm-3 col-xs-12 user-profil-part pull-left">
						<div class="row">
							<div
								class="col-md-12 col-md-12-sm-12 col-xs-12 user-image text-center"
								style="width: 80%; height: 100%; border: 2px solid; background-color: #FFCCCC;">

								<!-- 프로필 조회 이동 -->
								<br> <br> <img name="proImg"
									src="${contextPath}/downProfileImg?id=${member.id}"
									style="border: 1px solid;" width="50%" height="120px"
									onerror="this.src='resources/image/onerror.png'"><br>
								<br>

								<!-- leader -->
								<input type="text" name="leader" value="${member.id}" readonly
									style="text-align: center; border: 0; font-weight: 700; background-color: #FFCCCC; width: 70%;"><br>
								<br>

								<!-- kakao -->
								<b>카카오톡 오픈채팅 :</b><br> <br> <input type="text"
									name="kakao" placeholder="링크 입력" id="kakao"
									style="text-align: center; border: 1; background-color: #FFCCCC; width: 80%;"><br>
								<br>
							</div>
						</div>
					</div>

					<!-- 우측 내용 : pImg / pTitle / memberCount / pContents -->
					<div class="card px-3"
						style="width: 50rem; border: 1px solid; background-color: #FFCC99">

						<!-- pImg -->
						<div align="center">
							<br> <img id="preview" src="resources/image/onerror.png"
								width=100% height=300 style="border: 1px solid;" /><br> <br>
							<label class="btn btn-outline-dark" for="pImg"> 대표 이미지 변경
							</label><input type="file" id="pImg" name="pImg"
								onchange="readURL(this);" style="display: none;" />
						</div>

						<!-- pTitle / memberCount / pContents 입력 -->
						<div class="project">

							<!-- pTitle 입력 -->
							<hr>
							<input name="pTitle" type="text" placeholder="제목을 입력하세요."
								id="pTitle" style="border: 1; text-align: center; width: 100%;">
							<hr>

							<!-- memberCount 입력 -->
							인원 : <input name="memberCount" type="number" id="memberCount"
								placeholder="인원수를 입력하세요." style="border: 1; width: 30%;">&nbsp;<b>명</b>
							<hr>

							<!-- pField 선택 -->
							분야 : <select style="text-align: center; width: 30%;" id="pField"
								name="pField">
								<option id="empty">-- 선택 --</option>
								<option id="pField1" value="pField1">Web</option>
								<option id="pField2" value="pField2">Mobile App</option>
								<option id="pField3" value="pField3">Embedded</option>
							</select>
							<hr>

							<!-- level 선택 -->
							난이도 : <select style="text-align: center; width: 30%;" id="level"
								name="level">
								<option id="empty">-- 선택 --</option>
								<option id="level1" value="level1">Basic</option>
								<option id="level2" value="level2">Intermediate</option>
								<option id="level3" value="level3">Advanced</option>
							</select>
							<hr>

							<!-- pContents 입력 -->
							<!-- textarea 닫아주는거 붙여써야함 -->
							세부 내용 : <br> <br>
							<textarea name="pContents" rows="10" cols="20" id="pContents"
								placeholder="프로젝트 개요 및 포지션 별 자격요건을 써주세요. (공백포함 2000자 이내)"
								style="border: 1; width: 100%; resize: none;"></textarea>
							<hr>

							&nbsp;모임 장소 : <input type="text" name="map" id="addr" size="35"
								placeholder='장소를 입력해주세요.'> <input type="button"
								name="send" id="sendMark" value="검색"><br> <br>
							<div id="map" style="width: 100%; height: 400px;"></div>
							<hr>

						</div>

						<!-- 작성(submit) + 취소(버튼) -->
						<div class="card-body" style="text-align: center">
							<button type="submit" class="btn btn-outline-dark"
								onclick="nullCheck()">작성</button>
							&nbsp; <a href="/cocoa/view_projectCate"
								class="btn btn-outline-dark">취소</a>
						</div>
					</div>

				</div>
			</div>
		</section>
	</form>

	<!-- 하단바 -->
	<jsp:include page="../footer.jsp"></jsp:include>

</body>
</html>