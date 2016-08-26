package dataservicios.com.ttauditalicorp.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dataservicios.com.ttauditalicorp.Model.Product;
import dataservicios.com.ttauditalicorp.R;
import dataservicios.com.ttauditalicorp.SQLite.DatabaseHelper;
import dataservicios.com.ttauditalicorp.util.SessionManager;

/**
 * Created by Jaime on 17/04/2016.
 */
public class SearchProductAdapter extends BaseAdapter implements Filterable{
    private static final String TAG = ProductsAdapter.class.getSimpleName();


    private Context activityParent ;
    //private LayoutInflater inflater;
    private List<Product> productsItems;
    public List<Product> productsItemsFilter;
    private DatabaseHelper db;
    private ProgressDialog pDialog;
    private int product_id;
    private int store_id;
    private int user_id;
    private int company_id;
    private SessionManager session;


    //ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public SearchProductAdapter( List<Product> productsItems ) {
        //this.activity = activity;
        this.productsItems = productsItems;
        this.productsItemsFilter = productsItems;
    }

    @Override
    public int getCount() {
        return productsItemsFilter.size();
    }

    @Override
    public Object getItem(int position) {
        return productsItemsFilter.get(position);
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
            convertView = inflater.inflate(R.layout.list_row_search_product, parent, false);
        }

        final Product m = productsItemsFilter.get(position);
        activityParent= parent.getContext();
       // session = new SessionManager(activityParent);
       // HashMap<String, String> user = session.getUserDetails();
        // id
       // user_id = Integer.valueOf(user.get(SessionManager.KEY_ID_USER)) ;
        TextView tvId = (TextView) convertView.findViewById(R.id.tvId);
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvCategoria = (TextView) convertView.findViewById(R.id.tvCategoria);
        TextView tvEAM = (TextView) convertView.findViewById(R.id.tvEAM);

        // thumbNail.setImageUrl(m.getImage(), imageLoader);
        tvId.setText(String.valueOf(m.getId()));
        tvName.setText(m.getName());
        tvCategoria.setText("CATEGORÍA: " +  m.getCategory_name());
        tvEAM.setText(m.getCode());
        return convertView;
    }


    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                productsItemsFilter = (ArrayList<Product>) results.values; // has
                notifyDataSetChanged();
            }
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults(); // Holds the
                // results of a
                // filtering
                // operation in
                // values
                // List<String> FilteredArrList = new ArrayList<String>();
               // List<Product> FilteredArrList = new ArrayList<Product>();
                /********
                 *
                 * Si el constraint (CharSequence que se recibe) retorna null
                 * entonces retorna los valores originales de productsItems caso contrario
                 * retornos lista productsItemsFilter (filtrada)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {
                    // set the Original result to return
                    results.count = productsItems.size();
                    results.values = productsItems;
                } else {
                    Locale locale = Locale.getDefault();
                    constraint = constraint.toString().toLowerCase(locale);

                    List<Product> resultsData = new ArrayList<>();
                    String searchStr = constraint.toString().toUpperCase();

                    int length = searchStr.length();
                    int length_extraer = 10;

                    if(length > 10){
                        //int position = length - length_extraer;
                        int position = 0;
                        searchStr = searchStr.substring(position, length_extraer);
                    }

                    //String

                    for (Product o : productsItems) {
                        //o.getCode().startsWith()
                        if (o.getCode().contains(searchStr) || o.getName().contains(searchStr))
                            resultsData.add(o);
                    }

                    results.count = resultsData.size();
                    results.values = resultsData;


                }
                return results;
            }
        };
        return filter;
    }
}
