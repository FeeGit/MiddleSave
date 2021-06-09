package com.example.locationtutorial;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MemoDbHelper extends SQLiteOpenHelper {    //OpenHelper 를 기본적으로 상속받아 사용.
    private static com.example.locationtutorial.MemoDbHelper sInstance;

    //이 db version을 선언해준다. 사실 활용 할 일은 없었지만, 어떤식으로 이용되야 할 지는 감이 잡힌다.
    //test를 하면서 table에 개수가 변하게 된 적이 있었다. 그때, 아무리 테스트를 해도 저장에서 오류가 발생했었는데,
    //이때 해결은 app을 지우고 다시 설치하여 문제를 해결하였다.
    //허나, 이때, version을 한단계 올려주고, 내부적인 문제를 해결하도록 하는 방법이 있지 않았을까 싶다.
    private static final int DB_VERSION = 1;        //db 버젼. 1번.
    //db의 이름 선언
    private static final String DB_NAME = "Memo.db";        //db 파일 이름.
    //db에 담기는 형식을 결정해준다. 아래는 sql문법이라는데, 우선 활용할 부분만 찾아서 활용하였다.
    private static final String SQL_CREATE_ENTRIES =        //유지보수를 위하여 미리 상수로 제작.
            String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT)",      //autoimpliment: 자동 증가.
                    MemoContract.MemoEntry.TABLE_NAME,  //각 %s에 들어갈 항목들. 순서대로.
                    MemoContract.MemoEntry._ID,
                    MemoContract.MemoEntry.COLUMN_NAME_TITLE,
                    MemoContract.MemoEntry.COLUMN_NAME_LAT,
                    MemoContract.MemoEntry.COLUMN_NAME_LNG
            );

    //db에서 데이터를 불러올 때, MemoDbHelper dbHelper = MemoDbHelper.getInstance(this);
    //와 같은 방법으로 데이터를 가져오게 된다.
    public static com.example.locationtutorial.MemoDbHelper getInstance(Context context){
        if (sInstance == null){
            sInstance = new com.example.locationtutorial.MemoDbHelper(context);
        }
        return sInstance;
    }
    //db를 지우는 방식 구현
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS" + MemoContract.MemoEntry.TABLE_NAME;

    public MemoDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //처음 활용할 때, sql을 만들어준다.
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {   //최초의 db를 생성하는 부분. db를 사용하게 되면 호출.
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);     //sql문으로 테이블을 생성하는 코드 작성.
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //이전 테이블과의 변경점을 처리.
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
    }
}
