package com.example.locationtutorial;

import android.provider.BaseColumns;

public final class MemoContract {
    private  MemoContract(){

    }

    public  static class MemoEntry implements BaseColumns {
        public static final String TABLE_NAME = "memo";     //테이블 명
        public static final String COLUMN_NAME_TITLE = "title";     //컬럼 네임
        public static final String COLUMN_NAME_LAT = "lat";         //위도
        public static final String COLUMN_NAME_LNG = "lng";         //경도
    }
}
