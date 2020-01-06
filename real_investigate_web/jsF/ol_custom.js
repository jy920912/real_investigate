//페이지 접근 시 대상지 출력 함수 실행
$(document).ready(function(){
  ajax_searchpicpic(sidoCode);
})

//대상지 벡터소스
var onOffSource = new ol.source.Vector();
//대상지 벡터
var onOffVector = new ol.layer.Vector();

var maxzoom = 19;
if(mapType == "PHOTO") {
  maxzoom = 18;
}

//지도 뷰 오브젝트 생성
var view = new ol.View({
  center:ol.proj.transform([lng, lat], 'EPSG:4326', 'EPSG:3857'),
  maxZoom: maxzoom,
  minZoom:10,
  zoom: 18
});

//마커 생성
var markerImg = document.getElementById('popup');
var marker = new ol.Overlay({
  position: ol.proj.transform([lng, lat], 'EPSG:4326', 'EPSG:3857'),
  element: markerImg
});

//더블클릭 제한(지도회전 제한 제거)
var rotateinteractions = ol.interaction.defaults({
  //altShiftDragRotate:false,
  //pinchRotate:false,
  doubleClickZoom:false
});

//맵 오브젝트 생성(spatial_map div에 적용)
var map = new ol.Map({
  target: 'spatial_map',
  layers: [JIBUN_Layer, RI_Layer, UMD_Layer, ROAD_Layer, SELECT_JIBUN_Layer, JIBUN_Label, onOffVector],
  view: view,
  overlays: [marker],
  renderer: 'canvas',
  interactions: rotateinteractions,
  loadTilesWhileAnimating: true,
  loadTilesWhileInteracting: true,
});

//안드로이드 설정 적용
//지적 출력 여부
if(jjk == 'true') {
  JIBUN_Layer.setVisible(true);
}
else {
  JIBUN_Layer.setVisible(false);
}
//지번 출력 여부
if(jbn == 'true') {
  JIBUN_Label.setVisible(true);
}
else {
  JIBUN_Label.setVisible(false);
}
//왼쪽클릭(안드로이드에서 터치 시)
map.on('singleclick', findClickMarger);

//클릭 마커 주소 찾기
function findClickMarger(e) {
  var loc = e.coordinate;
  //클릭한 지점에서 가까운 대상지 찾기
  var feature = map.forEachFeatureAtPixel(e.pixel, function(feature) {return feature;});
  if(feature) {
    clickSource = onOffSource.getClosestFeatureToCoordinate(loc);
    var onOff = new Array();
    onOff[0] = clickSource.getProperties().search;
    onOff[1] = clickSource.getProperties().send;
    onOff[2] = clickSource.getProperties().dron;
    var pnu = clickSource.getProperties().pnu;
    ajax_searchInfomation(pnu, sidoCode, loc, 'A', onOff);
  }
  //없으면 클릭한 지적 표시
  else {
    ajax_clickPNU(loc);
  }
}

//마커 완료 미완료 표시 변경
function changeMarker(pnu,cpnf, sdnf, drnf) {
  var onOff = [cpnf, sdnf, drnf];
    ajax_updateOX("U",sidoCode,onOff,pnu,0,0);
}

//더블클릭
map.on('dblclick', createMarker);
function createMarker(e) {
  var loc = e.coordinate;

  //클릭한 지점에서 가까운 대상지 찾기
  var feature = map.forEachFeatureAtPixel(e.pixel, function(feature) {return feature;});
  if(feature) {
    //마커 제거
    delSource = onOffSource.getClosestFeatureToCoordinate(loc);
    onOffSource.removeFeature(delSource);
    var onOff = new Array();
    onOff[0] = delSource.getProperties().search;
    onOff[1] = delSource.getProperties().send;
    onOff[2] = delSource.getProperties().dron;
    var pnu = delSource.getProperties().pnu;
    ajax_updateOX("D",sidoCode,onOff,pnu,0,0);
  }
  //없으면 마커 생성
  else {
    var onOff = ['X','X','X'];
    ajax_findPNU(loc,sidoCode, 'W', onOff);
  }
}

//센터 이동 시 when go to center
view.on('change:center', function(){
  var center = view.getCenter();
  mapVmap.getView().setCenter(center);
});

//확대 및 축소 시 when zoom in or zoom out
view.on('change:resolution', function(){
  var center = view.getCenter();
  var zoomLevel = view.getZoom();
  mapVmap.getView().setCenter(center);
  mapVmap.getView().setZoom(zoomLevel);
});
//화면 회전 시
view.on('change:rotation', function(){
  var sp_rotation = map.getView().getRotation();
  mapVmap.getView().setRotation(sp_rotation);
});
