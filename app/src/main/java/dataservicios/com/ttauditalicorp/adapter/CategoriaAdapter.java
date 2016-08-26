package dataservicios.com.ttauditalicorp.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import dataservicios.com.ttauditalicorp.Model.Categoria;
import dataservicios.com.ttauditalicorp.R;

import java.util.List;


/**
 * Created by usuario on 30/01/2015.
 */
public class CategoriaAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Categoria> categoriaItems;
    //ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CategoriaAdapter(Activity activity, List<Categoria> categoriaItems) {
        this.activity = activity;
        this.categoriaItems = categoriaItems;
    }

    @Override
    public int getCount() {
        return categoriaItems.size();
    }

    @Override
    public Object getItem(int location) {
        return categoriaItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_categorias, null);
        TextView id = (TextView) convertView.findViewById(R.id.tvId);
        TextView nombre = (TextView) convertView.findViewById(R.id.tvNombre);


        // getting ruta data for the row
        Categoria m = categoriaItems.get(position);

        // rutaDia
        id.setText(String.valueOf(m.getId()));
        nombre.setText(m.getNombre());
        // pdvs


        return convertView;
    }

}