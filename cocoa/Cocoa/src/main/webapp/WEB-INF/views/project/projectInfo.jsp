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
		$('#p_pImgMod').hide(); //페이지를 로드할 때 숨길 요소
		$('#p_modBtn').hide();
		$('#mod_kakaoLink').hide();
		$('#sendMark').hide();
		$('#p_mod').show();
		$('#p_rmv').show();
		$('#p_mod').click(function() {
			$('#p_mod').hide(); //클릭 시 첫 번째 요소 숨김
			$('#p_rmv').hide(); //클릭 시 첫 번째 요소 숨김
			$('#p_pImgMod').show(); //클릭 시 두 번째 요소 표시
			$('#mod_kakaoLink').show();
			$('#sendMark').show();
			$('#kakaoLink').hide();
			$('#p_modBtn').show();
			$('#p_pTitle').prop('disabled', false);
			$('#p_pField').prop('disabled', false);
			$('#p_pLevel').prop('disabled', false);
			$('#p_pMemberCount').prop('disabled', false);
			$('#p_pContents').prop('disabled', false);
			$('#addr').prop('disabled', false);
			$('#getpField').attr('hidden', '');
			return false;
		});

		// 맵
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

		$('#sendMark').click(function() {
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

	// 영역 변경시 개발툴 초기화 셋팅
	function categoryChange(e) {
		var level_pField_value = [ "level", "level1", "level2", "level3" ];
		var level_pField_out = [ "-- 선택 --", "Basic", "Intermediate",
				"Advanced" ];
		var target = document.getElementById("p_pLevel");
		var v = level_pField_value;
		var o = level_pField_out;
		target.options.length = 0;
		for (x in v, o) {
			var opt = document.createElement("option");
			opt.value = v[x];
			opt.innerHTML = o[x];
			target.appendChild(opt);
		}
	}

	function nullCheck() {
		var _pTitle = $("#p_pTitle").val();
		var _pMemberCount = $("#p_pMemberCount").val();
		var _level = $("#p_pLevel").val();
		var _pContents = $("#p_pContents").val();
		if (_pTitle == "" || _level == "" || _pContents == "") {
			alert("빈칸없이 입력하세요.");
			$('#projectInfo').attr('onSubmit', "return false;");
		} else if (_pMemberCount < 2) {
			alert("인원 수를 확인하세요. (최소 인원 수 : 2명)");
			$('#projectInfo').attr('onSubmit', "return false;");
		} else {
			$('#projectInfo').removeAttr('onSubmit');
		}
	}
</script>
<title>CoCoa</title>
</head>
<body style="background-color: #FFEBCD">

	<!-- 상단바 -->
	<jsp:include page="../header.jsp"></jsp:include>

	<!-- 프로젝트 글 구간 -->
	<form method="post" enctype="multipart/form-data" name="frmProject"
		action="${contextPath}/modProject" id="projectInfo">
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
								<br> <br> <a
									href="/cocoa/view_profileInfo?profileId=${projectInfo.leader}">
									<img name="proImg"
									src="${contextPath}/downProfileImg?id=${projectInfo.leader }"
									onerror="this.src='resources/image/onerror.png'"
									style="border: 1px solid black;" width="50%" height="120px"><br>
									<br>
								</a>

								<!-- leader -->
								<input type="text" name="leader" value="${projectInfo.leader}"
									readonly
									style="outline: none; text-color: black; text-align: center; border: 0; font-weight: 700; background-color: #FFCCCC; width: 70%"><br>
								<input type="hidden" name="projectNO"
									value="${projectInfo.projectNO}" /> <br>

								<!-- 후기 조회 이동 -->
								<span style="text-align: center;"><a
									href="/cocoa/view_reviewInfo?target=${projectInfo.leader}">
										<input type="button" name="view_reviewInfo" value="후기보기"
										class="btn btn-third-dark"
										style="font-size: 15px; border-radius: 12px; width: 50%;">
								</a> </span><br> <br>

								<!-- kakao -->
								<!-- 본인 검증 -->
								<c:if
									test="${isLogOn == true && member.id !=projectInfo.leader}">
									<a href="${projectInfo.kakao}"> <input type="button"
										id="kakaoLink" name="kakaoLink" value="   대화하기   "
										class="btn btn-third-dark"
										style="text-align: center; border: 1; border-radius: 12px;">
									</a>
								</c:if>

								<!-- 카카오톡 링크 (수정시) -->
								<div id="mod_kakaoLink">
									<b>카카오톡 오픈채팅 :</b><br> <br> <input type="text"
										name="kakao" id="mod_kakaoLink" name="kakao"
										value="${projectInfo.kakao}"
										style="text-align: center; border: 1; background-color: #FFCCCC;">
								</div>

								<!-- 본인이면 수정(submit) / 삭제(버튼) 표시 -->
								<c:choose>
									<c:when
										test="${isLogOn == true && member.id ==projectInfo.leader}">
										<input type="submit" class="btn btn-third-dark" value="수정"
											id="p_mod"> &nbsp;
								<input type="button" class="btn btn-third-dark" value="삭제"
											id="p_rmv"
											onClick="location.href='${contextPath}/removeProject?leader=${projectInfo.leader}&projectNO=${projectInfo.projectNO}'">
									</c:when>
								</c:choose>
								<br> <br>
							</div>
						</div>
					</div>

					<!-- 우측 내용 : pImg / pTitle / memberCount / level / pContents -->
					<div class="card px-3"
						style="width: 50rem; border: 1px solid; background-color: #FFCC99">

						<!-- pImg -->
						<div align="center">
							<input type="hidden" name="originalFileName"
								value="${projectInfo.pImg }" /> <br> <img id="preview"
								src="${contextPath}/download?leader=${projectInfo.leader}&pImg=${projectInfo.pImg}&projectNO=${projectInfo.projectNO}"
								width=90% height=300 style="border: 1px solid;"
								onerror="this.src='resources/image/onerror.png'" /><br> <br>
							<label class="btn btn-outline-dark" for="p_pImg" id="p_pImgMod">대표
								이미지 변경 </label> <input type="file" id="p_pImg" name="pImg"
								onchange="readURL(this);" style="display: none;" /> <br> <br>
						</div>

						<!-- pTitle / memberCount / level / pContents 조회 -->
						<div class="project">

							<!-- pTitle 표시 -->
							<hr>
							<input name="pTitle" type="text" value="${projectInfo.pTitle}"
								id="p_pTitle" disabled
								style="border: 0; text-align: center; width: 100%; background-color: #FFCC99; font-weight: 700; color: black;">
							<hr>

							<!-- memberCount 표시 -->
							인원 : <input name="memberCount" type="number"
								value="${projectInfo.memberCount}" id="p_pMemberCount" disabled
								style="border: 0; width: 5%; text-align: center; background-color: #FFCC99; font-weight: 700; color: black;">
							<b>명</b>
							<hr>

							<!-- pField 표시 -->
							영역 : <select
								style="border: 0; text-align: center; width: auto; background-color: #FFCC99; font-weight: 700; color: black;"
								name="pField" disabled id="p_pField"
								onchange="categoryChange(this)">
								<option id="getpField" value="${projectInfo.pField}">
									<c:choose>
										<c:when test="${projectInfo.pField == 'pField1'}">Web</c:when>
										<c:when test="${projectInfo.pField == 'pField2'}">Mobile App</c:when>
										<c:when test="${projectInfo.pField == 'pField3'}">Embedded</c:when>
									</c:choose>
								</option>
								<option id="pField1" value="pField1">Web</option>
								<option id="pField2" value="pField2">Mobile App</option>
								<option id="pField3" value="pField3">Embedded</option>
							</select>
							<hr>

							<!-- level 표시 -->
							등급 : <select
								style="border: 0; text-align: center; width: auto; background-color: #FFCC99; font-weight: 700; color: black;"
								name="level" disabled id="p_pLevel">
								<option id="default" value="${projectInfo.level}">
									<c:choose>
										<c:when test="${projectInfo.level == 'level1'}">Basic</c:when>
										<c:when test="${projectInfo.level == 'level2'}">Intermediate</c:when>
										<c:when test="${projectInfo.level == 'level3'}">Advanced</c:when>
									</c:choose>
								</option>
							</select>
							<hr>

							<!-- pContents 표시 -->
							<!-- textarea 닫아주는거 붙여써야함 -->
							세부 내용 : <br> <br>
							<textarea name="pContents" rows="10" cols="20" id="p_pContents"
								disabled
								style="border: 1; width: 100%; background-color: #FFCC99; font-weight: 700; color: black; resize: none;">${projectInfo.pContents}</textarea>
							<hr>

							<!-- map -->
							&nbsp;모임 장소 <input type="text" name="map" id="addr" size="30"
								value="${projectInfo.map}" disabled> <input
								type="button" name="send" id="sendMark" value="검색"><br>
							<br>
							<div id="map" style="width: 100%; height: 400px;"></div>
							<hr>

							<!-- 수정 확인 -->
							<div align="center">
								<input type="submit" onclick="nullCheck()" value="확 인"
									class="btn btn-outline-dark" id="p_modBtn">&nbsp;&nbsp;<input
									type=button value="목록으로" class="btn btn-outline-dark"
									onClick="history.back()" id="goBack"><br> <br>
							</div>
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