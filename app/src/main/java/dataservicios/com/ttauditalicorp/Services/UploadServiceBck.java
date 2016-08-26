package dataservicios.com.ttauditalicorp.Services;

import android.app.AlertDialog;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import dataservicios.com.ttauditalicorp.AlbumStorageDirFactory;
import dataservicios.com.ttauditalicorp.BaseAlbumDirFactory;
import dataservicios.com.ttauditalicorp.FroyoAlbumDirFactory;
import dataservicios.com.ttauditalicorp.R;
import dataservicios.com.ttauditalicorp.util.AndroidMultiPartEntity;
import dataservicios.com.ttauditalicorp.util.GlobalConstant;
import dataservicios.com.ttauditalicorp.util.JSONParser;


import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;

/**
 * Created by Jaime on 21/04/2016.
 */
public class UploadServiceBck extends IntentService {
    long totalSize = 0;
    private NotificationManager notificationManager;
    private Notification notification;
    private Context context = this;

    ArrayList<String> names_file = new ArrayList<String>();
    private static final String url_upload_image = GlobalConstant.dominio + "/uploadImagesAudit";
    // private static final String url_insert_image = GlobalConstant.dominio + "/insertImagesPublicities";
    private String url_insert_image ;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    String store_id,publicities_id,invoices_id,tipo,product_id,sod_ventana_id, company_id, poll_id;


