package com.example.orderingproject;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class VPAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> items;
    private ArrayList<String> itext = new ArrayList<String>();

    public VPAdapter(FragmentManager fm) {
        super(fm);
        items = new ArrayList<Fragment>();
        items.add(new KoreanFoodFragment());
        items.add(new SnackBarFragment());
        items.add(new CafeFragment());
        items.add(new JapaneseFoodFragment());
        items.add(new ChickenFragment());
        items.add(new PizzaFragment());
        items.add(new AsianWesternFoodFragment());
        items.add(new ChineseFoodFragment());
        items.add(new JokbalBossamFragment());
        items.add(new SoupFragment());
        items.add(new FastFoodFragment());



        itext.add("한식");
        itext.add("분식");
        itext.add("카페·디저트");
        itext.add("돈까스·회·초밥");
        itext.add("치킨");
        itext.add("피자");
        itext.add("아시안·양식");
        itext.add("중식");
        itext.add("족발·보쌈");
        itext.add("찜·탕");
        itext.add("패스트푸드");

    }

    @Override
    public Fragment getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return itext.get(position);
    }
}
