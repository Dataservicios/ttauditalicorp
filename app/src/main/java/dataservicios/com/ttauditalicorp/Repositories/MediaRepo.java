package dataservicios.com.ttauditalicorp.Repositories;

/**
 * Created by Webmaster on 27/08/2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import dataservicios.com.ttauditalicorp.Model.Media;
import dataservicios.com.ttauditalicorp.SQLite.DatabaseHelper;

public class MediaRepo extends DatabaseHelper {
    private static final String TABLE_MEDIAS = "medias";
    private static final String LOG = "MediaTable";
    private static final String KEY_ID = "id";
    private static final String KEY_PRODUCT_ID = "product_id";
    private static final String KEY_STORE_ID = "store_id";
    private static final String KEY_COMPANY_ID = "company_id";
    private static final String KEY_POLL_ID = "poll_id";
    private static final String KEY_PUBLICITY_ID = "publicity_id";
    private static final String KEY_DATE_CREATED= "created_at";

    public MediaRepo(Context context) {
        super(context);
    }


    public int update(Media media) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, media.getId());
        // updating row
        return db.update(TABLE_MEDIAS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(media.getId()) });
    }

    public long insert(Media media) {
        long todo_id;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());
        media.setCreated_at(date);

        // values.put(KEY_ID, audit.getId());
        values.put(KEY_STORE_ID, media.getStore_id());
        values.put(KEY_POLL_ID, media.getPoll_id());
        values.put(KEY_COMPANY_ID, media.getCompany_id());
        values.put(KEY_PRODUCT_ID, media.getProduct_id());
        values.put(KEY_PUBLICITY_ID, media.getPublicity_id());
        values.put(KEY_DATE_CREATED, media.getCreated_at());

        todo_id = db.insert(TABLE_MEDIAS, null, values);
        db.close();
        return todo_id;
    }

    public Media getMedia(long idMedia) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_MEDIAS + " WHERE "
                + KEY_ID + " = " + idMedia;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        Media pd = new Media();
        if (c.moveToFirst()) {

            int id = Integer.parseInt(c.getString(c.getColumnIndex(KEY_ID)));
            pd.setId(id);
            pd.setStore_id(c.getInt(c.getColumnIndex(KEY_STORE_ID)));
            pd.setPoll_id((c.getInt(c.getColumnIndex(KEY_POLL_ID))));
            pd.setCompany_id((c.getInt(c.getColumnIndex(KEY_COMPANY_ID))));
            pd.setProduct_id((c.getInt(c.getColumnIndex(KEY_PRODUCT_ID))));
            pd.setCreated_at((c.getString(c.getColumnIndex(KEY_DATE_CREATED))));
        }
        c.close();
        db.close();
        return pd;
    }


}

