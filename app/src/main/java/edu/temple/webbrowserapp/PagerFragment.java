package edu.temple.webbrowserapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.ArrayList;

public class PagerFragment extends Fragment implements Serializable {

    private PagerInterface browserActivity;
    transient ViewPager viewPager;
    private ArrayList<PageViewerFragment> pages;
    private static final String PAGES_KEY = "pages";

    public PagerFragment() {}

    public static PagerFragment newInstance(ArrayList<PageViewerFragment> pages) {
        PagerFragment fragment = new PagerFragment();
        Bundle args = new Bundle();
        args.putSerializable(PAGES_KEY, pages);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pages = (ArrayList) getArguments().getSerializable(PAGES_KEY);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof PagerInterface) {
            browserActivity = (PagerInterface) context;
        } else {
            throw new RuntimeException("You must implement PagerInterface to attach this fragment");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View l = inflater.inflate(R.layout.fragment_pager, container, false);

        viewPager = l.findViewById(R.id.viewPager);

        viewPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return pages.get(position);
            }

            @Override
            public int getCount() {
                return pages.size();
            }

            @Override
            public int getItemPosition(@NonNull Object object) {
                if (pages.contains(object))
                    return pages.indexOf(object);
                else
                    return POSITION_NONE;
            }
        });

        //viewPager.getAdapter().notifyDataSetChanged();

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                browserActivity.updateUrl((pages.get(position)).getUrl());
                browserActivity.updateTitle((pages.get(position)).getTitle());
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        return l;
    }

    public void notifyWebsitesChanged()
    {
        viewPager.getAdapter().notifyDataSetChanged();
    }

    public void showPage(int index)
    {
        viewPager.setCurrentItem(index);
    }

    public void go(String url)
    {
        getPageViewer(viewPager.getCurrentItem()).go(url);
    }

    public void back()
    {
        getPageViewer(viewPager.getCurrentItem()).back();
    }

    public void forward()
    {
        getPageViewer(viewPager.getCurrentItem()).forward();
    }

    public String getCurrentUrl()
    {
        return getPageViewer(viewPager.getCurrentItem()).getUrl();
    }

    public String getCurrentTitle()
    {
        return getPageViewer(viewPager.getCurrentItem()).getTitle();
    }

    public int size()
    {
        return pages.size();
    }

    private PageViewerFragment getPageViewer(int position) {
        return (PageViewerFragment) ((FragmentStatePagerAdapter) viewPager.getAdapter()).getItem(position);
    }

    interface PagerInterface {
        void updateUrl(String url);
        void updateTitle(String title);
    }

}