package com.example.waitlist;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import  com.example.waitlist.GuestAdapter.Guestviewholder;


import Data.WaitlistContract.WaitlistEntry;

public class GuestAdapter extends RecyclerView.Adapter<Guestviewholder>
{
    Context context;
    Cursor cursor;  // for the data that we get from SQL lite

    public GuestAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public Guestviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(context).inflate(R.layout.list_item,
                parent,
                false);
        return new Guestviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Guestviewholder holder, int position)
    {
        /************** Elgoz2 da bta3 el listItem  elly htzhr ******************/
        if (!cursor.moveToPosition(position))
        {
            return;
        }

        String name = cursor.getString(cursor.getColumnIndex(WaitlistEntry.COLUMN_GUEST_NAME));
        String size = cursor.getString(cursor.getColumnIndex(WaitlistEntry.COLUMN_PARTY_SIZE));

        long id = cursor.getLong(cursor.getColumnIndex(WaitlistEntry._ID));

        holder.name.setText(name);
        holder.size.setText(size);
        holder.itemView.setTag(id);
    }

    @Override
    public int getItemCount() {
        return  cursor.getCount();
    }

    // call it when any new changes happened "add guest or delete guest"
    public void swapCursor(Cursor newCursor)
    {
        // always close the previous cursor first
        if(cursor!=null)
        {
            cursor.close();
        }
        cursor=newCursor;

        if(newCursor!=null)
        {
            //Force the recycler view to refresh
            this.notifyDataSetChanged();
        }
    }

    public  static final class Guestviewholder extends RecyclerView.ViewHolder
    {
        TextView name,size;

        public Guestviewholder(@NonNull View itemView)
        {
            super(itemView);

            name=itemView.findViewById(R.id.guest_name);
            size=itemView.findViewById(R.id.party_size);

        }
    }
}
