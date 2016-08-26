package dataservicios.com.ttauditalicorp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dataservicios.com.ttauditalicorp.Model.Product;
import dataservicios.com.ttauditalicorp.SQLite.DatabaseHelper;
import dataservicios.com.ttauditalicorp.adapter.SearchProductAdapter;
import dataservicios.com.ttauditalicorp.util.SessionManager;

/**
 * Created by Jaime on 17/04/2016.
 */
public class SearchProduct extends Activity {
    private static final String LOG_TAG = SearchProduct.class.getSimpleName();
    private Activity MyActivity= this;


    private SessionManager session;


    private ListView listView;
    private SearchProductAdapter adapter;
    private DatabaseHelper db;


    private List<Product> productsList = new ArrayList<Product>();
    private int store_id, rout_id, company_id , audit_id,user_id , category_id;
    private String category_name;
    private EditText etSearcProduct;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_product);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Buscar Producto");


        Bundle bundle = getIntent().getExtras();

        category_id = bundle.getInt("category_id");
        category_name = bundle.getString("category_name");

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        user_id = Integer.valueOf(user.get(SessionManager.KEY_ID_USER)) ;


        db = new DatabaseHelper(getApplicationContext());

        listView = (ListView) findViewById(R.id.listProducts);
        etSearcProduct = (EditText) findViewById(R.id.etSearcProduct);

        productsList = db.getProductForCategory(category_id);
        adapter = new SearchProductAdapter(productsList);

       // tvPregunta.setText("Categoría: " + category_name);
       // tvContador.setText(String.valueOf(adapter.getCount()));

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String selectedProduct = ((TextView) view.findViewById(R.id.tvName)).getText().toString();
                String code_eam = ((TextView) view.findViewById(R.id.tvEAM)).getText().toString();
                Toast.makeText(MyActivity, code_eam , Toast.LENGTH_SHORT).show();
               // return false;
                //String cad = (String)lvString.getAdapter().getItem(arg2);
                Intent data = new Intent();
                data.setData(Uri.parse(code_eam));
                setResult(RESULT_OK, data);
                finish();
            }
        });

        etSearcProduct.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                SearchProduct.this.adapter.getFilter().filter(cs);
                adapter.getFilter().filter(cs);
                //MainActivity.this.adapter.get
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
    }
}
