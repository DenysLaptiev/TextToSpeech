package com.example.texttospeech;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final Integer RecordAudioRequestCode = 1;

    private String LOG_TAG = "MainActivity";

    private SpeechRecognizer speechRecognizer;

    private EditText editText;
    private ImageView ivMic;
    private Button btnStop;

    private TextView tvTotal;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkPermission();
        }

        editText = findViewById(R.id.text);
        ivMic = findViewById(R.id.ivMic);
        btnStop = findViewById(R.id.btnStop);

        tvTotal = findViewById(R.id.tvTotal);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.example.texttospeech");
        //speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 120 * 1000);

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                //Toast.makeText(MainActivity.this, "onReadyForSpeech", Toast.LENGTH_SHORT).show();
                System.out.println("onReadyForSpeech");
                System.out.println("bundle="+bundle);
            }

            @Override
            public void onBeginningOfSpeech() {
                //Toast.makeText(MainActivity.this, "onBeginningOfSpeech", Toast.LENGTH_SHORT).show();
                System.out.println("onBeginningOfSpeech");

                editText.setText("");
                editText.setHint("Listening...");
            }

            @Override
            public void onRmsChanged(float v) {
                //Toast.makeText(MainActivity.this, "onRmsChanged", Toast.LENGTH_SHORT).show();
                System.out.println("onRmsChanged");
            }

            @Override
            public void onBufferReceived(byte[] bytes) {
                //Toast.makeText(MainActivity.this, "onBufferReceived", Toast.LENGTH_SHORT).show();
                System.out.println("onBufferReceived");
            }

            @Override
            public void onEndOfSpeech() {
                //Toast.makeText(MainActivity.this, "onEndOfSpeech", Toast.LENGTH_SHORT).show();
                System.out.println("onEndOfSpeech");

                System.out.println("onEndOfSpeech-speechRecognizer.stopListening");
                speechRecognizer.stopListening();

                System.out.println("onEndOfSpeech-speechRecognizer.startListening");
                speechRecognizer.startListening(speechRecognizerIntent);
            }

            @Override
            public void onError(int errorCode) {
                System.out.println("onError");
                String errorMessage = getErrorText(errorCode);
                Log.d(LOG_TAG, "FAILED " + errorMessage);

//                System.out.println("sonError - peechRecognizer.stopListening");
//                speechRecognizer.stopListening();
//
                System.out.println("onError - speechRecognizer.startListening");
                speechRecognizer.startListening(speechRecognizerIntent);
            }

            @Override
            public void onResults(Bundle bundle) {
                //Toast.makeText(MainActivity.this, "onResults", Toast.LENGTH_SHORT).show();
                System.out.println("onResults");

                ivMic.setImageResource(R.drawable.ic_mic_black_off);
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                String tmpResult = data.get(0);
                editText.setText(tmpResult);
                tvTotal.append(tmpResult);
                bundle.clear();
                System.out.println("tmpResult="+ tmpResult);

                System.out.println("speechRecognizer.stopListening");
                speechRecognizer.stopListening();

                System.out.println("speechRecognizer.startListening");
                speechRecognizer.startListening(speechRecognizerIntent);
                System.out.println("bundle="+bundle);
            }

            @Override
            public void onPartialResults(Bundle bundle) {
                //Toast.makeText(MainActivity.this, "onPartialResults", Toast.LENGTH_SHORT).show();

                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                editText.setText(data.get(0));
                System.out.println("onPartialResults");
                System.out.println("bundle="+bundle);
            }

            @Override
            public void onEvent(int i, Bundle bundle) {
                //Toast.makeText(MainActivity.this, "onEvent", Toast.LENGTH_SHORT).show();
                System.out.println("onEvent");

            }
        });

        ivMic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //speechRecognizer.stopListening();
                    System.out.println("ivMic.setOnTouchListener");
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    ivMic.setImageResource(R.drawable.ic_mic_black_24dp);
                    speechRecognizer.startListening(speechRecognizerIntent);
                    System.out.println("speechRecognizer.startListening");
                }
                return false;
            }


        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speechRecognizer.stopListening();
                System.out.println("btnStop.setOnClickListener");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
        System.out.println("onDestroy");
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RecordAudioRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }
    }
    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

}
