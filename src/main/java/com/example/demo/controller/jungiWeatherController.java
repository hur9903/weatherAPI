package com.example.demo.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.weather.jungiRegion;
import com.example.demo.weather.jungiWeather;
import com.example.demo.weather.region;
import com.example.demo.weather.weather;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.json.JSONArray;
import org.json.JSONObject;

@RestController
@RequestMapping("/jungi")
public class jungiWeatherController {
	
	@PersistenceContext
	private EntityManager em;
	
	@RequestMapping("/jungi") 
	public ModelAndView dangi(){
		 ModelAndView mav = new ModelAndView("jungi");
		return mav;
	}
    
    @RequestMapping("/log")
    public void log() {
    	String fileLocation = "C:/Users/User/Desktop/허건/이클립스/hg_workspace/demo/storage/init/중기예보CSV.csv";
        Path path = Paths.get(fileLocation);
        URI uri = path.toUri();
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new UrlResource(uri).getInputStream()))) {
	        String line = br.readLine(); // head 떼기
	        while ((line = br.readLine()) != null) {
	            System.out.println(line);
	        }      
        } catch (IOException e) {
        	e.printStackTrace();
		}
    }
    
    @Transactional
    @RequestMapping("/saveDB")
    public void saveDB() {
    	String fileLocation = "C:/Users/User/Desktop/허건/이클립스/hg_workspace/demo/storage/init/중기예보CSV.csv";
        Path path = Paths.get(fileLocation);
        URI uri = path.toUri();
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new UrlResource(uri).getInputStream()))
        ) {
            String line = br.readLine(); // head 떼기
            while ((line = br.readLine()) != null) {
                String[] splits = line.split(",");
                em.persist(new jungiRegion(Long.parseLong(splits[0]), splits[1], splits[2]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("finally")
	@Transactional
    @GetMapping("/updateDB")
    @ResponseBody
    public jungiRegion updateDB(@RequestParam Long regionId) {
    	// 1. 날씨 정보를 요청한 지역 조회
        jungiRegion jungiRegion = em.find(jungiRegion.class, regionId);
        StringBuilder urlBuilder =  new StringBuilder("http://apis.data.go.kr/1360000/MidFcstInfoService/getMidTa");

        // 2. 요청 시각 조회
        LocalDateTime now = LocalDateTime.now();
        String yyyyMMdd = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int hour = now.getHour();
        int min = now.getMinute();
        if(min <= 30) { // 해당 시각 발표 전에는 자료가 없음 - 이전시각을 기준으로 해야함
            hour -= 1;
        }
        String hourStr = hour + "00"; // 정시 기준
        String currentChangeTime = now.format(DateTimeFormatter.ofPattern("yy.MM.dd ")) + hour;
        
        String regionCode = jungiRegion.getRegionCode();
        
        jungiWeather prevWeather = jungiRegion.getJungiWeather();
        if(prevWeather != null && prevWeather != null) {
            if(prevWeather.getLastUpdateTime().equals(currentChangeTime)) {
                System.out.println("기존 자료를 재사용합니다");
            }
        }

        System.out.println("API 요청 발송 >>> 지역:" + jungiRegion.getRegionName() + ", 연월일: " + yyyyMMdd + ", 시각: " + hourStr);

        try {
            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=zE5CP0T9xU%2BMUVnkcmlXhEgsUdjhXzOLvfumEoYdiion9vck2uRb%2BlJ3zjTcoOYaNyF1JYwAmEDCMcAU4%2FHKqQ%3D%3D"); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
            urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON)Default: XML*/
            urlBuilder.append("&" + URLEncoder.encode("regId","UTF-8") + "=" + URLEncoder.encode(regionCode, "UTF-8")); /*11B10101 서울, 11B20201 인천 등 ( 별첨엑셀자료 참고)*/
            urlBuilder.append("&" + URLEncoder.encode("tmFc","UTF-8") + "=" + URLEncoder.encode(yyyyMMdd + "0600", "UTF-8")); /*-일 2회(06:00,18:00)회 생성 되며 발표시각을 입력- YYYYMMDD0600(1800) 최근 24시간 자료만 제공*/
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            System.out.println("Response code: " + conn.getResponseCode());
            BufferedReader rd;
            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();
            String data = sb.toString();
            System.out.println(data);

            //// 응답 수신 완료 ////
            //// 응답 결과를 JSON 파싱 ////
            
            Double taMin3 = null;
            Double taMin3Low = null;
            Double taMin3High = null;
            Double taMax3 = null;
            Double taMax3Low = null;
            Double taMax3High = null;

            JSONObject jObject = new JSONObject(data);
            JSONObject response = jObject.getJSONObject("response");
            JSONObject body = response.getJSONObject("body");
            JSONObject items = body.getJSONObject("items");
            JSONArray jArray = items.getJSONArray("item");
            
            for(int i = 0; i < jArray.length(); i++) {
            	JSONObject obj = jArray.getJSONObject(i);
            	taMin3 = obj.getDouble("taMin3");
            	taMin3Low = obj.getDouble("taMin3Low");
            	taMin3High = obj.getDouble("taMin3High");
            	taMax3 = obj.getDouble("taMax3");
            	taMax3Low = obj.getDouble("taMax3Low");
            	taMax3High = obj.getDouble("taMax3High");
            }

            jungiWeather jungiWeather = new jungiWeather(taMin3, taMin3Low, taMin3High, taMax3, taMax3Low, taMax3High, currentChangeTime);
            jungiRegion.updateRegionWeather(jungiWeather); // DB 업데이트
            
            return jungiRegion;

        } catch (IOException e) {
        	jungiRegion jungiRegion2 = null;
        	return jungiRegion2;
        } 
    }
}
