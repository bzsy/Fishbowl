package com.github.bzsy.fishbowl.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.github.bzsy.fishbowl.R;
import com.github.bzsy.fishbowl.adapter.ImageRecyclerViewAdapter;
import com.github.bzsy.fishbowl.sqlite.ImageEntityDBHelper;
import com.github.bzsy.fishbowl.sqlite.greendao.ImageEntity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by bzsy on 16/1/12.
 */
public class ImagesActivity extends AppCompatActivity {
    @Bind(R.id.rv_images)
    RecyclerView rvImages;
    private ImageRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        List<ImageEntity> imageEntities = ImageEntityDBHelper.getInstance(this).loadData(100);
        rvImages.setLayoutManager(new LinearLayoutManager(this));
        if (adapter == null) {
            adapter = new ImageRecyclerViewAdapter(this, imageEntities);
        }
        rvImages.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