    public UploadServiceBck(String name) {
        super(name);
    }
    public UploadServiceBck(){
        super("UploadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // notificationManager = (NotificationManager) getApplicationContext().getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
        //Uri uri  = intent.getData();
        names_file =intent.getStringArrayListExtra("names_file");
        store_id=intent.getStringExtra("store_id");

        publicities_id=intent.getStringExtra("publicities_id");
        product_id=intent.getStringExtra("product_id");
        poll_id=intent.getStringExtra("poll_id");
        company_id = intent.getStringExtra("company_id");
        sod_ventana_id = intent.getStringExtra("sod_ventana_id");
        url_insert_image=intent.getStringExtra("url_insert_image");
        tipo=intent.getStringExtra("tipo");

        //Log.i("FOO", uri.toString());
        new ServerUpdate().execute(names_file);
    }
    class ServerUpdate extends AsyncTask<ArrayList<String>,String,String> {
        //ProgressDialog pDialog;
        @Override
        protected String doInBackground(ArrayList<String>... arg0) {
            Uri uri;
            int lastPercent = 0;
            //uri=arg0[0];
            for (int i = 0; i < arg0[0].size(); i++) {
                String foto = arg0[0].get(i);
                if (uploadFoto(getAlbumDirTemp().getAbsolutePath() + "/" + foto) ) {
                    File file = new File(getAlbumDirTemp().getAbsolutePath() + "/" + foto);
                    file.delete();
                }
            }
            return null;


        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String s) {
            new loadInsert().execute(names_file);
        }
    }


    class loadInsert extends AsyncTask<ArrayList<String>, Integer , Boolean> {
        /**
         * Antes de comenzar en el hilo determinado, Mostrar progresión
         * */
        boolean failure = false;
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(ArrayList<String>... arg0) {
            // TODO Auto-generated method stub
            //cargaTipoPedido();
            for (int i = 0; i < arg0[0].size(); i++) {
                String foto = arg0[0].get(i);
                onInsert(foto);

            }
            return true;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted


        }
    }


    //Metodo que escala la imágen
    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }


    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }


    private boolean uploadFoto(String imag){
        File file = new File(imag);
        Bitmap bbicon;
        String responseString = null;
        HttpClient httpClient = new DefaultHttpClient();

        try {
            bbicon= BitmapFactory.decodeFile(String.valueOf(file));
            Bitmap scaledBitmap;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int soad_ventana_evaluate_id =  Integer.valueOf(sod_ventana_id);
            if(soad_ventana_evaluate_id > 0){

                if(Build.MODEL.equals("MotoG3")){
                    scaledBitmap = rotateImage(scaleDown(bbicon, 700 , true),0);
                } else {
                    scaledBitmap = rotateImage(scaleDown(bbicon, 700 , true),90);
                }

                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                InputStream in = new ByteArrayInputStream(bos.toByteArray());
            } else {
                if(Build.MODEL.equals("MotoG3")){
                    scaledBitmap = rotateImage(scaleDown(bbicon, 450 , true),0);
                } else {
                    scaledBitmap = rotateImage(scaleDown(bbicon, 450 , true),90);
                }

                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);

                InputStream in = new ByteArrayInputStream(bos.toByteArray());
            }
            InputStream in = new ByteArrayInputStream(bos.toByteArray());
            // InputStream in = new ByteArrayInputStream(bos.toByteArray());
            //If you are stuck with HTTPClient 4.0, use InputStreamBody instead:
            //ContentBody foto = new InputStreamBody(in, "image/jpeg", file.getName());
            //ContentBody foto = new FileBody(file, "image/jpeg");
            //Use a ByteArrayBody instead (available since HTTPClient 4.1), despite its name it takes a file name, too:
            ContentBody foto = new ByteArrayBody(bos.toByteArray(), file.getName());
            HttpClient httpclient = new DefaultHttpClient();
            httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            HttpPost httppost = new HttpPost(url_upload_image);
            //MultipartEntity mpEntity = new MultipartEntity();
            AndroidMultiPartEntity mpEntity = new AndroidMultiPartEntity(new AndroidMultiPartEntity.ProgressListener() {
                @Override
                public void transferred(long num) {
                    //notification.contentView.setProgressBar(R.id.progressBar1, 100,(int) ((num / (float) totalSize) * 100), true);
                    // notificationManager.notify(1, notification);
                }
            });

            totalSize =  mpEntity.getContentLength();
            mpEntity.addPart("fotoUp", foto);
            httppost.setEntity(mpEntity);

            HttpResponse response;
            response = httpClient.execute(httppost);
            HttpEntity r_entity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();

//            if (statusCode == HttpStatus.SC_OK) {
//
//                Log.i("FOO", "All done");
//
//            } else {
//                Log.i("FOO", "Screw up with http - " + resp.getStatusLine().getStatusCode());
//            }

            if (statusCode == 200) {
                // Server response
                responseString = EntityUtils.toString(r_entity);
            } else {
                responseString = "Error occurred! Http Status Code: "
                        + statusCode;
            }

            //response.getEntity().consumeContent();
            return true;
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            responseString = e.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            responseString = e.toString();
        }
        return false;
    }

    /* Photo album for this application */
    private String getAlbumName() {
        return GlobalConstant.albunName ;
    }

    private String getAlbunNameTemp(){
        return  getString(R.string.album_name_temp);
    }


    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }


    private File getAlbumDirTemp() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbunNameTemp());

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d(getAlbunNameTemp(), "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }



//    private boolean onInsert(String imag_name){
//        HttpClient httpclient;
//        List<NameValuePair> nameValuePairs;
//        HttpPost httppost;
//        httpclient=new DefaultHttpClient();
//        httppost= new HttpPost(url_insert_image); // Url del Servidor
//        //Añadimos nuestros datos
//        nameValuePairs = new ArrayList<NameValuePair>(1);
//
//
//        nameValuePairs.add(new BasicNameValuePair("archivo",imag_name));
//        nameValuePairs.add(new BasicNameValuePair("'store_id",store_id));
//        nameValuePairs.add(new BasicNameValuePair("publicities_id",publicities_id));
//        nameValuePairs.add(new BasicNameValuePair("invoices_id",invoices_id));
//        nameValuePairs.add(new BasicNameValuePair("tipo",tipo));
//
//
//
//        try {
//            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//            httpclient.execute(httppost);
//            return true;
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    private boolean onInsert(String imag_name) {
        int success;
        try {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("archivo", String.valueOf(imag_name)));
            params.add(new BasicNameValuePair("store_id", String.valueOf(store_id)));
            params.add(new BasicNameValuePair("poll_id", String.valueOf(poll_id)));
            params.add(new BasicNameValuePair("publicities_id", String.valueOf(publicities_id)));
            params.add(new BasicNameValuePair("product_id", String.valueOf(product_id)));
            params.add(new BasicNameValuePair("company_id", String.valueOf(company_id)));
            params.add(new BasicNameValuePair("sod_ventana_id", String.valueOf(sod_ventana_id)));
            params.add(new BasicNameValuePair("sod_ventana_id", String.valueOf(sod_ventana_id)));
            params.add(new BasicNameValuePair("tipo", String.valueOf(tipo)));


            JSONParser jsonParser = new JSONParser();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(url_insert_image,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            success = json.getInt("success");
            if (success == 1) {
                return  true;

            }else{
                // Log.d(LOG_TAG, json.getString("message"));
                // return json.getString("message");
                return  false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  false;
    }

    /**
     * Method to show alert dialog
     * */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}

