package com.unipro.labisha.Screens.purchase;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.unipro.labisha.R;
import com.unipro.labisha.adapter.ViewPagerAdapter;
import com.unipro.labisha.fragments.CompareFragment;
import com.unipro.labisha.fragments.InvoiceFragment;
import com.unipro.labisha.fragments.SummaryFragment;
import com.unipro.labisha.fragments.SupplierFragment;


/**
 * Created by kalaivanan on 07/03/2018.
 */
public class PurchaseOrderActivity extends AppCompatActivity {
    ImageView imgGrpMenu,imgGrpBack,imggrpClose;
    TextView txtMenuTitle;
    public TabLayout tabLayout;
    public ViewPager viewPager;
    ViewPagerAdapter adapter;
    String sTag="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchaseorder);
        txtMenuTitle=(TextView)findViewById(R.id.txtMenuTitle);
        imgGrpMenu=(ImageView)findViewById(R.id.grpthreeMenu);
        imgGrpBack=(ImageView)findViewById(R.id.imgGrpBack);
        imggrpClose=(ImageView)findViewById(R.id.grpClose);
        tabLayout = (TabLayout)findViewById(R.id.tabs);
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        imggrpClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        txtMenuTitle.setText("Purchase Order");
        onNewIntent(getIntent());
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }
    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        if (!sTag.equalsIgnoreCase("PoVerification")) {
            adapter.addFragment(new SupplierFragment(), "Supplier");
        }
        adapter.addFragment(new InvoiceFragment(), "Add Item");
        if (!sTag.equalsIgnoreCase("PoVerification")) {
            adapter.addFragment(new SummaryFragment(), "Summary");
        }else{
            adapter.addFragment(new CompareFragment(), "Summary");
        }

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onNewIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("Tag")) {
                sTag=extras.getString("Tag");
                txtMenuTitle.setText(sTag);
            }
        }
    }
    public void switchFragment(int mTarget) {
        try {
            viewPager.setCurrentItem(mTarget);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
