package mc.sn.cocoa.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import mc.sn.cocoa.service.RequestService;
import mc.sn.cocoa.vo.Criteria;
import mc.sn.cocoa.vo.MemberVO;
import mc.sn.cocoa.vo.PageMaker;
import mc.sn.cocoa.vo.RequestVO;
import net.coobird.thumbnailator.Thumbnails;

@Controller("requestController")
public class RequestControllerImpl implements RequestController {

	// 파일 저장 경로
	private static final String request_IMAGE_REPO = "/opt/cocoa/image/request_image";

	@Autowired
	private RequestService requestService;

	// coachInfo.jsp에서 요청서 작성 선택시 실행
	// RequestParam으로 쿼리스트링으로 받아온 "coachId"를 res로 저장
	@Override
	@RequestMapping(value = "/view_reqWriteForm", method = RequestMethod.GET)
	public ModelAndView view_reqWriteForm(@RequestParam("coachId") String res,
			@RequestParam("basicPrice") int basicPrice, HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();
		String url = "/coach/reqWriteForm";
		// 위의 res를 키값 'res'로 addobject
		mav.addObject("res", res);
		mav.addObject("basicPrice", basicPrice);
		mav.setViewName(url);
		// reqWriteForm.jsp를 열었을 때 res object도 같이 보내짐
		return mav;
	}

