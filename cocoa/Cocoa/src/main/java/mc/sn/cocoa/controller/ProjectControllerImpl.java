package mc.sn.cocoa.controller;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import mc.sn.cocoa.service.ProjectService;
import mc.sn.cocoa.service.ReviewService;
import mc.sn.cocoa.vo.Criteria;
import mc.sn.cocoa.vo.MemberVO;
import mc.sn.cocoa.vo.PageMaker;
import mc.sn.cocoa.vo.ProjectVO;
import net.coobird.thumbnailator.Thumbnails;

@Controller("projectController")
public class ProjectControllerImpl implements ProjectController {

	// 저장되는 경로
	private static final String project_IMAGE_REPO = "/opt/cocoa/image/project_image";
	@Autowired
	private ProjectService projectService;
	@Autowired
	private ReviewService reviewService;

	// 프로젝트 글 조회
	@Override
	@RequestMapping(value = "/view_projectCate", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView view_projectCate(HttpServletRequest request, HttpServletResponse response, Criteria cri)
			throws Exception {
		ModelAndView mav = new ModelAndView();

		// 내가 쓴 글을 선택시 실행해서 projectOrder 설정
		String projectOrder = cri.getProjectOrder();
		if (projectOrder.equals("and leader like")) {
			HttpSession session = request.getSession();
			MemberVO vo = (MemberVO) session.getAttribute("member");
			String id = vo.getId();
			String project = projectOrder + "'" + id + "'";
			cri.setProjectOrder(project);
		}

		// 쪽 번호 생성 메서드 객체 생성
		PageMaker pageMaker = new PageMaker();

		// 쪽 번호와 한 페이지에 게시할 글의 수 세팅
		pageMaker.setCri(cri);

		// 총 게시글의 수
		pageMaker.setTotalCount(projectService.countProject(cri));

		// 서비스에서 listCoaches() 메소드 실행하여 리턴 값을 List타입의 coachesList에 저장
		List projectList = projectService.listProjects(cri);

		// 맵 생성
		Map<String, Object> reCountMap = new HashMap<String, Object>();

		// 리스트 생성해서 target들을 불러오고 불러온 값들로 for문을 돌려서 리뷰 갯수를 불러옴->맵에 저장
		List target = reviewService.targetsReview();

		for (int i = 0; i < target.size(); i++) {
			String key = (String) target.get(i);
			int value = reviewService.targetReviewCount(key);
			reCountMap.put(key, value);
		}

		// 맵 생성
		Map<String, Object> reAvgMap = new HashMap<String, Object>();

		// target들을 불러오고 불러온 값들로 for문을 돌려서 리뷰 갯수를 불러옴->맵에 저장
		for (int i = 0; i < target.size(); i++) {
			String key = (String) target.get(i);
			float value = reviewService.targetReviewAvg(key);
			reAvgMap.put(key, value);
		}

		// mav에 reAvg 키값으로 reAvgMap 밸류 값을 저장
		mav.addObject("reAvg", reAvgMap);

		// mav에 reCount 키값으로 reCountMap 밸류 값을 저장
		mav.addObject("reCount", reCountMap);

		// mav에 "coachesList" 키값으로 coachesList 밸류 값을 저장
		mav.addObject("projectList", projectList);

		mav.addObject("pageMaker", pageMaker);

		mav.addObject("cri", cri);

		String url = "/projectCate";
		mav.setViewName(url);

		return mav;
	}

	// 프로젝트 글 작성 창으로 이동
	@Override
	@RequestMapping(value = "/view_projectWrite", method = RequestMethod.GET)
	public ModelAndView view_projectWrite(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		String url = "/project/projectWriteForm";
		mav.setViewName(url);
		return mav;
	}

	// 프로젝트 상세 이동
	@Override
	@RequestMapping(value = "/view_projectInfo", method = RequestMethod.GET)
	public ModelAndView projectInfo(@RequestParam("id") String id, @RequestParam("projectNO") int projectNO,
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		request.setCharacterEncoding("utf-8");
		ModelAndView mav = new ModelAndView();
		ProjectVO projectVO = new ProjectVO();
		projectVO.setLeader(id);
		projectVO.setProjectNO(projectNO);
		ProjectVO vo = projectService.searchProject(projectVO);
		mav.addObject("projectInfo", vo);
		String url = "/project/projectInfo";
		mav.setViewName(url);
		return mav;
	}

	// 프로젝트 글 작성
	@Override
	@RequestMapping(value = "/projectWrite", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity addNewProject(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)
			throws Exception {

		multipartRequest.setCharacterEncoding("utf-8");

		// DB에 담을 프로젝트 글 정보
		Map<String, Object> projectMap = new HashMap<String, Object>();

		// 받아온 정보들을 projectMap에 [key & value]로 설정
		Enumeration enu = multipartRequest.getParameterNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			String value = multipartRequest.getParameter(name);
			projectMap.put(name, value);
		}

		// 세션 불러오기
		HttpSession session = multipartRequest.getSession();

		// memberVO로 세션에 저장된 로그인한 회원의 정보를 저장
		MemberVO memberVO = (MemberVO) session.getAttribute("member");

		// projectWriteForm에 존재하지 않는 id 직접 입력
		String id = memberVO.getId();
		projectMap.put("leader", id);
		// projectWriteForm에 불러온 파일(이미지) 직접 입력
		String pImg = this.upload(multipartRequest);
		projectMap.put("pImg", pImg);

		// 성공, 실패 시 알림
		String message;
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");

		try {
			// 다운로드 파일을 작성하는 회원의 id(leader)로 폴더 생성
			// 삭제가 되어도 그 후에 해당 회원이 업로드를 하면 같은 폴더에 생성이 됌
			// 삭제 로직 짤때 고려해야함
			int projectNO = projectService.addNewProject(projectMap);
			if (pImg != null && pImg.length() != 0) {
				File srcFile = new File(project_IMAGE_REPO + "/" + "temp" + "/" + pImg);
				File destDir = new File(project_IMAGE_REPO + "/" + id + "/" + projectNO);
				FileUtils.moveFileToDirectory(srcFile, destDir, true);
			}

			message = "<script>";
			message += " alert('등록이 완료되었습니다.');";
			message += " location.href='" + multipartRequest.getContextPath() + "/view_projectCate'; ";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);

		} catch (Exception e) {
			// 예외발생시 취소 및 삭제
			File srcFile = new File(project_IMAGE_REPO + "/" + "temp" + "/" + pImg);
			srcFile.delete();

			message = " <script>";
			message += " alert('오류가 발생했습니다. 다시 시도해주세요.');');";
			message += " location.href='" + multipartRequest.getContextPath() + "/view_projectCate'; ";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
			e.printStackTrace();
		}
		return resEnt;
	}

