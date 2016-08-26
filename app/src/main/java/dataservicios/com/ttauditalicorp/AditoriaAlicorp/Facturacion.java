package dataservicios.com.ttauditalicorp.AditoriaAlicorp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import dataservicios.com.ttauditalicorp.AndroidCustomGalleryActivity;
import dataservicios.com.ttauditalicorp.Model.Audit;
import dataservicios.com.ttauditalicorp.R;
import dataservicios.com.ttauditalicorp.SQLite.DatabaseHelper;
import dataservicios.com.ttauditalicorp.app.AppController;
import dataservicios.com.ttauditalicorp.util.GlobalConstant;
import dataservicios.com.ttauditalicorp.util.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jaime Eduardo on 09/10/2015.
 */
public class Facturacion extends Activity {
    private Activity MyActivity= this;
    private EditText etCodigo;
    private TextView tvResultado ;
    private Button bt_guardar, bt_photo;
    private SessionManager session;
    private DatabaseHelper db;
    private ProgressDialog pDialog;

    private int pdv_id, rout_id, compay_id,auditory_id,user_id;
    private int invoice_id ;
    private double monto ;
    private int  score = 0  ;
    private String fechaRuta ;

    private RadioGroup rg_Tipo;
    private RadioButton rb_A,rb_B,rb_C, rb_D;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facturacion);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Volumen Facturación");

        bt_guardar = (Button) findViewById(R.id.btGuardar);
        bt_photo = (Button) findViewById(R.id.btPhoto);
        rg_Tipo=(RadioGroup) findViewById(R.id.rgTipo);
        rb_A=(RadioButton) findViewById(R.id.rbA);
        rb_B=(RadioButton) findViewById(R.id.rbB);
        rb_C = (RadioButton) findViewById(R.id.rbC);
        rb_D = (RadioButton) findViewById(R.id.rbD);



        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);

        Bundle bundle = getIntent().getExtras();
        compay_id =  bundle.getInt("company_id");
        pdv_id = bundle.getInt("idPDV");
        rout_id = bundle.getInt("idRuta");
        fechaRuta = bundle.getString("fechaRuta");
        auditory_id = bundle.getInt("idAuditoria");

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        // id
        user_id = Integer.valueOf(user.get(SessionManager.KEY_ID_USER)) ;
        db = new DatabaseHelper(getApplicationContext());
        bt_photo.setEnabled(false);
        bt_photo.setVisibility(View.INVISIBLE);

        bt_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        rg_Tipo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                bt_photo.setEnabled(true);
                bt_photo.setVisibility(View.VISIBLE);
                switch (checkedId) {
                    case R.id.rbA:
                        invoice_id= 4;
                        break;
                    case R.id.rbB:
                        invoice_id= 1;
                        break;
                    case R.id.rbC:
                        invoice_id = 2 ;
                        break;
                    case R.id.rbD:
                        invoice_id = 3 ;
                        break;
                }
            }
        });


        bt_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity);
                builder.setTitle("Guardar Encuesta");
                builder.setMessage("Está seguro de guardar la lista de publicidades: ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {


                        long id = rg_Tipo.getCheckedRadioButtonId();
                        if (id == -1){
                            //no item selected
                            //valor ="";
                            Toast toast;
                            toast = Toast.makeText(MyActivity, "Debe seleccionar una opción", Toast.LENGTH_LONG);
                            toast.show();
                            return;
                        }
                        else{
                            if (id == rb_A.getId()){
                                //Do something with the button
                                monto = 0 ;
                                score = 0;
                            } else if (id == rb_B.getId()){
                                //Do something with the button
                                monto = 1000.00 ;
                                score = 100;
                            } else if(id == rb_C.getId()){
                                monto = 2000.00 ;
                                score = 150;
                            } else if(id == rb_D.getId()){
                                monto = 4000.00 ;
                                score = 200;
                            }

                        }

                        Audit audit =new Audit();

                        db.updateAuditScore(auditory_id,score, pdv_id);
                        List<Audit> audits1 = new ArrayList<Audit>();
                        audits1=db.getAllAudits();

                        JSONObject paramsData;
                        paramsData = new JSONObject();
                        try {
                            paramsData.put("store_id", pdv_id);
                            paramsData.put("audit_", auditory_id);
                            paramsData.put("company_id", compay_id);
                            paramsData.put("invoices_id",invoice_id);
                            paramsData.put("rout_id", rout_id);
                            paramsData.put("user_id",user_id);
                            paramsData.put("monto",monto);
                            paramsData.put("status", "1");
                            paramsData.put("score", String.valueOf(score));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        insertAudit(paramsData);
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


    private void insertAudit(JSONObject paramsData) {
        showpDialog();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST , GlobalConstant.dominio + "/saveAuditFacturacion" ,paramsData,
        //JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST , "http://www.dataservicios.com/webservice/success.php" ,paramsData,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d("DATAAAA", response.toString());
                        //adapter.notifyDataSetChanged();
                        try {
                            //String agente = response.getString("agentes");
                            int success =  response.getInt("success");
                            //compay_id =response.getInt("company");
                            if (success == 1) {

                                Toast toast;
                                toast = Toast.makeText(MyActivity, "Ha ganado " + String.valueOf(score), Toast.LENGTH_LONG);
                                toast.show();

                                finish();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        hidepDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //VolleyLog.d(TAG, "Error: " + error.getMessage());
                        hidepDialog();
                    }
                }
        );

        AppController.getInstance().addToRequestQueue(jsObjRequest);
    }


    private void takePhoto() {
        /*Intent intent = new Intent((MediaStore.ACTION_IMAGE_CAPTURE));
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());

        startActivityForResult(intent, 100);
        */

        //Bundle bundle = getIntent().getExtras();
        //String id_agente = bundle.getString(TAG_ID);

        // getting values from selected ListItem
        // String aid = id_agente;
        // Starting new intent
        Intent i = new Intent( MyActivity, AndroidCustomGalleryActivity.class);
        Bundle bolsa = new Bundle();



        bolsa.putString("store_id", String.valueOf(pdv_id));
        bolsa.putString("publicities_id","0");
        bolsa.putString("invoices_id", String.valueOf(invoice_id));
        bolsa.putString("url_insert_image",GlobalConstant.dominio + "/insertImagesInvoices");
        bolsa.putString("tipo","1");

        i.putExtras(bolsa);
        startActivity(i);


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


    public void onBackPressed() {
        super.onBackPressed();
        this.finish();

        //a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        Bundle argPDV = new Bundle();
//        argPDV.putInt("pdv_id", pdv_id );
//        argPDV.putInt("idRuta", idRuta );
//        argPDV.putString("fechaRuta",fechaRuta);
//        Intent a = new Intent(this,DetallePdv.class);
//        a.putExtras(argPDV);
//
//        startActivity(a);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }

    @Override
    public void onResume() {
        super.onResume();


    }

//    @Override
//    public void onRestart() {
//        super.onRestart();
//        //When BACK BUTTON is pressed, the activity on the stack is restarted
//        //Do what you want on the refresh procedure here
//        finish();
//        startActivity(getIntent());
//    }



    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
