package com.example.controller;


import java.util.ArrayList;
import java.util.Arrays;
import java.sql.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.example.model.ServiceStat;
import com.example.model.UsedService;

@Controller
@RequestMapping("/stat")
public class StatController {
	private RestTemplate rest=new RestTemplate();
	private String url="http://localhost:8082";
	//private String url="https://da-server2-production.up.railway.app";
	@GetMapping("/getServiceStatShow")
	private String show(HttpSession session) {
		List<ServiceStat>listServiceStat=new ArrayList<>();
		session.setAttribute("listServiceStat", listServiceStat);
		return "manageStat/serviceStat.html";
	}
	@GetMapping("/getServiceStat")
	private String getListStat(@RequestParam(name="dateStart")Date dateStart,
			@RequestParam(name="dateEnd")Date dateEnd, HttpSession session){
		List<ServiceStat>listServiceStat=Arrays.asList(rest.getForObject(url+"/stat/getServiceStat?dateStart="+dateStart+"&dateEnd="+dateEnd, ServiceStat[].class));
		session.setAttribute("listServiceStat", listServiceStat);
		session.setAttribute("dateStart", dateStart);
		session.setAttribute("dateEnd", dateEnd);
		return "manageStat/serviceStat.html";
	}
	@GetMapping("/getListUsedServiceByServiceId")
	private String getListUsedServiceByServiceId(@RequestParam(name="id")int id, HttpSession session) {
		Date dateStart=(Date)session.getAttribute("dateStart");
		Date dateEnd=(Date)session.getAttribute("dateEnd");
		
		List<UsedService>listUsedService=Arrays.asList(rest.getForObject(url+"/usedService/getListUsedServiceByServiceId?dateStart="+dateStart+"&dateEnd="+dateEnd+"&id="+id, UsedService[].class));
		//System.out.println(listUsedService.size());
		session.setAttribute("listUsedService", listUsedService);
		System.out.println(dateStart);
		System.out.println(dateEnd);
		return "manageStat/detailService.html";
	}
	@GetMapping("/getListUsedService")
	private String getListUsedService(HttpSession session) {
		List<UsedService> listUsedService=Arrays.asList(rest.getForObject(url+"/usedService/getListUsedService", UsedService[].class));
		session.setAttribute("listUs", listUsedService);
		return "manageStat/listUsedService.html";
	}
	@GetMapping("/getListUsedServiceLoc")
	private String getListUsedServiceLoc(HttpSession session, @RequestParam(name="status")String status) {
		List<UsedService> listUsedService=Arrays.asList(rest.getForObject(url+"/usedService/getListUsedServiceLoc?status="+status, UsedService[].class));
		session.setAttribute("listUs", listUsedService);
		return "manageStat/listUsedService.html";
	}
}
