package edu.temple.webbrowserapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

public class BrowserActivity extends AppCompatActivity implements
        PageControlFragment.PageControlInterface,
        PageViewerFragment.PageViewerInterface,
        BrowserControlFragment.BrowserControlInterface,
        PagerFragment.PagerInterface,
        PageListFragment.PageListInterface,
        Serializable
{

    transient FragmentManager fm;

    private final String PAGES_KEY = "pages";

    PageControlFragment pageControlFragment;
    BrowserControlFragment browserControlFragment;
    PageListFragment pageListFragment;
    PagerFragment pagerFragment;
    PageViewerFragment pageViewerFragment;
    Fragment tmpFragment;

    ArrayList<PageViewerFragment> pages;
    ArrayList<String> bookmarks = new ArrayList<String>();
    ArrayList<String> markTitle = new ArrayList<String>();

    int LAUNCH_SECOND_ACTIVITY = 1;

    boolean listMode;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            pages = (ArrayList) savedInstanceState.getSerializable(PAGES_KEY);
        } else {
            pages = new ArrayList<>();
        }

        fm = getSupportFragmentManager();

        listMode = findViewById(R.id.page_list) != null;


        // If PageControlFragment already added (activity restarted) then hold reference
        // otherwise add new fragment. Only one instance of fragment is ever present
        if ((tmpFragment = fm.findFragmentById(R.id.page_control)) instanceof PageControlFragment) {
            pageControlFragment = (PageControlFragment) tmpFragment;
        } else {
            pageControlFragment = new PageControlFragment();
            fm.beginTransaction()
                    .add(R.id.page_control, pageControlFragment)
                    .commit();
        }

        // If BrowserFragment already added (activity restarted) then hold reference
        // otherwise add new fragment. Only one instance of fragment is ever present
        if ((tmpFragment = fm.findFragmentById(R.id.browser_control)) instanceof BrowserControlFragment) {
            browserControlFragment = (BrowserControlFragment) tmpFragment;
        } else {
            browserControlFragment = new BrowserControlFragment();
            fm.beginTransaction()
                    .add(R.id.browser_control, browserControlFragment)
                    .commit();
        }

        // If PagerFragment already added (activity restarted) then hold reference
        // otherwise add new fragment. Only one instance of fragment is ever present
        if ((tmpFragment = fm.findFragmentById(R.id.page_viewer)) instanceof PagerFragment)
            pagerFragment = (PagerFragment) tmpFragment;
        else {
            pagerFragment = PagerFragment.newInstance(pages);
            fm.beginTransaction()
                    .add(R.id.page_viewer, pagerFragment)
                    .commit();
        }


        // If fragment already added (activity restarted) then hold reference
        // otherwise add new fragment IF container available. Only one instance
        // of fragment is ever present
        if (listMode) {
            if ((tmpFragment = fm.findFragmentById(R.id.page_list)) instanceof PageListFragment)
                pageListFragment = (PageListFragment) tmpFragment;
            else {
                pageListFragment = PageListFragment.newInstance(pages);
                fm.beginTransaction()
                        .add(R.id.page_list, pageListFragment)
                        .commit();
            }
        }

        // ATTENTION: This was auto-generated to handle app links.
        handleIntent();
    }

    private void clearIdentifiers() {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("");
        pageControlFragment.updateUrl("");
    }

    // Notify all observers of collections
    private void notifyWebsitesChanged() {
        pagerFragment.notifyWebsitesChanged();
        if (listMode)
            pageListFragment.notifyWebsitesChanged();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save list of open pages for activity restart
        outState.putSerializable(PAGES_KEY, pages);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SECOND_ACTIVITY)
        {
            if(resultCode == Activity.RESULT_OK)
            {
               bookmarks = data.getStringArrayListExtra("BOOKMARK");
               markTitle = data.getStringArrayListExtra("TITLE");
            }
            if (resultCode == Activity.RESULT_CANCELED)
            {
                String url = new String();
                url = data.getStringExtra("URL_TO_OPEN");
                newPage();
                pagerFragment.go(url);
            }
        }
    }
    @Override
    public void go(String url) {
        if (pages.size() > 0)
            pagerFragment.go(url);
        else {
            pages.add(PageViewerFragment.newInstance(url));
            notifyWebsitesChanged();
            pagerFragment.showPage(pages.size() - 1);
        }

    }

    @Override
    public void back() {
        pagerFragment.back();
    }

    @Override
    public void forward() {
        pagerFragment.forward();
    }

    @Override
    public void updateUrl(String url) {
        if (url != null && url.equals(pagerFragment.getCurrentUrl())) {
            pageControlFragment.updateUrl(url);

            // Update the ListView in the PageListFragment - results in updated titles
            notifyWebsitesChanged();
        }
    }

    @Override
    public void updateTitle(String title) {
        if (title != null && title.equals(pagerFragment.getCurrentTitle()) && getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);

        // Results in the ListView in PageListFragment being updated
        notifyWebsitesChanged();
    }

    @Override
    public void pageSelected(int position) {
        pagerFragment.showPage(position);
    }

    @Override
    public void newPage() {
        // Add page to list
        pages.add(new PageViewerFragment());
        // Update all necessary views
        notifyWebsitesChanged();
        // Display the newly created page
        pagerFragment.showPage(pages.size() - 1);
        // Clear the displayed URL in PageControlFragment and title in the activity
        clearIdentifiers();
    }

    @Override
    public void savePage() {
        if(pagerFragment.size() != 0)
        {
            String url = pagerFragment.getCurrentUrl();
            String title = pagerFragment.getCurrentTitle();
            bookmarks.add(url);
            markTitle.add(title);
            Toast toast = Toast.makeText(BrowserActivity.this, "URL added to bookmark", Toast.LENGTH_LONG);
            toast.show();
        }
        else
        {
            Toast toast = Toast.makeText(BrowserActivity.this, "Unable to add bookmark", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public void launchBookmark() {
        Intent intent = new Intent(this, BookmarkActivity.class);
        if(bookmarks.size() != 0 && markTitle.size() != 0)
        {
            intent.putExtra("Bookmarks", bookmarks);
            intent.putExtra("Title", markTitle);
        }
        startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY);
    }

    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        handleIntent();
    }

    private void handleIntent()
    {
        String url = new String();
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
        if(Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null)
        {
            url = appLinkData.toString();
            pages.add(PageViewerFragment.newInstance(url));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.title_bar, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                Intent webIntent = new Intent(Intent.ACTION_SEND);
                if(pagerFragment.size() != 0)
                {
                    String url = pagerFragment.getCurrentUrl();
                    webIntent.putExtra(Intent.EXTRA_TEXT, url);
                    webIntent.setType("text/plain");
                    startActivity(Intent.createChooser(webIntent, "Share This Page With"));
                }
                else
                {
                    Toast toast = Toast.makeText(BrowserActivity.this, "Unable to share empty page", Toast.LENGTH_LONG);
                    toast.show();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}