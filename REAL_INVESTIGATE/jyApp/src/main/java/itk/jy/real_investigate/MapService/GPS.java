package itk.jy.real_investigate.MapService;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import itk.jy.real_investigate.MainActivity;


public class GPS extends Service implements LocationListener {
    private Context mContext;
    //GPS on/off
    private boolean isGPSEnable = false;
    //네트워크 on/off
    private boolean isNetWorkEnable = false;
    //좌표 받기 on/off
    private boolean isGetLocation = false;
    //좌표
    private Location location;

    //GPS좌표 업데이트 주기(거리, 시간 두가지 변경 되어야 업데이트 가능)
    private static final long MIN_DISTANCE_UPDATE = 1; //좌표 변경 시 업데이트 할 거리(m)
    private static final long MIN_TIME_UPDATE = 2000; //좌표 업데이트 시간 주기(milliseconds)

    protected LocationManager locationManager;

    public GPS(Context mContext) {
        this.mContext = mContext;
        getLocation();
    }

    /*
     * GPS 혹은 네트워크로 좌표 받기
     * GPS 혹은 네트워크 연결상태 확인 후 GPS -> 네트워크 순으로 좌표 받음
     */
    @SuppressLint("MissingPermission")
    public Location getLocation() {
        try {
            //LocationManager 객체 생성
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            //GPS on/off 확인
            isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            //네트워크 on/off 확인
            isNetWorkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            //둘 다 연결 안되어 있으면
            if (!isGPSEnable && !isNetWorkEnable) {
                // GPS OFF 일때 Dialog 표시
                AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
                gsDialog.setTitle("위치 서비스 설정");
                gsDialog.setMessage("무선 네트워크 사용, GPS 위성 사용을 모두 체크하셔야 정확한 위치 서비스가 가능합니다.\n위치 서비스 기능을 설정하시겠습니까?");
                gsDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // GPS설정 화면으로 이동
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        startActivity(intent);
                    }
                })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();
            }
            else {
                this.isGetLocation = true;
                //GPS로 좌표 받기
                if (isGPSEnable) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_UPDATE, MIN_DISTANCE_UPDATE, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }
                //네트워크로 좌표 받기
                if (isNetWorkEnable) {
                    if(location == null) {
                        //좌표 업데이트 간격
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_UPDATE, MIN_DISTANCE_UPDATE, this);
                        if (locationManager != null) {
                            //마지막 좌표 불러오기
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    public double getLatitude() {
        if (location != null)
            return location.getLatitude();
        else return 0;
    }

    public double getLongitude() {
        if (location != null)
            return  location.getLongitude();
        else return 0;
    }

    public boolean isGetLocation() {
        return this.isGetLocation;
    }

    public void stopUsingGPS() {
        if (locationManager != null)
            locationManager.removeUpdates(GPS.this);
        isGetLocation = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        //GPS좌표 변경 시 행동
        double lon = location.getLongitude();
        double lat = location.getLatitude();
        ((MainActivity)mContext).func_output_lon_lat(lon,lat);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
