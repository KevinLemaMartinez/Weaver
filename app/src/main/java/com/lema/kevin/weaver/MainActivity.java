package com.lema.kevin.weaver;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import static java.lang.System.exit;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener {


    private static final int REQUEST_CODE = 1234;
    private TextView respuesta;
    private TextToSpeech tts;
    Locale spanish = new Locale("es", "ES");
    public static String url="https://api.wit.ai/message?v=20160526&q=hello";




    /**
     * Called with the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_recog);

        Button speakButton = (Button) findViewById(R.id.speakButton);

        respuesta = (TextView) findViewById(R.id.respuesta);


        // Disable button if no recognition service is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0)
        {
            speakButton.setEnabled(false);
            speakButton.setText("Recognizer not present");
        }

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(spanish);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }


                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });

    }

    private void speak(String text){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    /**
     * Handle the action of the button being clicked
     */
    public void speakButtonClicked(View v)
    {
        startVoiceRecognitionActivity();
    }

    /**
     * Fire an intent to start the voice recognition activity.
     */
    private void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition Demo...");
        startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * Handle the results from the voice recognition activity.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            // Populate the wordsList with the String values the recognition engine thought it heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);


            // Busqueda busqueda=new Busqueda(matches);
            //String srespuesta="";
           // srespuesta=busqueda.buscar();

            //respuesta.setText(srespuesta);

            /*if(srespuesta.equalsIgnoreCase("cerrar")){
                finish();
            }

            if(srespuesta.contains("email")){
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(Intent.EXTRA_REFERRER_NAME, "carlos");
                Intent mailer = Intent.createChooser(emailIntent, null);
                startActivity(mailer);
            }

            if(srespuesta.contains("tiempo")){
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(Intent.EXTRA_REFERRER_NAME, "carlos");
                Intent mailer = Intent.createChooser(emailIntent, null);
                startActivity(mailer);
            }*/

            URL url = null;
            try {
             //   url = new URL("https://api.wit.ai/message?q=" + matches.get(0) + " ");


                url = new URL("https://api.wit.ai/converse?v=20160526&session_id=123abc&q=" + remove(matches.get(0)).replaceAll(" ", "%20"));
                Log.i("url", url.toString());

               // new GetResponse().execute(url);

                new GetResponse(new GetResponse.AsyncResponse(){

                    @Override
                    public void processFinish(String output) {



                        try {

                            if(output!=null){
                                JSONObject jObject = new JSONObject(output);

                                 JSONObject entities = jObject.getJSONObject("entities");

                                 if(jObject.getString("type").equalsIgnoreCase("msg")){

                                     speak(jObject.getString("msg"));

                               /*        while(!(jObject.getString("type").equalsIgnoreCase("stop"))){
                                          speak(jObject.getString("msg"));
                                          new GetResponse(new GetResponse.AsyncResponse(){

                                              @Override
                                              public void processFinish(String output) {

                                              }
                                          }).execute(new URL("https://api.wit.ai/converse?v=20160526&session_id=123abc"));
                               */


                                 }else if(jObject.getString("type").equalsIgnoreCase("merge")){
                                     speak("asd");
                                 }

                                if(entities.getJSONArray("clima")!=null){

                                    speak("Hace mucho calor");

                               //     new GetResponse(new GetResponse.AsyncResponse() {

                             //           @Override
                            //            public void processFinish(String output) {

                            //            }
                            //        }).execute(new URL(""));






                                }




                            }else{
                                speak("Lo siento, no te he entendido");
                            }




                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }).execute(url);


            } catch (MalformedURLException e) {
                e.printStackTrace();
            }




        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    /**
     * Función que elimina acentos y caracteres especiales de
     * una cadena de texto.
     * @param input
     * @return cadena de texto limpia de acentos y caracteres especiales.
     */
    public static String remove(String input) {
        // Cadena de caracteres original a sustituir.
        String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ";
        // Cadena de caracteres ASCII que reemplazarán los originales.
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
        String output = input;
        for (int i=0; i<original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }//for i
        return output;
    }


        @Override
        public void onDestroy() {
            if (tts != null) {
                tts.stop();
                tts.shutdown();
            }
            super.onDestroy();
        }

    @Override
    public void onInit(int status) {

    }
}
