package com.unipro.labisha.utils;

import android.view.View;

/**
 * Created by Kalaivanan on 3/3/2016.
 */
public interface CustomItemClickListener {
    public void onItemClick(View v, int position, String Tag);

    public void OnRecycleItemClick(String Code, String Desc);
}
