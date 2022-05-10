package com.example.orderingproject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

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