	// 프로젝트 글 삭제
	@Override
	@RequestMapping(value = "/removeProject", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity removeProject(@RequestParam("projectNO") int projectNO, @RequestParam("leader") String id,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/html; charset=UTF-8");
		System.out.println(projectNO + "," + id);
		String message;
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		try {
			projectService.removeProject(projectNO);
			File destDir = new File(project_IMAGE_REPO + "/" + id + "/" + projectNO);
			FileUtils.deleteDirectory(destDir);

			message = "<script>";
			message += " alert('프로젝트 게시글을 삭제하였습니다');";
			message += " location.href='" + request.getContextPath() + "/view_projectCate';";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);

		} catch (Exception e) {
			message = "<script>";
			message += " alert('삭제에 실패했습니다');";
			message += " location.href='" + request.getContextPath() + "/view_projectCate';";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
			e.printStackTrace();
		}
		return resEnt;

	}

	// 파일 업로드
	private String upload(MultipartHttpServletRequest multipartRequest) throws Exception {

		String pImg = null;
		Iterator<String> fileNames = multipartRequest.getFileNames();

		while (fileNames.hasNext()) {

			String fileName = fileNames.next();
			MultipartFile mFile = multipartRequest.getFile(fileName);
			pImg = mFile.getOriginalFilename();

			File file = new File(project_IMAGE_REPO + "/" + "temp" + "/" + fileName);

			if (mFile.getSize() != 0) {
				if (!file.exists()) {
					if (file.getParentFile().mkdirs()) {
						file.createNewFile();
					}
				}
				mFile.transferTo(new File(project_IMAGE_REPO + "/" + "temp" + "/" + pImg));
			}
		}
		return pImg;
	}

