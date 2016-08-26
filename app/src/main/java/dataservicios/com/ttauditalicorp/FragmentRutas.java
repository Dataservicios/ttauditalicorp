package dataservicios.com.ttauditalicorp;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import dataservicios.com.ttauditalicorp.Model.Ruta;
import dataservicios.com.ttauditalicorp.adapter.RutasAdapter;
import dataservicios.com.ttauditalicorp.app.AppController;
import dataservicios.com.ttauditalicorp.util.GlobalConstant;
import dataservicios.com.ttauditalicorp.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by usuario on 08/01/2015.
 */
public class FragmentRutas extends Fragment {
    private ViewGroup linearLayout;
    private Button bt;

    EditText pdvs1,pdvsAuditados1,porcentajeAvance1;


    // Log tag
    private static final String TAG = MainActivity.class.getSimpleName();
    // Movies json url
    private static final String URL_AUDITORIAS = "http://www.dataservicios.com/webservice/rutas.php";
    private static final String URL_PDVS = "http://www.dataservicios.com/webservice/pdvs.php";
    private List<Ruta> rutaList = new ArrayList<Ruta>();
    private ListView listView;
    private RutasAdapter adapter;
    private ProgressDialog pDialog;
    private TextView tvPDVS  ;
    Activity MyActivity = (Activity) getActivity();
    private JSONObject params;
    private SessionManager session;
    private String email_user, id_user, name_user;

    public FragmentRutas(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        session = new SessionManager(getActivity());
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // name
        name_user = user.get(SessionManager.KEY_NAME);
        // email
        email_user = user.get(SessionManager.KEY_EMAIL);
        // id
        id_user = user.get(SessionManager.KEY_ID_USER);
        //Añadiendo parametros para pasar al Json por metodo POST
        params = new JSONObject();
        try {
            params.put("id", id_user);
            params.put("company_id", GlobalConstant.company_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        View rootView = inflater.inflate(R.layout.fragment_rutas, container, false);
        pdvs1 = (EditText) rootView.findViewById(R.id.etPDVS);
        pdvsAuditados1 = (EditText) rootView.findViewById(R.id.etPDVSAuditados);
        porcentajeAvance1 = (EditText) rootView.findViewById(R.id.etPorcentajeAvance);
        tvPDVS = (TextView) rootView.findViewById(R.id.tvPDVS);



        //tvPDVS.setText(id);

//        pdvs1.setText("80") ;
//        pdvsAuditados1.setText("10");
//        porcentajeAvance1.setText("25");

       // linearLayout = (ViewGroup) rootView.findViewById(R.id.lyControles);
//        for (int j = 0; j < 2; j++) {
//            //bt = new Button(getActivity());
//            bt=new Button(getActivity(), null, R.drawable.bottom_base);
//           // ContextThemeWrapper newContext = new ContextThemeWrapper(getActivity(), R.drawable.bottom_base);
//           // bt.setBackgroundResource(R.draw able.bottom_base);
//           // bt = new Button(newContext);
//            bt.setText("A Button" + j);
//            //bt.setBackground();
//            bt.setId( j );
//            bt.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
//            bt.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // Toast.makeText(getActivity(), j  , Toast.LENGTH_LONG).show();
//                    Button button = (Button) v;
//                    Toast.makeText(getActivity(), button.getText().toString(), Toast.LENGTH_SHORT).show();
//                }
//            });
//            linearLayout.addView(bt);
//        }
//        Button MyBoton = (Button) rootView.findViewById(R.id.button1);
//        MyBoton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent("com.dataservicios.systemauditor.LISTGENTE");
//                startActivity(intent);
//            }
//        });
        listView = (ListView) rootView.findViewById(R.id.list);
        adapter = new RutasAdapter(getActivity(), rutaList);
        listView.setAdapter(adapter);
        // Click event for single list row
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // selected item
                String selected =((TextView)view.findViewById(R.id.tvRutaDia)).getText().toString();
                String idRuta =((TextView)view.findViewById(R.id.tvId)).getText().toString();
                Toast toast= Toast.makeText(getActivity(), selected, Toast.LENGTH_SHORT);
                toast.show();
                Bundle bolsa = new Bundle();
                bolsa.putInt("idRuta", Integer.valueOf(idRuta));
                bolsa.putString("fechaRuta", selected);

               Intent intent = new Intent("dataservicios.com.ttauditalicorp.PUNTOSVENTA");
                intent.putExtras(bolsa);
               startActivity(intent);
                //getActivity().finish();

            }
        });

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        cargaRutasAndPdvs();
        //cargaPdvs();
        //cargaRutasPdvs();
        // Creando objeto Json y llenando pdvs para auditar


        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //hidePDialog();
    }

