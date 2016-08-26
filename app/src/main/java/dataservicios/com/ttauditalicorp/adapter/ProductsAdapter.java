package dataservicios.com.ttauditalicorp.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import dataservicios.com.ttauditalicorp.AditoriaAlicorp.Precio;
import dataservicios.com.ttauditalicorp.Model.PresenceProduct;
import dataservicios.com.ttauditalicorp.Model.Product;
import dataservicios.com.ttauditalicorp.R;
import dataservicios.com.ttauditalicorp.SQLite.DatabaseHelper;
import dataservicios.com.ttauditalicorp.util.GlobalConstant;
import dataservicios.com.ttauditalicorp.util.JSONParser;
import dataservicios.com.ttauditalicorp.util.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
/**
 * Created by Jaime Eduardo on 30/09/2015.
 */
public class ProductsAdapter extends BaseAdapter {
    private static final String TAG = ProductsAdapter.class.getSimpleName();

    private Context activityParent ;
    //private LayoutInflater inflater;
    private List<Product> productsItems;
    private DatabaseHelper db;
    private ProgressDialog pDialog;
    private int product_id;
    private int store_id;
    private int user_id;
    private int company_id;
    private SessionManager session;


    //ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ProductsAdapter( List<Product> productsItems ) {
        //this.activity = activity;
        this.productsItems = productsItems;

    }


    @Override
    public int getCount() {
        return productsItems.size();
    }

    @Override
    public Object getItem(int position) {
        return productsItems.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView( int position, View convertView, final ViewGroup parent) {
        //View view = convertView;
        View view =null;
        convertView = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.list_row_product, parent, false);
        }


        final Product m = productsItems.get(position);
        activityParent= parent.getContext();

        session = new SessionManager(activityParent);
        HashMap<String, String> user = session.getUserDetails();
        // id
        user_id = Integer.valueOf(user.get(SessionManager.KEY_ID_USER)) ;

        TextView tvId = (TextView) convertView.findViewById(R.id.tvId);
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvCategoría = (TextView) convertView.findViewById(R.id.tvCategoría);
        TextView tvPrecio = (TextView) convertView.findViewById(R.id.tvPrecio);
        TextView tvPrecioChecked = (TextView) convertView.findViewById(R.id.tvPrecioChecked);
        ImageView imgStatus = (ImageView) convertView.findViewById(R.id.imgStatus);
        final CheckBox ckPrecioVisible = (CheckBox) convertView.findViewById(R.id.ckPrecioVisible);



        Button bt_do=(Button)convertView.findViewById(R.id.bt_do);


       // thumbNail.setImageUrl(m.getImage(), imageLoader);

        tvId.setText(String.valueOf(m.getId()));
        tvName.setText(m.getName());
        tvCategoría.setText("CATEGORÍA: " +  m.getCategory_name());
        tvPrecio.setText("PRECIO: S/." + m.getPrecio());


        db = new DatabaseHelper(parent.getContext());
         final int  status = db.getCountPresenseProductForProductId(m.getId());



        if(status==0){
            tvPrecioChecked.setVisibility(View.INVISIBLE);
            imgStatus.setImageResource(R.drawable.ic_check_off);
            bt_do.setEnabled(false);
            ckPrecioVisible.setEnabled(false);
        } else if(status==1){

            imgStatus.setImageResource(R.drawable.ic_check_on);

            if (db.getPresenceProduct(m.getId()).getPrecio_check().toString().trim().equals("")) {
                tvPrecioChecked.setVisibility(View.INVISIBLE);
                bt_do.setEnabled(true);

            } else {
                tvPrecioChecked.setVisibility(View.VISIBLE);
                tvPrecioChecked.setText("S/. " + db.getPresenceProduct(m.getId()).getPrecio_check());
                bt_do.setEnabled(false);
                //ckPrecioVisible.setEnabled(false);
            }
            if(db.getPresenceProduct(m.getId()).getPrecio_visible()==0) {
                ckPrecioVisible.setEnabled(true);
            } else if (db.getPresenceProduct(m.getId()).getPrecio_visible()==1)  {
                ckPrecioVisible.setEnabled(false);
                ckPrecioVisible.setChecked(true);
            }


        }

        ckPrecioVisible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
//                    PresenceProduct pp=  new PresenceProduct();
//                    pp = db.getPresenceProduct(m.getId());
//                    Toast.makeText(parent.getContext(), String.valueOf(pp.getStore_id())  + "-" + String.valueOf(m.getId()) , Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
                    builder.setTitle("Guardar visibilidad de precio");
                    builder.setMessage("Se guardara la visibilidad de precio, ya no podrá modificar");
                    builder.setPositiveButton("Si", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // Actualizo todo los productos
                            // db.updateProductStatus(0);
                            // db.updateProductStatusForCategory(category_id, 1);
                           // finish();
                            db.updateProductPresencePrecioVisible(m.getId(), 1);
                            PresenceProduct pp =new PresenceProduct();
                            pp=db.getPresenceProduct(m.getId());
                            product_id=pp.getProduct_id();
                            store_id=pp.getStore_id();
                            //user_id = 0;
                            company_id=GlobalConstant.company_id;


                            new auditPollProducts().execute();
                            ckPrecioVisible.setEnabled(false);
                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //ckPrecioVisible.setEnabled(true);
                            ckPrecioVisible.setChecked(false);
                            dialog.dismiss();
                        }
                    });

                    builder.show();
                    builder.setCancelable(false);



                } else {
                   // Toast.makeText(parent.getContext(), "button clicked: " + String.valueOf(m.getId()) , Toast.LENGTH_SHORT).show();
                }
            }
        });


        //bt_do.setOnClickListener(listener);
        bt_do.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PresenceProduct pp=  new PresenceProduct();
                pp = db.getPresenceProduct(m.getId());


                Bundle argRuta = new Bundle();
                argRuta.clear();
                argRuta.putInt("product_id", m.getId());
                argRuta.putInt("store_id", pp.getStore_id());



                Intent intent;
                intent = new Intent(parent.getContext(), Precio.class);
                intent.putExtras(argRuta);
                parent.getContext().startActivity(intent);

               // Toast.makeText(parent.getContext(),  String.valueOf(m.getId()), Toast.LENGTH_SHORT).show();
            }
        });


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(parent.getContext(), "button clicked: " + String.valueOf(m.getId()) , Toast.LENGTH_SHORT).show();
            }
        });


        return convertView;
    }

    class auditPollProducts extends AsyncTask<Void, Integer , Boolean> {
        /**
         * Antes de comenzar en el hilo determinado, Mostrar progresión
         * */
        boolean failure = false;
        @Override
        protected void onPreExecute() {
            //tvCargando.setText("Cargando Product...");
            pDialog = new ProgressDialog(activityParent);
            pDialog.setMessage("Cargando...");
            pDialog.setCancelable(false);
            pDialog.show();
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub
            //cargaTipoPedido();


            InsertAuditProduct(store_id,product_id,company_id,user_id);

            return true;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted

            if (result) {
                pDialog.dismiss();

            }
        }
    }
    private void InsertAuditProduct(int store_id,  int product_id , int company_id, int user_id) {
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
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/updatePriceProdVisble" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            success = json.getInt("success");
            if (success == 1) {
               // Log.d(LOG_TAG, json.getString("Ingresado correctamente"));
            }else{
               // Log.d(LOG_TAG, json.getString("message"));
                // return json.getString("message");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