	// 이미지파일 썸네일로 다운로드
	@RequestMapping("/pthumbnails")
	// RequestParam으로 key&value 값을 가져와 변수에 저장
	protected void pthumbnails(@RequestParam("pImg") String pImg, @RequestParam("leader") String leader,
			@RequestParam("projectNO") String projectNO, HttpServletResponse response) throws Exception {
		OutputStream out = response.getOutputStream();
		// 파일 경로
		String filePath = project_IMAGE_REPO + "/" + leader + "/" + projectNO + "/" + pImg;
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

	// 프로젝트 이미지 다운로드
	@RequestMapping("/download")
	protected void download(@RequestParam("pImg") String pImg, @RequestParam("leader") String leader,
			@RequestParam("projectNO") String projectNO, HttpServletResponse response) throws Exception {
		OutputStream out = response.getOutputStream();
		String filePath = project_IMAGE_REPO + "/" + leader + "/" + projectNO + "/" + pImg;
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

	// 프로젝트 글 수정
	@Override
	@RequestMapping(value = "/modProject", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity modProject(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)
			throws Exception {
		multipartRequest.setCharacterEncoding("utf-8");
		Map<String, Object> projectMap = new HashMap<String, Object>();
		Enumeration enu = multipartRequest.getParameterNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			String value = multipartRequest.getParameter(name);
			projectMap.put(name, value);
		}
		HttpSession session = multipartRequest.getSession();

		String pImg = upload(multipartRequest);
		projectMap.put("pImg", pImg);

		String projectNO = (String) projectMap.get("projectNO");
		String leader = (String) projectMap.get("leader");
		String message;
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		try {
			projectService.modProject(projectMap);
			if (pImg != null && pImg.length() != 0) {
				String originalFileName = (String) projectMap.get("originalFileName");
				File oldFile = new File(project_IMAGE_REPO + "/" + leader + "/" + projectNO + "/" + originalFileName);
				oldFile.delete();

				File srcFile = new File(project_IMAGE_REPO + "/" + "temp" + "/" + pImg);
				File destDir = new File(project_IMAGE_REPO + "/" + leader + "/" + projectNO);
				FileUtils.moveFileToDirectory(srcFile, destDir, true);
			}
			message = "<script>";
			message += " alert('수정이 완료되었습니다.');";
			message += " location.href='" + multipartRequest.getContextPath() + "/view_projectCate'; ";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		} catch (Exception e) {
			// 예외발생시 취소 및 삭제
			File srcFile = new File(project_IMAGE_REPO + "/" + "temp" + "/" + pImg);
			srcFile.delete();

			message = " <script>";
			message += " alert('오류가 발생했습니다. 다시 시도해주세요.');');";
			message += " location.href='" + multipartRequest.getContextPath() + "/view_projectCate'; ";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		}
		return resEnt;
	}

	// 맵 정보 추가
	@Override
	@ResponseBody
	@RequestMapping(value = "/map", method = RequestMethod.GET, produces = "application/text; charset=UTF-8")
	public void getGeo(HttpServletRequest req, HttpServletResponse res) throws IOException {
		req.setCharacterEncoding("utf-8");
		String addr = req.getParameter("addr");
		res.setContentType("text/text; charset=utf-8");
		String result = projectService.geocode(addr);
		res.getWriter().print(result);
	}
}