package dataservicios.com.ttauditalicorp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import dataservicios.com.ttauditalicorp.Model.Audit;
import dataservicios.com.ttauditalicorp.Model.Product;
import dataservicios.com.ttauditalicorp.Model.Publicity;
import dataservicios.com.ttauditalicorp.SQLite.DatabaseHelper;
import dataservicios.com.ttauditalicorp.util.ConexionInternet;
import dataservicios.com.ttauditalicorp.util.GlobalConstant;
import dataservicios.com.ttauditalicorp.util.JSONParser;



import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    // Logcat tag
    private static final String LOG_TAG = "Load Activity";

    private int splashTime = 3000;
    private Thread thread;


    private ProgressBar mSpinner;
    private TextView tvCargando, tv_Version ;
    private ConexionInternet cnInternet ;
    private Activity MyActivity;
    //private JSONParser jsonParser;
    // Database Helper
   private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyActivity = (Activity) this;
        mSpinner = (ProgressBar) findViewById(R.id.Splash_ProgressBar);
        mSpinner.setIndeterminate(true);
        tvCargando = (TextView) findViewById(R.id.tvCargando);
        tv_Version = (TextView) findViewById(R.id.tvVersion);

        PackageInfo pckInfo ;
        try {
            pckInfo= getPackageManager().getPackageInfo(getPackageName(),0);
            tv_Version.setText(pckInfo.versionName);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        cnInternet=new ConexionInternet(MyActivity);
        db = new DatabaseHelper(getApplicationContext());
        if (cnInternet.isOnline()){
            if (db.checkDataBase(MyActivity)){

                mSpinner = (ProgressBar) findViewById(R.id.Splash_ProgressBar);
                mSpinner.setIndeterminate(true);
//                thread = new Thread(runable);
//                thread.start();

                //db.deleteAllUser();
                db.deleteAllProducts();
                db.deleteAllPublicity();
                //db.deleteAllAudits();
                db.deleteAllPresenseProduct();
                db.deleteAllPresensePublicity();

                //new loadProducts().execute();
                loadLoginActivity();

            }else{
                db.deleteAllUser();
                db.deleteAllPublicity();
                db.deleteAllProducts();
                db.deleteAllAudits();
                db.deleteAllPresenseProduct();
                db.deleteAllPresensePublicity();
                //new loadProducts().execute();
                loadLoginActivity();
            }

        }else  {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Conexion a internet?");
            alertDialogBuilder
                    .setMessage("No se encontro conexion a Internet!")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    moveTaskToBack(true);
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                    System.exit(1);
                                }
                            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }



    }


    private void loadLoginActivity()
    {
        Intent intent = new Intent(MyActivity, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public Runnable runable = new Runnable() {
        public void run() {
            try {
                Thread.sleep(splashTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                //startActivity(new Intent(MainActivity.this,LoginActivity.class));
                //Intent intent = new Intent(MainActivity.this,LoginActivity.class);
//                Intent intent = new Intent("com.dataservicios.systemauditor.LOGIN");
//
//                startActivity(intent);
//                finish();

                loadLoginActivity();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    };





    class loadProducts extends AsyncTask<Void, Integer , Boolean> {
        /**
         * Antes de comenzar en el hilo determinado, Mostrar progresión
         * */
        boolean failure = false;
        @Override
        protected void onPreExecute() {
            tvCargando.setText("Cargando Productos...");
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub
            //cargaTipoPedido();
                readJsonProducts();
//            Intent intent = new Intent("com.dataservicios.redagenteglobalapp.LOGIN");
//            startActivity(intent);
//            finish();
            return true;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted

            if (result){
               // loadLoginActivity();
                new loadPublicity().execute();
            }
        }
    }

    class loadPublicity extends AsyncTask<Void, Integer , Boolean> {
        /**
         * Antes de comenzar en el hilo determinado, Mostrar progresión
         * */
        boolean failure = false;
        @Override
        protected void onPreExecute() {
            tvCargando.setText("Cargando publicidades...");
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub
            //cargaTipoPedido();
            readJsonPublicity();
//            Intent intent = new Intent("com.dataservicios.redagenteglobalapp.LOGIN");
//            startActivity(intent);
//            finish();
            return true;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted

            if (result){
                loadLoginActivity();
            }
        }
    }




    private void readJsonProducts() {
        int success;
        try {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("company_id", String.valueOf(GlobalConstant.company_id)));
            JSONParser jsonParser = new JSONParser();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/JsonListProductsCompany" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            success = json.getInt("success");
            if (success == 1) {
                JSONArray ObjJson;
                ObjJson = json.getJSONArray("products");

                if(ObjJson.length() > 0) {
                    for (int i = 0; i < ObjJson.length(); i++) {
                        try {
                            JSONObject obj = ObjJson.getJSONObject(i);
                            Product product = new Product();
                            product.setId(Integer.valueOf(obj.getString("id")));
                            product.setName(obj.getString("fullname"));
                            product.setCode(obj.getString("eam"));
                            product.setStatus(0);
                            product.setCategory_id(Integer.valueOf(obj.getString("category_id")));
                            product.setCategory_name(obj.getString("categoria"));
                            product.setImage(GlobalConstant.dominio + "/media/images/colgate/products/" + obj.getString("imagen"));
                            product.setPrecio(obj.getString("precio"));
                            product.setCompany_id(Integer.valueOf(obj.getString("company_id")));
                            db.createProduct(product);
                            //pedido.setDescripcion(obj.getString("descripcion"));
                            // adding movie to movies array
                            // tipoPedidoList.add(pedido);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //poblandoSpinnerTipoPedido();
                    Log.d(LOG_TAG, String.valueOf(db.getAllProducts()));
                }
            }else{
                Log.d(LOG_TAG, json.getString("message"));
                // return json.getString("message");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Reed publicity de json
     */
    private void readJsonPublicity() {
        int success;
        try {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("company_id", String.valueOf(GlobalConstant.company_id)));
            JSONParser jsonParser = new JSONParser();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/JsonListPublicities" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
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
                Log.d(LOG_TAG, json.getString("message"));
                // return json.getString("message");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void readJsonAudits() {
        int success;
        try {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("company_id", "7"));
            JSONParser jsonParser = new JSONParser();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/JsonAuditsForStore" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            success = json.getInt("success");
            if (success == 1) {
                JSONArray ObjJson;
                ObjJson = json.getJSONArray("audits");

                if(ObjJson.length() > 0) {
                    for (int i = 0; i < ObjJson.length(); i++) {
                        try {
                            JSONObject obj = ObjJson.getJSONObject(i);

                            String idAuditoria = obj.getString("id");
                            String auditoria = obj.getString("fullname");

                            Audit audit = new Audit();
                            audit.setId(Integer.valueOf(obj.getString("id")));
                            audit.setName(obj.getString("fullname"));
                            audit.setStore_id(0);
                            audit.setScore(0);

                            db.createAudit(audit);
                            //pedido.setDescripcion(obj.getString("descripcion"));
                            // adding movie to movies array
                            // tipoPedidoList.add(pedido);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //poblandoSpinnerTipoPedido();
                    Log.d(LOG_TAG, String.valueOf(db.getAllAudits()));
                }
            }else{
                Log.d(LOG_TAG, json.getString("message"));
                // return json.getString("message");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
