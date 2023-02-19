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

import com.example.demo.weather.region;
import com.example.demo.weather.weather;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.json.JSONArray;
import org.json.JSONObject;

@RestController
@RequestMapping("/dangi")
public class dangiWeatherController {
	
	@PersistenceContext
	private EntityManager em;
	
	@RequestMapping("/dangi") 
	public ModelAndView dangi(){
		 ModelAndView mav = new ModelAndView("dangi");
		return mav;
	}
	
	@GetMapping("/weather")
    public String restApiGetWeather() throws Exception {
        /* 
            @ API LIST ~
            
            getUltraSrtNcst 초단기실황조회 
            getUltraSrtFcst 초단기예보조회 
            getVilageFcst 동네예보조회 
            getFcstVersion 예보버전조회
        */
        String url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst"
            + "?serviceKey=zE5CP0T9xU%2BMUVnkcmlXhEgsUdjhXzOLvfumEoYdiion9vck2uRb%2BlJ3zjTcoOYaNyF1JYwAmEDCMcAU4%2FHKqQ%3D%3D"
            + "&dataType=JSON"            // JSON, XML
            + "&numOfRows=10"             // 페이지 ROWS
            + "&pageNo=1"                 // 페이지 번호
            + "&base_date=20230213"       // 발표일자
            + "&base_time=0800"           // 발표시각
            + "&nx=60"                    // 예보지점 X 좌표
            + "&ny=127";                  // 예보지점 Y 좌표
        
        HashMap<String, Object> resultMap = getDataFromJson(url, "UTF-8", "get", "");
        
        System.out.println("# RESULT : " + resultMap);

        JSONObject jsonObj = new JSONObject();
        
        jsonObj.put("result", resultMap);
        
        return jsonObj.toString();
    }
    
    public HashMap<String, Object> getDataFromJson(String url, String encoding, String type, String jsonStr) throws Exception {
        boolean isPost = false;

        if ("post".equals(type)) {
            isPost = true;
        } else {
            url = "".equals(jsonStr) ? url : url + "?request=" + jsonStr;
        }

        return getStringFromURL(url, encoding, isPost, jsonStr, "application/json");
    }
    
    public HashMap<String, Object> getStringFromURL(String url, String encoding, boolean isPost, String parameter, String contentType) throws Exception {
        URL apiURL = new URL(url);

        HttpURLConnection conn = null;
        BufferedReader br = null;
        BufferedWriter bw = null;

        HashMap<String, Object> resultMap = new HashMap<String, Object>();

        try {
            conn = (HttpURLConnection) apiURL.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setDoOutput(true);

            if (isPost) {
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", contentType);
                conn.setRequestProperty("Accept", "*/*");
            } else {
                conn.setRequestMethod("GET");
            }

            conn.connect();

            if (isPost) {
                bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
                bw.write(parameter);
                bw.flush();
                bw = null;
            }

            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), encoding));

            String line = null;

            StringBuffer result = new StringBuffer();

            while ((line=br.readLine()) != null) result.append(line);

            ObjectMapper mapper = new ObjectMapper();

            resultMap = mapper.readValue(result.toString(), HashMap.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(url + " interface failed" + e.toString());
        } finally {
            if (conn != null) conn.disconnect();
            if (br != null) br.close();
            if (bw != null) bw.close();
        }

        return resultMap;
    }
    
    @RequestMapping("/log")
    public void log() {
    	String fileLocation = "C:/Users/User/Desktop/허건/이클립스/hg_workspace/demo/storage/init/단기예보CSV.csv";
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
    	String fileLocation = "C:/Users/User/Desktop/허건/이클립스/hg_workspace/demo/storage/init/단기예보CSV.csv";
        Path path = Paths.get(fileLocation);
        URI uri = path.toUri();
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new UrlResource(uri).getInputStream()))
        ) {
            String line = br.readLine(); // head 떼기
            while ((line = br.readLine()) != null) {
                String[] splits = line.split(",");
                em.persist(new region(Long.parseLong(splits[0]), splits[1], splits[2],
                        Integer.parseInt(splits[3]), Integer.parseInt(splits[4])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("finally")
	@Transactional
    @GetMapping("/updateDB")
    @ResponseBody
    public region updateDB(@RequestParam Long regionId) {
    	// 1. 날씨 정보를 요청한 지역 조회
        region region = em.find(region.class, regionId);
        StringBuilder urlBuilder =  new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst");

        // 2. 요청 시각 조회
        LocalDateTime now = LocalDateTime.now();
        String yyyyMMdd = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int hour = now.getHour();
        int min = now.getMinute();
        if(min <= 30) { // 해당 시각 발표 전에는 자료가 없음 - 이전시각을 기준으로 해야함
            hour -= 1;
        }
        String hourStr = hour + "00"; // 정시 기준
        String nx = Integer.toString(region.getNx());
        String ny = Integer.toString(region.getNy());
        String currentChangeTime = now.format(DateTimeFormatter.ofPattern("yy.MM.dd ")) + hour;

        // 기준 시각 조회 자료가 이미 존재하고 있다면 API 요청 없이 기존 자료 그대로 넘김
        weather prevWeather = region.getWeather();
        if(prevWeather != null && prevWeather.getLastUpdateTime() != null) {
            if(prevWeather.getLastUpdateTime().equals(currentChangeTime)) {
                System.out.println("기존 자료를 재사용합니다");
            }
        }

        System.out.println("API 요청 발송 >>> 지역:" + region + ", 연월일: " + yyyyMMdd + ", 시각: " + hourStr);

        try {
            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=zE5CP0T9xU%2BMUVnkcmlXhEgsUdjhXzOLvfumEoYdiion9vck2uRb%2BlJ3zjTcoOYaNyF1JYwAmEDCMcAU4%2FHKqQ%3D%3D");
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8")); /*한 페이지 결과 수*/
            urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
            urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(yyyyMMdd, "UTF-8")); /*‘현재시각 발표*/
            urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode(hourStr, "UTF-8")); /*06시 발표(정시단위) */
            urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8")); /*예보지점의 X 좌표값*/
            urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8")); /*예보지점의 Y 좌표값*/

            URL url = new URL(urlBuilder.toString());
            System.out.println("request url: " + url);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

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

            Double temp = null;
            Double humid = null;
            Double rainAmount = null;

            JSONObject jObject = new JSONObject(data);
            JSONObject response = jObject.getJSONObject("response");
            JSONObject body = response.getJSONObject("body");
            JSONObject items = body.getJSONObject("items");
            JSONArray jArray = items.getJSONArray("item");

            for(int i = 0; i < jArray.length(); i++) {
                JSONObject obj = jArray.getJSONObject(i);
                String category = obj.getString("category");
                double obsrValue = obj.getDouble("obsrValue");

                switch (category) {
                    case "T1H":
                        temp = obsrValue;
                        break;
                    case "RN1":
                        rainAmount = obsrValue;
                        break;
                    case "REH":
                        humid = obsrValue;
                        break;
                }
            }

            weather weather = new weather(temp, rainAmount, humid, currentChangeTime);
            region.updateRegionWeather(weather); // DB 업데이트
            
            return region;

        } catch (IOException e) {
        	region region2 = null;
        	return region2;
        } 
    }
}
