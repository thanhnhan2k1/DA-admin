package com.example.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.example.model.User;
import com.example.model.UserDTO;
@Controller
@RequestMapping("/")
public class HomeController {
	private RestTemplate rest=new RestTemplate();
	private String url="http://localhost:8082";
	
	@GetMapping
	private String getLogin(HttpSession session, HttpServletRequest request, Model model) {
		User user=(User)session.getAttribute("user");
		if(user==null)
		{
			model.addAttribute("user", new UserDTO());
			return "login";
		}
		return "redirect:/index";
	}
	
	@GetMapping("/index")
	private String get1(HttpSession session) {
		int amountWood=rest.getForObject(url+"/wood/getAmountWood", Integer.class);
		session.setAttribute("amountWood", amountWood);
		int amountBlog=rest.getForObject(url+"/blog/getAmountBlog", Integer.class);
		session.setAttribute("amountBlog", amountBlog);
		double amountDoanhThu=rest.getForObject(url+"/usedService/getAmountDoanhThu", Double.class);
		session.setAttribute("amountDoanhThu", amountDoanhThu);
		int amountService=rest.getForObject(url+"/service/getAmountService", Integer.class);
		session.setAttribute("amountService", amountService);
		System.out.println(amountWood);
		return "index";
	}
	@GetMapping("/editor")
	private String get2() {
		return "editors";
	}
	@GetMapping("/utilities-animation")
	private String get3() {
		return "utilities-animation";
	}
	@GetMapping("/utilities-border")
	private String get4() {
		return "utilities-border";
	}
	@GetMapping("/utilities-color")
	private String get5() {
		return "utilities-color";
	}
	@GetMapping("/utilities-orther")
	private String get6() {
		return "utilities-orther";
	}
	@GetMapping("/forgot-password")
	private String get7() {
		return "forgot-password";
	}
	@GetMapping("/charts")
	private String get8() {
		return "charts";
	}
	@GetMapping("/cards")
	private String get9() {
		return "card8";
	}
	@GetMapping("/blank")
	private String get10() {
		return "blank";
	}
	@GetMapping("/buttons")
	private String get11() {
		return "buttons";
	}
	@GetMapping("/register")
	private String get12() {
		return "register";
	}
}
