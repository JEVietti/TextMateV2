package com.textmate.dovaj.textmate.models;

/**
 * Created by dovaj on 1/13/2018.
 */

public class RatingContact extends ContactModel{

    protected boolean isFav;

    public RatingContact() {
        this.isFav = false;
    }
    public RatingContact(ContactModel contactModel) {
        this.id = contactModel.id;
        this.name = contactModel.name;
        this.mobileNumber = contactModel.mobileNumber;
        this.photoURI = contactModel.photoURI;
        this.photo = contactModel.photo;
        this.isFav = false;
    }

    public void setFav(boolean status){
        this.isFav = status;
    }

    public boolean getFav(){
        return this.isFav;
    }
}
