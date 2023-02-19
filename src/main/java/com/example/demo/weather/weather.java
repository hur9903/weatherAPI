package com.example.demo.weather;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class weather {
	 private Double temp; // 온도
	 private Double rainAmount; // 강수량
	 private Double humid; // 습도
	 private String lastUpdateTime; // 마지막 갱신 시각 (시간 단위)
}
