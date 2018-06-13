package com.example.prashu.assignment201;

import android.Manifest;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText editName, editNumber;
    Button createContact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editName = findViewById(R.id.name);
        editNumber = findViewById(R.id.number);
        createContact = findViewById(R.id.create);

        createContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkMyPermissions(); //check whether there is permission to write contacts or not
                insertContact(); //call the method to insert contacts
            }
        });
    }


    private void insertContact() {
        String name = editName.getText().toString();
        String number = editNumber.getText().toString();
        ArrayList<ContentProviderOperation> list = new ArrayList<>();
//insert data using content uri
        list.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        if (!name.isEmpty() && !number.isEmpty()) {
            // insert the name
            list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                    .build());

// insert the number
            list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());

            // try to apply batch with the content resolver to the array list.
            try {
                getApplicationContext().getContentResolver().
                        applyBatch(ContactsContract.AUTHORITY, list);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //display successful message if contact is saved
            Toast.makeText(this, "New contact is created", Toast.LENGTH_SHORT).show();
        }
        // if contact is not saved
        else Toast.makeText(this, "Fill the data", Toast.LENGTH_SHORT).show();


// after the contact is saved clear the edit texts.
        editNumber.setText("");
        editName.setText("");
    }


            private void checkMyPermissions() {
                if (Build.VERSION.SDK_INT > 23){
                    // check if permission to write contacts is already granted.
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED){
                        // if permission is not granted then ask for runtime permission.
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_CONTACTS}, 0);
                    }
                }
                
            }


}