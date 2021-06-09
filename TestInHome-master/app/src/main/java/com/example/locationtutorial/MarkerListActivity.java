package com.example.locationtutorial;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MarkerListActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_INSERT = 1000;

    private MemoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_list);

        FloatingActionButton fab = findViewById(R.id.fab);      // + 버튼을 누르면 작동. 플로팅 액션
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(com.example.locationtutorial.MarkerListActivity.this, MarkerAddActivity.class), REQUEST_CODE_INSERT);
            }
        });     //변경사항이 바로 보이게끔. 받는 코드 필요.

        //쿼리 항목을 리스트로 표현하기 위해서, xml에 있는 리스트 id에 등록을 하자.
        ListView listView = findViewById(R.id.memo_list);

        //쿼리를 가져오자.
        Cursor cursor = getMemoCursor();        // query 의 리턴은 cursor. 커서로 받는다.
        //query 항목을 처리를 해주자.
        mAdapter = new MemoAdapter(this, cursor);       // 어댑터 생성 후 커서 던져주기.
        //처리한 데이터를 리스트에 등록한다.
        listView.setAdapter(mAdapter);      //리스트 뷰에 어댑터 던지기.

        //이 아이템을 눌르면, 수정하게 만들기.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(com.example.locationtutorial.MarkerListActivity.this, MarkerAddActivity.class);

                //데이터를 Cursor에 담아 가져오자. 이때, 아이템은 position을 통해 원하는 것을 골라 가져온다.
                Cursor cursor = (Cursor) mAdapter.getItem(position);        // 커서 얻어오기.

                //가져온 데이터의 TITLE, LAT, LNG을 string에 담아 //클릭한 곳의 데이터 가져오기.
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry.COLUMN_NAME_TITLE));
                String lat = cursor.getString(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry.COLUMN_NAME_LAT));
                String lng = cursor.getString(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry.COLUMN_NAME_LNG));

                //intent 내부에 넣어준다.(editText 부분)     //intent 에 putextra 로 전달.
                intent.putExtra("id", id);
                intent.putExtra("title", title);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);

                //전달할 데이터를 담아서 전달. 수정이 될 수도 있기 때문에 아래와 같이 전달.
                startActivityForResult(intent, REQUEST_CODE_INSERT);
            }
        });

        //리스트를 삭제.
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {     //길게 눌렀을때 반응
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                final long deleteId = id;       //삭제용 아이디.
                //리스트를 삭제하는것은 알람 팝업을 따로 구현하여 추가적인 확인을 구하자.
                AlertDialog.Builder builder = new AlertDialog.Builder(com.example.locationtutorial.MarkerListActivity.this);     //다이얼을 띄워서 물어보고 지우기.
                //내부에 들어갈 글을 적어주자.
                builder.setTitle("메모 삭제");
                builder.setMessage("메모를 삭제하시겠습니까?");
                //yes버튼이 눌리면
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //db를 write 형식으로 수정이 가능하도록 가져온 다음.      //db 얻어오기.
                        SQLiteDatabase db = MemoDbHelper.getInstance(com.example.locationtutorial.MarkerListActivity.this).getWritableDatabase();
                        //삭제할 id부분을 지워주자.(MemoDbHelper에 구현이 되어있다.)
                        //이때, return 값은 삭제한 data의 개수로 1이 반환이 된다.        //db delete. deletid 와 id가 같은것을 찾아 삭제.
                        int deletedCount = db.delete(MemoContract.MemoEntry.TABLE_NAME, MemoContract.MemoEntry._ID + " = " + deleteId, null);

                        //만일 삭제가 되지 않았다면 오류
                        if(deletedCount == 0){      //삭제 갯수가 0개면
                            Toast.makeText(com.example.locationtutorial.MarkerListActivity.this, "삭제에 문제가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //삭제 된 갯수가 0이 아니라면
                            mAdapter.swapCursor(getMemoCursor());
                            Toast.makeText(com.example.locationtutorial.MarkerListActivity.this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.show();
                return true;
            }
        });
    }


    //쿼리를 가져오는 메소드
    private  Cursor getMemoCursor(){
        //db에서 instance를 가져온다.
        MemoDbHelper dbHelper = MemoDbHelper.getInstance(this);     //db에 있는 데이터를 가져온다.
        //query를 전부 가져오자.
        //MemoContract.MemoEntry._ID + "DESC"(마지막 순서에 적으면, 내림차순으로 정렬한다고 하는데, 어디에 이용할 지는 아직 고려중.)
        //쿼리로 가져온다. sql을 잘 모르고, 모든 테이블을 가져오고 싶다? 전부 null로 채우자.
        return dbHelper.getReadableDatabase().query(MemoContract.MemoEntry.TABLE_NAME, null, null, null,null,null,null,null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {       //startActivityforResult 를 받는 부분
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_INSERT && resultCode == RESULT_OK){      // result를 받는데 성공한다면
            //swapCursor는 cursor를 교체해주는 역할을 하는데,
            //지워져서 사라진 부분이 생기면 list를 다시 불러오는 기능을 한다.
            mAdapter.swapCursor(getMemoCursor());       //최신 데이터로 로드.
        }
    }

    //cursor을 처리해주는 내부 클래스를 만들자.
    private static class MemoAdapter extends CursorAdapter{
        //가져온 데이터를 뿌려주기 위해서 cursor을 가져온다.
        public MemoAdapter(Context context, Cursor c) {
            super(context, c, false);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            //안드로이드에서 제공하는 기본 레이아웃 사용.
            return LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, viewGroup, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            //textView에 연결해준다.
            TextView titleText = view.findViewById(android.R.id.text1); // simple_list_item_1의 id.
            //커서에 담긴 항목중, title값을 가져와서 추가한다. 텍스트뿌리기.
            titleText.setText(cursor.getString(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry.COLUMN_NAME_TITLE)));
        }
    }
}
