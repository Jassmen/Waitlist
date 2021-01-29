package com.example.waitlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import Data.WaitlistContract;
import Data.WaitlistDBhelper;

/******************************
 Elgoz2 da ahm goz2 l2n da elly h2olo ezay te insert el data we get ll data delete ll data mn el SQL lite
 ********************************/
public class MainActivity extends AppCompatActivity {

    FloatingActionButton add_user;
    ImageView waitlest;
    RecyclerView guestRecyclerView;

    TextView partySize,guestName;

    WaitlistDBhelper waitlistDBhelper;
    GuestAdapter guestAdapter;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        add_user = findViewById(R.id.add_new_user);
        waitlest = findViewById(R.id.waitlist_image);
        guestRecyclerView = findViewById(R.id.recycler_view);

        guestRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        guestRecyclerView.setHasFixedSize(true);
        guestRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));  // da el seperate line

        waitlistDBhelper = new WaitlistDBhelper(getApplicationContext());

        sqLiteDatabase = waitlistDBhelper.getWritableDatabase();

        cursor = getAllCursor();

        guestAdapter = new GuestAdapter(getApplicationContext(), cursor);

        guestRecyclerView.setAdapter(guestAdapter);

        /******** HNA h2olo e5feli el sora bta3t el list lma ndeefguests w lma ykon m3ndesh  guests ezhrha*********/
        if (cursor.getCount() != 0) {
            waitlest.setVisibility(View.GONE);
        } else {
            waitlest.setVisibility(View.VISIBLE);
        }

        add_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomDialog();
            }
        });
    }

    /******************Get all Guests ********************/
    private Cursor getAllCursor()
    {
        Cursor cursor=sqLiteDatabase.query(
                WaitlistContract.WaitlistEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                WaitlistContract.WaitlistEntry.COLUMN_TIMESTAMP
        );
            return cursor;
    }

    /***************** Custom Dialog ********************/
    private void showCustomDialog()
    {

        final Dialog dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // ezhrlii window without title
        dialog.setContentView(R.layout.newguest_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);  // lma ados 3la ay 7ta fl shash myt3mlosh cancel

        final WindowManager.LayoutParams lp=new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width=WindowManager.LayoutParams.MATCH_PARENT;
        lp.height=WindowManager.LayoutParams.WRAP_CONTENT;

        final Button add_guest=dialog.findViewById(R.id.add_guest_btn);
        final Button back=dialog.findViewById(R.id.back_btn);

        final EditText guest_name=dialog.findViewById(R.id.guest_name);
        final EditText guest_number=dialog.findViewById(R.id.guest_number);


        final View layout=getLayoutInflater().inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
        TextView text=findViewById(R.id.text);
//        text.setTextColor(Color.WHITE);
//        text.setText("Please");
        CardView lyt_card = layout.findViewById(R.id.lyt_card);
        lyt_card.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));


        final Toast toast=new Toast(getApplicationContext());

        add_guest.setOnClickListener(new View.OnClickListener() {
            String name = guest_name.getText().toString();
            String number = guest_number.getText().toString();
            @Override
            public void onClick(View view) {
                if(guest_name.length()==0 || guest_number.length()==0)
                {
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }else
                {
                    addNewGuest(name,number);
                    guestAdapter.swapCursor(getAllCursor());

                    waitlest.setVisibility(View.GONE);
                    dialog.dismiss();
                    toast.cancel();

                }/*
                dialog.show();
                dialog.getWindow().setAttributes(lp);*/
            }
        });

        /*************Item touch helper  *********************/
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)
        {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                long id=(long) viewHolder.itemView.getTag();
                removeGuests(id);
                guestAdapter.swapCursor(getAllCursor());

                if(getAllCursor().getCount()==0)
                {
                    waitlest.setVisibility(View.VISIBLE);
                }
            }
        }).attachToRecyclerView(guestRecyclerView);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                toast.cancel();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }


    /**************Add New Guest *********************/
    private long addNewGuest(String name, String partySize)
    {
        ContentValues cv = new ContentValues();
        cv.put(WaitlistContract.WaitlistEntry.COLUMN_GUEST_NAME, name);
        cv.put(WaitlistContract.WaitlistEntry.COLUMN_PARTY_SIZE, partySize);
        return sqLiteDatabase.insert(WaitlistContract.WaitlistEntry.TABLE_NAME, null, cv);
    }

    /***********************Remove Guests**************************/
    private Boolean removeGuests(long id)
    {
        return sqLiteDatabase.delete(WaitlistContract.WaitlistEntry.TABLE_NAME,
                WaitlistContract.WaitlistEntry._ID +"="+ id,
                null) > 0;
    }



}
