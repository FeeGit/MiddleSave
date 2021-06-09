package com.example.locationtutorial;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MarkerAddActivity extends AppCompatActivity {
    //화면에 적은 글자를 활용하도록 전역변수로 선언해준다.
    private EditText mTitleEditText;
    private EditText mLatEditText;      //위도
    private EditText mLngEditText;      //경도
    private long mMemoId = -1;      //수정을 위해선 id를 공유해야 하므로 생성.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_add);
        //위에 선언한 변수와 layout에 존재하는 edittext를 연결해준다.
        mTitleEditText = findViewById(R.id.title_edit);
        mLatEditText = findViewById(R.id.lat_edit);
        mLngEditText = findViewById(R.id.lng_edit);

        //이부분은 db를 연동하는 부분이다. 데이터 뿌려주기.
        Intent intent = getIntent();
        if(intent != null){     // 누군가가 intent를 호출했다면

            //이후에 db에 데이터를 저장하면서 관리하기 쉽게 id를 부여해준다.
            mMemoId = intent.getLongExtra("id", -1);        //아이디 갱신
            String title = intent.getStringExtra("title");
            String lat = intent.getStringExtra("lat");
            String lng = intent.getStringExtra("lng");

            mTitleEditText.setText(title);
            mLatEditText.setText(lat);
            mLngEditText.setText(lng);
        }
    }

    public void addMarker(View view) {      // addMaker 를 눌렀다면?
        //editText에 적혀있는 글을 string으로 읽어온다.
        String title = mTitleEditText.getText().toString();
        String lat = mLatEditText.getText().toString();
        String lng = mLngEditText.getText().toString();

        //db에 저장하는 형식으로 데이터를 변환해준다.
        ContentValues contentValues = new ContentValues();      // db 저장용 방법. contentValue 객체에 담아 저장한다.
        contentValues.put(MemoContract.MemoEntry.COLUMN_NAME_TITLE, title);     // 저장할것들.
        contentValues.put(MemoContract.MemoEntry.COLUMN_NAME_LAT, lat);
        contentValues.put(MemoContract.MemoEntry.COLUMN_NAME_LNG, lng);

        //db를 write형식으로 가져오자.
        SQLiteDatabase db = MemoDbHelper.getInstance(this).getWritableDatabase();       //db에 작성하는 코드. 에러 발생시 -1 return

        //만약 수정을 하고 뒤로 갔으면? 그에 대한 코드.
        if(mMemoId == -1){      // 삽입을 했다면?
            //db에 데이터를 넣어준다.
            long newRowId = db.insert(MemoContract.MemoEntry.TABLE_NAME,null, contentValues);       //db에 데이터 넣기. return long

            //제대로 들어갔는지 toast메시지로 확인하자.
            if(newRowId == -1){
                Toast.makeText(this, "저장에 문제가 발생하였습니다.", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "경계가 추가되었습니다", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);

                startActivity(new Intent(this, com.example.locationtutorial.MarkerListActivity.class));
            }
        }
        else{
            //수정을 해주는 코드.
            //db 업데이트 하기.   //현재 아이디가 mMemId와 같을때 그 객체를 contentValues로 덮어 쓰겠다.
            int count = db.update(MemoContract.MemoEntry.TABLE_NAME, contentValues, MemoContract.MemoEntry._ID + "=" + mMemoId, null);

            if(count == 0) {        //수정된 갯수: count. 수정된것이 없다? 문제 발생 .
                Toast.makeText(this, "수정에 문제가 발생하였습니다.", Toast.LENGTH_SHORT).show();
            }
            else {      //수정된 갯수가 0이 아님 : 수정 성공 .
                Toast.makeText(this, "경계가 수정되었습니다", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
            }
        }





    }
}
