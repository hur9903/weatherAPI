<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="EUC-KR">
<title>Insert title here</title>
</head>
<body>
<button onclick = "weatherInfoFetch();">�߱⿹�� ���</button>
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
                $("#weatherInfo").text("������ �ҷ����� �� ������ �߻��߽��ϴ�.");
            } else {
                $("#weatherInfo").text(jungiRegion.regionName + " " + "3�� �� �����������: " + jungiRegion.jungiWeather.taMin3
                		+ " ���� ����: " + jungiRegion.jungiWeather.taMin3Low + " ~ " + jungiRegion.jungiWeather.taMin3High
                		+ " " + "3�� �� �����ְ���: " + jungiRegion.jungiWeather.taMax3
                		+ " ���� ����: " + jungiRegion.jungiWeather.taMax3Low + " ~ " + jungiRegion.jungiWeather.taMax3High);
            }
        },
        error: function () {
        	$("#weatherInfo").text("������ �ҷ����� �� ������ �߻��߽��ϴ�.");
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
            $("#weatherInfo").text("������ �ҷ����� �� ������ �߻��߽��ϴ�.");
        } else {
            $("#weatherInfo").text(jungiRegion.regionName + " " + "3�� �� �����������: " + jungiRegion.jungiWeather.taMin3
            		+ " ���� ����: " + jungiRegion.jungiWeather.taMin3Low + " ~ " + jungiRegion.jungiWeather.taMin3High
            		+ " " + "3�� �� �����ְ���: " + jungiRegion.jungiWeather.taMax3
            		+ " ���� ����: " + jungiRegion.jungiWeather.taMax3Low + " ~ " + jungiRegion.jungiWeather.taMax3High);
        }
	})
	.catch((error) => {
		$("#weatherInfo").text("������ �ҷ����� �� ������ �߻��߽��ϴ�.");
	})
}
</script>
</html>