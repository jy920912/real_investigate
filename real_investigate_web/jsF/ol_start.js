var lat = 36.42407749298651; //위도
var lng = 127.406610175387; //경도
var sidoCode = "44131"; //시도코드
var mapType = "PHOTO"; //맵 종류
var jjk = true; //지적 출력 여부
var jbn = true; //지번 출력 여부

//주소 뒷부분 가져오기 (위도, 경도, 시도코드, 맵 종류, 지적, 지번 순)
var tempUrl = window.location.search.substring(1);
if(tempUrl != '')
  {
    var tempArray = tempUrl.split('&');
    //위도
    var keyValuelng = tempArray[0].split('=');
    lng = Number(keyValuelng[1]);
    //경도
    var keyValuelat = tempArray[1].split('=');
    lat = Number(keyValuelat[1]);
    //시도코드
    var keyValueSido = tempArray[2].split('=');
    sidoCode = keyValueSido[1];
    //맵 종류
    var keyValueMap = tempArray[3].split('=');
    mapType = keyValueMap[1];
    //지적 출력 여부
    var keyValueJjk = tempArray[4].split('=');
    jjk = keyValueJjk[1];
    //지번 출력 여부
    var keyValueJbn = tempArray[5].split('=');
    jbn = keyValueJbn[1];
  }
