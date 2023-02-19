<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="EUC-KR">
<title>Insert title here</title>
</head>
<body>
<button onclick = "weatherInfoFetch();">중기예보 출력</button>
<div id = "weatherInfo"></div>
</body>
<script src="http://code.jquery.com/jquery-latest.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	
});

function weatherInfo() {
	var regionId = Math.floor(Math.random() * 200) + 2;
	
	$.ajax({
        type: "GET",
        url: "/jungi/updateDB?regionId=" + regionId,
        dataType: "json",
        success: function (jungiRegion) {
            if(jungiRegion == null) {
                $("#weatherInfo").text("날씨를 불러오는 중 오류가 발생했습니다.");
            } else {
                $("#weatherInfo").text(jungiRegion.regionName + " " + "3일 후 예상최저기온: " + jungiRegion.jungiWeather.taMin3
                		+ " 오차 범위: " + jungiRegion.jungiWeather.taMin3Low + " ~ " + jungiRegion.jungiWeather.taMin3High
                		+ " " + "3일 후 예상최고기온: " + jungiRegion.jungiWeather.taMax3
                		+ " 오차 범위: " + jungiRegion.jungiWeather.taMax3Low + " ~ " + jungiRegion.jungiWeather.taMax3High);
            }
        },
        error: function () {
        	$("#weatherInfo").text("날씨를 불러오는 중 오류가 발생했습니다.");
        }
    });
}

function weatherInfoFetch() {
	const option = {
	  headers: {
	    "Content-Type": "application/json",
	  },
	};
	
	var regionId = Math.floor(Math.random() * 200) + 2;
	
	fetch("/jungi/updateDB?regionId=" + regionId, option)
	.then((res) => res.json())
	.then((jungiRegion) => {
		if(jungiRegion == null) {
            $("#weatherInfo").text("날씨를 불러오는 중 오류가 발생했습니다.");
        } else {
            $("#weatherInfo").text(jungiRegion.regionName + " " + "3일 후 예상최저기온: " + jungiRegion.jungiWeather.taMin3
            		+ " 오차 범위: " + jungiRegion.jungiWeather.taMin3Low + " ~ " + jungiRegion.jungiWeather.taMin3High
            		+ " " + "3일 후 예상최고기온: " + jungiRegion.jungiWeather.taMax3
            		+ " 오차 범위: " + jungiRegion.jungiWeather.taMax3Low + " ~ " + jungiRegion.jungiWeather.taMax3High);
        }
	})
	.catch((error) => {
		$("#weatherInfo").text("날씨를 불러오는 중 오류가 발생했습니다.");
	})
}
</script>
</html>