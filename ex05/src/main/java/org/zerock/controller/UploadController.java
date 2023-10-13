package org.zerock.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.domain.AttachFileDTO;

import lombok.extern.log4j.Log4j;
import net.coobird.thumbnailator.Thumbnailator;

@Controller
@Log4j
public class UploadController {
	
	
	@GetMapping("/uploadForm")
	public void uploadForm() {
		log.info("업로드폼 요청됨..");
	}
	@PostMapping("/uploadFormAction")
	public String uploadFormAction(MultipartFile[] uploadfile, Model model) {
		log.info("업로드처리 요청됨..");
		
		//해당 날짜의 폴더만들기
		String uploadFolder = "C:\\upload";
		File uploadPath = new File(uploadFolder, getFolder());
		log.info("만들어진 경로: "+uploadPath);
		
		if(!uploadPath.exists()) { //해당 경로가 존재하지않는다면
			uploadPath.mkdirs();	//폴더 생성
		}
		String result = "uploadSuccess";
		for (MultipartFile mf : uploadfile) {
			log.info("=============================");
			log.info("업로드 파일이름: "+mf.getOriginalFilename());
			log.info("업로드 파일 사이즈: "+mf.getSize());
			log.info("getName은 뭐임: "+mf.getName()); //폼에서 보낸 이름(input file의 name값)
			log.info("contentType도있네: "+mf.getContentType()); //헤더에 있는 그친구인듯
			
			String sFileName = UUID.randomUUID().toString()+"_"+mf.getOriginalFilename();
			File saveFile = new File(uploadPath, sFileName);
			try {
				mf.transferTo(saveFile);			
				if(checkImgType(saveFile)) {
					FileOutputStream thumbnail = new FileOutputStream(new File(uploadPath,"s_"+sFileName));
																	  			//크기(가로,세로)
					Thumbnailator.createThumbnail(mf.getInputStream(), thumbnail, 100,100);
					thumbnail.close();
				}
				
			} catch (Exception e) {
				log.error("파일 저장실패");
				result = "uploadFail";
				e.printStackTrace();
			}
		}
		model.addAttribute("filecount", uploadfile.length);
		return result;
	}
	
	@PostMapping(value = "/uploadAjaxAction", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public List<AttachFileDTO> uploadAjaxAction(MultipartFile[] uploadfile) {
		log.info("업로드처리 요청됨..");
		
		List<AttachFileDTO> list = new ArrayList<>();
		
		//해당 날짜의 폴더만들기
		String uploadFolder = "C:\\upload";
		File uploadPath = new File(uploadFolder, getFolder());
		log.info("만들어진 경로: "+uploadPath);
		
		if(!uploadPath.exists()) { //해당 경로가 존재하지않는다면
			uploadPath.mkdirs();	//폴더 생성
		}
		for (MultipartFile mf : uploadfile) {
			
			//logger
			log.info("=============================");
			log.info("업로드 파일이름: "+mf.getOriginalFilename());
			log.info("업로드 파일 사이즈: "+mf.getSize());
			log.info("getName은 뭐임: "+mf.getName()); //폼에서 보낸 이름(input file의 name값)
			log.info("contentType도있네: "+mf.getContentType()); //헤더에 있는 그친구인듯
			//logger end
			
			
			String oFileName = mf.getOriginalFilename();
			String uuid = UUID.randomUUID().toString();
			String sFileName = uuid +"_"+oFileName;
			boolean isImg = false;
			File saveFile = new File(uploadPath, sFileName);
			try {
				mf.transferTo(saveFile);		
				isImg = checkImgType(saveFile);
				if(isImg) {
					FileOutputStream thumbnail = new FileOutputStream(new File(uploadPath,"s_"+sFileName));
																	  			//크기(가로,세로)
					Thumbnailator.createThumbnail(mf.getInputStream(), thumbnail, 100,100);
					thumbnail.close();
				}
				
			} catch (Exception e) {
				log.error("파일 저장실패");
				e.printStackTrace();
			}
			//응답내용 만들기
			AttachFileDTO dto = new AttachFileDTO(oFileName, uploadPath.toString(), uuid, isImg);
			list.add(dto);
		}
		return list;
	}
	
	
	@GetMapping("/uploadAjax")
	public void uploadAjax() {
		log.info("업로드Ajax 요청됨..");
		getFolder();
	}
	
	//현재시점의 년/월/일 리턴
	private String getFolder() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String dateStr = sdf.format(date);
		dateStr = dateStr.replace("-", File.separator);
		log.info("만들어진 경로"+dateStr);
		return dateStr;
	}
	
	//이미지인지 확인
	private boolean checkImgType(File file) {
		boolean isImg = false;
		try {
			String contentType = Files.probeContentType(file.toPath());
			log.info("업로드파일 이름: "+file.getName()+" 타입: "+contentType);
			
			isImg = contentType.startsWith("image"); //시작하는 글자를 테스트할수있는 메소드				
			
		} catch (Exception e) {
			log.warn("알 수 없는 파일타입");
		}
		return isImg;
	}
	
	@GetMapping("quiz/quiz1")
	public void quiz1() {
		
	}
	

	@GetMapping("quiz/quiz2")
	public void quiz2() {
		
	}
}