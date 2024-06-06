package com.example.uberclone;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 1;

    private ListView chatListView;
    private EditText chatInput;
    private Button sendButton ,mback;
    private ArrayList<String> messages;
    private ChatAdapter chatAdapter;
    private String phoneNumber;

    private TextView chatHistory;

    private final BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    for (Object pdu : pdus) {
                        SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu);
                        String sender = message.getDisplayOriginatingAddress();
                        String body = message.getDisplayMessageBody();
                        if (sender.equals(phoneNumber)) {
                            appendMessage("Driver: " + body); // Append received message
                            messages.add("Driver: " + body); // Add to message list
                            chatAdapter.notifyDataSetChanged(); // Notify ListView adapter
                        }
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatHistory = findViewById(R.id.chatHistory); // Reference to TextView
        chatListView = findViewById(R.id.chatListView);
        chatInput = findViewById(R.id.chatInput);
        sendButton = findViewById(R.id.sendButton);
        messages = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, messages);
        chatListView = findViewById(R.id.chatListView);
        chatInput = findViewById(R.id.chatInput);
        sendButton = findViewById(R.id.sendButton);
        messages = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, messages);
        chatListView.setAdapter(chatAdapter);
        mback=findViewById(R.id.back);
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, HelperViewCart.class);

                startActivity(intent);
            }
        });


        phoneNumber = getIntent().getStringExtra("phoneNumber");

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = chatInput.getText().toString();
                if (!message.isEmpty()) {
                    appendMessage("You: " + message); // Append message to TextView
                    messages.add("You: " + message);
                    chatAdapter.notifyDataSetChanged();
                    chatInput.setText("");
                    sendSms(phoneNumber, message);
                } else {
                    Toast.makeText(ChatActivity.this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        } else {
            registerSmsReceiver();
        }
    }

    private void sendSms(String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }
    private void appendMessage(String message) {
        String currentText = chatHistory.getText().toString();
        chatHistory.setText(currentText + "\n" + message);
    }

    private void registerSmsReceiver() {
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, filter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                registerSmsReceiver();
            } else {
                Toast.makeText(this, "SMS permissions are required to use this feature", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
