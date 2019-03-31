package schoolofai.hackathon.healthvision;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.UUID;

public class ChatActivity extends Activity {

    private static final String TAG = ChatActivity.class.getSimpleName();
    private static final int USER = 10001;
    private static final int BOT = 10002;
    private static final int REQUEST_IMAGE_CAPTURE = 0001;

    private String uuid = UUID.randomUUID().toString();
    private LinearLayout chatLayout;
    private EditText queryEditText;

    // Android client
    //private AIRequest aiRequest;
    //private AIDataService aiDataService;
    //private AIServiceContext customAIServiceContext;

    // Java V2
    //private SessionsClient sessionsClient;
    //private SessionName session;
    private String[] botReply1 =  {
            "Bem vinda, Catarina",
            "Fazem 6 meses e 3 dias desdo seu último exame de pele.",
            "Seu médico Dr. Jacó Saraiva recomendou um novo exame de acompanhamento a partir de 27 de março de 2019.",
            "Vamos fazer um novo exame de pele?"};
    private String[] botReply2 =  {
            "Vamos tirar uma foto, Catarina?",
            "Aqui vão algumas dicas para você tirar a melhor foto.",
            "Coloque seu celular 15 centímetros do alvo.",
            "Garanta boa iluminação.",
            "Caso necessário, peça ajuda de alguém."};
    private String[] botReply3 =  {
            "Muito bem, Catarina!",
            "Vou compartilhar as informações com o Dr. Jacó Saraiva para agendarmos uma nova consulta.",
            "Você gostaria que fosse uma teleconsulta?"};
    private int messageCounter;
    private int chatCounter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        final ScrollView scrollview = findViewById(R.id.chatScrollView);
        scrollview.post(() -> scrollview.fullScroll(ScrollView.FOCUS_DOWN));

        chatLayout = findViewById(R.id.chatLayout);

        ImageView sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this::sendMessage);

        queryEditText = findViewById(R.id.queryEditText);
        queryEditText.setOnKeyListener((view, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        sendMessage(sendBtn);
                        return true;
                    default:
                        break;
                }
            }
            return false;
        });

        startChatBot(botReply1);

        // Android client
//        initChatbot();

        // Java V2
        //initV2Chatbot();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
        }
    }

    /*private void initChatbot() {
        final AIConfiguration config = new AIConfiguration(BuildConfig.ClientAccessToken,
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiDataService = new AIDataService(this, config);
        customAIServiceContext = AIServiceContextBuilder.buildFromSessionId(uuid);// helps to create new session whenever app restarts
        aiRequest = new AIRequest();
    }*/

    /*private void initV2Chatbot() {
        try {
            InputStream stream = getResources().openRawResource(R.raw.test_agent_credentials);
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream);
            String projectId = ((ServiceAccountCredentials)credentials).getProjectId();

            SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
            SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
            sessionsClient = SessionsClient.create(sessionsSettings);
            session = SessionName.of(projectId, uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private void sendMessage(View view) {
        String msg = queryEditText.getText().toString();
        if (msg.trim().isEmpty()) {
            Toast.makeText(ChatActivity.this, "Please enter your query!", Toast.LENGTH_LONG).show();
        } else {
            showTextView(msg, USER);
            queryEditText.setText("");
            // Android client
//            aiRequest.setQuery(msg);
//            RequestTask requestTask = new RequestTask(MainActivity.this, aiDataService, customAIServiceContext);
//            requestTask.execute(aiRequest);
            // Java V2
            //QueryInput queryInput = QueryInput.newBuilder().setText(TextInput.newBuilder().setText(msg).setLanguageCode("en-US")).build();
            //new RequestJavaV2Task(MainActivity.this, session, sessionsClient, queryInput).execute();
            messageCounter = 0;
            switch (chatCounter) {
                case 0:
                    startChatBot(botReply2);
                    break;
                case 1:
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                    break;
                case 2:
                    startChatBot(botReply3);
                default:
                    startChatBot(new String[] {"Como posso ajudar?"});
            }
            chatCounter++;
        }
    }

    private void startChatBot(String[] botReply) {
        if (messageCounter < botReply.length) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showTextView(botReply[messageCounter++], BOT);
                    startChatBot(botReply);

                }
            }, 1000);
        }
    }

    /*public void callback(AIResponse aiResponse) {
        if (aiResponse != null) {
            // process aiResponse here
            String botReply = aiResponse.getResult().getFulfillment().getSpeech();
            Log.d(TAG, "Bot Reply: " + botReply);
            showTextView(botReply, BOT);
        } else {
            Log.d(TAG, "Bot Reply: Null");
            showTextView("There was some communication issue. Please Try again!", BOT);
        }
    }

    public void callbackV2(DetectIntentResponse response) {
        if (response != null) {
            // process aiResponse here
            String botReply = response.getQueryResult().getFulfillmentText();
            Log.d(TAG, "V2 Bot Reply: " + botReply);
            showTextView(botReply, BOT);
        } else {
            Log.d(TAG, "Bot Reply: Null");
            showTextView("There was some communication issue. Please Try again!", BOT);
        }
    }*/

    private void showTextView(String message, int type) {
        FrameLayout layout;
        switch (type) {
            case USER:
                layout = getUserLayout();
                break;
            case BOT:
                layout = getBotLayout();
                break;
            default:
                layout = getBotLayout();
                break;
        }
        layout.setFocusableInTouchMode(true);
        chatLayout.addView(layout); // move focus to text view to automatically make it scroll up if softfocus
        TextView tv = layout.findViewById(R.id.chatMsg);
        tv.setText(message);
        layout.requestFocus();
        queryEditText.requestFocus(); // change focus back to edit text to continue typing
    }

    FrameLayout getUserLayout() {
        LayoutInflater inflater = LayoutInflater.from(ChatActivity.this);
        return (FrameLayout) inflater.inflate(R.layout.user_msg_layout, null);
    }

    FrameLayout getBotLayout() {
        LayoutInflater inflater = LayoutInflater.from(ChatActivity.this);
        return (FrameLayout) inflater.inflate(R.layout.bot_msg_layout, null);
    }
}
