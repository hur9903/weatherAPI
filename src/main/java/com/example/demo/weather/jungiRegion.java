package com.example.demo.weather;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class jungiRegion {
	@Id @Column(name = "region_id")
    private Long regionId; // id

    @Column(name = "region_name")
    private String regionName; // 지역명

    @Column(name = "region_code")
    private String regionCode; // 지역코드
    
    @Embedded
    private jungiWeather jungiWeather; // 지역 날씨 정보
    
    public jungiRegion(Long id, String regionName, String regionCode) {
    	this.regionId = id;
    	this.regionName = regionName;
    	this.regionCode = regionCode;
    }
    
 // 날씨 갱신
    public void updateRegionWeather(jungiWeather jungiWeather) {
        this.jungiWeather = jungiWeather;
    }
}
