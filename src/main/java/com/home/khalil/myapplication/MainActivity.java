package com.home.khalil.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private IntentIntegrator qrScan;
    public String QR_Output = "";
    FirebaseDatabase database;

    EditText eventKey;
    private FloatingActionButton buttonScan;
    SharedPreferences sp;
    TextView keyText;
    TextView mode;
    Switch toggle;
    Button submit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        submit = (Button) findViewById(R.id.button_submit);
        keyText = (TextView) findViewById(R.id.text_key);
        eventKey = (EditText) findViewById(R.id.event_key);
        database = FirebaseDatabase.getInstance();
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);

        sp = getPreferences(Context.MODE_PRIVATE);
        boolean b = sp.getBoolean("switch", false);

        mode = (TextView) findViewById(R.id.text2);
        toggle = (Switch) findViewById(R.id.toggle);
        toggle.setChecked(b);



       /* if(toggle.isChecked()) {
            DatabaseReference db = database.getReference("hard");
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot sampleSnapshot : dataSnapshot.getChildren()) {
                        Log.d("FB",sampleSnapshot.getKey()+" "+key);
                        if(key.equals(sampleSnapshot.getKey().toString())){
                            Toast.makeText(MainActivity.this, "ACCESS GRANTED", Toast.LENGTH_LONG).show();
                            editor.putString("hardkey", key);
                            editor.commit();
                            keyText.setText(key.toUpperCase());
                            break;
                        }
                        Toast.makeText(MainActivity.this, "INVALID CODE", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else{
            DatabaseReference db = database.getReference("soft");
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Log.d("FB",dataSnapshot.toString());
                    for (DataSnapshot sampleSnapshot : dataSnapshot.getChildren()) {
                        // Log.d("FB",sampleSnapshot.getKey().toString());
                        if(key.equals(sampleSnapshot.getKey())){
                            Toast.makeText(MainActivity.this, "ACCESS GRANTED", Toast.LENGTH_LONG).show();
                            editor.putString("softkey", key);
                            editor.commit();
                            keyText.setText(key.toUpperCase());
                            break;
                        }
                        Toast.makeText(MainActivity.this, "INVALID CODE", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }*/
        //keyText.setText(k);


        //Getting the corresponding mode with keys based on what the user did the first time
        //So once he closes and re-opens the apps, all his changes are saved, and does not need
        //to repeat the process.
        if (toggle.isChecked()) {
            String k = sp.getString("hardkey", "");
            keyText.setText(k.toUpperCase());
            mode.setText("Hard Copy Mode");
            mode.setTextColor(Color.parseColor("#DD3333"));
            submit.setBackgroundColor(Color.parseColor("#DD3333"));

        } else {
            String k = sp.getString("softkey", "");
            keyText.setText(k.toUpperCase());
            mode.setText("Email Tickets Mode");
            mode.setTextColor(Color.parseColor("#808080"));
            submit.setBackgroundColor(Color.parseColor("#000000"));
        }

        //Saving mode status when user clicks on the switch button, and apply corresponding UI changes
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            SharedPreferences.Editor editor = sp.edit();

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    String k = sp.getString("hardkey", "");
                    keyText.setText(k.toUpperCase());
                    mode.setText("Hard Copy Mode");
                    mode.setTextColor(Color.parseColor("#DD3333"));
                    editor.putBoolean("switch", true);
                    editor.commit();
                    submit.setBackgroundColor(Color.parseColor("#DD3333"));

                } else {
                    String k = sp.getString("softkey", "");
                    keyText.setText(k.toUpperCase());
                    mode.setText("Email Tickets Mode");
                    mode.setTextColor(Color.parseColor("#808080"));
                    editor.putBoolean("switch", false);
                    editor.commit();
                    submit.setBackgroundColor(Color.parseColor("#000000"));


                }
            }
        });




        /*THIS IS ONLY IN CASE YOU WANT TO GENERATE HARD COPY TCIKETS ON FIREBASE
        JUST GIVE IT THE NUMBER OF TICKETS AND THE PREFIX
        EXAMPLE: 20 silver sales tickets. PREFIX is : SS00
        WE ADD TO THE PREFIX i
        N.B: We first need to loop 9 times to give the prefix SS00 + i
        Then we change the loop and make it start from 10 and the prefix becomes SS0 + i which gives SS010*/

       /* for(int i=10; i<=40; i++){
            database.getReference("hard").child("HAQWEZT").push().child("GS0"+i).setValue(1);
        }
        database.getReference("hard").child("HAQWEZT").push().child("SS001").setValue(1);
*/


        //This is just some UX enhancement for opening and closing the keyboard
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventKey.clearFocus();
                eventKey.setFocusable(false);
                eventKey.setFocusableInTouchMode(true);
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

            }
        });


        //Saving the key for the corresponding mode when user clicks on Submit
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sp.edit();
           /* DatabaseReference db = database.getReference("soft").child(keyText.getText().toString().toUpperCase()).child("quantity");
            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //totalText.setText();
                    t = (List) dataSnapshot.getValue();
                   // Log.d("QWERTY",t.get(0)+"");
                    totalText.setText(t.get(0).toString());
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });*/

                final String key = eventKey.getText().toString();
                if (toggle.isChecked()) {
                    editor.putString("hardkey", key);
                    editor.commit();
                } else {
                    editor.putString("softkey", key);
                    editor.commit();
                }
                keyText.setText(key.toUpperCase());
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        });

        buttonScan = (FloatingActionButton) findViewById(R.id.scanBtn);
        qrScan = new IntentIntegrator(this);
        qrScan.setBeepEnabled(false);
        qrScan.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        qrScan.setBarcodeImageEnabled(true);
        qrScan.setPrompt("Scan a Barcode");

        //Opens the Scanner
        buttonScan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                qrScan.initiateScan();


            }
        });


    }

    // Once a QR is scanned we gets its output and decompose it to access the corresponding child in Firebase
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {

                QR_Output = result.getContents();

                //This is just for free tickets that were given by DUNKIN for PACHA IBIZA TOUR
                if (QR_Output.equals("DUNKIN")) {
                    Toast.makeText(MainActivity.this, "DUNKIN'", Toast.LENGTH_LONG).show();

                    // This is for the hard copy mode or any tickets
                    // that do not have ticketlist template in the email
                } else if (toggle.isChecked()) {
                    final String oid = QR_Output;

                    DatabaseReference db = database.getReference();

                    db.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            String s = dataSnapshot.getValue().toString();
                            if (s.contains(keyText.getText().toString().toUpperCase())) {
                                Log.d("here", oid);

                                final DatabaseReference ref = database.getReference("hard").child(keyText.getText().toString().toUpperCase());
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Log.d("ICE2", dataSnapshot + "");
                                        for (DataSnapshot sampleSnapshot : dataSnapshot.getChildren()) {

                                            //Log.d("TEST",sampleSnapshot.child(oid)+"");
                                            try {
                                                long n = (long) sampleSnapshot.child(oid).getValue();
                                                Log.d("TEST", n + "");
                                                n = n - 1;
                                                if (n >= 0) {

                                                    Log.d("TEST", "HERE bro");
                                                    ref.child(sampleSnapshot.getKey()).child(oid).setValue(n);

                                                    Toast.makeText(MainActivity.this, "SUCCESS", Toast.LENGTH_LONG).show();


                                                } else {
                                                    Toast.makeText(MainActivity.this, "Ticket Already Used. Access Denied", Toast.LENGTH_LONG).show();
                                                }
                                            } catch (NullPointerException e) {
                                                // Toast.makeText(MainActivity.this, "INVALID TICKET. Make sure you are on the CORRECT MODE3", Toast.LENGTH_LONG).show();
                                            }

                                        }


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        // Failed to read value
                                        Log.w("HEREE", "Failed to read value.", error.toException());
                                        Toast.makeText(MainActivity.this, "Error Occured. Try Again", Toast.LENGTH_LONG).show();
                                    }
                                });

                            } else {
                                Toast.makeText(MainActivity.this, "INVALID KEY", Toast.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    //This is for all email tickets sent from from tickelist,
                    //the ones that have ticketlist email templates
                    //THIS CASE IS THE MOST USED CASE
                } else if (!toggle.isChecked()) {
                    try {

                        String parts[] = QR_Output.split(",");
                        String id = parts[0];
                        final int type = Integer.parseInt(parts[1]) - 1;
                        String event = parts[2];
                        String uid = parts[3];

                        if (uid.equals(keyText.getText().toString().toUpperCase())) {
                            try {
                                final DatabaseReference myRef = database.getReference("soft").child(keyText.getText().toString().toUpperCase()).child(event).child(id).child("tickets");
                                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot sampleSnapshot : dataSnapshot.getChildren()) {
                                            Log.d("ICE2", sampleSnapshot.getKey() + "");
                                            List list = new ArrayList();
                                            list = (List) sampleSnapshot.getValue();
                                            long n = (long) list.get(type) - 1; //decrementing by 1 because in firebase, ticket types start from 0 and not 1 (We have a maximum of 5 types of tickets, in bubble every type is numbered from 1 to 5 but in firebase, from 0 to 4)
                                            if (n >= 0) {
                                                Log.d("changed", n + "");
                                                list.set(type, n);
                                                myRef.child(sampleSnapshot.getKey()).setValue(list);
                                                Toast.makeText(MainActivity.this, "SUCCESS! " + n + " left for this category", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(MainActivity.this, "ACCESS DENIED! All tickets for this order's category have been scanned", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        // Failed to read value
                                        Log.w("HEREE", "Failed to read value.", error.toException());
                                        Toast.makeText(MainActivity.this, "Error Occured. Try Again", Toast.LENGTH_LONG).show();
                                    }
                                });

                            } catch (NullPointerException e) {
                                Toast.makeText(MainActivity.this, "INVALID TICKET. Make sure you are on the CORRECT MODE1", Toast.LENGTH_LONG).show();
                            }

                        } else {
                            Toast.makeText(MainActivity.this, "INVALID CODE", Toast.LENGTH_LONG).show();
                        }

                    } catch (RuntimeException e) {
                        Toast.makeText(MainActivity.this, "INVALID TICKET. Make sure you are on the CORRECT MODE2", Toast.LENGTH_LONG).show();

                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
