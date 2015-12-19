package com.example.user.everydaylifelogger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    SQLiteDatabase db;
    String dbInput = "idList.db";
    String tableInput = "idListTable";
    int dbMode = Context.MODE_PRIVATE;

    Button mBtSave;
    Button mBtList;
    EditText mEtInput;
    CheckBox checkBox;
    //일어난 사건의 카테고리를 공부와 공부가 아닌것으로 나누었고, 사건의 변수를 event라는 전역변수로 두었다.
    String study = "not study";
    String event = "not null";

    ArrayAdapter<String> baseAdapter;
    ArrayList<String> inputList;

    private GoogleMap map;
    static final LatLng SEOUL = new LatLng( 37.56, 126.97);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //데이터베이스 생성
        db = openOrCreateDatabase(dbInput,dbMode,null);
        createTable();

        mBtList = (Button) findViewById(R.id.bt_list);
        mBtSave = (Button) findViewById(R.id.bt_save);
        mEtInput = (EditText) findViewById(R.id.et_text);
        checkBox = (CheckBox) findViewById(R.id.cb_study);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //공부인지 아닌지의 여부를 checkbox의 check여부로 구분한다.
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            //checkbox에 체크가 되었거나 취소하였을때 토스트메세지를 통해서 알림을 띄운다.
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.getId() == R.id.cb_study){

                    if(isChecked){
                        study = "study";
                        Toast.makeText(getApplicationContext(),"STUDY",Toast.LENGTH_LONG).show();
                    }
                    else {
                        study = "not study";
                        Toast.makeText(getApplicationContext(),"NOT STUDY",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        map = mapFragment.getMap();

        //현재 위치로 가는 버튼을 표시한다
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom( SEOUL, 15));

        MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
            @Override
            public void gotLocation(Location location) {

                String msg = "lon: "+location.getLongitude()+" -- lat: "+location.getLatitude();
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                drawMarker(location);

            }
        };

        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(getApplicationContext(), locationResult);

        //Save버튼을 누른경우, EditText를 통해 사용자로 부터 사건을 입력받아서 db에 저장한다.
        mBtSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                event = mEtInput.getText().toString();
                insertData();
                Toast.makeText(getApplicationContext(), "저장완료!", Toast.LENGTH_LONG).show();
            }

        });

        //View List버튼을 누른경우, 창을 전환하여 사용자가 입력한 사건을 공부와 공부가 아닌 사건으로 구분하여 나열한다.
        mBtList.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, ViewList.class);
                startActivity(intent);
            }
        });
    }

    //테이블생성
    public void createTable() {
        try {
            String sql = "create table " + tableInput + "(id integer primary key autoincrement, " + "study text, event text )";
            db.execSQL(sql);
        } catch (android.database.sqlite.SQLiteException e) {
            Log.d("Lab sqlite","error: "+ e);
        }
    }

    //데이터 추가
    public void insertData() {
        String sql = "insert into " + tableInput + " values(NULL, '" + study + "', '" + event + "');";
        db.execSQL(sql);
    }


    private void drawMarker(Location location) {

        //기존 마커 지우기
        map.clear();
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

        //currentPosition 위치로 카메라 중심을 옮기고 화면 줌을 조정한다.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom( currentPosition, 17));
        map.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);

        //마커 추가
        map.addMarker(new MarkerOptions()
                .position(currentPosition)
                .snippet("Lat:" + location.getLatitude() + "Lng:" + location.getLongitude())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("현재위치"));
    }

}