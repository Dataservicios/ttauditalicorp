package dataservicios.com.ttauditalicorp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import dataservicios.com.ttauditalicorp.AditoriaAlicorp.StoreOpenClose;
import dataservicios.com.ttauditalicorp.Model.Pdv;
import dataservicios.com.ttauditalicorp.Model.Publicity;
import dataservicios.com.ttauditalicorp.Model.SODVentanas;
import dataservicios.com.ttauditalicorp.SQLite.DatabaseHelper;
import dataservicios.com.ttauditalicorp.adapter.PdvsAdapter;
import dataservicios.com.ttauditalicorp.app.AppController;
import dataservicios.com.ttauditalicorp.util.GPSTracker;
import dataservicios.com.ttauditalicorp.util.GlobalConstant;
import dataservicios.com.ttauditalicorp.util.JSONParser;
import dataservicios.com.ttauditalicorp.util.JSONParserX;
import dataservicios.com.ttauditalicorp.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * Created by usuario on 06/01/2015.
 */
public class PuntosVenta extends Activity {
    private static final String LOG_TAG = PuntosVenta.class.getSimpleName();
    private static final String TAG = PuntosVenta.class.getSimpleName();
    private Activity MyActivity = this ;

    EditText pdvs1,pdvsAuditados1,porcentajeAvance1;
    private TextView tvPDVSdelDía;

    // Movies json url

    private ProgressDialog pDialog;
    private List<Pdv> pdvList = new ArrayList<Pdv>();
    private ListView listView;
    private PdvsAdapter adapter;
    private int IdRuta ;
    private String fechaRuta;
    private Button bt_MapaRuta;
    private DatabaseHelper db;

   // Activity MyActivity ;
    private JSONObject params;
    private SessionManager session;
    private String email_user, id_user, name_user, region , typeBodega,type ,typeBodega_id;
    private int store_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puntos_venta);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("PDVs");
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);

        pdvs1 = (EditText) findViewById(R.id.etPDVS);
        pdvsAuditados1 = (EditText) findViewById(R.id.etPDVSAuditados);
        porcentajeAvance1 = (EditText) findViewById(R.id.etPorcentajeAvance);
        tvPDVSdelDía = (TextView) findViewById(R.id.tvPDVSdelDia);

        Bundle bundle = getIntent().getExtras();
        IdRuta= bundle.getInt("idRuta");
        fechaRuta = bundle.getString("fechaRuta");

        tvPDVSdelDía.setText(fechaRuta);


        session = new SessionManager(MyActivity);
        HashMap<String, String> user = session.getUserDetails();
        name_user = user.get(SessionManager.KEY_NAME);
        email_user = user.get(SessionManager.KEY_EMAIL);
        id_user = user.get(SessionManager.KEY_ID_USER);

        db = new DatabaseHelper(getApplicationContext());

        pDialog = new ProgressDialog(MyActivity);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);

        //Añadiendo parametros para pasar al Json por metodo POST
        params = new JSONObject();
        try {
            params.put("id", IdRuta);
            params.put("company_id", GlobalConstant.company_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        bt_MapaRuta = (Button) findViewById(R.id.btMapaRuta);

        bt_MapaRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle argRuta = new Bundle();
                argRuta.putInt("id", IdRuta);
                Intent intent = new Intent("dataservicios.com.ttauditalicorp.MAPARUTAS");
                intent.putExtras(argRuta);
                startActivity(intent);


            }
        });
        listView = (ListView) findViewById(R.id.list);
        adapter = new PdvsAdapter(this, pdvList);

        listView.setAdapter(adapter);
        // Click event for single list row
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Obteniendo fecha y hora
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String strDate = sdf.format(c.getTime());
                GlobalConstant.inicio = strDate;
                Log.i("FECHA", strDate);

                //Obteniendo Ubicacion
                GPSTracker gps = new GPSTracker(MyActivity);

                // Verificar si GPS esta habilitado
                if (gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    GlobalConstant.latitude_open = latitude;
                    GlobalConstant.longitude_open = longitude;
                    //Toast toast = Toast.makeText(getApplicationContext(), "Lat: " + String.valueOf(latitude) + "Long: " + String.valueOf(longitude), Toast.LENGTH_SHORT);
                    //toast.show();
                } else {
                    // Indicar al Usuario que Habilite su GPS
                    gps.showSettingsAlert();
                }

                // selected item
                String selected = ((TextView) view.findViewById(R.id.tvId)).getText().toString();
                store_id= Integer.valueOf(selected);
                type = ((TextView) view.findViewById(R.id.tvType)).getText().toString();
                region = ((TextView) view.findViewById(R.id.tvRegion)).getText().toString();
                typeBodega = ((TextView) view.findViewById(R.id.tvTypeBodega)).getText().toString();
                if (typeBodega.equals("Mini Market"))   typeBodega_id = "1";
                if (typeBodega.equals("Bodega Clásica"))   typeBodega_id = "2";
                if (typeBodega.equals("Bodega Alto Tráfico"))   typeBodega_id = "3";


                db.deleteAllPublicity();
                db.deleteAllSODVentanas();
                db.deleteAllPresensePublicity();

                new loadPoll().execute();

            }
        });
        listView.setAdapter(adapter);

        //pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
       // pDialog.setMessage("Loading...");
        pDialog.show();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST , GlobalConstant.dominio + "/JsonRoadsDetail" ,params,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
