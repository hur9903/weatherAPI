package com.example.demo.weather;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class jungiWeather {	
	 private Double taMin3; // 3일 후 예상최저기온
	 private Double taMin3Low; // 3일 후 예상최저기온 하한
	 private Double taMin3High; // 3일 후 예상최저기온 상한
	 private Double taMax3; // 3일 후 예상최고기온
	 private Double taMax3Low; // 3일 후 예상최고기온 하한
	 private Double taMax3High; // 3일 후 예상최고기온 상한
	 private String lastUpdateTime; // 마지막 갱신 시각 (시간 단위)
}
