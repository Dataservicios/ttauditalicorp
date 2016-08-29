package dataservicios.com.ttauditalicorp.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.io.File;
import java.util.List;

import dataservicios.com.ttauditalicorp.Model.Media;
import dataservicios.com.ttauditalicorp.R;

/**
 * Created by Jaime on 29/08/2016.
 */
public class MediaAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Media> mediaItems;
    //ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public MediaAdapter(Activity activity, List<Media> productsItems) {
        this.activity = activity;
        this.mediaItems = productsItems;
    }


    @Override
    public int getCount() {
        return mediaItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mediaItems.get(position);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //View view = convertView;
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_media, null);

       // if (imageLoader == null)  imageLoader = AppController.getInstance().getImageLoader();

        //NetworkImageView thumbNail = (NetworkImageView) convertView.findViewById(R.id.thumbnail);
        ImageView thumbNail = (ImageView) convertView.findViewById(R.id.thumbnail) ;
        TextView tvId = (TextView) convertView.findViewById(R.id.tvId);
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvFecha = (TextView) convertView.findViewById(R.id.tvFecha);

        //ImageView imgStatus = (ImageView) convertView.findViewById(R.id.imgStatus);

        Media m = mediaItems.get(position);

        //thumbNail.setImageUrl(m.getImage(), imageLoader);
        File imgFile = new File("/sdcard/Images/test_image.jpg");
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            thumbNail.setImageBitmap(myBitmap);
        }

        tvId.setText(String.valueOf(m.getId()));

        tvName.setText(m.getFile());

        tvFecha.setText(m.getCreated_at().toString());

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {

        // Deshabilitando los items del adptador segun el statu
//        if( mediaItems.get(position).getActive()==1){
//
//            return false;
//
//        }
        return true;
    }

}