//    private void hidePDialog() {
//        if (pDialog != null) {
//            pDialog.dismiss();
//            pDialog = null;
//        }
//    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void cargaPdvs(){
//        pDialog = new ProgressDialog(getActivity());
//        // Showing progress dialog before making http request
//        pDialog.setMessage("Loading...");
//        pDialog.show();
        String url_pdv_nueva =  URL_PDVS + "?id=" + id_user;
        showpDialog();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest( url_pdv_nueva , null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Cargando PDV: ", response.toString());

                        try {

                            String Sdpvs = String.valueOf(response.getInt("pdvs"));
                            String Sporcentajeavance = String.valueOf(response.getInt("porcentajeavance"));
                            String Sauditados = String.valueOf(response.getInt("auditados"));
                           // Log.d("eeeeeERRR", String.valueOf(response.getInt("pdvs")));
                            pdvs1.setText(Sdpvs) ;
                            pdvsAuditados1.setText(Sauditados);
                            porcentajeAvance1.setText(Sporcentajeavance);
//                             pdvs1.setText("80") ;
//                             pdvsAuditados1.setText("10");
//                             porcentajeAvance1.setText("25");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        hidepDialog();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "EEEError: " + error.getMessage());
                hidepDialog();
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void cargaRutasPdvs(){
//        //        // Creando objeto Json y llenado en el lista pdvs de la semana
//        pDialog = new ProgressDialog(getActivity());
//        // Showing progress dialog before making http request
//        pDialog.setMessage("Loading...");
//        pDialog.show();
        showpDialog();
        String url_rutas_nueva =  URL_AUDITORIAS + "?id=" + id_user;
        JsonArrayRequest rutaReq = new JsonArrayRequest(URL_AUDITORIAS,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                Ruta ruta = new Ruta();
                                ruta.setId(obj.getInt("id"));
                                ruta.setRutaDia(obj.getString("ruta"));
                                ruta.setPdvs(obj.getInt("pdvs"));
                                ruta.setPorcentajeAvance(obj.getInt("porcentajeavance"));
                                // adding movie to movies array
                                rutaList.add(ruta);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();
                        hidepDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidepDialog();
            }
        }
        );

        AppController.getInstance().addToRequestQueue(rutaReq);


    }

    private void cargaRutasAndPdvs(){
        // Creando objeto Json y llenado en el lista pdvs de la semana
        showpDialog();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST , GlobalConstant.dominio + "/JsonRoadsTotal" ,params,
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
                            float contadorPDVS =0 ;
                            float auditadosPDV =0;
                            if (success == 1) {
//                                String Sdpvs = String.valueOf(response.getInt("pdvs"));
//                                String Sporcentajeavance = String.valueOf(response.getInt("porcentajeavance"));
//                                String Sauditados = String.valueOf(response.getInt("auditados"));
//                                // Log.d("eeeeeERRR", String.valueOf(response.getInt("pdvs")));
//                                pdvs1.setText(Sdpvs) ;
//                                pdvsAuditados1.setText(Sauditados);
//                                porcentajeAvance1.setText(Sporcentajeavance);
                                JSONArray agentesObjJson;
                                agentesObjJson = response.getJSONArray("roads");
                                // looping through All Products

                                if(agentesObjJson.length() > 0) {
                                    for (int i = 0; i < agentesObjJson.length(); i++) {
//                                    JSONObject obj = agentesObjJson.getJSONObject(i);
//                                    // Storing each json item in variable
//                                    String idAuditoria = obj.getString("id");
//                                    String auditoria = obj.getString("auditoria");
//                                    int status = obj.getInt("status");
                                        try {

                                            JSONObject obj = agentesObjJson.getJSONObject(i);
                                            contadorPDVS = contadorPDVS + Integer.valueOf(obj.getString("pdvs"));
                                            auditadosPDV =  auditadosPDV + Integer.valueOf(obj.getString("auditados"));
                                            Ruta ruta = new Ruta();
                                            ruta.setId(obj.getInt("id"));
                                            ruta.setRutaDia(obj.getString("fullname"));
                                            ruta.setPdvs(Integer.valueOf(obj.getString("pdvs")) );
                                            ruta.setPorcentajeAvance(Integer.valueOf(obj.getString("auditados")));
                                            // adding movie to movies array
                                            rutaList.add(ruta);
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        adapter.notifyDataSetChanged();
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

}
