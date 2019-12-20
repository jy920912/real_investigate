var lat = 36.42407749298651; //위도
var lng = 127.406610175387; //경도
var sidoCode = "44131";
var mapType = "PHOTO";
var jjk = true;
var jbn = true;
var tempUrl = window.location.search.substring(1);
if(tempUrl != '')
  {
    var tempArray = tempUrl.split('&');
    var keyValuelng = tempArray[0].split('=');
    lng = Number(keyValuelng[1]);
    var keyValuelat = tempArray[1].split('=');
    lat = Number(keyValuelat[1]);
    var keyValueSido = tempArray[2].split('=');
    sidoCode = keyValueSido[1];
    var keyValueMap = tempArray[3].split('=');
    mapType = keyValueMap[1];
    var keyValueJjk = tempArray[4].split('=');
    jjk = keyValueJjk[1];
    var keyValueJbn = tempArray[5].split('=');
    jbn = keyValueJbn[1];
  }
