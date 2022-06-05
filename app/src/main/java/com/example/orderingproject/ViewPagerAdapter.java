package com.example.orderingproject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.orderingproject.review.StoreReviewFragment;
import com.example.orderingproject.stores.AsianWesternFoodFragment;
import com.example.orderingproject.stores.CafeFragment;
import com.example.orderingproject.stores.ChickenFragment;
import com.example.orderingproject.stores.ChineseFoodFragment;
import com.example.orderingproject.stores.FastFoodFragment;
import com.example.orderingproject.stores.JapaneseFoodFragment;
import com.example.orderingproject.stores.JokbalBossamFragment;
import com.example.orderingproject.stores.KoreanFoodFragment;
import com.example.orderingproject.stores.PizzaFragment;
import com.example.orderingproject.stores.SnackBarFragment;
import com.example.orderingproject.stores.SoupFragment;

public class  ViewPagerAdapter extends FragmentStateAdapter {
    int num, count;
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, int num, int count) {
        super(fragmentActivity);
        this.num = num;
        this.count = count;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (num == 0) {
            switch (position) {
                case 0:
                    return new KoreanFoodFragment();
                case 1:
                    return new SnackBarFragment();
                case 2:
                    return new CafeFragment();
                case 3:
                    return new JapaneseFoodFragment();
                case 4:
                    return new ChickenFragment();
                case 5:
                    return new PizzaFragment();
                case 6:
                    return new AsianWesternFoodFragment();
                case 7:
                    return new ChineseFoodFragment();
                case 8:
                    return new JokbalBossamFragment();
                case 9:
                    return new SoupFragment();
                default:
                    return new FastFoodFragment();
            }
        }

        else {
            switch (position) {
                case 0:
                    return new MenuListFragment();
                case 1:
                    return new StoreInfoFragment();
                default:
                    return new StoreReviewFragment();
            }
        }

    }

    @Override
    public int getItemCount() {
        return count;
    }
}
