package com.perezjquim.kolmisender;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.perezjquim.PermissionChecker;

import java.io.FileNotFoundException;

import static com.perezjquim.UIHelper.toast;

public class MainActivity extends AppCompatActivity
{
    private static final int CONTACT_REQUEST_CODE = 2;

    private static final String ERROR_NO_CONTACT = "No contact selected!";

    private static final String KOLMI_FORMAT = "*#121*@#";

    private TextView txtContact;
    private String contactPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        PermissionChecker.init(this);
        super.setTheme(R.style.AppTheme);

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
                if(data != null)
                {
                    Uri uri = data.getData();
                    String[] projection =
                    {
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.PHOTO_URI
                    };

                    if(uri != null)
                    {
                        Cursor cursor = getContentResolver().query(uri, projection,
                                null, null, null);
                        cursor.moveToFirst();

                        int numberColumnIndex = cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        int photoColumnIndex = cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI);
                        int nameColumnIndex = cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

                        contactPhone = cursor
                                .getString(numberColumnIndex)
                                .trim()
                                .replace("-","")
                                .replace("+351","");

                        QuickContactBadge badge = findViewById(R.id.badge);

                        txtContact.setText(cursor.getString(nameColumnIndex));
                        badge.assignContactUri(uri);

                        try
                        {
                            badge.setImageURI(Uri.parse(cursor.getString(photoColumnIndex)));
                        }
                        catch(NullPointerException ex)
                        {
                            badge.setImageResource(android.R.drawable.ic_menu_gallery);
                        }

                        if(badge.getDrawable() == null)
                        {
                            badge.setImageResource(android.R.drawable.ic_menu_gallery);
                        }

                        cursor.close();
                    }
                }
                else
                {
                    toast(this,ERROR_NO_CONTACT);
                    txtContact.setText(getString(R.string.TXT_CONTACT_DEFAULT));
                    contactPhone = "";
                }
                break;
            default:
                break;
        }
    }

    public void selectContact(View v)
    {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent, CONTACT_REQUEST_CODE);
    }

    @SuppressLint("MissingPermission")
    public void sendKolmi(View v)
    {
        if(!contactPhone.equals(""))
        {
            Intent intent = new Intent(Intent.ACTION_CALL,
                    Uri.parse("tel:"+Uri.encode(KOLMI_FORMAT.replace("@",contactPhone))));
            startActivity(intent);
        }
        else
        {
            toast(this,ERROR_NO_CONTACT);
        }
    }
}
