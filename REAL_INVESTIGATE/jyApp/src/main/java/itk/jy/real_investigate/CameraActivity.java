/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package itk.jy.real_investigate;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import itk.jy.real_investigate.Camera.Camera2BasicFragment;
import itk.jy.real_investigate.Permission.PermissionRequester;

/* ** 화면을 풀 스크린으로 변경?*/
public class CameraActivity extends AppCompatActivity {
    private static String addressString;
    private static int near_zzic;
    private static int far_zzic;
    private Intent addressIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //title bar 제거
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //status bar 제거
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);

        //FragmentContent에서 보낸 주소 데이터 받기
        addressIntent = getIntent();
        addressString = addressIntent.getStringExtra("pnuName");

        //camera surface view 등록
        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2BasicFragment.newInstance())
                    .commit();
        }
        PermissionRequester.Builder requester = new PermissionRequester.Builder(this);
        int result = requester.create()
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, 20000, new PermissionRequester.OnClickDenyButtonListener() {
                    @Override
                    public void onClick(Activity activity) {

                    }
                });

        // 사용자가 권한을 수락한 경우
        if (result == PermissionRequester.ALREADY_GRANTED || result == PermissionRequester.REQUEST_PERMISSION) {

        } else {

        }

    }
    @Override
    public void onBackPressed() {
        getIntent().removeExtra("pnuName");
        //찍은 사진 목록을 FragmentContent로 전송
        setResult(1213,addressIntent);
        near_zzic = 0; far_zzic = 0;

        super.onBackPressed();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    public void zzicCount(int zzicWhat) {
        if(zzicWhat == 0) { ++near_zzic; }
        else if(zzicWhat == 1) { ++far_zzic; }
    }
    public int getzzic(int zzicWhat) {
        int zzic = 0;
        if (zzicWhat == 0) {zzic = near_zzic; }
        else if (zzicWhat == 1) { zzic = far_zzic; }
        return zzic;
    }
    public String getAddressString() { return addressString; }
    public void putFileName(String Count, String Name) { addressIntent.putExtra(Count,Name); }
}
