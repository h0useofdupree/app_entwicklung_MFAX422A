package de.fhdw.app_entwicklung.chatgpt;

import android.os.Bundle;
import android.service.controls.templates.TemperatureControlTemplate;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

import java.util.Locale;

import de.fhdw.app_entwicklung.chatgpt.openai.ChatGpt;
import de.fhdw.app_entwicklung.chatgpt.speech.LaunchSpeechRecognition;

public class MainFragment extends Fragment {

    private static final String CHAT_SEPARATOR = "\n\n";
    private static String answer;
    private TextToSpeech tts;

    private final ActivityResultLauncher<LaunchSpeechRecognition.SpeechRecognitionArgs> getTextFromSpeech = registerForActivityResult(
            new LaunchSpeechRecognition(),
            query -> {
                getTextView().append(query);

                MainActivity.backgroundExecutorService.execute(() -> {
                    ChatGpt chatGpt = new ChatGpt("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");
                    answer = chatGpt.getChatCompletion(query);

                    getTextView().append(CHAT_SEPARATOR);
                    getTextView().append(answer);
                });
            });

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getAskButton().setOnClickListener(v -> {
            getTextFromSpeech.launch(new LaunchSpeechRecognition.SpeechRecognitionArgs(Locale.GERMAN));
            Toast.makeText(requireContext(), "I'm Listening", Toast.LENGTH_SHORT).show();
        });

        getSpeakButton().setOnClickListener(v -> {
            processTextToSpeech();
        });

        tts = new TextToSpeech(requireContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.GERMAN);
                    if (result == tts.LANG_MISSING_DATA || result == tts.LANG_NOT_SUPPORTED) {
                        // handle ERROR
                    } else {
                        // SUCCESS
                    }
                } else {
                    // INIT failed
                }
            }
        });
    }

    private TextView getTextView() {
        //noinspection ConstantConditions
        return getView().findViewById(R.id.textView);
    }

    private Button getAskButton() {
        //noinspection ConstantConditions
        return getView().findViewById(R.id.button_ask);
    }

    private Button getSpeakButton() {
        return getView().findViewById(R.id.button_speak);
    }
    public void processTextToSpeech() {
        if (answer == null) {
            // Display a Toast message when the string is empty
            Toast.makeText(requireContext(), "Ask me something first.", Toast.LENGTH_SHORT).show();
        } else {
            // Proceed with TextToSpeech processing if the string is not empty
            // Your TextToSpeech code here
            tts.speak(answer, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }


}