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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dataservicios.com.ttauditalicorp.DetallePdv;
import dataservicios.com.ttauditalicorp.Model.Dex;
import dataservicios.com.ttauditalicorp.R;
import dataservicios.com.ttauditalicorp.util.GlobalConstant;
import dataservicios.com.ttauditalicorp.util.JSONParser;
import dataservicios.com.ttauditalicorp.util.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
/**
 * Created by Jaime on 19/03/2016.
 */
public class TipoDex extends Activity {
    private static final String LOG_TAG = TipoDex.class.getSimpleName();
    private Activity MyActivity= this;

    private TextView tvPDVSdelDía;

    private ProgressDialog pDialog;

    private String fechaRuta;
    private Button btGuardar;
    private Spinner spTipoDex;

    private JSONObject params;
    private SessionManager session;
    private Integer user_id, company_id,store_id,rout_id,audit_id, product_id, poll_id;
    List<Dex> listDex;
    String codigo_dex ,region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.tipo_dex);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Tipo Dex");


        pDialog = new ProgressDialog(MyActivity);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);

        btGuardar = (Button) findViewById(R.id.btGuardar);
        spTipoDex = (Spinner) findViewById(R.id.spTipoDex);


//        Bundle argRuta = new Bundle();
//        argRuta.clear();
//        argRuta.putInt("idPDV", store_id);
//        argRuta.putString("fechaRuta", fechaRuta);
//        argRuta.putInt("idAuditoria", audit_id);
//        argRuta.putInt("rout_id", rout_id);





        Bundle bundle = getIntent().getExtras();
        store_id= bundle.getInt("idPDV");
        rout_id= bundle.getInt("rout_id");
        fechaRuta= bundle.getString("fechaRuta");
        audit_id= bundle.getInt("idAuditoria");
        region= bundle.getString("region");


        company_id= GlobalConstant.company_id;
        poll_id = 386;
        product_id=0;

        listDex  = new ArrayList<Dex>();
        //*********************************CAMBIAR SIEMPRE LOS CÓDIGOS
        listDex.add(new Dex(1101, "DISTRIBUIDORA JC SAC",                                "386a",     "Arequipa"));
        listDex.add(new Dex(1102, "DISTRIBUIDORA MADEX SAC",                             "386b",     "Arequipa"));
        listDex.add(new Dex(1103, "DISTRIBUIDORA DE PRODUCTOS DE CONSUMO MASIVO SAC",    "386c",     "CHICLAYO"));
        listDex.add(new Dex(1104, "DISTRIBUIDORA VITALE DEX S.A.C.",                     "386d",     "CHIMBOTE"));
        listDex.add(new Dex(1105, "DEXSUR S.A.C. - CUZCO",                               "386e",     "CUZCO"));
        listDex.add(new Dex(1106, "DISTRIBUIDORA D.R.MARRACHE S.A.C.",                   "386f",     "HUANCAYO"));
        listDex.add(new Dex(1107, "DISTRIBUIDORA NUGENT SA",                             "386g",     "Lima"));
        listDex.add(new Dex(1108, "D L F MEDINA RIVERA SA",                              "386h",     "Lima"));
        listDex.add(new Dex(1109, "DISTRIBUIDORA COBERDEX SAC",                          "386i",     "Lima"));
        listDex.add(new Dex(1110, "DISTRIBUIDORA CUNZA S.A.",                            "386j",     "Lima"));
        listDex.add(new Dex(1111, "FUERZADEX SAC",                                       "386k",     "Lima"));
        listDex.add(new Dex(1112, "ATIPANA DEX S.A.C.",                                  "386l",     "Lima"));
        listDex.add(new Dex(1113, "DISTRIBUIDORA DIFARO S.A.C",                          "386m",     "PIURA"));
        listDex.add(new Dex(1114, "DISTRIBUIDORA OTOYA S.A.C.",                          "386n",     "PIURA"));
        listDex.add(new Dex(1115, "ROSET DISTRIBUCIONES E.I.R.L",                        "386o",     "TACNA"));
        listDex.add(new Dex(1116, "DIST. ALIMENTOS DEL VALLE S.A.C. - TRUJILLO",         "386p",     "TRUJILLO"));

        List<String> lables = new ArrayList<String>();
        //lables.add(tipoPedidoList.get(0).getTipo());
        for (int i = 0; i < listDex.size(); i++) {
            String strRegion = region.toUpperCase().trim();
            String strLisDexRegion = listDex.get(i).getRegion().toUpperCase().trim();

            if(strRegion.equals(strLisDexRegion)){
                lables.add(listDex.get(i).getFullname());
            }
//            if(region.equals(listDex.get(i).getRegion())){
//                lables.add(listDex.get(i).getFullname());
//            }

        }

        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lables);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoDex.setAdapter(null);
        spTipoDex.setAdapter(adaptador);

        spTipoDex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                codigo_dex = listDex.get(position).getCodigo();
                String label = parent.getItemAtPosition(position).toString();
                //Toast.makeText(MyActivity, label + " ID: " + String.valueOf(codigo_dex), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity);
                builder.setTitle("Guardar Encuesta");
                builder.setMessage("Está seguro de guardar la encuestas: ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


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
            InsertAuditProductPoll();
            return true;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted

            if (result){
                // loadLoginActivity();
                    Bundle argRuta = new Bundle();
                    argRuta.clear();
                    argRuta.putInt("idPDV", store_id);
                    argRuta.putString("fechaRuta", fechaRuta);
                    argRuta.putInt("idAuditoria", audit_id);
                    argRuta.putInt("idRuta", rout_id);
                    Intent intent;
                    //intent = new Intent(MyActivity, Product.class);
                    intent = new Intent(MyActivity, DetallePdv.class);
                    intent.putExtras(argRuta);
                    startActivity(intent);
                    finish();
                   hidepDialog();

            }
        }
    }



    private void InsertAuditProductPoll() {
        int success;
        try {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("poll_id",String.valueOf(poll_id)));
            params.add(new BasicNameValuePair("store_id", String.valueOf(store_id)));
            params.add(new BasicNameValuePair("media", "0"));
            params.add(new BasicNameValuePair("coment", "0"));
            params.add(new BasicNameValuePair("options", "1"));
            params.add(new BasicNameValuePair("opcion", codigo_dex));
            params.add(new BasicNameValuePair("sino", "0"));
            params.add(new BasicNameValuePair("comentario", ""));
            params.add(new BasicNameValuePair("result", "0"));
            params.add(new BasicNameValuePair("company_id", String.valueOf(GlobalConstant.company_id)));
            params.add(new BasicNameValuePair("idroute", String.valueOf(rout_id)));
            params.add(new BasicNameValuePair("idaudit", String.valueOf(audit_id)));
            params.add(new BasicNameValuePair("status", "1"));

            params.add(new BasicNameValuePair("limits", "0"));

            params.add(new BasicNameValuePair("coment_options", "0"));
            params.add(new BasicNameValuePair("comentario_options", ""));

            params.add(new BasicNameValuePair("limite", ""));



            JSONParser jsonParser = new JSONParser();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/JsonInsertPollsAlicorp" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            success = json.getInt("success");
            if (success == 1) {
                Log.d(LOG_TAG, json.getString("Ingresado correctamente"));
            }else{
                Log.d(LOG_TAG, json.getString("message"));
                // return json.getString("message");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
