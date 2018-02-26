package com.textmate.dovaj.textmate;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.textmate.dovaj.textmate.helpers.RetrofitHelper;
import com.textmate.dovaj.textmate.interfaces.CallbackWrapper;
import com.textmate.dovaj.textmate.interfaces.TextMateAPIInterface;
import com.textmate.dovaj.textmate.models.ContactModel;
import com.textmate.dovaj.textmate.models.RatingContact;
import com.textmate.dovaj.textmate.models.RatingRequest;
import com.textmate.dovaj.textmate.models.RatingResponse;
import com.textmate.dovaj.textmate.models.RatingResponseSenderReceiver;
import com.textmate.dovaj.textmate.models.RatingResult;
import com.textmate.dovaj.textmate.models.Text;

import org.reactivestreams.Subscriber;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import java.util.ArrayList;
import java.util.List;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class ContactActivity extends AppCompatActivity {

    private static final String TAG = "RatingActivity";
    private ArrayList<String> messages;
    private ArrayList<String> sentMessages;
    private ArrayList<String> recMessages;
    private RatingResult result;
    private TextMateAPIInterface apiInterface;
    Observable<RatingResult> sentObservable, recObservable;
    /**
     * Collects all subscriptions to unsubscribe later
     */

    private DisposableObserver<RatingResponseSenderReceiver> mCompositeDisposable;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        ContactModel c = (ContactModel)getIntent().getSerializableExtra("Contact");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        RatingContact contact = new RatingContact(c);
        this.setTitle( getString(R.string.title_activity_contact, contact.name));
        TextView contactName = (TextView) findViewById(R.id.contactNameView);
        contactName.setText(contact.name);

        TextView contactNumber = (TextView) findViewById(R.id.contactPhone);
        contactNumber.setText(contact.mobileNumber);
        messages = new ArrayList<>();
        if(contact.photoURI != null){
            ImageView contactPhoto = (ImageView) findViewById(R.id.contactImage);
        }
        String[] numbers = new String[7];
        String localNum = "";
        String areacodeNum = "";
        String nationalNum ="";
        String intNum ="";
        String eNum ="";
        try {
            Phonenumber.PhoneNumber temp;
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String countryCode = tm.getSimCountryIso();
            temp = phoneUtil.parse(contact.mobileNumber, countryCode.toUpperCase());
            localNum = Long.toString(temp.getNationalNumber());
            nationalNum = phoneUtil.format(temp, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
            areacodeNum = nationalNum.replaceAll("\\s+","").replaceAll("-", "");
            intNum = phoneUtil.format(temp, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            eNum = phoneUtil.format(temp, PhoneNumberUtil.PhoneNumberFormat.E164);
            numbers = new String[]{contact.mobileNumber, localNum, nationalNum, intNum, areacodeNum, eNum,  contact.name.replaceAll("\\s+","")};
        }catch (Exception e){
            Log.e("Error", e.toString());
        }

        this.sentMessages = this.getMessages(numbers,"sent");
        this.recMessages = this.getMessages(numbers,"received");

        if(this.sentMessages.size() > 0 && this.recMessages.size() > 0){
            apiInterface = new RetrofitHelper().Ratings();
            recObservable = requestRatings(recMessages);
            sentObservable = requestRatings(sentMessages);
            Observable<RatingResponseSenderReceiver> combined = Observable.zip(sentObservable, recObservable, (sentResult, recResult) -> new RatingResponseSenderReceiver(sentResult, recResult));
            mCompositeDisposable = combined.subscribeWith(new CallbackWrapper<RatingResponseSenderReceiver>(){
                @Override
                protected void onSuccess(RatingResponseSenderReceiver result) {
                    displayResult(result);
                }
                @Override
                public void onComplete() {
                    super.onComplete();
                    if (mCompositeDisposable != null) mCompositeDisposable.dispose();
                }
            });

        } else {
            calcFinished();
            View v = findViewById(R.id.contactView);
            Snackbar.make(v, "Not enough messages to analyze.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(contact.getFav()) {
                    contact.setFav(false);
                    fab.setImageDrawable(ContextCompat.getDrawable(view.getContext(), android.R.drawable.btn_star_big_off));
                    Snackbar.make(view, "Removed From Favorites", Snackbar.LENGTH_LONG)
                            .setAction("Remove Fav", null).show();
                } else {
                    contact.setFav(true);
                    fab.setImageDrawable(ContextCompat.getDrawable(view.getContext(), android.R.drawable.btn_star_big_on));
                    Snackbar.make(view, "Added To Favorites", Snackbar.LENGTH_LONG)
                            .setAction("Add Fav", null).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        // DO NOT CALL .dispose()
        if(mCompositeDisposable != null){
            mCompositeDisposable.dispose();
        }
        super.onDestroy();
    }


    protected ArrayList<String> getMessages(String[] phoneNumbers, String flag){
        ArrayList<String> messages = new ArrayList<>();
        //Todo add area code phone number
        //String[] phoneNumber = new String[] { number, name, unFormattedNumber }; //the wanted phone number
        String queryURL = flag == "sent" ? "content://sms/sent" : "content://sms/inbox";
        Cursor messageCursor = getContentResolver().query(Uri.parse(queryURL), new String[] {"address", "date", "body", "type" },
                "address=? OR address=? OR address=? OR address=? OR address=?  OR address=? OR address=? ",
                phoneNumbers, "date DESC LIMIT 30");
        Log.e(TAG, Integer.toString(messageCursor.getCount()));

            if (messageCursor.getCount() > 0) {
                try {
                while (messageCursor.moveToNext()) {
                    messages.add(messageCursor.getString(messageCursor.getColumnIndex("body")));
                }
            } catch(Exception e){
                Log.e("Error", e.toString());
            } finally{
                messageCursor.close();
            }
        }
        return messages;
    }

    private void displayResult(RatingResult response){
        Log.e("Response", response.getSentimentScore().getSentimentStruct().getSentences().get(0).getText().getContent());
        //TextView RatingScore = (TextView)  findViewById(R.id.contactScore);
        String score = String.format( "%.1f", response.getScore());
        Log.d(TAG, "Score: " + score.toString());
       // RatingScore.setText(score);
        calcFinished();
        //RatingScore.setVisibility(View.VISIBLE);

        Log.d(TAG, score);
    }

    private void displayResult(RatingResponseSenderReceiver response){
        Log.e("Response Sender", response.sender.getSentimentScore().getSentimentStruct().getSentences().get(0).getText().getContent());
        Log.e("Response Receiver", response.receiver.getScore().toString());
        Log.e("Response Sender",  response.sender.getScore().toString());
        Log.e("Response Receiver", response.receiver.getSentimentScore().getSentimentStruct().getSentences().get(0).getText().getContent());
        TextView RatingScore = (TextView)  findViewById(R.id.contactScore);
        TextView RatingMaxScore = (TextView)  findViewById(R.id.contactScoreMax);
        Double relscore =  (response.sender.getScore() + response.receiver.getScore()) / 2;
        String score = String.format( "%.1f", relscore);
        Log.d(TAG, "Score: " + score.toString());
        RatingScore.setText(score);
        calcFinished();
        RatingScore.setVisibility(View.VISIBLE);
        RatingMaxScore.setVisibility(View.VISIBLE);

        Log.d(TAG, score);
    }

    private void calcFinished(){
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private Observable<RatingResult> requestRatings( ArrayList<String> messages){
        RatingRequest req = new RatingRequest();
        req.setMessageList(messages);
        req.setMessageCount(messages.size());
        Log.d(TAG, "Requesting");
        return apiInterface.getRatingFromList(req)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
                /*.subscribeWith(new CallbackWrapper<RatingResult>() {
                    @Override
                    protected void onSuccess(RatingResult result) {
                        displayResult(result);
                    }
                });*/
    }





}
