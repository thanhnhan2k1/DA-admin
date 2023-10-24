package com.example.controller;

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

import com.example.model.AppendixCITES;

@Controller
@RequestMapping("/cites")
public class CitesController {
	private RestTemplate rest=new RestTemplate();
	private String url="http://localhost:8082";
	//private String url="https://da-server2-production.up.railway.app";
	@GetMapping
	private String get(@RequestParam(name="key", defaultValue = "", required = false)String key, HttpSession session, Model model) {
		List<AppendixCITES>cites=Arrays.asList(rest.getForObject(url+"/cites/get?key="+key, AppendixCITES[].class));
		session.setAttribute("cites", cites);
		model.addAttribute("length", cites.size());
		session.setAttribute("key", key);
		return "manageCites/listCites";
	}
	@GetMapping("/add")
	private String getAdd(Model model) {
		model.addAttribute("cite", new AppendixCITES());
		return "manageCites/addCites";
	}
	@PostMapping("/add")
	private String saveAdd(AppendixCITES cite, Model model) {
		Date date=new Date(System.currentTimeMillis());
		cite.setUpdateAt(date);
		AppendixCITES ci=rest.postForObject(url+"/cites/save", cite, AppendixCITES.class);
		if(ci==null)
			model.addAttribute("result", "fail");
		else
			model.addAttribute("result", "success");
		model.addAttribute("cite", cite);
		return "manageCites/addCites";
	}
	@GetMapping("/edit")
	private String getEdit(@RequestParam("id")int id,Model model, HttpSession session) {
		List<AppendixCITES>cites=(List<AppendixCITES>) session.getAttribute("cites");
		for(AppendixCITES i:cites) {
			if(i.getId()==id) {
				model.addAttribute("cite",i);
				break;
			}
		}
		
		return "manageCites/editCites";
	}
	@PostMapping("/edit")
	private String saveEdit(AppendixCITES cite, Model model) {
		Date date=new Date(System.currentTimeMillis());
		cite.setUpdateAt(date);
		AppendixCITES ci=rest.postForObject(url+"/cites/save", cite, AppendixCITES.class);
		if(ci==null)
			model.addAttribute("result", "fail");
		else
			model.addAttribute("result", "success");
		model.addAttribute("cite", cite);
		return "manageCites/editCites";
	}
	@GetMapping("/delete")
	private String deleteItem(@RequestParam(name="id")int id, HttpSession session) {
		String key=(String)session.getAttribute("key");
		rest.delete(url+"/cites/delete?id="+id);
		return "redirect:/cites?key="+key;
	}
	
}
