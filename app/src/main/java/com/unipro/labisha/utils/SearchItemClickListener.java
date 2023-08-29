package com.unipro.labisha.utils;

import android.view.View;

import java.util.HashMap;

/**
 * Created by Kalaivanan on 3/3/2016.
 */
public interface SearchItemClickListener {
    public void onItemClick(View v, int position, String Tag);

    public void OnRecycleItemClick(HashMap<String, String> Code, String Desc);
}
