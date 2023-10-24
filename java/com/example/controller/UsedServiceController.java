package com.example.controller;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.example.model.Service;
import com.example.model.TransactionHistory;
import com.example.model.UsedService;
import com.example.model.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usedService")
public class UsedServiceController {
	private RestTemplate rest=new RestTemplate();
	//kiem tra xem da dang ki su dung dich vu nao chua
	@GetMapping("/get")
	private String get(HttpSession session) {
		User user=(User) session.getAttribute("user");
		if(user==null)
			return "redirect:/user/login";
		else
		{
			// kiem tra neu dang nhap roi thi dang ki dich vu nao chua
			UsedService usedService=rest.getForObject("https://da-server2-production.up.railway.app/usedService/get?id="+user.getId(), UsedService.class);
			// neu dang su dung dich vu
			if(usedService!=null)
				return "identificationWood";
			// neu chua su dung dich vu
			List<Service>listService=Arrays.asList(rest.getForObject("https://da-server2-production.up.railway.app/service/get", Service[].class));
			session.setAttribute("listService", listService);
			return "registerService";
		}
	}
	
	@GetMapping("/payment")
	private void payemnt(@RequestParam(name="serviceId")int idService, HttpSession session, HttpServletResponse response) throws IOException {
		User user=(User) session.getAttribute("user");
		List<Service>listService=(List<Service>) session.getAttribute("listService");
		Service service=new Service();
		for(Service s:listService) {
			if(s.getId()==idService)
			{
				service=s;
				break;
			}
		}
		UsedService usedService=new UsedService();
		usedService.setUser(user);
		usedService.setService(service);
		usedService.setStatus("PENDING");
		usedService.setTypePayment("VNPAY");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR_OF_DAY, 7);
		usedService.setDateStart(cal.getTime());
		cal.add(Calendar.MONTH, service.getDuration());
		usedService.setDateEnd(cal.getTime());
//		
//		// tao hoa don thanh toan
		String url=rest.postForObject("https://da-server2-production.up.railway.app/usedService/payment",usedService, String.class);
		response.sendRedirect(url);

	}
	@GetMapping("/vnpay_return")
	private String returnVNP(HttpServletRequest request, Model model) throws ParseException {
		model.addAttribute("att", request.getParameter("vnp_ResponseCode"));
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat inputFormat1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String payDate = inputFormat1.format(inputFormat.parse(request.getParameter("vnp_PayDate")));
		model.addAttribute("paymentDate", payDate);
		//System.out.println(tran.getAmount());
		return "resultPayment";
	}
}
