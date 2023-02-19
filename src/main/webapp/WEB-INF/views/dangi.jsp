<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="EUC-KR">
<title>Insert title here</title>
</head>
<body>
<button onclick = "weatherInfoFetch();">�ܱ⿹�� ���</button>
<div id = "weatherInfo"></div>
</body>
<script src="http://code.jquery.com/jquery-latest.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	weatherLog();
});

function weatherLog() {
    jQuery.ajax({
        url : "/dangi/weather",
        type : "get",
        timeout: 30000,
        contentType: "application/json",
        dataType : "json",
        success : function(data, status, xhr) {

            let dataHeader = data.result.response.header.resultCode;

            if (dataHeader == "00") {
               console.log("success == >");
               console.log(data);
            } else {
               console.log("fail == >");
               console.log(data);               
            }
        },
        error : function(e, status, xhr, data) {
            console.log("error == >");
            console.log(e);
        }
    });
}

function weatherInfo() {
	var regionId = Math.floor(Math.random() * 100) + 1;
	
	$.ajax({
        type: "GET",
        url: "/dangi/updateDB?regionId=" + regionId,
        dataType: "json",
        success: function (region) {
            if(region == null) {
                $("#weatherInfo").text("������ �ҷ����� �� ������ �߻��߽��ϴ�.");
            } else {
                var lastUpdateTime = region.weather.lastUpdateTime;
                var temp = region.weather.temp;
                var humid = region.weather.humid;
                var rainAmount = region.weather.rainAmount;
                $("#weatherInfo").text(region.parentRegion + " " + region.childRegion + " �µ�: " + temp + "��, ����: " + humid
                    + "%, ������: " + rainAmount + "mm (���� ����: " + lastUpdateTime + "��)");
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
	
	var regionId = Math.floor(Math.random() * 100) + 1;
	
	fetch("/dangi/updateDB?regionId=" + regionId, option)
	.then((res) => res.json())
	.then((region) => {
		if(region == null) {
            $("#weatherInfo").text("������ �ҷ����� �� ������ �߻��߽��ϴ�.");
        } else {
        	var lastUpdateTime = region.weather.lastUpdateTime;
            var temp = region.weather.temp;
            var humid = region.weather.humid;
            var rainAmount = region.weather.rainAmount;
        	$("#weatherInfo").text(region.parentRegion + " " + region.childRegion + " �µ�: " + temp + "��, ����: " + humid
            	+ "%, ������: " + rainAmount + "mm (���� ����: " + lastUpdateTime + "��)");
        }
	})
	.catch((error) => {
		$("#weatherInfo").text("������ �ҷ����� �� ������ �߻��߽��ϴ�.");
	})
}
</script>
</html>