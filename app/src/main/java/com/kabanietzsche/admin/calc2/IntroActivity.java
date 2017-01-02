package com.kabanietzsche.admin.calc2;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

public class IntroActivity extends AppCompatActivity {

    Button nameButton;
    EditText nameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        nameButton = (Button) findViewById(R.id.name_button);
        nameEditText = (EditText) findViewById(R.id.name_edit_text);

        nameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                if (!name.equals("")) {
                    Info.setYourName(name);
                    startActivity(new Intent(IntroActivity.this, MainActivity.class));
                }
            }
        });
    }
}
