package com.example.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.file.ConfigurationSource.Resource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.model.Glossary;

@Controller
@RequestMapping("/glossary")
public class GlossaryController {
	private RestTemplate rest = new RestTemplate();
	private String url = "http://localhost:8082";

	//private String url="https://da-server2-production.up.railway.app";
	@GetMapping
	private String get(@RequestParam(name = "key", defaultValue = "", required = false) String key, HttpSession session,
			Model model) {
		List<Glossary> glossarys = Arrays
				.asList(rest.getForObject(url + "/glossary/getAll?key=" + key, Glossary[].class));
		session.setAttribute("glossarys", glossarys);
		model.addAttribute("length", glossarys.size());
		session.setAttribute("key", key);
		return "manageGlossary/listGlossary";
	}

	@GetMapping("/add")
	private String getAdd(Model model) {
		model.addAttribute("glossary", new Glossary());
		return "manageGlossary/addGlossary";
	}

	@PostMapping("/add")
	private String saveAdd(Model model, @RequestParam("vietnamese") String vietnamese,
			@RequestParam("english") String english, @RequestParam("definition") String definition,
			@RequestParam("note") String note, @RequestParam("image") MultipartFile file) throws IOException {
		Glossary glossary = new Glossary();
		glossary.setVietnamese(vietnamese);
		glossary.setEnglish(english);
		glossary.setDefinition(definition);
		glossary.setNote(note);
		model.addAttribute("vietnamese", vietnamese);
		model.addAttribute("english", english);
		model.addAttribute("definition", definition);
		model.addAttribute("note", note);

		MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);// Main request's headers

		HttpHeaders requestHeadersAttachment = new HttpHeaders();
		HttpEntity<ByteArrayResource> attachmentPart;
		ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()) {
			@Override
			public String getFilename() {
				return file.getOriginalFilename();
			}
		};
		attachmentPart = new HttpEntity<>(fileAsResource, requestHeadersAttachment);

		multipartRequest.add("file", attachmentPart);

		HttpHeaders requestHeadersJSON = new HttpHeaders();
		requestHeadersJSON.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Glossary> requestEntityJSON = new HttpEntity<>(glossary, requestHeadersJSON);
		multipartRequest.add("glossary", requestEntityJSON);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multipartRequest, requestHeaders);// final
																														// request
		ResponseEntity<Glossary> response = rest.postForEntity(url +"/glossary/add", requestEntity,
				Glossary.class);
		if (response.getBody() == null)
			model.addAttribute("result", "fail");
		else
			model.addAttribute("result", "success");
		return "manageGlossary/addGlossary";
	}

	@GetMapping("/edit")
	private String getEdit(@RequestParam("id") int id, HttpSession session, Model model) {
		List<Glossary> listGlossary = (List<Glossary>) session.getAttribute("glossarys");
		Glossary glossary = new Glossary();
		for (Glossary i : listGlossary)
			if (i.getId() == id) {
				glossary = i;
				break;
			}
		session.setAttribute("url", glossary.getImage());
		session.setAttribute("glossary", glossary);
		return "manageGlossary/editGlossary";
	}

	@PostMapping("/edit")
	private String saveEdit(HttpSession session, Model model, @RequestParam("vietnamese") String vietnamese,
			@RequestParam("english") String english, @RequestParam("definition") String definition,
			@RequestParam("note") String note, @RequestParam("image") MultipartFile file,
			@RequestParam("status")int status) throws IOException {
		Glossary glossary = (Glossary) session.getAttribute("glossary");
		glossary.setVietnamese(vietnamese);
		glossary.setEnglish(english);
		glossary.setDefinition(definition);
		glossary.setNote(note);
		Date date = new Date(System.currentTimeMillis());
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, 7);
		glossary.setUpdateAt(calendar.getTime());
		MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);// Main request's headers
		HttpHeaders requestHeadersAttachment = new HttpHeaders();
		HttpEntity<ByteArrayResource> attachmentPart;
		ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()) {
			@Override
			public String getFilename() {
				return file.getOriginalFilename();
			}
		};
		attachmentPart = new HttpEntity<>(fileAsResource, requestHeadersAttachment);

		multipartRequest.add("file", attachmentPart);
		HttpHeaders requestHeadersJSON = new HttpHeaders();
		requestHeadersJSON.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Glossary> requestEntityJSON = new HttpEntity<>(glossary, requestHeadersJSON);
		multipartRequest.add("glossary", requestEntityJSON);
		multipartRequest.add("status", status);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multipartRequest, requestHeaders);// final
		
		System.out.println(status);
		// request
		ResponseEntity<Glossary> response = rest.postForEntity(url +"/glossary/update", requestEntity,
				Glossary.class);
		session.setAttribute("glossary", glossary);
		if(file.isEmpty())
			session.setAttribute("url", null);
		else {
			String base64Encoded= Base64.getEncoder().encodeToString(file.getBytes());
			String url="data:"+file.getContentType()+";base64,"+base64Encoded;
			session.setAttribute("url", url);
		}
		if (response.getBody() == null)
			model.addAttribute("result", "fail");
		else
			model.addAttribute("result", "success");
		return "manageGlossary/editGlossary";
	}

	@GetMapping("/delete")
	private String deleteItem(@RequestParam("id") int id, HttpSession session) {
		List<Glossary> listGlossary = (List<Glossary>) session.getAttribute("glossarys");
		Glossary glossary = new Glossary();
		for (Glossary i : listGlossary)
			if (i.getId() == id) {
				glossary = i;
				break;
			}
		rest.postForObject(url+"/glossary/delete", glossary, Void.class);
		return "redirect:/glossary";
	}

}
