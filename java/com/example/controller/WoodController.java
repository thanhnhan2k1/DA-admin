package com.example.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.example.model.AppendixCITES;
import com.example.model.GeographicalArea;
import com.example.model.PlantFamily;
import com.example.model.Preservation;
import com.example.model.Wood;
import com.example.model.WoodPagination;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/wood")
public class WoodController {
	private RestTemplate rest = new RestTemplate();

	@GetMapping("/get")
	private String get(@RequestParam(name = "key", defaultValue = "", required = false) String key,
			@RequestParam(name = "pageNum", defaultValue = "1", required = false) int pageNum,
			@RequestParam(name = "family", required = false) List<String> familys,
			@RequestParam(name = "area", required = false) List<String> areas,
			@RequestParam(name = "color", required = false) List<String> colors,
			@RequestParam(name = "cites", required = false) List<String> cites,
			@RequestParam(name = "sort", required = false) String sort,
			@RequestParam(name="from", required = false, defaultValue = "")String from,
			@RequestParam(name="to", required = false, defaultValue = "")String to,
			@RequestParam(name = "preservation", required = false) List<String> preservations, HttpSession session,
			Model model, HttpServletRequest request) {
		// lay thon tin duong dan hien tai
		String urlCur=request.getRequestURL().toString()+"?";
		String query=request.getQueryString();
		if(query!=null) {
			if(query.contains("pageNum=")==false)
			{
				urlCur=urlCur+query+"&";
				session.setAttribute("url", urlCur);
			}
		}else
			session.setAttribute("url", urlCur);
		// lay thong tin key
		String url = "https://da-server2-production.up.railway.app/wood/get?pageNum=" + pageNum;
		if (!key.isEmpty()) {
			url = url + "&key=" + key;
		}
		session.setAttribute("key", key);
		// lay thon tin sap xep
		Map<String, Boolean> mapSort = (Map<String, Boolean>) session.getAttribute("mapSort");
		List<String> listSort;
		if (mapSort == null) {
			listSort = new ArrayList<String>();
			listSort.add("Tên Việt Nam thường gọi");
			listSort.add("Tên khoa học");
			listSort.add("Tên thương mại");
			listSort.add("Trọng lượng riêng");
			mapSort = new LinkedHashMap<String, Boolean>();
		} else {
			listSort = new ArrayList<>(mapSort.keySet());
		}
		if (sort == null) {
			mapSort.put(listSort.get(0), true);
			mapSort.put(listSort.get(1), true);
			for (int i = 1; i < listSort.size(); i++)
				mapSort.put(listSort.get(i), false);
		} else {
			for (String i : listSort) {
				if (i.equalsIgnoreCase(sort)) {
					mapSort.put(i, true);
					url=url+"&sort="+i;
				} else
					mapSort.put(i, false);
			}
		}
		session.setAttribute("mapSort", mapSort);
		// lay thong tin from to
		Object maxXau =session.getAttribute("max");
		Object minXau=session.getAttribute("min");
		int max, min;
		if (maxXau==null || minXau==null) {
			max=rest.getForObject("https://da-server2-production.up.railway.app/wood/getMaxSpecificGravity", Integer.class);
			min=rest.getForObject("https://da-server2-production.up.railway.app/wood/getMinSpecificGravity", Integer.class);
			session.setAttribute("min", min);
			session.setAttribute("max", max);
		}
		else {
			max=(int)maxXau;
			min=(int)minXau;
		}
		if(!from.isEmpty() && !to.isEmpty()) {
			url=url+"&from="+from;
			url=url+"&to="+to;
			session.setAttribute("from", from);
			session.setAttribute("to", to);
		}else {
			session.setAttribute("from",min);
			session.setAttribute("to",max);
		}
		// lay thong tin family
		List<PlantFamily> listFamily;
		Map<PlantFamily, Boolean> mapFamily = (Map<PlantFamily, Boolean>) session.getAttribute("mapFamily");
		if (mapFamily == null) {
			listFamily = Arrays.asList(rest.getForObject("https://da-server2-production.up.railway.app/plantfamily/get", PlantFamily[].class));
			mapFamily = new LinkedHashMap<PlantFamily, Boolean>();
		} else
			listFamily = (List<PlantFamily>) mapFamily.keySet();

		if (familys == null)
			for (PlantFamily i : listFamily)
				mapFamily.put(i, false);
		else
			for (PlantFamily i : listFamily) {
				if (familys.contains(i.getEnglish())) {
					mapFamily.put(i, true);
					url = url + "&family=" + i.getEnglish();
				} else
					mapFamily.put(i, false);
			}

		session.setAttribute("mapfamily", mapFamily);

		// lay thon tin vi tri dia ly
		Map<GeographicalArea, Boolean> mapArea = (Map<GeographicalArea, Boolean>) session.getAttribute("mapArea");
		List<GeographicalArea> listAreas = new ArrayList<>();
		if (mapArea == null) {
			listAreas = Arrays.asList(rest.getForObject("https://da-server2-production.up.railway.app/area/get", GeographicalArea[].class));
			mapArea = new LinkedHashMap<GeographicalArea, Boolean>();
		} else
			listAreas = new ArrayList<>(mapArea.keySet());

		if (areas == null)
			for (GeographicalArea i : listAreas)
				mapArea.put(i, false);
		else
			for (GeographicalArea i : listAreas) {
				if (areas.contains(i.getEnglish())) {
					mapArea.put(i, true);
					url = url + "&area=" + i.getEnglish();
				} else
					mapArea.put(i, false);
			}
		session.setAttribute("mapArea", mapArea);
		// lay danh sach thong tin cites
		Map<AppendixCITES, Boolean> mapCites = (Map<AppendixCITES, Boolean>) session.getAttribute("mapCites");
		List<AppendixCITES> listCites = new ArrayList<>();
		if (mapCites == null) {
			listCites = Arrays.asList(rest.getForObject("https://da-server2-production.up.railway.app/cites/get", AppendixCITES[].class));
			mapCites = new LinkedHashMap<AppendixCITES, Boolean>();
		} else
			listCites = new ArrayList<>(mapCites.keySet());

		if (cites == null)
			for (AppendixCITES i : listCites)
				mapCites.put(i, false);
		else
			for (AppendixCITES i : listCites) {
				if (cites.contains(i.getName())) {
					mapCites.put(i, true);
					url = url + "&cites=" + i.getName();
				} else
					mapCites.put(i, false);
			}
		session.setAttribute("mapCites", mapCites);
		// lay thon tin ve sự bảo tồn(preservation)
		Map<Preservation, Boolean> mapPre = (Map<Preservation, Boolean>) session.getAttribute("mapPre");
		List<Preservation> listPre;
		if (mapPre == null) {
			listPre = Arrays.asList(rest.getForObject("https://da-server2-production.up.railway.app/preservation/get", Preservation[].class));
			mapPre = new LinkedHashMap<Preservation, Boolean>();
		} else
			listPre = new ArrayList<>(mapPre.keySet());

		if (preservations == null)
			for (Preservation i : listPre)
				mapPre.put(i, false);
		else
			for (Preservation i : listPre) {
				if (preservations.contains(i.getAcronym())) {
					mapPre.put(i, true);
					url = url + "&preservation=" + i.getAcronym();
				} else
					mapPre.put(i, false);
			}
		session.setAttribute("mapPre", mapPre);
		// lấy thông tin dựa vào color
		
		Map<String, Boolean> mapColor = (Map<String, Boolean>) session.getAttribute("mapColor");
		List<String> listColor;
		if (mapColor == null) {
			listColor = new ArrayList<String>();
			listColor.add("Đen");
			listColor.add("Xanh");
			listColor.add("Đỏ");
			listColor.add("Hồng");
			listColor.add("Vàng");
			mapColor = new LinkedHashMap<String, Boolean>();
		} else
			listColor = new ArrayList<>(mapColor.keySet());

		if (colors == null)
			for (String i : listColor)
				mapColor.put(i, false);
		else
			for (String i : listColor) {
				if (colors.contains(i)) {
					mapColor.put(i, true);
					url = url + "&color=" + i;
				} else
					mapColor.put(i, false);
			}
		session.setAttribute("mapColor", mapColor);
		System.out.println(url);
		WoodPagination woodpag = rest.getForObject(url, WoodPagination.class);
		System.out.println(woodpag);
		session.setAttribute("woodpag", woodpag);
		model.addAttribute("currentPage", pageNum);
		//model.addAttribute("totalPages", woodpag.getTotalPages());
		return "wood.html";
	}

	@GetMapping("/detail")
	private String getDetailWood(@RequestParam("index") int index, HttpSession session, Model model) {
		WoodPagination woodpag = (WoodPagination) session.getAttribute("woodpag");
		Wood wood = woodpag.getContent().get(index);
		String distribute = "";
		for (GeographicalArea i : wood.getListAreas())
			distribute += i.getVietnamese() + ", ";
		distribute = distribute.substring(0, distribute.length() - 2);
		model.addAttribute("distribute", distribute);
		session.setAttribute("wood", wood);
		return "woodDetail.html";
	}
}
