package com.example.tsandoval.events;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class novoActivity extends Activity {

    private static String url_create_product = "http://beijoseabracos.com.br/create_event.php";
    double lat;
    double lon;
    Button ok;
    EditText txttitulo, txtdescricao, txtlocal;
    int dia, mes, ano, hora, minuto;
    TimePicker horario;
    DatePicker data;
    private ProgressDialog pDialog;
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    JSONParser jsonParser = new JSONParser();
    String titulo, local, descricao, la, lo;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo);

        Bundle extras = getIntent().getExtras();
        lat = extras.getDouble("lat");
        lon = extras.getDouble("lon");
        la = String.valueOf(lat);
        lo = String.valueOf(lon);
        ok = (Button) findViewById(R.id.btnok);
        horario = (TimePicker) findViewById(R.id.time);
        horario.setIs24HourView(true);
        txttitulo = (EditText) findViewById(R.id.ettitle);
        txtlocal = (EditText) findViewById(R.id.etlocal);
        txtdescricao = (EditText) findViewById(R.id.etsnippet);
        data = (DatePicker) findViewById(R.id.datePicker);

        //teste = (TextView) findViewById(R.id.textView);
        //teste.setText(la);

       /* ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 titulo = txttitulo.getText().toString();
                 descricao = txtdescricao.getText().toString();
                 local = txtlocal.getText().toString();
                hora = horario.getCurrentHour()+1;
                minuto = horario.getCurrentMinute();
                String ehora = ((new DecimalFormat("00").format(hora)) + ":" + (new DecimalFormat("00").format(minuto)));
                ano = data.getYear();
                mes = data.getMonth() + 1;
                dia = data.getDayOfMonth();
                String edata = (dia + "-" + mes + "-" + ano);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date convertedDate = new Date();
                try {
                    convertedDate = dateFormat.parse(edata);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                // creating new product in background thread
                //new CreateNewProduct().execute();
            }
        });

    }*/
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titulo = txttitulo.getText().toString();
                descricao = txtdescricao.getText().toString();
                local = txtlocal.getText().toString();
                hora = horario.getCurrentHour() + 1;
                minuto = horario.getCurrentMinute();
                String ehora = ((new DecimalFormat("00").format(hora)) + ":" + (new DecimalFormat("00").format(minuto)));
                ano = data.getYear();
                mes = data.getMonth() + 1;
                dia = data.getDayOfMonth();
                String edata = (dia + "-" + mes + "-" + ano);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date convertedDate = new Date();
                try {
                    convertedDate = dateFormat.parse(edata);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                StringRequest request = new StringRequest(Request.Method.POST, url_create_product, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        System.out.println(response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parameters = new HashMap<String, String>();
                        parameters.put("nome", txttitulo.getText().toString());
                        parameters.put("descricao", txtdescricao.getText().toString());
                        parameters.put("local", txtlocal.getText().toString());
                        parameters.put("latitude", la.toString());
                        parameters.put("longitude", lo.toString());

                        return parameters;
                    }
                };
                requestQueue.add(request);
            }

        });


        /**
         * Background Async Task to Create new product
         * */
    /*class CreateNewProduct extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         *
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(novoActivity.this);
            pDialog.setMessage("Creating Product..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... args) {



            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("nome", titulo));
            params.add(new BasicNameValuePair("descricao", descricao));
            params.add(new BasicNameValuePair("local", local));
            params.add(new BasicNameValuePair("latitude", la));
            params.add(new BasicNameValuePair("longitude", lo));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_product,
                    "POST", params);
            try {
                JSONArray dataa = new JSONArray(getHttpGet(url_create_product,
                        "POST", params));
                Log.d("Create Response", dataa.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // check log cat fro response
            Log.i("Script", "INSERT INTO events(nome, descricao, local, latitude, longitude) VALUES("+titulo+", "+descricao+", "+local+" , "+la+", "+lo+"");
            Log.d("Create Response", json.toString());
            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);

                    Log.i("Script", "ok");
                    // closing this screen
                    finish();
                } else {
                    // failed to create product
                    Log.i("Script", "Erro");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         *
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }

    }
    public static String getHttpGet(String url, String post, List<NameValuePair> params) {
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) { // Download OK
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
            } else {
                Log.e("Log", "Failed to download result..");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }*/
    }
}
