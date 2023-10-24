package com.example.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import org.springframework.core.io.ByteArrayResource;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.model.Blog;
import com.example.model.CategoryBlog;
import com.example.model.Wood;

@Controller
@RequestMapping("/blog")
public class BlogController {
	private RestTemplate rest=new RestTemplate();
	
	private String url="http://localhost:8082";
	//private String url="https://da-server2-production.up.railway.app";
	@GetMapping
	private String getListBlog(HttpSession session,@RequestParam(name="status", required = false, defaultValue = "publish")String status) {
		List<Blog>listBlog=new ArrayList<>();
		listBlog=Arrays.asList(rest.getForObject(url+"/blog/getAll?status="+status, Blog[].class));
		session.setAttribute("listBlog", listBlog);
		session.setAttribute("status", status);
			return "manageBlog/listBlog";	
	}
	@GetMapping("/add")
	private String getAdd(HttpSession session) {
		List<CategoryBlog>listCate=new ArrayList<>();
		listCate=Arrays.asList(rest.getForObject(url+"/category-blog/getAll", CategoryBlog[].class));
		session.setAttribute("listCate", listCate);
		return "manageBlog/addBlog";
	}
	@PostMapping("/add")
	private String saveAdd(
			@RequestParam(name="title")String title,
			@RequestParam(name="content")String content,
			@RequestParam(name="category")int category,
			@RequestParam(name="precontent")String precontent,
			@RequestParam(name="status")String status,
			@RequestParam(name="image")MultipartFile file,
			@RequestParam(name="author")String author,
			HttpSession session,
			Model model) throws IOException {
		List<CategoryBlog>listCate=(List<CategoryBlog>)session.getAttribute("listCate");
		CategoryBlog cate=listCate.get(category);
		Blog blog=new Blog();
		blog.setTitle(title);
		blog.setContent(content);
		blog.setAuthor(author);
		blog.setCategoryBlog(cate);
		Calendar c=Calendar.getInstance();
		c.add(Calendar.HOUR, 7);
		blog.setDateUpdate(c.getTime());
		blog.setStatus(status);
		blog.setPrecontent(precontent);
		
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
		HttpEntity<Blog> requestEntityJSON = new HttpEntity<>(blog, requestHeadersJSON);
		multipartRequest.add("blog", requestEntityJSON);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multipartRequest, requestHeaders);// final
				
		// request
		ResponseEntity<Blog> response = rest.postForEntity(url+"/blog/save", requestEntity,Blog.class);
		System.out.println(response.getBody());
		if (response.getBody() == null)
			model.addAttribute("result", "fail");
		else
			model.addAttribute("result", "success");
		return "manageBlog/addBlog";
	}
	@GetMapping("/edit")
	private String getEdit(HttpSession session, @RequestParam(name="index")int index) {
		List<CategoryBlog>listCate=new ArrayList<>();
		listCate=Arrays.asList(rest.getForObject(url+"/category-blog/getAll", CategoryBlog[].class));
		session.setAttribute("listCate", listCate);
		List<Blog>listBlog=(List<Blog>) session.getAttribute("listBlog");
		Blog blog=listBlog.get(index);
		session.setAttribute("blog", blog);
		return "manageBlog/editBlog";
	}
	@PostMapping("/edit")
	private String saveEdit(@RequestParam(name="title")String title,
			@RequestParam(name="content")String content,
			@RequestParam(name="category")int category,
			@RequestParam(name="precontent")String precontent,
			@RequestParam(name="status")String status,
			@RequestParam(name="image")MultipartFile file,
			@RequestParam(name="author")String author,
			@RequestParam("statusimg")int statusimg,
			HttpSession session,
			Model model) throws IOException {
		List<CategoryBlog>listCate=(List<CategoryBlog>)session.getAttribute("listCate");
		CategoryBlog cate=listCate.get(category);
		Blog blog=(Blog) session.getAttribute("blog");
		blog.setTitle(title);
		blog.setContent(content);
		blog.setAuthor(author);
		blog.setCategoryBlog(cate);
		Calendar c=Calendar.getInstance();
		c.add(Calendar.HOUR, 7);
		blog.setDateUpdate(c.getTime());
		blog.setStatus(status);
		blog.setPrecontent(precontent);
		System.out.println(blog.getId());
		System.out.println(status);
		
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
		HttpEntity<Blog> requestEntityJSON = new HttpEntity<>(blog, requestHeadersJSON);
		multipartRequest.add("blog", requestEntityJSON);
		multipartRequest.add("status", statusimg);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multipartRequest, requestHeaders);// final
				
		// request
		ResponseEntity<Blog> response = rest.postForEntity(url+"/blog/update", requestEntity,Blog.class);
		System.out.println(response.getBody());
		if (response.getBody() == null)
			model.addAttribute("result", "fail");
		else
			model.addAttribute("result", "success");
		return "manageBlog/editBlog";
	}
	@GetMapping("/delete")
	private String deleteItem(@RequestParam("index")int index, HttpSession session) {
		List<Blog>listBlog=(List<Blog>) session.getAttribute("listBlog");
		Blog blog=listBlog.get(index);
		rest.postForObject(url+"/wood/delete",blog, Void.class);
		String status=(String) session.getAttribute("status");
		return "redirect:/blog?status="+status;
	}
}
