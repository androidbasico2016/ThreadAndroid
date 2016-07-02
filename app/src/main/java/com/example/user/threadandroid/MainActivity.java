package com.example.user.threadandroid;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

//comentario por wilder
//cvomnetario por tabo

//comentario desde android studio

public class MainActivity extends AppCompatActivity {

    public static final int INCREMENTO = 10;
    public static final int TIME_SLEEP = 1000;
    public static final int CONTADOR = 10;
    public static final String TEXTO = "texto";
    ProgressBar progressBar;
    ProgressBar progressBar2;
    TextView txtAvance;
    boolean ejecutando1 = true;
    boolean ejecutando2 = true;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progressBar.incrementProgressBy(INCREMENTO);
            txtAvance.setText(msg.getData().getString(TEXTO));

        }
    };
    Thread background, thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.barraProgreso);
        progressBar2 = (ProgressBar) findViewById(R.id.barraProgreso2);
        txtAvance = (TextView) findViewById(R.id.avance);
        txtAvance.setText(getResources().getQuantityString(R.plurals.contador,0,0));
        progressBar.setProgress(0);
        progressBar2.setProgress(0);
        progressBar.setMax(100);

        background = new Thread(new Runnable() {
            @Override
            public void run() {


                for (int i = 0; i < CONTADOR; i++) {
                    if (ejecutando1) {
                        try {
                            Thread.sleep(TIME_SLEEP);
                            Message message = new Message();
                            Bundle bundle = new Bundle();
                            int cantidad=(i + 1) * INCREMENTO;
                            bundle.putString(TEXTO, getResources().getQuantityString(R.plurals.contador,cantidad,cantidad));
                            message.setData(bundle);
                            handler.sendMessageAtTime(message, 0);


                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } else {
                        i--;
                    }
                }
            }
        }

        );

        background.start();


        thread = new Thread() {

            @Override
            public void run() {
                try {
                    for (int i = 0; i < CONTADOR; i++) {
                        if (ejecutando2) {
                            Thread.sleep(TIME_SLEEP);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar2.incrementProgressBy(INCREMENTO);
                                }
                            });
                        } else {
                            i--;
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }

        ;
        thread.start();


    }

    public void detener(View view) {
        int id = view.getId();
        Button boton = (Button) view;
        String text1 = boton.getText().toString();
        switch (id) {
            case R.id.btn1:
                if (text1.equals(getString(R.string.detener1))) {
                    //Toast.makeText(this,getString(R.string.avanzaTexto,1),Toast.LENGTH_SHORT).show();
                    Toast.makeText(this,String.format(getString(R.string.avanzaTexto),1,"dos"),Toast.LENGTH_SHORT).show();
                    ejecutando1 = false;
                    boton.setText(R.string.reiniciar1);
                } else {
                    ejecutando1 = true;
                    boton.setText(R.string.detener1);
                }

                break;
            case R.id.btn2:
                if (text1.equals(getString(R.string.detener_2))) {
                    ejecutando2 = false;
                    boton.setText(R.string.reiniciar2);
                    Toast.makeText(this,getString(R.string.avanzaTexto,2,"uno"),Toast.LENGTH_SHORT).show();
                } else {
                    ejecutando2 = true;
                    boton.setText(R.string.detener_2);
                }
                break;
        }
    }

    public void iniciarDescarga(View view) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Descargando");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        progressDialog.setMax(100);

        new Thread() {
            @Override
            public void run() {
                int i = 0;

                while (i <= 100) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    final int finalWorkRealized = doWork(i);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.setProgress(finalWorkRealized);
                            if (finalWorkRealized == 100)
                                progressDialog.hide();

                        }
                    });

                    i = finalWorkRealized;

                }
            }
        }.start();
    }

    private int doWork(int i) {
        return i + 20;
    }

    public void iniciarDescargaAsync(View view) {
        new ProgresoTask().execute(100,0,0);
    }

    class ProgresoTask extends AsyncTask<Integer,Integer,Integer>{

        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog= new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Descargando con Asynctask");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

            progressDialog.setMax(100);
            progressDialog.show();

        }

        @Override
        protected Integer doInBackground(Integer... params) {
            int i=0;
            while(i<=100){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i= doWork(i);
                publishProgress(i);

            }
            return i;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);

        }

        @Override
        protected void onPostExecute(Integer i) {
            super.onPostExecute(i);
            progressDialog.hide();
        }
    }
}
