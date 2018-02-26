package com.textmate.dovaj.textmate.models;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.Serializable;

/**
 * Created by dovaj on 1/6/2018.
 */

public class ContactModel implements Serializable {
    public String id;
    public String name;
    public String mobileNumber;
    public transient Bitmap photo;
    public transient String photoURI;
    private boolean fav;

    public ContactModel() {

    }

    public ContactModel(String name) {
        this.name = name;
    }

    public ContactModel(String cID, String name, String number, String photoURI) {
        this.id = cID;
        this.name = name;
        this.mobileNumber = number;
        this.photoURI = photoURI;
    }

}
//https://stackoverflow.com/questions/30159649/android-query-sms-inbox-by-sender-name