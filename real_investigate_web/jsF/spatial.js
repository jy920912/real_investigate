//GIS서버 wms 주소
var wmsServer = 'http://115.95.67.133:5088/geoserver/SPATIAL_'+sidoCode+'/wms';

//지번 레이어
var JIBUN_Source = new ol.source.TileWMS({
  url:wmsServer,
  params: {VERSION: '1.3.0',LAYERS: 'SPATIAL_'+sidoCode+':'+sidoCode+'_JIBUN'},
  serverType: 'geoserver',
  crossOrigin: 'anonymous'
});
var JIBUN_Layer = new ol.layer.Tile({
  source : JIBUN_Source,
  minResolution: 0,
  maxResolution: 4,
  layerName : 'SPATIAL_'+sidoCode+':'+sidoCode+'_JIBUN',
  layerCategory : 'WMS',
  type : 'WMS',
  visible : true,
});

//지번 라벨
var JIBUN_LSource = new ol.source.TileWMS({
  url: wmsServer, //geoserver wms
  params: {VERSION: '1.3.0',LAYERS: 'SPATIAL_'+sidoCode+':'+sidoCode+'_JIBUN_LABEL'},
  crossOrigin: 'anonymous', //except crossOrigin
  serverType: 'geoserver'
});
var JIBUN_Label = new ol.layer.Tile({
  source: JIBUN_LSource,
  minResolution: 0,
  maxResolution: 2,
  layerName : 'SPATIAL_'+sidoCode+':'+sidoCode+'_JIBUN_LABEL',
  layerCategory : 'WMS',
  type : 'WMS',
  visible : true,
});

//도로 레이어
var ROAD_Source = new ol.source.TileWMS({
  url:wmsServer,
  params: {VERSION: '1.3.0',LAYERS: 'SPATIAL_'+sidoCode+':'+sidoCode+'_ROAD'},
  serverType: 'geoserver',
  crossOrigin: 'anonymous'
});
var ROAD_Layer = new ol.layer.Tile({
  source : ROAD_Source,
  minResolution: 0,
  maxResolution: 64,
  layerName : 'SPATIAL_'+sidoCode+':'+sidoCode+'_ROAD',
  layerCategory : 'WMS',
  type : 'WMS',
  visible : true,
});

//읍면동 레이어
var UMD_Source = new ol.source.TileWMS({
  url:wmsServer,
  params: {VERSION: '1.3.0',LAYERS: 'SPATIAL_'+sidoCode+':'+sidoCode+'_UMD'},
  serverType: 'geoserver',
  crossOrigin: 'anonymous'
});
var UMD_Layer = new ol.layer.Tile({
  source : UMD_Source,
  layerName : 'SPATIAL_'+sidoCode+':'+sidoCode+'_UMD',
  layerCategory : 'WMS',
  type : 'WMS',
  visible : true,
});

//리 레이어
var RI_Source = new ol.source.TileWMS({
  url:wmsServer,
  params: {VERSION: '1.3.0',LAYERS: 'SPATIAL_'+sidoCode+':'+sidoCode+'_RI'},
  serverType: 'geoserver',
  crossOrigin: 'anonymous'
});
var RI_Layer = new ol.layer.Tile({
  source : RI_Source,
  minResolution: 0,
  maxResolution: 8,
  layerName : 'SPATIAL_'+sidoCode+':'+sidoCode+'_RI',
  layerCategory : 'WMS',
  type : 'WMS',
  visible : true,
});
