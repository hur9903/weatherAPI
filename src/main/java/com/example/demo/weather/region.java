package com.example.demo.weather;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class region {
	
	@Id @Column(name = "region_id")
    private Long id; // 지역 순번

    @Column(name = "region_parent")
    private String parentRegion; // 시, 도

    @Column(name = "region_child")
    private String childRegion; // 시, 군, 구

    private int nx; // x좌표

    private int ny; // y좌표

    @Embedded
    private weather weather; // 지역 날씨 정보

    // 날씨 정보 제외하고 지역 생성
    public region(Long id, String parentRegion, String childRegion, int nx, int ny) {
        this.id = id;
        this.parentRegion = parentRegion;
        this.childRegion = childRegion;
        this.nx = nx;
        this.ny = ny;
    }

    // 날씨 갱신
    public void updateRegionWeather(weather weather) {
        this.weather = weather;
    }
    
    @Override
    public String toString() {
        return parentRegion + " " + childRegion;
    }

}
