
vw.ol3.MapOptions = {
  basemapType: vw.ol3.BasemapType.PHOTO, //_HYBRID,
  controlDensity: vw.ol3.DensityType.EMPTY,
  interactionDensity: vw.ol3.DensityType.EMPTY,
  controlsAutoArrange: true,
  homePosition: vw.ol3.CameraPosition,
  initPosition: vw.ol3.CameraPosition
 };
 mapVmap = new vw.ol3.Map("vworld_map",  vw.ol3.MapOptions);
 mapVmap.getView().setCenter(ol.proj.transform([lng, lat], 'EPSG:4326', 'EPSG:3857'));
 mapVmap.getView().setZoom(18);

 //맵 종류에 따른 변경
 switch (mapType) {
   case "PHOTO":
    mapVmap.setBasemapType(vw.ol3.BasemapType.PHOTO);
   break;
   case "BASIC":
    mapVmap.setBasemapType(vw.ol3.BasemapType.GRAPHIC);
   break;
   case "HYBRID":
    mapVmap.setBasemapType(vw.ol3.BasemapType.PHOTO_HYBRID);
   break;
   default:
    mapVmap.setBasemapType(vw.ol3.BasemapType.PHOTO);
   break;
 }
