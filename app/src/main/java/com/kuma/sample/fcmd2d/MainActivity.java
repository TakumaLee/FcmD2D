package com.kuma.sample.fcmd2d;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kuma.sample.fcmd2d.manager.FirebaseManager;

public class MainActivity extends AppCompatActivity {

    private Button button1;
    private EditText registerIdEditText;
    private EditText idEditText;
    private EditText nameEditText;
    private EditText messageEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = (Button) findViewById(R.id.button_register);
        registerIdEditText = (EditText) findViewById(R.id.editText_register_id);
        idEditText = (EditText) findViewById(R.id.editText_sendUser_id);
        nameEditText = (EditText) findViewById(R.id.editText_sendUser_name);
        messageEditText = (EditText) findViewById(R.id.editText_sendUser_mesage);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String regId = registerIdEditText.getText().toString();
                String id = idEditText.getText().toString();
                String name = nameEditText.getText().toString();
                String message = messageEditText.getText().toString();
                FirebaseManager.getInstance().sendUserPush(id, regId, name, message);
//                FirebaseManager.getInstance().sendPush("Test", id, "eJ-yAt4vbNQ:APA91bHBeumraPu2gDtdRZX9bvULZXDAvOPlluIgWnp-Nwd6afhkWG4AEgyRT85cUvcxuPsmlzHb-Vyzhft2rgvTBOEJeFc6C3qL2pnNemjVDz4b046CDva7psaPff4IJgVz-4W6XXj2", false);
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = registerIdEditText.getText().toString();
                FirebaseManager.getInstance().updateUserPushData(id);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
