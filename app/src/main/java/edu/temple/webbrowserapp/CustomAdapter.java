package edu.temple.webbrowserapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {

    ArrayList<String> bookmarksList;
    ArrayList<String> titleList;
    Activity activity;
    private static LayoutInflater inflater = null;

    public CustomAdapter(Activity activity, ArrayList<String> bookmarks, ArrayList<String> title)
    {
        this.bookmarksList = bookmarks;
        this.titleList = title;
        this.activity = activity;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return bookmarksList.size();
    }

    @Override
    public Object getItem(int i) {
        return bookmarksList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        final int positionToRemove = position;
        if(convertView==null)
        {
            view = inflater.inflate(R.layout.list_row, null);
        }

        TextView title = (TextView)view.findViewById(R.id.title);
        TextView url = (TextView)view.findViewById(R.id.url);
        ImageButton delete = (ImageButton)view.findViewById(R.id.delete);

        title.setText(titleList.get(position));
        url.setText(bookmarksList.get(position));

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog(positionToRemove);
            }
        });

        return view;
    }

    private void dialog(final int positionToRemove)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Are you share you want to delete this bookmark?");
        builder.setTitle("Deleting Bookmark");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                bookmarksList.remove(positionToRemove);
                titleList.remove(positionToRemove);
                Toast toast = Toast.makeText(activity, "Bookmark removed", Toast.LENGTH_LONG);
                toast.show();
                notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()  {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
