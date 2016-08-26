package dataservicios.com.ttauditalicorp.AditoriaAlicorp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dataservicios.com.ttauditalicorp.Model.PresenceProduct;
import dataservicios.com.ttauditalicorp.Model.Product;
import dataservicios.com.ttauditalicorp.R;
import dataservicios.com.ttauditalicorp.SQLite.DatabaseHelper;
import dataservicios.com.ttauditalicorp.util.GlobalConstant;
import dataservicios.com.ttauditalicorp.util.JSONParser;
import dataservicios.com.ttauditalicorp.util.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * Created by Jaime on 16/03/2016.
 */
public class Precio extends Activity {
    private static final String LOG_TAG = PresenciaProducto.class.getSimpleName();
    private Activity MyActivity= this;

    private Button btGuardar;

    private SessionManager session;


    private Integer product_id;
    private DatabaseHelper db;

    private ProgressDialog pDialog;

    private int store_id, rout_id, company_id , idAuditoria,user_id , countProducts=0;
    private int  score = 0  ;
    private String fechaRuta , precio_nuevo="" ;


    private TextView tvProduct , tvCategoria;
    private EditText etPrecio ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.precio);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Precio");

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);


        tvProduct = (TextView) findViewById(R.id.tvProducto);
        etPrecio = (EditText) findViewById(R.id.etPrecio);
        tvCategoria = (TextView) findViewById(R.id.tvCategoria);

        btGuardar = (Button) findViewById(R.id.btGuardar);

        Bundle bundle = getIntent().getExtras();
        product_id = bundle.getInt("product_id");
        store_id = bundle.getInt("store_id");
        company_id = GlobalConstant.company_id;




        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        // id
        user_id = Integer.valueOf(user.get(SessionManager.KEY_ID_USER)) ;

        db = new DatabaseHelper(getApplicationContext());




        Product p = new Product();
        p = db.getProduct(product_id);

        tvProduct.setText( p.getName());
        //tvPrecio.setText("Precio: " + p.getPrecio());
        tvCategoria.setText("Categoría: " + p.getCategory_name());


        PresenceProduct pp= new PresenceProduct();
        pp=db.getPresenceProduct(product_id);
        store_id = pp.getStore_id();






        btGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                precio_nuevo = etPrecio.getText().toString();
                if (etPrecio.getText().toString().trim().equals("") )
                {
                    Toast toast = Toast.makeText(MyActivity, "Ingrese el precio", Toast.LENGTH_SHORT);
                    toast.show();
                    etPrecio.requestFocus();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity);
                builder.setTitle("Guardar Presencia de productos");
                builder.setMessage("Está seguro de guardar todas los datos: ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        db.updateProductPresencePrecioCheck(product_id,precio_nuevo);
                        new auditPollProductPrecio().execute();
                        Toast toast;
                        toast = Toast.makeText(MyActivity, "Ha guardó correctamente" , Toast.LENGTH_LONG);
                        toast.show();


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
    class auditPollProductPrecio extends AsyncTask<Void, Integer , Boolean> {
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
            //cargaTipoPedido();

            InsertAuditProduct(store_id,product_id, company_id, precio_nuevo);

            return true;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted

            if (result){
                hidepDialog();
                finish();
            }
        }
    }


    private void InsertAuditProduct(int store_id,  int product_id , int company_id, String precio_nuevo) {
        int success;
        try {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("store_id", String.valueOf(store_id)));
            params.add(new BasicNameValuePair("company_id", String.valueOf(company_id)));
            params.add(new BasicNameValuePair("product_id", String.valueOf(product_id)));
            //params.add(new BasicNameValuePair("idRuta", String.valueOf(rout_id)));
            //params.add(new BasicNameValuePair("idaudit", String.valueOf(audit_id)));
            params.add(new BasicNameValuePair("price", String.valueOf(precio_nuevo)));
            params.add(new BasicNameValuePair("user_id", String.valueOf(user_id)));

            JSONParser jsonParser = new JSONParser();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/updatePriceProdAlicorp" ,"POST", params);
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

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
