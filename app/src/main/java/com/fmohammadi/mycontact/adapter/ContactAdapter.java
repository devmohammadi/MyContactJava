package com.fmohammadi.mycontact.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fmohammadi.mycontact.R;
import com.fmohammadi.mycontact.model.Contact;

import java.util.List;


public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>{
    Context mContext;
    List<Contact> mData;

    public ContactAdapter(Context mContext, List<Contact> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.name.setText(mData.get(position).getName());
        holder.contact.setText(mData.get(position).getContact());
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView contact;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tvNameContact);
            contact = itemView.findViewById(R.id.tvPhoneContact);
        }
    }
}