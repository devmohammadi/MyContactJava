package com.fmohammadi.mycontact.controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fmohammadi.mycontact.R;
import com.fmohammadi.mycontact.adapter.ContactAdapter;
import com.fmohammadi.mycontact.model.Contact;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private FloatingActionButton floatingActionButton;

    private EditText name;
    private EditText phoneNumber;
    private TextView add;
    private TextView cancel;

    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private ArrayList<String> mKeys = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewContact);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mRootRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("name")
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getSupportActionBar().setTitle(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });


        final List<Contact> list = new ArrayList<>();
        final ContactAdapter adapter = new ContactAdapter(this, list);
        linearLayoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);


        final DatabaseReference mRef = mRootRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("Contact");
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getKey();
                mKeys.add(key);
                mRef.child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String nameContact = snapshot.child("name").getValue().toString();
                        String phoneNumber = snapshot.child("contact").getValue().toString();
                        Contact contact = new Contact(nameContact, phoneNumber);
                        list.add(contact);
                        adapter.notifyDataSetChanged();
                        Collections.reverse(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(error.getMessage(), "Failed to read value.");
                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View itemView = getLayoutInflater().inflate(R.layout.dialog_add_contact, null);

                name = itemView.findViewById(R.id.etNameContactDialog);
                phoneNumber = itemView.findViewById(R.id.etPhoneNumberDialog);
                add = itemView.findViewById(R.id.tvAdd);
                cancel = itemView.findViewById(R.id.tvCancel);

                alertDialogBuilder = new AlertDialog.Builder(view.getContext()).setView(itemView);
                alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String textName = name.getText().toString().trim();
                        String textPhoneNumber = phoneNumber.getText().toString().trim();
                        addContact(textName, textPhoneNumber);
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
            }
        });
    }

    private void addContact(final String textName, final String textPhoneNumber) {

        if (TextUtils.isEmpty(textName) || TextUtils.isEmpty(textPhoneNumber))
            Toast.makeText(MainActivity.this, "Please Enter All Information", Toast.LENGTH_LONG).show();
        else if (textPhoneNumber.length() != 11)
            Toast.makeText(MainActivity.this, "Phone Number Is Not Valid ", Toast.LENGTH_LONG).show();
        else {
            HashMap<String, String> map = new HashMap<>();
            map.put("name", textName);
            map.put("contact", textPhoneNumber);
            mRootRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("Contact").push().setValue(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                alertDialog.dismiss();
                                Toast.makeText(MainActivity.this, "Create Contact is Successful", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    alertDialog.dismiss();
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu , menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.logout_menu){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this , LoginActivity.class)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}