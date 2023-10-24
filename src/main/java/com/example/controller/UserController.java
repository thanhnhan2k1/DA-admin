package com.example.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
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
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.example.model.User;
import com.example.model.UserDTO;

@Controller
@RequestMapping("/user")
public class UserController {
	private RestTemplate rest=new RestTemplate();
	private String url="http://localhost:8082";
	//private String url="https://da-server2-production.up.railway.app";
	
	@PostMapping("/checkLogin")
	private String checkLogin(UserDTO userDto,
			HttpSession session, Model model, HttpServletResponse response) {
		try {
			String token=rest.postForObject(url+"/user/login", userDto, String.class);
				// luu thong tin vao cookies
				Cookie ctoken=new Cookie("token", token);
				ctoken.setMaxAge(24*365*2*60*60);
				ctoken.setPath("/");
				response.addCookie(ctoken);
			//lay thong tin ng dung
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer "+token);
			ResponseEntity<User> u=rest.exchange(url+"/user/getUserInfo",HttpMethod.GET, new HttpEntity<Void>(headers), User.class);
			session.setAttribute("user", u.getBody());
			session.setAttribute("token", token);
			return "redirect:/index";
		}catch(HttpStatusCodeException e) {
			model.addAttribute("error", "fail");
			model.addAttribute("user", userDto);
			return "login.html";
		}
	}
	@GetMapping("/logout")
	private String logout(HttpSession session, Model model) {
		session.setAttribute("user", null);
		model.addAttribute("user", new UserDTO());
		return "login.html";
	}
}
