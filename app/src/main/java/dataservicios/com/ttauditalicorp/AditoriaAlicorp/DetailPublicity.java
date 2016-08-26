package dataservicios.com.ttauditalicorp.AditoriaAlicorp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import dataservicios.com.ttauditalicorp.AndroidCustomGalleryActivity;
import dataservicios.com.ttauditalicorp.DetallePdv;
import dataservicios.com.ttauditalicorp.Model.PresencePublicity;
import dataservicios.com.ttauditalicorp.Model.Publicity;
import dataservicios.com.ttauditalicorp.R;
import dataservicios.com.ttauditalicorp.SQLite.DatabaseHelper;
import dataservicios.com.ttauditalicorp.Ubicacion;
import dataservicios.com.ttauditalicorp.app.AppController;
import dataservicios.com.ttauditalicorp.util.GlobalConstant;
import dataservicios.com.ttauditalicorp.util.JSONParserX;
import dataservicios.com.ttauditalicorp.util.SessionManager;

/**
 * Created by Jaime Eduardo on 07/10/2015.
 */
public class DetailPublicity extends Activity {
    private static final String LOG_TAG = DetailPublicity.class.getSimpleName();

    private ProgressDialog pDialog;
    private Activity MyActivity = this ;
    private SessionManager session;
    private Integer store_id, rout_id, publicity_id , audit_id, user_id ;
    private String fechaRuta , comentario;
    private Switch sw_visible, sw_layout, sw_contaminado;
    private Button bt_guardar, bt_photo;
    private EditText et_comentario;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private DatabaseHelper db ;
    int  is_visible,   is_found, is_contaminated; //is_layout
    Publicity publicity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_publicity);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Presencia de material publicitario");

        bt_guardar = (Button) findViewById(R.id.btGuardar);
        bt_photo = (Button) findViewById(R.id.btPhoto);
        et_comentario = (EditText) findViewById(R.id.etComentario);
        sw_visible = (Switch) findViewById(R.id.swVisible);
       //sw_layout = (Switch) findViewById(R.id.swLayoutCorrecto);
        sw_contaminado = (Switch) findViewById(R.id.swContaminado);

        Bundle bundle = getIntent().getExtras();
        store_id = bundle.getInt("store_id");
        rout_id =  bundle.getInt("rout_id");
        publicity_id =  bundle.getInt("publicity_id");
        audit_id = bundle.getInt("audit_id");
        fechaRuta = bundle.getString("fechaRuta");

        is_visible = 0;
        //is_layout = 0;
        is_found = 1;

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);

        db = new DatabaseHelper(getApplicationContext());

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        // id
        user_id = Integer.valueOf(user.get(SessionManager.KEY_ID_USER)) ;

        if (imageLoader == null)  imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) findViewById(R.id.thumbnail);


        publicity = new Publicity();
        publicity = db.getPublicity(publicity_id);

        if (publicity.getCategory_id() == 42){

            sw_layout.setEnabled(false);
            sw_layout.setVisibility(View.INVISIBLE);
        }

        thumbNail.setImageUrl(GlobalConstant.dominio + "/media/images/alicorp/publicities/" + publicity.getImage(), imageLoader);

