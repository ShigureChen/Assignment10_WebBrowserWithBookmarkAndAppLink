package edu.temple.webbrowserapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;

public class BookmarkActivity extends AppCompatActivity implements Serializable {

    ArrayList<String> bookmarks;
    ArrayList<String> title;
    CustomAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        Intent intent = getIntent();
        bookmarks = intent.getStringArrayListExtra("Bookmarks");
        title = intent.getStringArrayListExtra("Title");
        Button close = findViewById(R.id.closeButton);


        if(bookmarks != null)
        {
            ListView listView = (ListView)findViewById(R.id.listView);
            adapter = new CustomAdapter(this, bookmarks, title);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String url = bookmarks.get(i);
                    launchNewBookMark(url);
                }
            });
        }

        else
        {
            this.setTitle("No Bookmark Added");
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookmarkActivity.this, BrowserActivity.class);
                intent.putExtra("BOOKMARK", bookmarks);
                intent.putExtra("TITLE", title);

                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    public void launchNewBookMark(String url)
    {
        Intent intent = new Intent(this, BookmarkActivity.class);
        intent.putExtra("URL_TO_OPEN", url);
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("BOOKMARKS", bookmarks);
        outState.putSerializable("TITLE", title);
    }
}