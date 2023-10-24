package com.example.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.example.model.CategoryBlog;

@Controller
@RequestMapping("/category-blog")
public class CategoryBlogController {
	private RestTemplate rest=new RestTemplate();
	
	private String url="http://localhost:8082";
	//private String url="https://da-server2-production.up.railway.app";
	@GetMapping
	private String getListCategory(HttpSession session) {
		List<CategoryBlog>listCate=new ArrayList<>();
		listCate=Arrays.asList(rest.getForObject(url+"/category-blog/getAll", CategoryBlog[].class));
		session.setAttribute("listCate", listCate);
		return "manageCategoryBlog/listCategoryBlog";
	}
	@GetMapping("/add")
	private String getAddCategory(Model model) {
		CategoryBlog cate=new CategoryBlog();
		model.addAttribute("cate", cate);
		return "manageCategoryBlog/addCategoryBlog";
	}
	@PostMapping("/add")
	private String saveAdd(CategoryBlog cate, Model model) {
		Date date=new Date(System.currentTimeMillis());
		cate.setDateUpdate(date);
		CategoryBlog ci=rest.postForObject(url+"/category-blog/save", cate, CategoryBlog.class);
		if(ci==null)
			model.addAttribute("result", "fail");
		else
			model.addAttribute("result", "success");
		model.addAttribute("cate", cate);
		return "manageCategoryBlog/addCategoryBlog";
	}
	@GetMapping("/delete")
	private String deleteItem(@RequestParam(name="id")int id, HttpSession session) {
		String key=(String)session.getAttribute("key");
		rest.delete(url+"/category-blog/delete?id="+id);
		return "redirect:/category-blog?key="+key;
	}
	@GetMapping("/edit")
	private String getEdit(Model model, HttpSession session,@RequestParam("index")int index) {
		List<CategoryBlog>listCate=(List<CategoryBlog>) session.getAttribute("listCate");
		model.addAttribute("cate",listCate.get(index));
		
		return "manageCategoryBlog/editCategoryBlog";
	}
	@PostMapping("/edit")
	private String saveEdit(CategoryBlog cate, Model model) {
		Date date=new Date(System.currentTimeMillis());
		cate.setDateUpdate(date);
		CategoryBlog ci=rest.postForObject(url+"/category-blog/save", cate, CategoryBlog.class);
		if(ci==null)
			model.addAttribute("result", "fail");
		else
			model.addAttribute("result", "success");
		model.addAttribute("cate", cate);
		return "manageCategoryBlog/editCategoryBlog";
	}
}
