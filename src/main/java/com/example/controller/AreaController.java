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

import com.example.model.GeographicalArea;

@Controller
@RequestMapping("/area")
public class AreaController {
	private RestTemplate rest=new RestTemplate();
	private String url="http://localhost:8082";
	//private String url="https://da-server2-production.up.railway.app";
	@GetMapping
	private String get(@RequestParam(name="key",defaultValue = "", required = false)String key,
			HttpSession session, Model model) {
		List<GeographicalArea>areas=Arrays.asList(rest.getForObject(url+"/area/getAll?key="+key, GeographicalArea[].class));
		session.setAttribute("areas", areas);
		model.addAttribute("length", areas.size());
		session.setAttribute("key", key);
		return "manageGeoArea/listArea";
	}
	@GetMapping("/add")
	private String getAdd(Model model) {
		model.addAttribute("area", new GeographicalArea());
		return "manageGeoArea/addArea";
	}
	@PostMapping("/add")
	private String saveAdd(GeographicalArea area, Model model) {
		Date date=new Date(System.currentTimeMillis());
		area.setUpdateAt(date);
		GeographicalArea ci=rest.postForObject(url+"/area/save", area, GeographicalArea.class);
		if(ci==null)
			model.addAttribute("result", "fail");
		else
			model.addAttribute("result", "success");
		model.addAttribute("area", area);
		return "manageGeoArea/addArea";
	}
	@GetMapping("/edit")
	private String getEdit(Model model, HttpSession session,@RequestParam("id")int id) {
		List<GeographicalArea>areas=(List<GeographicalArea>) session.getAttribute("areas");
		for(GeographicalArea i:areas) {
			if(i.getId()==id) {
				model.addAttribute("area",i);
				break;
			}
		}
		return "manageGeoArea/editArea";
	}
	@PostMapping("/edit")
	private String saveEdit(GeographicalArea area, Model model) {
		Date date=new Date(System.currentTimeMillis());
		area.setUpdateAt(date);
		GeographicalArea ci=rest.postForObject(url+"/area/save", area, GeographicalArea.class);
		if(ci==null)
			model.addAttribute("result", "fail");
		else
			model.addAttribute("result", "success");
		model.addAttribute("area", area);
		return "manageGeoArea/editArea";
	}
	@GetMapping("/delete")
	private String deleteItem(@RequestParam(name="id")int id, HttpSession session) {
		String key=(String)session.getAttribute("key");
		rest.delete(url+"/area/delete?id="+id);
		return "redirect:/area?key="+key;
	}
}
