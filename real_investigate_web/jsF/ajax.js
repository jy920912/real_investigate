//PNU 찾기 onOff는 array[3]
function ajax_findPNU(loc,sido, AorW, onOff) {
  //선택한 좌표에 맞는 pnu 구하기
  var url = JIBUN_Source.getGetFeatureInfoUrl(loc, view.getResolution(), view.getProjection(), {
    INFO_FORMAT: 'application/json',
    FEATURE_COUNT: 1
  });
  $.ajax({
    url: url,
    dataType:'json',
    error: function(xhr, status, err) {
      return false;
    },
    success: function(data, status, xhr) {
      var dif = new ol.format.GeoJSON(),
      features = dif.readFeatures(data);
      if (features.length) {
        var feature = features[0];
        var pnu = feature.get('PNU');
        //PNU 기반으로 DATA 가져오기
        ajax_searchInfomation(pnu, sido, loc, AorW, onOff);
      }
    }
  });
}
//PNU 데이터 기반으로 DATA 가져오기 onOff는 array[3]
function ajax_searchInfomation(name, sido, loc, AorW, onOff){
  $.ajax({
    type : "POST",
    url : "./jsp/search_ClickInformation.jsp",
    datatype : "text",
    data:{"name":name,"SIDO":sido},
    error : function(){
      return false;
    },
    success : function(data){
      var spData = data.split('\r\n').join('');
      var Data = spData.slice(0,-1);
      var coord = Data.split("|");
      if(AorW == 'A') window.android.android_sendMSG(coord[0],coord[1],onOff[0],onOff[1],onOff[2],coord[2],coord[3],coord[4]);
      else if(AorW == 'W') {
        //마커 생성 onOff는 array[3]
        ajax_createMarker(name, sido, loc, onOff);
      }
    }
  })
}
//마커 생성 onOff는 array[3]
function ajax_createMarker(name, sido, loc, onOff) {
  var iconFeature = new ol.Feature({
    geometry: new ol.geom.Point(loc),
    pnu: name,
    search: onOff[0],
    send: onOff[1],
    dron: onOff[2]
  });
  var iconStyle = new ol.style.Style({
    image: new ol.style.Icon({
      anchor: [0.5, 10],
      anchorXUnits: 'fraction',
      anchorYUnits: 'pixels',
      src: "./PIC/dis.png"
    })
  });
  iconFeature.setStyle(iconStyle);
  onOffSource.addFeature(iconFeature);
  onOffVector.setSource(onOffSource);
  ajax_updateOX('I',sido,onOff,name,loc[0],loc[1])
}
//대상지 불러오기
function ajax_searchpicpic(sido){
  $.ajax({
    type : "POST",
    url : "./jsp/search_picpic.jsp",
    datatype : "text",
    data:{"SIDO":sido},
    error : function(){
      return false;
    },
    success : function(data){
      var spData = data.split('\r\n').join('');
      var Data = spData.slice(0,-1);
      var coord = Data.split("|");
      var pic_onOff; var send_onOff; var dron_onOff;
      var imgPath;
      var rsCount = 6;
      if(coord.length/rsCount >= 1){
        for(var i=0;i<coord.length/rsCount;i++) {
          var pnu = coord[rsCount*i];
          var loc = [Number(coord[rsCount*i+4]), Number(coord[rsCount*i+5])];
          if(coord[rsCount*i+1] == 'X') {
            pic_onOff = 'X';
            imgPath = "./PIC/dis.png";
          }
          else {
            pic_onOff = 'O';
            imgPath = "./PIC/cap.png";
          }
          if(coord[rsCount*i+2] == 'X') {
            send_onOff = 'X';
          }
          else {
            send_onOff = 'O';
            imgPath = "./PIC/en.png";
          }
          if(coord[rsCount*i+3] == 'X') {
            dron_onOff = 'X';
          }
          else {
            dron_onOff = 'O';
            imgPath = "./PIC/dronCap.png";
          }
          var iconFeature = new ol.Feature({
            geometry: new ol.geom.Point(loc),
            pnu: pnu,
            search: pic_onOff,
            send: send_onOff,
            dron: dron_onOff
          });
          var iconStyle = new ol.style.Style({
            image: new ol.style.Icon({
              anchor: [0.5, 10],
              anchorXUnits: 'fraction',
              anchorYUnits: 'pixels',
              src: imgPath
            })
          });
          iconFeature.setStyle(iconStyle);
          onOffSource.addFeature(iconFeature);
        }
        onOffVector.setSource(onOffSource);
      }
    }
  })
}
//마커 업데이트
function ajax_updateOX(UID,SIDO,ONOFF,PNU,COORDX,COORDY) {
  $.ajax({
    type : "POST",
    url : "./jsp/Permute_picpic.jsp",
    datatype : "text",
    data:{"UID":UID,"SIDO":SIDO,"CAPONFF":ONOFF[0],"SNDONOFF":ONOFF[1],"DRNONOFF":ONOFF[2],"PNU":PNU,"X":COORDX,"Y":COORDY},
    error : function(data){
      return false;
    },
    success : function(data){
    }
  })
}
Proj4js.defs["EPSG:3857"] = "+proj=merc +a=6378137 +b=6378137 +lat_ts=0.0 +lon_0=0.0 +x_0=0.0 +y_0=0 +k=1.0 +units=m +nadgrids=@null +no_defs";
Proj4js.defs["EPSG:5181"] = "+proj=tmerc +lat_0=38 +lon_0=127 +k=1 +x_0=200000 +y_0=500000 +ellps=GRS80 +units=m +no_defs";
Proj4js.defs["BESSEL"] = "+proj=longlat +ellps=bessel +no_defs +towgs84=-115.80,474.99,674.11,1.16,-2.31,-1.63,6.43";
Proj4js.defs["GRS80"] = "+proj=longlat +ellps=GRS80 +no_defs";
var epsg3857 = new Proj4js.Proj('EPSG:3857');
var epsg5181 = new Proj4js.Proj('EPSG:5181');
var bessel = new Proj4js.Proj('BESSEL');
var grs80 = new Proj4js.Proj('GRS80');
function ajax_findCoordXY(searchT) {
  $.ajax({
    type : "POST",
    url : "./jsp/search_address.jsp",
    datatype : "text",
    data:{"NAME":searchT,"SIDO":sidoCode},
    error : function(data){
      return false;
    },
    success : function(data){
      var spData = data.split('\r\n').join('');
      var Data = spData.slice(0,-1);
      var coord = Data.split(",");
      var coord_X = coord[0];
      var coord_Y = coord[1];
      if(coord_X != null && coord_X != '') {
        var pointInput = coord_X + ',' +coord_Y;
        var pointSource = new Proj4js.Point(pointInput);
        var pointDest = Proj4js.transform(epsg5181, epsg3857, pointSource);
        var newCenter = [pointDest.x, pointDest.y];
        view.setCenter(newCenter);
        view.setZoom(18);
      }
      else {
        window.android.android_sendNoAddress(searchT);
      }
    }
  });
}
