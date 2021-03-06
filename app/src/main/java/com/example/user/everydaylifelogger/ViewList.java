package com.example.user.everydaylifelogger;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ViewList extends AppCompatActivity {

    SQLiteDatabase db;
    String dbInput = "idList.db";
    String tableInput = "idListTable";
    int dbMode = Context.MODE_PRIVATE;

    ListView mList;
    ArrayAdapter<String> baseAdapter;
    ArrayList<String> inputList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);

        db = openOrCreateDatabase(dbInput,dbMode,null);

        ListView mList = (ListView) findViewById(R.id.list_view);

        // Create listview
        inputList = new ArrayList<String>();
        baseAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, inputList);
        mList.setAdapter(baseAdapter);

        //ListView의 카테고리를 구분한다.
        baseAdapter.add("Number  /  Study or Not   /   Event");
        selectAll();

    }

    //모든데이터를 읽어온다
    public void selectAll() {
        String sql = "select * from " + tableInput + ";";
        Cursor results = db.rawQuery(sql, null);
        results.moveToFirst();

        while (!results.isAfterLast()) {
            //칼럼0에는 index를, 칼럼1에는 공부의 여부를, 칼럼2에는 사용자가 입력한 사건을 입력받는다.
            int id = results.getInt(0);
            String study = results.getString(1);
            String event = results.getString(2);
//            Toast.makeText(this, "index= " + id + " name=" + name, Toast.LENGTH_LONG).show();
            Log.d("lab_sqlite", "index= " + id + "study= " + study + " name= " + event);

            //ListView를 통해 출력한다
            inputList.add(id + "  /  " + study + "  /  " + event);
            //inputList.add(event);
            results.moveToNext();
        }

        results.close();
    }

}

