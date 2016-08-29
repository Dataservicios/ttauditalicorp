package dataservicios.com.ttauditalicorp.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Date;

import dataservicios.com.ttauditalicorp.Model.Media;

/**
 * Created by Jaime on 28/08/2016.
 */
public class AuditAlicorp {
    public static final String LOG_TAG = AuditAlicorp.class.getSimpleName();





    public boolean uploadMediaPublicity(Media media){


        final String url_upload_image = GlobalConstant.dominio + "/insertImagesPublicitiesAlicorp";

        String imag = media.getFile();
        int company_id = media.getCompany_id();
        int poll_id = media.getPoll_id();
        int product_id = media.getProduct_id();
        int publicity_id = media.getPublicity_id();
        int store_id= media.getStore_id();
        int type = media.getType();
        Date created_at = media.getCreated_at();

        long totalSize = 0;

        File file = new File(imag);
        Bitmap bbicon;
        HttpResponse resp;
        HttpClient httpClient = new DefaultHttpClient();
        Log.i("FOO", "Notification started");
        bbicon= BitmapFactory.decodeFile(String.valueOf(file));
        Bitmap scaledBitmap;
        if(Build.MODEL.equals("MotoG3")){
            scaledBitmap = rotateImage(scaleDown(bbicon, 400 , true),0);
        } else {
            scaledBitmap = rotateImage(scaleDown(bbicon, 400 , true),90);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);

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




        httppost.setEntity(mpEntity);
        try {

            totalSize =  mpEntity.getContentLength();
            mpEntity.addPart("fotoUp", foto);
            mpEntity.addPart("archivo", new StringBody(String.valueOf(file.getName())));
            mpEntity.addPart("store_id", new StringBody(String.valueOf(store_id)));
            mpEntity.addPart("product_id", new StringBody(String.valueOf(product_id)));
            mpEntity.addPart("poll_id", new StringBody(String.valueOf(poll_id)));
            mpEntity.addPart("publicities_id", new StringBody(String.valueOf(publicity_id)));
            mpEntity.addPart("company_id", new StringBody(String.valueOf(company_id)));
            mpEntity.addPart("tipo", new StringBody(String.valueOf(type)));

            Log.i("FOO", "About to call httpClient.execute");
            resp = httpClient.execute(httppost);
            // resp.getEntity().getContent();
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

            } else {

                Log.i("FOO", "Screw up with http - " + resp.getStatusLine().getStatusCode());
                return  false ;
            }
            //resp.getEntity().consumeContent();
            //return true;
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return  false;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return  false ;
        }
        return true;
    }


    /**
     * method sacle image
     * @param realImage
     * @param maxImageSize
     * @param filter
     * @return
     */
    private static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    /**
     * method rotate image
     * @param img
     * @param degree
     * @return
     */
    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

}
