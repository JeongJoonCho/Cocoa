package mc.sn.cocoa.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mc.sn.cocoa.service.MemberService;
import mc.sn.cocoa.vo.MemberVO;

@Controller("memberController")
public class MemberControllerImpl implements MemberController {

	// 프로필 이미지 저장 경로
	private static final String profile_IMAGE_REPO = "C:\\cocoa\\profile_image";
	@Autowired
	private MemberService memberService;

	// 회원가입 화면으로 이동
	@Override
	@RequestMapping(value = "/view_join", method = RequestMethod.GET)
	public ModelAndView view_join(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();
		String url = "/joinForm";
		mav.setViewName(url);
		return mav;
	}

	// 로그인 화면으로 이동
	@Override
	@RequestMapping(value = "/view_login", method = RequestMethod.GET)
	public ModelAndView view_login(@RequestParam(value = "result", required = false) String result,
			HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();
		String url = "/loginForm";
		mav.addObject("result", result);
		mav.setViewName(url);
		return mav;
	}

	// 회원가입
	@Override
	@ResponseBody
	@RequestMapping(value = "/join", method = RequestMethod.POST)
	public int join(@ModelAttribute("member") MemberVO memberVO, HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		request.setCharacterEncoding("utf-8");
		int result = 0;
		result = memberService.joinMember(memberVO);
		return result;
	}

	// 타인 프로필 화면 이동
	@RequestMapping(value = "/view_profileInfo", method = RequestMethod.GET)
	public ModelAndView view_proFileInfo(@RequestParam("profileId") String id, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		System.out.println(id);
		MemberVO memberVO = memberService.searchMember(id);
		System.out.println(memberVO.getName());
		mav.addObject("profileId", memberVO);
		String url = "/profileInfo";
		mav.setViewName(url);
		return mav;
	}

	// 로그인
	@Override
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView login(@ModelAttribute("member") MemberVO member, RedirectAttributes rAttr,
			HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();
		MemberVO memberVO = memberService.login(member);
		if (memberVO != null) {
			HttpSession session = request.getSession();
			session.setAttribute("member", memberVO);
			session.setAttribute("isLogOn", true);
			mav.setViewName("redirect:/");
		} else {
			rAttr.addAttribute("result", "loginFailed");
			mav.setViewName("redirect:/view_login");
		}
		return mav;
	}

	// 로그아웃
	@Override
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		session.removeAttribute("member");
		session.removeAttribute("isLogOn");
		ModelAndView mav = new ModelAndView();
		mav.setViewName("redirect:/");
		return mav;
	}

	// 아이디 체크
	@ResponseBody
	@RequestMapping(value = "/idChk", method = RequestMethod.POST)
	public int idChk(MemberVO vo) throws Exception {
		int result = 0;
		result = memberService.idChk(vo);
		return result;
	}

	// 파일 업로드 (프로필사진)
	private String upload(MultipartHttpServletRequest multipartRequest) throws Exception {

		String proImg = null;
		Iterator<String> fileNames = multipartRequest.getFileNames();

		while (fileNames.hasNext()) {

			String fileName = fileNames.next();
			MultipartFile mFile = multipartRequest.getFile(fileName);
			proImg = mFile.getOriginalFilename();

			File file = new File(profile_IMAGE_REPO + "\\" + "temp" + "\\" + fileName);

			if (mFile.getSize() != 0) {
				if (!file.exists()) {
					if (file.getParentFile().mkdirs()) {
						file.createNewFile();
					}
				}
				mFile.transferTo(new File(profile_IMAGE_REPO + "\\" + "temp" + "\\" + proImg));
			}
		}
		return proImg;
	}

	// 프로필 이미지 가져오기
	@RequestMapping("/downProfileImg")
	protected void download(@RequestParam("id") String id, HttpServletResponse response) throws Exception {
		OutputStream out = response.getOutputStream();
		MemberVO vo = memberService.searchMember(id);
		String proImg = vo.getproImg();
		String filePath = profile_IMAGE_REPO + "\\" + id + "\\" + proImg;
		File image = new File(filePath);

		response.setHeader("Cache-Control", "no-cache");
		response.addHeader("Content-disposition", "attachment; fileName=" + proImg);
		FileInputStream in = new FileInputStream(image);
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

	// 프로필 수정
	@RequestMapping(value = "/modProfile", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity modProfile(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)
			throws Exception {
		multipartRequest.setCharacterEncoding("utf-8");
		Map<String, Object> profileMap = new HashMap<String, Object>();
		Enumeration enu = multipartRequest.getParameterNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			String value = multipartRequest.getParameter(name);
			profileMap.put(name, value);
		}
		HttpSession session = multipartRequest.getSession();

		String proImg = upload(multipartRequest);
		profileMap.put("proImg", proImg);

		String id = (String) profileMap.get("id");
		String message;
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		try {
			memberService.modProfile(profileMap);
			if (proImg != null && proImg.length() != 0) {
				File srcFile = new File(profile_IMAGE_REPO + "\\" + "temp" + "\\" + proImg);
				File destDir = new File(profile_IMAGE_REPO + "\\" + id);
				FileUtils.moveFileToDirectory(srcFile, destDir, true);

				String originalFileName = (String) profileMap.get("originalFileName");
				File oldFile = new File(profile_IMAGE_REPO + "\\" + id + "\\" + originalFileName);
				oldFile.delete();
			}
			message = "<script>";
			message += " alert('수정이 완료되었습니다.');";
			message += " location.href='" + multipartRequest.getContextPath() + "/'; ";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		} catch (Exception e) {
			// 예외발생시 취소 및 삭제
			File srcFile = new File(profile_IMAGE_REPO + "\\" + "temp" + "\\" + proImg);
			srcFile.delete();

			message = " <script>";
			message += " alert('오류가 발생했습니다. 다시 시도해주세요.');');";
			message += " location.href='" + multipartRequest.getContextPath() + "/'; ";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		}
		return resEnt;
	}

	// 마이페이지 이동
	@Override
	@RequestMapping(value = "/view_myPageProfile", method = RequestMethod.GET)
	public ModelAndView view_myPageProfile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		MemberVO vo = (MemberVO) session.getAttribute("member");
		String id = vo.getId();
		MemberVO memberVO = memberService.searchMember(id);
		System.out.println(memberVO.getName());
		mav.addObject("profileId", memberVO);
		String url = "/myPageProfile";
		mav.setViewName(url);
		return mav;
	}
}