	// 요청글 작성
	@Override
	@RequestMapping(value = "/requestWrite", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity sendRequest(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)
			throws Exception {
		multipartRequest.setCharacterEncoding("utf-8");

		// DB에 담을 요청글 정보
		Map<String, Object> reqMap = new HashMap<String, Object>();

		// 받아온 정보들을 reqMap에 [key & value]로 설정
		Enumeration enu = multipartRequest.getParameterNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			String value = multipartRequest.getParameter(name);
			reqMap.put(name, value);
		}

		// reqWriteForm에 불러온 파일(이미지) 직접 입력
		String rImg = this.upload(multipartRequest);
		reqMap.put("rImg", rImg);

		// 성공, 실패 시 알림
		String message;
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");

		try {
			// requestService의 sendRequest 메소드에서 요청글 넘버를 리턴받아 가져옴
			int reqNO = requestService.sendRequest(reqMap);
			// temp 폴더에 있던 다운로드 파일을 요청글 넘버로 폴더 생성하여 이동
			if (rImg != null && rImg.length() != 0) {
				File srcFile = new File(request_IMAGE_REPO + "/" + "temp" + "/" + rImg);
				File destDir = new File(request_IMAGE_REPO + "/" + reqNO);
				FileUtils.moveFileToDirectory(srcFile, destDir, true);
			}
			// insert 성공시 메시지창 뜨고 홈화면으로 이동
			message = "<script>";
			message += " alert('요청 신청이 완료되었습니다.');";
			message += " location.href='" + multipartRequest.getContextPath() + "/'; ";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);

		} catch (Exception e) {

			// 예외발생시 취소 및 삭제
			File srcFile = new File(request_IMAGE_REPO + "/" + "temp" + "/" + rImg);
			srcFile.delete();

			message = " <script>";
			message += " alert('오류가 발생했습니다. 다시 시도해주세요.');');";
			message += " location.href='" + multipartRequest.getContextPath() + "/'; ";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
			e.printStackTrace();
		}
		return resEnt;

	}

	// 파일 업로드
	private String upload(MultipartHttpServletRequest multipartRequest) throws Exception {

		String rImg = null;
		Iterator<String> fileNames = multipartRequest.getFileNames();

		while (fileNames.hasNext()) {

			String fileName = fileNames.next();
			MultipartFile mFile = multipartRequest.getFile(fileName);
			rImg = mFile.getOriginalFilename();

			File file = new File(request_IMAGE_REPO + "/" + "temp" + "/" + fileName);

			if (mFile.getSize() != 0) {
				if (!file.exists()) {
					if (file.getParentFile().mkdirs()) {
						file.createNewFile();
					}
				}
				mFile.transferTo(new File(request_IMAGE_REPO + "/" + "temp" + "/" + rImg));
			}
		}
		return rImg;
	}

	// 보낸 요청 리스트 화면 이동
	@Override
	@RequestMapping(value = "/view_sendReq", method = RequestMethod.GET)
	public ModelAndView view_sendReq(HttpServletRequest request, HttpServletResponse response, Criteria cri)
			throws Exception {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();

		MemberVO vo = (MemberVO) session.getAttribute("member");
		String id = vo.getId();

		// id 반영
		cri.setReqId(id);

		// 1페이지에 게시될 글의 수
		int perPageNum = 10;
		cri.setPerPageNum(perPageNum);

		// 쪽 번호 생성 매서드
		PageMaker pageMaker = new PageMaker();

		pageMaker.setCri(cri);

		pageMaker.setTotalCount(requestService.countSendRequest(cri));

		// 보낸 요청 리스트 가져오기
		List reqSentList = requestService.listReqSent(cri);
		mav.addObject("reqSentList", reqSentList);
		mav.addObject("cri", cri);
		mav.addObject("pageMaker", pageMaker);

		String url = "/myPage/myPageSent";
		mav.setViewName(url);
		return mav;
	}

	// 받은 요청 리스트 화면 이동
	@Override
	@RequestMapping(value = "/view_receiveReq", method = RequestMethod.GET)
	public ModelAndView view_receiveReq(HttpServletRequest request, HttpServletResponse response, Criteria cri)
			throws Exception {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();

		MemberVO vo = (MemberVO) session.getAttribute("member");
		String id = vo.getId();

		// id 반영
		cri.setResId(id);

		// 1페이지에 게시될 글의 수
		int perPageNum = 10;
		cri.setPerPageNum(perPageNum);

		// 쪽 번호 생성 매서드
		PageMaker pageMaker = new PageMaker();

		pageMaker.setCri(cri);

		pageMaker.setTotalCount(requestService.countReceiveRequest(cri));

		// 받은 요청 리스트 가져오기
		List reqGotList = requestService.listReqGot(cri);
		mav.addObject("reqGotList", reqGotList);
		mav.addObject("cri", cri);
		mav.addObject("pageMaker", pageMaker);

		String url = "/myPage/myPageGot";
		mav.setViewName(url);
		return mav;
	}

	// 받은 요청 대기글 이동
	@Override
	@RequestMapping(value = "/view_gotReqWait", method = RequestMethod.GET)
	public ModelAndView view_gotReqWait(@RequestParam("reqNO") int reqNO, HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		request.setCharacterEncoding("utf-8");
		ModelAndView mav = new ModelAndView();
		RequestVO vo = requestService.searchRequest(reqNO);
		mav.addObject("requestInfo", vo);
		String url = "/got/gotReqWait";
		mav.setViewName(url);
		return mav;
	}

	// 보낸 요청 대기글 이동
	@Override
	@RequestMapping(value = "/view_sentReqWait", method = RequestMethod.GET)
	public ModelAndView view_sentReqWait(@RequestParam("reqNO") int reqNO, HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		request.setCharacterEncoding("utf-8");
		ModelAndView mav = new ModelAndView();
		// reqNO로 요청글을 search하고 정보들을 vo에 저장후 addObject
		RequestVO vo = requestService.searchRequest(reqNO);
		mav.addObject("requestInfo", vo);
		String url = "/sent/sentReqWait";
		mav.setViewName(url);
		return mav;
	}

	// 요청글 첨부파일 이미지 가져오기
	@RequestMapping("/downRImg")
	protected void download(@RequestParam("reqNO") int reqNO, HttpServletResponse response) throws Exception {
		OutputStream out = response.getOutputStream();
		RequestVO vo = requestService.searchRequest(reqNO);
		String rImg = vo.getrImg();
		String filePath = request_IMAGE_REPO + "/" + reqNO + "/" + rImg;
		File image = new File(filePath);

		if (image.exists()) {
			// 원본 이미지에 대한 썸네일 이미지를 생성한 후 OutputStream 객체에 할당
			Thumbnails.of(image).size(1024, 1024).outputFormat("png").toOutputStream(out);
		}
		// 썸네일 이미지를 OutputStream 객체를 이용해 브라우저로 전송
		byte[] buffer = new byte[1024 * 8];
		out.write(buffer);
		out.close();
	}

	// 보낸 요청글 수정
	@RequestMapping(value = "/modRequest", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity modRequest(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)
			throws Exception {
		multipartRequest.setCharacterEncoding("utf-8");
		Map<String, Object> requestMap = new HashMap<String, Object>();
		Enumeration enu = multipartRequest.getParameterNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			String value = multipartRequest.getParameter(name);
			requestMap.put(name, value);
		}

		String rImg = upload(multipartRequest);
		requestMap.put("rImg", rImg);

		String reqNO = (String) requestMap.get("reqNO");
		String message;
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		try {
			requestService.modRequest(requestMap);
			if (rImg != null && rImg.length() != 0) {
				String originalFileName = (String) requestMap.get("originalFileName");
				File oldFile = new File(request_IMAGE_REPO + "/" + reqNO + "/" + originalFileName);
				oldFile.delete();

				File srcFile = new File(request_IMAGE_REPO + "/" + "temp" + "/" + rImg);
				File destDir = new File(request_IMAGE_REPO + "/" + reqNO);
				FileUtils.moveFileToDirectory(srcFile, destDir, true);
			}
			message = "<script>";
			message += " alert('수정이 완료되었습니다.');";
			message += " location.href='" + multipartRequest.getContextPath() + "/view_sentReqWait?reqNO=" + reqNO
					+ "'; ";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		} catch (Exception e) {
			// 예외발생시 취소 및 삭제
			File srcFile = new File(request_IMAGE_REPO + "/" + "temp" + "/" + rImg);
			srcFile.delete();

			message = " <script>";
			message += " alert('오류가 발생했습니다. 다시 시도해주세요.');');";
			message += " location.href='" + multipartRequest.getContextPath() + "/view_sentReqWait?reqNO=" + reqNO
					+ "'; ";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		}
		return resEnt;
	}

	// 보낸 요청글 삭제
	@Override
	@RequestMapping(value = "/removeRequest", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity removeRequest(@RequestParam("reqNO") int reqNO, HttpServletRequest request,
			HttpServletResponse response) {
		response.setContentType("text/html; charset=UTF-8");
		String message;
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		try {
			requestService.removeRequest(reqNO);
			File destDir = new File(request_IMAGE_REPO + "/" + reqNO);
			FileUtils.deleteDirectory(destDir);

			message = "<script>";
			message += " alert('요청글을 철회하였습니다');";
			message += " location.href='" + request.getContextPath() + "/view_sendReq';";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);

		} catch (Exception e) {
			message = "<script>";
			message += " alert('철회에 실패했습니다');";
			message += " location.href='" + request.getContextPath() + "/view_sendReq';";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
			e.printStackTrace();
		}
		return resEnt;
	}

	// 받은 요청서에서 첨부파일 로컬로 다운로드
	@Override
	@RequestMapping("/downloadGotImg")
	public void downloadGotImg(@RequestParam("reqNO") int reqNO, HttpServletResponse response) throws Exception {
		System.out.println(reqNO);
		RequestVO vo = requestService.searchRequest(reqNO);
		String rImg = vo.getrImg();
		OutputStream out = response.getOutputStream();
		String downFile = request_IMAGE_REPO + "/" + reqNO + "/" + rImg;
		File file = new File(downFile);
		rImg = URLEncoder.encode(rImg, "UTF-8");

		response.setHeader("Cache-Control", "no-cache");
		response.addHeader("Content-disposition", "attachment; fileName=" + rImg);
		FileInputStream in = new FileInputStream(file);
		byte[] buffer = new byte[1024 * 8];
		while (true) {
			int count = in.read(buffer);
			if (count == -1)
				break;
			out.write(buffer, 0, count);
		}
		in.close();
		out.close();
	}

	// 받은 요청 수락 전송 화면 이동
	@Override
	@RequestMapping(value = "/gotWaitYes", method = RequestMethod.GET)
	public ModelAndView view_gotReqYes(@RequestParam("reqNO") int reqNO, HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		request.setCharacterEncoding("utf-8");
		ModelAndView mav = new ModelAndView();
		RequestVO vo = requestService.searchRequest(reqNO);
		mav.addObject("requestInfo", vo);
		String url = "/got/gotReqWaitY";
		mav.setViewName(url);
		return mav;
	}

	// 받은 요청 거절 전송 화면 이동
	@Override
	@RequestMapping(value = "/gotWaitNo", method = RequestMethod.GET)
	public ModelAndView view_gotReqNo(@RequestParam("reqNO") int reqNO, HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		request.setCharacterEncoding("utf-8");
		ModelAndView mav = new ModelAndView();
		mav.addObject("reqNO", reqNO);
		String url = "/got/gotReqWaitN";
		mav.setViewName(url);
		return mav;
	}

	// 거절 사유 전송
	@Override
	@ResponseBody
	@RequestMapping(value = "/submitNoReason", method = RequestMethod.POST)
	public ResponseEntity submitNoReason(@ModelAttribute("request") RequestVO requestVO, HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		request.setCharacterEncoding("utf-8");
		int result = 0;
		result = requestService.submitReason(requestVO);
		String message;
		int reqNO = requestVO.getReqNO();
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		if (result != 0) {
			HttpSession session = request.getSession();
			message = "<script>";
			message += " alert('거절사유를 전송하였습니다.');";
			message += " location.href='" + request.getContextPath() + "/view_receiveReq'; ";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		} else {

			message = " <script>";
			message += " alert('오류가 발생했습니다. 다시 시도해주세요.');');";
			message += " location.href='" + request.getContextPath() + "/gotWaitNo?reqNO=" + reqNO + "'; ";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		}
		return resEnt;
	}

	// 수락 정보 전송
	@Override
	@ResponseBody
	@RequestMapping(value = "/submitReqYes", method = RequestMethod.POST)
	public ResponseEntity submitReqYes(@ModelAttribute("request") RequestVO requestVO, HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		request.setCharacterEncoding("utf-8");
		int result = 0;
		result = requestService.submitReqYes(requestVO);
		String message;
		int reqNO = requestVO.getReqNO();
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		if (result != 0) {
			HttpSession session = request.getSession();
			message = "<script>";
			message += " alert('수락정보를 전송하였습니다.');";
			message += " location.href='" + request.getContextPath() + "/view_receiveReq'; ";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		} else {

			message = " <script>";
			message += " alert('오류가 발생했습니다. 다시 시도해주세요.');');";
			message += " location.href='" + request.getContextPath() + "/gotWaitYes?reqNO=" + reqNO + "'; ";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		}
		return resEnt;
	}
}