var wmsServer = 'http://115.95.67.133:5088/geoserver/SPATIAL_'+sidoCode+'/wms';


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
