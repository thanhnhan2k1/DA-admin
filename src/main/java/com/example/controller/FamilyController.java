package com.example.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.example.model.PlantFamily;

@Controller
@RequestMapping("/family")
public class FamilyController {
	private RestTemplate rest=new RestTemplate();
	private String url="http://localhost:8082";
	//private String url="https://da-server2-production.up.railway.app";
	@GetMapping
	private String getList(@RequestParam(name="key", defaultValue = "", required = false)String key, Model model, HttpSession session) {
		System.out.println(url+"/plantfamily/get?key="+key);
		List<PlantFamily>listFamily=Arrays.asList(rest.getForObject(url+"/plantfamily/getAll?key="+key, PlantFamily[].class));
		session.setAttribute("listFamily", listFamily);
		model.addAttribute("length", listFamily.size());
		session.setAttribute("key", key);
		
		return "manageFamily/listFamily";
	}
	@GetMapping("/add")
	private String getadd(Model model) {
		model.addAttribute("family", new PlantFamily());
		return "manageFamily/addFamily";
	}
	@PostMapping("/add")
	private String saveAdd(PlantFamily family, Model model) {
		Date date=new Date(System.currentTimeMillis());
		family.setUpdateAt(date);
		PlantFamily familyReponse=rest.postForObject(url+"/plantfamily/save", family, PlantFamily.class);
		if(familyReponse==null)
			model.addAttribute("result", "fail");
		else
			model.addAttribute("result", "success");
		model.addAttribute("family", family);
		return "manageFamily/addFamily";
	}
	@GetMapping("/edit")
	private String getEdit(@RequestParam(name="id")int id,Model model, HttpSession session) {
		List<PlantFamily>listFamily=(List<PlantFamily>) session.getAttribute("listFamily");
		for(PlantFamily i:listFamily) {
			if(i.getId()==id) {
				model.addAttribute("family",i);
				break;
			}
		}
		return "manageFamily/editFamily";
	}
	@PostMapping("/edit")
	private String saveEdit(PlantFamily family, Model model) {
		Date date=new Date(System.currentTimeMillis());
		family.setUpdateAt(date);
		PlantFamily familyReponse=rest.postForObject(url+"/plantfamily/save", family, PlantFamily.class);
		if(familyReponse==null)
			model.addAttribute("result", "fail");
		else
			model.addAttribute("result", "success");
		model.addAttribute("family", family);
		return "manageFamily/editFamily";
	}
	@GetMapping("/delete")
	private String deleteItem(@RequestParam(name="id")int id, HttpSession session) {
		String key=(String)session.getAttribute("key");
		rest.delete(url+"/delete?id="+id);
		return "redirect:/family?key="+key;
	}
}
