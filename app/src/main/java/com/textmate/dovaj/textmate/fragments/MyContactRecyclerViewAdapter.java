package com.textmate.dovaj.textmate.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.textmate.dovaj.textmate.R;
import com.textmate.dovaj.textmate.fragments.ContactsFragment.OnListFragmentInteractionListener;
import com.textmate.dovaj.textmate.fragments.dummy.ContactContent;
import com.textmate.dovaj.textmate.models.ContactModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyContactRecyclerViewAdapter extends RecyclerView.Adapter<MyContactRecyclerViewAdapter.ViewHolder> {

    private List<ContactModel> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyContactRecyclerViewAdapter(List<ContactModel> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contact, parent, false);
        return new ViewHolder(view);
    }

    public void swapItems(List<ContactModel> items) {
        this.mValues = items;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Context context = holder.mView.getContext();
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        Uri imageUri = Uri.parse(holder.mItem.photoURI);
        Bitmap image =  BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_account_circle_black_24dp);
        if(holder.mItem.photoURI != null) {

/*
            try {
                image = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
*/

        }
        //holder.cImage.setImageBitmap(image);
        holder.mContentView.setText(String.valueOf(holder.mItem.name));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public ImageView cImage;
        public final TextView mContentView;
        public ContactModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            cImage = (ImageView) view.findViewById(R.id.listContactImage);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