//
                        Log.d("DATAAAA", response.toString());
                        //adapter.notifyDataSetChanged();
                        try {
                            //String agente = response.getString("agentes");
                            int success =  response.getInt("success");
                            float contadorPDVS =0 ;
                            float auditadosPDV =0;
                            if (success == 1) {
//
                                JSONArray ObjJson;
                                ObjJson = response.getJSONArray("roadsDetail");
                                // looping through All Products
                                if(ObjJson.length() > 0) {

                                    contadorPDVS = contadorPDVS + Integer.valueOf(response.getString("pdvs"));
                                    auditadosPDV =  auditadosPDV + Integer.valueOf(response.getString("auditados"));

                                    for (int i = 0; i < ObjJson.length(); i++) {

                                        try {

                                            JSONObject obj = ObjJson.getJSONObject(i);
                                            Pdv pdv = new Pdv();
                                            pdv.setId(Integer.valueOf(obj.getString("id")));
                                            pdv.setPdv(obj.getString("fullname"));
                                            //pdv.setThumbnailUrl(obj.getString("image"));
                                            pdv.setDireccion(obj.getString("address"));
                                            pdv.setDistrito(obj.getString("district"));
                                            pdv.setType(obj.getString("type"));
                                            pdv.setRegion(obj.getString("region"));
                                            pdv.setTypeBodega(obj.getString("tipo_bodega"));
                                            pdv.setStatus(obj.getInt("status"));

//
                                            // adding movie to movies array
                                            pdvList.add(pdv);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }

                                    pdvs1.setText(String.valueOf(contadorPDVS)) ;
                                    pdvsAuditados1.setText(String.valueOf(auditadosPDV));

                                    float porcentajeAvance=(auditadosPDV / contadorPDVS) *100;
                                    BigDecimal big = new BigDecimal(porcentajeAvance);
                                    big = big.setScale(2, RoundingMode.HALF_UP);
                                    porcentajeAvance1.setText( String.valueOf(big) + " % ");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            hidePDialog();
                        }

                        adapter.notifyDataSetChanged();
                        hidePDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error: " + error.getMessage());
                        hidePDialog();
                    }
                }
        );


        AppController.getInstance().addToRequestQueue(jsObjRequest);
    }



    class loadPoll extends AsyncTask<Void, Integer , Boolean> {
        /**
         * Antes de comenzar en el hilo determinado, Mostrar progresión
         * */
        boolean failure = false;
        @Override
        protected void onPreExecute() {
            //tvCargando.setText("Cargando Product...");

            pDialog = new ProgressDialog(MyActivity);
            pDialog.setMessage("Cargando...");
            pDialog.setCancelable(false);
            pDialog.show();
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub
            //cargaTipoPedido();

           // readJsonProducts();
            if(!readJsonPublicity()) return  false;
            if(!readJsonSODVentanas()) return  false;

            return true;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted
            hidepDialog();
            if (result){
                // loadLoginActivity();
                Toast toast = Toast.makeText(getApplicationContext(), String.valueOf(store_id) , Toast.LENGTH_SHORT);
                toast.show();
                Bundle argPDV = new Bundle();
                argPDV.putInt("idPDV", Integer.valueOf(store_id) );
                argPDV.putInt("idRuta", Integer.valueOf(IdRuta));
                argPDV.putString("fechaRuta", fechaRuta);
                argPDV.putString("region",region);
                argPDV.putString("typeBodega",typeBodega);

                //Intent intent = new Intent("dataservicios.com.ttauditalicorp.DETALLEPDV");
                //Intent intent = new Intent(MyActivity, TipoDex.class);
                Intent intent = new Intent(MyActivity, StoreOpenClose.class);
                intent.putExtras(argPDV);
                startActivity(intent);


            }
        }
    }




    private boolean readJsonPublicity() {
        int success;
        try {

            HashMap<String, String> params = new HashMap<>();

            params.put("company_id", String.valueOf(GlobalConstant.company_id));
            params.put("tipo_bodega", String.valueOf(typeBodega_id));


            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/JsonListPublicitiesAlicorp" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            //
            if (json == null) {
                return false ;
            } else {

                success = json.getInt("success");
                if (success == 1) {
                    JSONArray ObjJson;
                    ObjJson = json.getJSONArray("publicities");

                    if(ObjJson.length() > 0) {
                        for (int i = 0; i < ObjJson.length(); i++) {
                            try {
                                JSONObject obj = ObjJson.getJSONObject(i);
                                Publicity publicity = new Publicity();
                                publicity.setId(Integer.valueOf(obj.getString("id")));
                                publicity.setName(obj.getString("fullname"));
                                publicity.setActive(1);
                                publicity.setCategory_id(Integer.valueOf(obj.getString("category_id")));
                                publicity.setCategory_name(obj.getString("categoria"));
                                publicity.setImage(obj.getString("imagen"));
                                publicity.setCompany_id(Integer.valueOf(obj.getString("company_id")));
                                db.createPublicity(publicity);
                                //pedido.setDescripcion(obj.getString("descripcion"));
                                // adding movie to movies array
                                // tipoPedidoList.add(pedido);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        //poblandoSpinnerTipoPedido();
                        Log.d(LOG_TAG, String.valueOf(db.getAllPublicity()));
                    }
                }else{
                    Log.d(LOG_TAG, "No hay datos de Gancheras");
                    // return json.getString("message");
                    //return false;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return false ;
        } catch (Exception e){
            return false;
        }

        return true;

    }


    private boolean readJsonSODVentanas() {
        int success;
        try {

            HashMap<String, String> params = new HashMap<>();

            params.put("company_id", String.valueOf(GlobalConstant.company_id));


            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/JsonListSODAlicorp" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                return false ;
            } else {
                success = json.getInt("success");
                if (success == 1) {
                    JSONArray ObjJson;
                    ObjJson = json.getJSONArray("sod");

                    if(ObjJson.length() > 0) {
                        for (int i = 0; i < ObjJson.length(); i++) {
                            try {
                                JSONObject obj = ObjJson.getJSONObject(i);
                                SODVentanas sodventanas = new SODVentanas();
                                sodventanas.setId(Integer.valueOf(obj.getString("id")));
                                sodventanas.setName(obj.getString("fullname"));
                                sodventanas.setImage(obj.getString("imagen"));
                                sodventanas.setStatus(0);
                                db.createSDOVentana(sodventanas);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        Log.d(LOG_TAG, String.valueOf(db.getAllPublicity()));
                    }
                }else{
                    Log.d(LOG_TAG, json.getString("message"));
                    // return json.getString("message");
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e){

            return false;
        }


        return true;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }
    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
//        Intent a = new Intent(this,PanelAdmin.class);
//        //a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(a);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
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



    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
        finish();
        startActivity(getIntent());
    }
}

