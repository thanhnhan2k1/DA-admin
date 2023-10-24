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

import com.example.model.Preservation;


@Controller
@RequestMapping("/preservation")
public class PreservationController {

	private RestTemplate rest=new RestTemplate();
	private String url="http://localhost:8082";
	//private String url="https://da-server2-production.up.railway.app";
	@GetMapping
	private String get(@RequestParam(name="key", defaultValue = "", required = false)String key, HttpSession session, Model model) {
		List<Preservation>listPre=Arrays.asList(rest.getForObject(url+"/preservation/get?key="+key, Preservation[].class));
		session.setAttribute("listPre", listPre);
		model.addAttribute("length", listPre.size());
		session.setAttribute("key", key);
		return "managePreservation/preservation";
	}
	
	@GetMapping("/add")
	private String getAdd(Model model) {
		model.addAttribute("pre", new Preservation());
		return "managePreservation/addPreservation";
	}
	@PostMapping("/add")
	private String saveAdd(Preservation pre, Model model) {
		Date date=new Date(System.currentTimeMillis());
		pre.setUpdateAt(date);
		Preservation preservation=rest.postForObject(url+"/preservation/save", pre, Preservation.class);
		if(preservation==null)
			model.addAttribute("result", "fail");
		else
			model.addAttribute("result", "success");
		model.addAttribute("pre", pre);
		return "managePreservation/addPreservation";
	}
	@GetMapping("/edit")
	private String getEdit(@RequestParam("id")int id,Model model, HttpSession session) {
		List<Preservation>listPre=(List<Preservation>) session.getAttribute("listPre");
		for(Preservation i:listPre) {
			if(i.getId()==id) {
				model.addAttribute("pre",i);
				break;
			}
		}
		
		return "managePreservation/editPreservation";
	}
	@PostMapping("/edit")
	private String saveEdit(Preservation pre, Model model) {
		Date date=new Date(System.currentTimeMillis());
		pre.setUpdateAt(date);
		Preservation preservation=rest.postForObject(url+"/preservation/save", pre, Preservation.class);
		if(preservation==null)
			model.addAttribute("result", "fail");
		else
			model.addAttribute("result", "success");
		model.addAttribute("pre", pre);
		return "managePreservation/editPreservation";
	}
	@GetMapping("/delete")
	private String deleteItem(@RequestParam(name="id")int id, HttpSession session) {
		String key=(String)session.getAttribute("key");
		rest.delete(url+"/preservation/delete?id="+id);
		return "redirect:/preservation?key="+key;
	}
}
