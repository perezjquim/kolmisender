package com.perezjquim.kolmisender;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.perezjquim.PermissionChecker;

import static com.perezjquim.UIHelper.toast;

public class MainActivity extends AppCompatActivity
{
    private static final int CONTACT_REQUEST_CODE = 2;
    private TextView txtContact;
    private String contactName;
    private int contactPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        PermissionChecker.init(this);
        setContentView(R.layout.activity_main);
        txtContact = findViewById(R.id.txtContact);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode)
        {
            case PermissionChecker.REQUEST_CODE:
                PermissionChecker.restart();
                break;
            case CONTACT_REQUEST_CODE:
                Uri uri = data.getData();
                String[] projection =
                {
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                };

                if(uri == null)
                {
                    toast(this,"No contact selected!");
                    txtContact.setText("Selected contact: (none)");
                    contactName = "";
                    contactPhone = -1;
                    return;
                }

                Cursor cursor = getContentResolver().query(uri, projection,
                        null, null, null);
                cursor.moveToFirst();

                int numberColumnIndex = cursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                contactPhone = cursor
                        .getInt(numberColumnIndex);

                int nameColumnIndex = cursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                contactName = cursor
                        .getString(nameColumnIndex);
                txtContact.setText("Selected contact: "+contactName);

                cursor.close();
                break;
            default:
                break;
        }
    }

    public void selectContact(View v)
    {
        Uri uri = Uri.parse("content://contacts");
        Intent intent = new Intent(Intent.ACTION_PICK, uri);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent, CONTACT_REQUEST_CODE);
    }

    @SuppressLint("MissingPermission")
    public void sendKolmi(View v)
    {
        if(contactPhone != -1)
        {
            Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+Uri.encode("*#121*"+contactPhone+"#")));
            startActivity(intent);
        }
        else
        {
            toast(this,"No contact selected!");
        }
    }
}
