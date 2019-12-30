//위치이동(고정)
function android_receiveMSGWithCenter(lon,lat){
  var latitude  = Number(lat);
  var longitude = Number(lon);
  var location = ol.proj.transform([longitude,latitude], 'EPSG:4326', 'EPSG:3857');
  marker.setPosition(location);
  map.getView().setCenter(location);
};
//위치이동(고정 ㄴㄴ)
function android_receiveMSGExceptCenter(lon,lat) {
  var latitude  = Number(lat);
  var longitude = Number(lon);
  var location = ol.proj.transform([longitude,latitude], 'EPSG:4326', 'EPSG:3857');
  marker.setPosition(location);
};
//포인트 visibility 설정
function android_receiveMSGPointVisible(TF) {
  if(TF) {
    onOffVector.setVisible(true);
  }
  else {
    onOffVector.setVisible(false);
  }
}
//지번 visibility 설정
function android_receiveMSGJibunVisible(TF) {
  if(TF) {
    JIBUN_Label.setVisible(true);
  }
  else {
    JIBUN_Label.setVisible(false);
  }
}

//검색
function android_receiveSearch(searchT) {
  ajax_findCoordXY(searchT);
}
