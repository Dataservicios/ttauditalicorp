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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import dataservicios.com.ttauditalicorp.Model.PresenceProduct;
import dataservicios.com.ttauditalicorp.Model.Product;
import dataservicios.com.ttauditalicorp.R;
import dataservicios.com.ttauditalicorp.SQLite.DatabaseHelper;
import dataservicios.com.ttauditalicorp.SearchProduct;
import dataservicios.com.ttauditalicorp.adapter.ProductsAdapter;
import dataservicios.com.ttauditalicorp.util.GlobalConstant;
import dataservicios.com.ttauditalicorp.util.JSONParser;
import dataservicios.com.ttauditalicorp.util.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * Created by Jaime Eduardo on 28/09/2015.
 */
public class PresenciaProducto extends Activity {

    private static final String LOG_TAG = PresenciaProducto.class.getSimpleName();
    int request_code = 1;
    private Activity MyActivity= this;
    private EditText etCodigo;
    private TextView tvResultado,tvPregunta,tvContador ;
    private Button btGuardar, btBuscar;

    private SessionManager session;


    private ListView listView;
    private ProductsAdapter adapter;
    private DatabaseHelper db;

    private ProgressDialog pDialog;


    private List<Product> productsList = new ArrayList<Product>();
    private View.OnClickListener onClik ;
    private List<PresenceProduct> presenceProd = new ArrayList<PresenceProduct>();

