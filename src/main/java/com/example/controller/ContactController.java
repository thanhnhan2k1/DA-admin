package com.example.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.model.Contact;

@Controller
@RequestMapping("/contact")
public class ContactController {
	private RestTemplate rest=new RestTemplate();
	//private String url="http://localhost:8082";
	private String url="https://server-production-004b.up.railway.app";
	
	@GetMapping
	private String getListContact(HttpSession session,@RequestParam(name="status", defaultValue = "-1", required = false)int status) {
		List<Contact> listContact=Arrays.asList(rest.getForObject(url+"/contact/getAll?status="+status, Contact[].class));
		session.setAttribute("listContact",listContact );
		return "manageContact/listContact.html";
	}
	@GetMapping("/changeStatus")
	private String changeStatus(@RequestParam(name="index")int index, HttpSession session) {
		List<Contact> listContact=(List<Contact>)session.getAttribute("listContact");
		Contact contact=listContact.get(index);
		contact.setStatus(true);
		rest.postForObject(url+"/contact/changeStatus", listContact.get(index), Contact.class);
		return "redirect:/contact";
	}
	@GetMapping("/delete")
	private String delete(@RequestParam(name="id")int id) {
		rest.getForObject(url+"/contact/delete?id="+id, Void.class);
		return "redirect:/contact";
	}
}
