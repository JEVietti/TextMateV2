package com.textmate.dovaj.textmate.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.textmate.dovaj.textmate.R;
import com.textmate.dovaj.textmate.helpers.RetrofitHelper;
import com.textmate.dovaj.textmate.interfaces.CallbackWrapper;
import com.textmate.dovaj.textmate.interfaces.TextMateAPIInterface;
import com.textmate.dovaj.textmate.models.RatingRequest;
import com.textmate.dovaj.textmate.models.RatingResponseSenderReceiver;
import com.textmate.dovaj.textmate.models.RatingResult;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OverviewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OverviewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "Overview";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextMateAPIInterface apiInterface;
    private ArrayList<String> sentMessages;
    private ArrayList<String> recMessages;
    RatingResponseSenderReceiver overviewResult;

    private Observable<RatingResult> recObservable;
    private Observable<RatingResult> sentObservable;
    private DisposableObserver<RatingResponseSenderReceiver> mCompositeDisposable;

    private OnFragmentInteractionListener mListener;

    public OverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OverviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OverviewFragment newInstance(String param1, String param2) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

       requestOverview();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void requestOverview(){
        this.sentMessages = new ArrayList<>();
        this.recMessages = new ArrayList<>();

        this.sentMessages = this.getOverviewMessages("sent");
        this.recMessages = this.getOverviewMessages("received");

        if(this.sentMessages.size() > 0 && this.recMessages.size() > 0){
            apiInterface = new RetrofitHelper().Ratings();
            recObservable = getAnalysis(recMessages);
            sentObservable = getAnalysis(sentMessages);
            Observable<RatingResponseSenderReceiver> combined = Observable.zip(sentObservable, recObservable, (sentResult, recResult) -> new RatingResponseSenderReceiver(sentResult, recResult));
            mCompositeDisposable = combined.subscribeWith(new CallbackWrapper<RatingResponseSenderReceiver>(){
                @Override
                protected void onSuccess(RatingResponseSenderReceiver result) {
                    overviewResult = result;

                }
                @Override
                public void onComplete() {
                    super.onComplete();
                    if (mCompositeDisposable != null) mCompositeDisposable.dispose();
                }
            });

        } else {
            //calcFinished();

        }

    }

    //https://stackoverflow.com/questions/13322026/wait-with-operation-until-fragments-views-are-created

    private void displayOverview(RatingResponseSenderReceiver res){
        if(getView() != null){

        }
    }

    private void displayResult(RatingResponseSenderReceiver response){
        Log.e("Response Sender", response.sender.getSentimentScore().getSentimentStruct().getSentences().get(0).getText().getContent());
        Log.e("Response Receiver", response.receiver.getScore().toString());
        Log.e("Response Sender",  response.sender.getScore().toString());
        Log.e("Response Receiver", response.receiver.getSentimentScore().getSentimentStruct().getSentences().get(0).getText().getContent());
        TextView RatingScore = (TextView)  getView().findViewById(R.id.contactScore);
        Double relscore =  (response.sender.getScore() + response.receiver.getScore()) / 2;
        String score = String.format( "%.1f", relscore);
        Log.d(TAG, "Score: " + score.toString());
        //RatingScore.setText(score);
        //calcFinished();
        //RatingScore.setVisibility(View.VISIBLE);

        Log.d(TAG, score);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public ArrayList<String> getOverviewMessages(String flag){
        ArrayList<String> messages = new ArrayList<>();
        String queryURL = flag == "sent" ? "content://sms/sent" : "content://sms/inbox";
        Cursor messageCursor = getActivity().getContentResolver().query(Uri.parse(queryURL), new String[] {"address", "date", "body", "type" },
                null, null, "date DESC LIMIT 30");
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



    public Observable<RatingResult> getAnalysis(ArrayList<String> messages){
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