//        sw_layout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    is_layout = 1;
//                } else {
//                    is_layout = 0;
//                }
//            }
//        });

        sw_visible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    is_visible = 1;
                } else {
                    is_visible = 0;
                }
            }
        });

        sw_contaminado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    is_contaminated = 1;
                } else {
                    is_contaminated = 0;
                }
            }
        });

        bt_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        bt_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity);
                builder.setTitle("Guardar Encuesta");
                builder.setMessage("Está seguro de guardar todas las encuestas: ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        comentario =  String.valueOf(et_comentario.getText()) ;

                       new loadPoll().execute();

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
                builder.setCancelable(false);
            }
        });
    }

    private void takePhoto() {

        Intent i = new Intent( MyActivity, AndroidCustomGalleryActivity.class);
        Bundle bolsa = new Bundle();



        bolsa.putString("store_id",String.valueOf(store_id));
        bolsa.putString("product_id",String.valueOf("0"));
        bolsa.putString("publicities_id",String.valueOf(publicity_id));
        bolsa.putString("poll_id",String.valueOf("0"));
        bolsa.putString("sod_ventana_id","0");
        bolsa.putString("company_id",String.valueOf(GlobalConstant.company_id));
        bolsa.putString("url_insert_image", GlobalConstant.dominio + "/insertImagesPublicitiesAlicorp");
        bolsa.putString("tipo", "1");


        i.putExtras(bolsa);
        startActivity(i);

    }

    class loadPoll extends AsyncTask<Void, Integer , Boolean> {
        /**
         * Antes de comenzar en el hilo determinado, Mostrar progresión
         * */
        boolean failure = false;
        @Override
        protected void onPreExecute() {
            //tvCargando.setText("Cargando Product...");
            pDialog.show();
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub


            if(!InsertAuditPollsProduct(store_id,audit_id,publicity_id,0,is_found,is_visible,is_contaminated,0,comentario)) return false ;



            return true;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted

            if (result){
                // loadLoginActivity();

                PresencePublicity presenceP = new PresencePublicity();
                presenceP.setCategory_id(publicity.getCategory_id());
                presenceP.setFound(is_found);
                //presenceP.setLayout_correcto(is_layout);
                presenceP.setVisible(is_visible);
                presenceP.setContaminated(is_contaminated);
                presenceP.setComment(String.valueOf(et_comentario.getText()) );
                presenceP.setPublicity_id(publicity_id);
                presenceP.setStore_id(store_id);
                db.createPresensePublicity(presenceP);
                publicity.setActive(0);
                db.updatePublicity(publicity);

                String mensaje ="";
                Toast toast = Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT);
                toast.show();

                finish();


            } else {
                Toast.makeText(MyActivity , "No se pudo guardar la información intentelo nuevamente",Toast.LENGTH_LONG).show();
            }
            hidepDialog();
        }
    }


    private Boolean InsertAuditPollsProduct(int store_id,int audit_id, int publicity_id, int category_id, int found , int visible, int contaminated, int status , String comentario ) {
        int success;
        try {


            HashMap<String, String> params = new HashMap<>();




            params.put("store_id", String.valueOf(store_id));
            params.put("audit_id", String.valueOf(audit_id));
            params.put("publicity_id", String.valueOf(publicity_id));
            params.put("category_id", String.valueOf(category_id));
            params.put("found", String.valueOf(found));
            params.put("visible", String.valueOf(visible));
            params.put("contaminated", String.valueOf(contaminated));
            params.put("status", String.valueOf(status));
            params.put("comment", String.valueOf(comentario));
            params.put("company_id", String.valueOf(GlobalConstant.company_id));
            params.put("rout_id", String.valueOf(rout_id));
            params.put("user_id", String.valueOf(user_id));




            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            //JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/json/prueba.json" ,"POST", params);
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/saveExhibidorBodegaAlicorp" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");
                return false;
            } else{
                success = json.getInt("success");
                if (success == 1) {
                    // Log.d(LOG_TAG, json.getString("Ingresado correctamente"));
                    Log.d(LOG_TAG, "Ingresado correctamente");

                }else{
                    //  Log.d(LOG_TAG, json.getString("message"));
                    // return json.getString("message");
                    Log.d(LOG_TAG, "Error al ingresar registro");
                    // return false;
                }
            }

        } catch (Exception e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
            e.printStackTrace();
            return  false;
        }

        return  true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
//                this.finish();
//                Intent a = new Intent(this,PanelAdmin.class);
//                //a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(a);
//                overridePendingTransition(R.anim.anim_slide_in_right,R.anim.anim_slide_out_right);
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        //return super.onOptionsItemSelected(item);
    }


//    public void onBackPressed() {
//        super.onBackPressed();
//        this.finish();
//
//        //a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////        Bundle argPDV = new Bundle();
////        argPDV.putInt("pdv_id", pdv_id );
////        argPDV.putInt("idRuta", idRuta );
////        argPDV.putString("fechaRuta",fechaRuta);
////        Intent a = new Intent(this,DetallePdv.class);
////        a.putExtras(argPDV);
////
////        startActivity(a);
//        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
//    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            //Toast.makeText(MyActivity, "No se puede volver atras, los datos ya fueron guardado, para modificar pongase en contácto con el administrador", Toast.LENGTH_LONG).show();
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onBackPressed() {
        Toast.makeText(MyActivity, "No se puede volver atras, los datos ya fueron guardado, para modificar póngase en contácto con el administrador", Toast.LENGTH_LONG).show();
//        super.onBackPressed();
//        this.finish();
//
//        overridePendingTransition(R.anim.anim_slide_in_right,R.anim.anim_slide_out_right);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