    private int store_id, rout_id, company_id , audit_id,user_id , category_id, countProducts=0, product_id=0;
    private int  score = 0  ;
    private String fechaRuta,category_name ;
    private ImageButton imgSearcProduct ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.presencia_producto);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Presencia Producto");

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);

        Bundle bundle = getIntent().getExtras();
        company_id =  GlobalConstant.company_id;
        store_id = bundle.getInt("store_id");
        rout_id = bundle.getInt("rout_id");
        fechaRuta = bundle.getString("fechaRuta");
        audit_id = bundle.getInt("audit_id");
        category_id = bundle.getInt("category_id");
        category_name = bundle.getString("category_name");




        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        // id
        user_id = Integer.valueOf(user.get(SessionManager.KEY_ID_USER)) ;

        etCodigo = (EditText) findViewById(R.id.etCodigo) ;
        tvResultado = (TextView) findViewById(R.id.tvResultado);
        tvPregunta = (TextView) findViewById(R.id.tvPregunta);
        tvContador = (TextView) findViewById(R.id.tvContador);
        btGuardar = (Button) findViewById(R.id.btGuardar) ;
        btBuscar = (Button) findViewById(R.id.btBuscar);
        imgSearcProduct = (ImageButton) findViewById(R.id.imgSearchProduct);

        db = new DatabaseHelper(getApplicationContext());



        listView = (ListView) findViewById(R.id.listProducts);

        productsList=db.getProductForCategory(category_id);
        adapter = new ProductsAdapter(productsList);

        tvPregunta.setText("Categoría: " + category_name);
        tvContador.setText(String.valueOf(adapter.getCount()));

        listView.setAdapter(adapter);


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedProduct = ((TextView) view.findViewById(R.id.tvName)).getText().toString();
                String product_id = ((TextView) view.findViewById(R.id.tvId)).getText().toString();
                Toast toast = Toast.makeText(MyActivity, selectedProduct + " id: " + product_id + " position: " + position, Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }
        });



        //db.getAllProducts();


       // adapter.notifyDataSetChanged();

        imgSearcProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle argRuta = new Bundle();
                argRuta.clear();
                argRuta.putInt("category_id", category_id);
                argRuta.putString("category_name", category_name);
                Intent intent;
                //intent = new Intent(MyActivity, Product.class);
                intent = new Intent(MyActivity, SearchProduct.class);
                intent.putExtras(argRuta);
                //startActivity(intent);
                startActivityForResult(intent, request_code);
            }
        });


        btBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean encontro = false;

                String codigo = "";
                codigo = etCodigo.getText().toString().trim();


                String[] items = new String[adapter.getCount()];
                for(int i = 0; i < adapter.getCount(); i++){
                    items[i] = adapter.getItem(i).toString();
                    Product prod =  new Product();
                    prod = (Product) adapter.getItem(i);

                    int length = prod.getCode().length();
                    int length_extraer = 5;
                    int position = length - length_extraer;

                    //String eam_abreviado = prod.getCode().substring(position,length);
                    String eam_abreviado = prod.getCode().toString();

                           // if (prod.getCode().equals(codigo.toString())){
                            if (eam_abreviado.equals(codigo.toString())){


                                Product pd = new Product();
                                //pd=db.getProductCode(codigo);
                                pd=db.getProductCode(prod.getCode());

                                PresenceProduct pp = new PresenceProduct();
                                pp.setProduct_id(pd.getId());
                                pp.setCategory_id(pd.getCategory_id());
                                pp.setProduct_Code(pd.getCode());
                                pp.setPrecio_check("");
                                pp.setPrecio_visible(0);
                                pp.setStore_id(store_id);
                                pp.setStatus(1);
                                db.createPresenseProduct(pp);

                                product_id=pd.getId();

                                new auditPollProducts().execute();

                                Toast toast = Toast.makeText(MyActivity, "Producto se encuentró en la lista", Toast.LENGTH_SHORT);
                                toast.show();
                                //View view = adapter.getView(i, null, null);
                                //ImageView imageView= (ImageView) listView.getChildAt(i).findViewById(R.id.imgStatus);
                                //ImageView imageView= (ImageView) view.findViewById(R.id.imgStatus);
                               // imageView.setImageResource(R.drawable.ic_check_on);
                                ((Product) adapter.getItem(i)).setStatus(1);
                                etCodigo.requestFocus();
                                //db.updateProductStatusForCode(codigo.toString(),1);
                                db.updateProductStatusForCode(prod.getCode().toString(),1);

                                listView.smoothScrollToPosition(i);
                                encontro=true;
                                etCodigo.setText("");
                                adapter.notifyDataSetChanged();
                                return;
                            }



                    //Log.d("TAG", "Item nr: " +i + " "+ adapter.getItem(i));
                }

                //adapter.notifyDataSetChanged();
                //listView.smoothScrollToPosition(5);
                //int conuntElement = adapter.getCount();
                etCodigo.setText("");

                if (encontro==false){
                    Toast.makeText(MyActivity, "No se encontró el producto", Toast.LENGTH_SHORT).show();
                }

                etCodigo.requestFocus();

            }

        });

        btGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity);
                builder.setTitle("Guardar Presencia de productos");
                builder.setMessage("Está seguro de guardar todas los datos: ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // Actualizo todo los productos
                       // db.updateProductStatus(0);
                       db.updateProductStatusForCategory(category_id, 1);
                        finish();
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


    class auditPollProducts extends AsyncTask<Void, Integer , Boolean> {
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

            InsertAuditProduct(store_id,product_id, company_id);

            return true;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted

            if (result){
                hidepDialog();

            }
        }
    }


    private void InsertAuditProduct(int store_id,  int product_id , int company_id) {
        int success;
        try {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("poll_id", String.valueOf("")));
            params.add(new BasicNameValuePair("store_id", String.valueOf(store_id)));
            params.add(new BasicNameValuePair("user_id", String.valueOf(user_id)));
            params.add(new BasicNameValuePair("company_id", String.valueOf(company_id)));
            params.add(new BasicNameValuePair("product_id", String.valueOf(product_id)));
           // params.add(new BasicNameValuePair("idRuta", String.valueOf(rout_id)));
           // params.add(new BasicNameValuePair("idaudit", String.valueOf(audit_id)));
            JSONParser jsonParser = new JSONParser();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/savePresenciaAlicorp" ,"POST", params);
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


        AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity);
        builder.setTitle("Presencia de productos");
        builder.setMessage("Está seguro desea salir");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener()

        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

               // Toast.makeText(MyActivity, "No se puede volver atras, los datos ya fueron guardado, para modificar póngase en contácto con el administrador", Toast.LENGTH_LONG).show();
               // super.onBackPressed();
                //this.finish();
                finish();

                overridePendingTransition(R.anim.anim_slide_in_right,R.anim.anim_slide_out_right);
                dialog.dismiss();

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                return;
            }
        });

        builder.show();
        builder.setCancelable(false);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == request_code) && (resultCode == RESULT_OK)){
            etCodigo.setText(data.getDataString());

        }
    }


}
