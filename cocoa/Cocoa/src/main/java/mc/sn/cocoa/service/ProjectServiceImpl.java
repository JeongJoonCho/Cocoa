package mc.sn.cocoa.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import mc.sn.cocoa.dao.ProjectDAO;
import mc.sn.cocoa.vo.Criteria;
import mc.sn.cocoa.vo.ProjectVO;

@Service("projectService")
@Transactional(propagation = Propagation.REQUIRED)
public class ProjectServiceImpl implements ProjectService {
	@Autowired
	private ProjectDAO projectDAO;

	// 새 프로젝트 글 삽입
	@Override
	public int addNewProject(Map projectMap) {
		return projectDAO.insertNewProject(projectMap);
	}

	// 프로젝트 글 조회
	@Override
	public List<ProjectVO> listProjects(Criteria cri) throws Exception {
		List projectsList = null;
		projectsList = projectDAO.selectAllProjectList(cri);
		return projectsList;
	}

	@Override
	public ProjectVO searchProject(ProjectVO projectVO) {
		ProjectVO vo = null;
		vo = projectDAO.selectProjectById(projectVO);
		return vo;
	}

	// 프로젝트 삭제
	@Override
	public void removeProject(int projectNO) {
		projectDAO.deleteProject(projectNO);
	}

	// 프로젝트 수정
	@Override
	public void modProject(Map projectMap) throws Exception {
		projectDAO.updateProject(projectMap);
	}

	// 코칭 글 개수
	@Override
	public int countProject(Criteria cri) throws Exception {
		return projectDAO.countProject(cri);
	}

	// 맵 정보 추가
	@Override
	public String geocode(String words) {
		StringBuffer res = null;
		String clientId = "7kuhr3ly8q";
		String clientSecret = "5DuApQHCQiNVjVewHsnzRF2KvduZsJwlwi7xIJzs";
		try {
			String text = URLEncoder.encode(words, "UTF-8");
			String apiURL = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=" + text;
			URL url = new URL(apiURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
			con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
			con.setDoOutput(true);

			int responseCode = con.getResponseCode();
			BufferedReader br;
			if (responseCode == 200) {
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			}
			String inputLine;
			res = new StringBuffer();
			while ((inputLine = br.readLine()) != null) {
				res.append(inputLine);
			}
			br.close();
			// System.out.println("service " + res.toString());
		} catch (Exception e) {
			// System.out.println(e);
		}

		return res.toString();
	}

}