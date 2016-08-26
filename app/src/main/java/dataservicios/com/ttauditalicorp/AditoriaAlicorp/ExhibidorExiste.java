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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dataservicios.com.ttauditalicorp.AndroidCustomGalleryActivity;
import dataservicios.com.ttauditalicorp.DetallePdv;
import dataservicios.com.ttauditalicorp.MainActivity;
import dataservicios.com.ttauditalicorp.Model.Publicity;
import dataservicios.com.ttauditalicorp.R;
import dataservicios.com.ttauditalicorp.SQLite.DatabaseHelper;
import dataservicios.com.ttauditalicorp.Ubicacion;
import dataservicios.com.ttauditalicorp.util.GlobalConstant;
import dataservicios.com.ttauditalicorp.util.JSONParser;
import dataservicios.com.ttauditalicorp.util.JSONParserX;
import dataservicios.com.ttauditalicorp.util.SessionManager;

/**
 * Created by Jaime on 16/08/2016.
 */
public class ExhibidorExiste extends Activity {

    private Activity MyActivity = this ;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private SessionManager session;

    private Switch swExhibidorExiste ;
    private Button  bt_guardar;
    private EditText et_Comentario,etComent2;
    private TextView tv_Pregunta;
    private LinearLayout lyOpciones;

    private String tipo,cadenaruc, fechaRuta, comentario="", type, region;

    private Integer user_id,store_id,rout_id,audit_id, publicity_id, poll_id, company_id ;

    int  is_exhibidor_existe=0 ;


    private DatabaseHelper db;

    private ProgressDialog pDialog;


    private RadioGroup rgOpt2;
    private  String opt2="";

    private RadioButton[] radioButton2Array;


    Publicity publicity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.exhibidor_existe);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Existe Exhibidor");

        swExhibidorExiste = (Switch) findViewById(R.id.swExhibidorExiste);

        lyOpciones = (LinearLayout) findViewById(R.id.lyOpciones);

        rgOpt2=(RadioGroup) findViewById(R.id.rgOpt2);

        radioButton2Array = new RadioButton[] {
                (RadioButton) findViewById(R.id.rbA2),
                (RadioButton) findViewById(R.id.rbB2),
                (RadioButton) findViewById(R.id.rbC2),
                (RadioButton) findViewById(R.id.rbD2),
                (RadioButton) findViewById(R.id.rbE2),
        };

       // tv_Pregunta = (TextView) findViewById(R.id.tvPregunta);
        bt_guardar = (Button) findViewById(R.id.btGuardar);

        //et_Comentario = (EditText) findViewById(R.id.etComentario);
        etComent2 = (EditText) findViewById(R.id.etComent2);

        Bundle bundle = getIntent().getExtras();


        company_id = GlobalConstant.company_id;
        store_id = bundle.getInt("store_id");
        rout_id = bundle.getInt("rout_id");
        publicity_id = bundle.getInt("publicity_id");
        audit_id = bundle.getInt("audit_id");
        fechaRuta = bundle.getString("fechaRuta");





        poll_id = 495;


        db = new DatabaseHelper(getApplicationContext());
        publicity = new Publicity();
        publicity = db.getPublicity(publicity_id);


        //poll_id = 72 , solo para exhibiciones de bayer, directo de la base de datos

        pDialog = new ProgressDialog(MyActivity);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        // id
        user_id = Integer.valueOf(user.get(SessionManager.KEY_ID_USER)) ;



        rgOpt2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(radioButton2Array[4].isChecked())
                {
                    etComent2.setEnabled(true);
                    etComent2.setVisibility(View.VISIBLE);
                    etComent2.setText("");

                }
                else
                {
                    etComent2.setEnabled(false);
                    etComent2.setVisibility(View.INVISIBLE);
                    etComent2.setText("");

                }
            }
        });


        swExhibidorExiste.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    is_exhibidor_existe = 1;
                    lyOpciones.setEnabled(false);
                    lyOpciones.setVisibility(View.INVISIBLE);
                    rgOpt2.clearCheck();

                } else {
                    is_exhibidor_existe = 0;
                    lyOpciones.setEnabled(true);
                    lyOpciones.setVisibility(View.VISIBLE);

                }
            }
        });




        bt_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!swExhibidorExiste.isChecked()){

                    long id2 = rgOpt2.getCheckedRadioButtonId();
                    if (id2 == -1){
                        Toast.makeText(MyActivity,"Selecione una opción" , Toast.LENGTH_LONG).show();
                        return;
                    }
                    else{
                        for (int x = 0; x < radioButton2Array.length; x++) {
                            if(id2 ==  radioButton2Array[x].getId())  opt2 = poll_id.toString() + radioButton2Array[x].getTag();
                        }

                    }

                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity);
                builder.setTitle("Guardar Encuesta");
                builder.setMessage("Está seguro de guardar todas las encuestas: ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        comentario = String.valueOf(etComent2.getText()) ;
                        new loadPoll().execute();
                        dialog.dismiss();
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

            if(is_exhibidor_existe==1) {
                if(!InsertAuditPollsProduct(store_id, publicity_id,poll_id,0 ,is_exhibidor_existe,"","", "")) return  false;
            } else if(is_exhibidor_existe==0){
                if(!InsertAuditPollsProduct(store_id, publicity_id,poll_id,0 ,is_exhibidor_existe,opt2,"", comentario)) return  false;
            }

            return true;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted

            if (result){
                // loadLoginActivity();
                if(is_exhibidor_existe==1) {
                    Bundle argPDV = new Bundle();
                    argPDV.putInt("store_id", Integer.valueOf(store_id));
                    argPDV.putInt("rout_id", Integer.valueOf(rout_id));
                    argPDV.putInt("publicity_id", Integer.valueOf(publicity_id));
                    argPDV.putString("fechaRuta", fechaRuta);
                    argPDV.putInt("audit_id", audit_id);
                    //Intent intent = new Intent("dataservicios.com.ttauditalicorp.DETAIPUBLICITY");
                    Intent intent = new Intent(MyActivity,DetailPublicity.class);
                    intent.putExtras(argPDV);
                    startActivity(intent);

                    finish();
                } else if(is_exhibidor_existe==0){

                    publicity.setActive(0);
                    db.updatePublicity(publicity);
                    finish();
                }



            } else {
                Toast.makeText(MyActivity , "No se pudo guardar la información intentelo nuevamente",Toast.LENGTH_LONG).show();
            }
            hidepDialog();
        }
    }



    private Boolean InsertAuditPollsProduct(int store_id, int publicity_id, int poll_id, int status , int result,String options,String comentario , String comentario_options) {
        int success;
        try {

            HashMap<String, String> params = new HashMap<>();
            params.put("poll_id",String.valueOf(poll_id));
            params.put("store_id", String.valueOf(store_id));

            params.put("media", "0");
            params.put("coment", "0");
            params.put("options", "1");
            params.put("opcion", options);
            params.put("sino", "1");
            params.put("comentario", String.valueOf(comentario));
            params.put("result", String.valueOf(result));
            params.put("company_id", String.valueOf(GlobalConstant.company_id));
            params.put("idroute", String.valueOf(rout_id));
            params.put("idaudit", String.valueOf(audit_id));
            params.put("user_id", String.valueOf(user_id));

            params.put("publicity_id", String.valueOf(publicity_id));
            params.put("coment_options", "1");
            params.put("comentario_options", comentario_options);


            params.put("status", String.valueOf(status));


            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            //JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/JsonInsertPollsAlicorp" ,"POST", params);
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/JsonInsertPollsAlicorp" ,"POST", params);
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
//        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
//    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

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

}
