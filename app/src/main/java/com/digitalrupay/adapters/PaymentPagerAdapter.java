package com.digitalrupay.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.digitalrupay.activities.Customer.fragment.PaymentHistoryFragment;

import java.util.HashMap;

/**
 * Created by Audlink_sri on 12/12/2016.
 */

public class PaymentPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    private HashMap<Integer, Fragment> mPageReferenceMap;

    public PaymentPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        mPageReferenceMap = new HashMap<>();
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                PaymentHistoryFragment paymentHistoryFragment = new PaymentHistoryFragment();
                mPageReferenceMap.put(position, paymentHistoryFragment);
                return paymentHistoryFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    /* To remove inactive fragments from mPageReferenceMap we need to implement destroyItem callback */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        mPageReferenceMap.remove(position);
    }

    public Fragment getFragment(int key) {
        return mPageReferenceMap.get(key);
    }
}
