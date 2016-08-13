package com.github.bzsy.fishbowl;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.github.bzsy.fishbowl.util.DateFormat;
import com.github.bzsy.fishbowl.util.DebugLog;
import com.github.bzsy.fishbowl.activity.ImagesActivity;
import com.github.bzsy.fishbowl.sqlite.AirTemperatureEntityDBHelper;
import com.github.bzsy.fishbowl.sqlite.HumidityEntityDBHelper;
import com.github.bzsy.fishbowl.sqlite.ImageEntityDBHelper;
import com.github.bzsy.fishbowl.sqlite.LightEntityDBHelper;
import com.github.bzsy.fishbowl.sqlite.greendao.AirTemperatureEntity;
import com.github.bzsy.fishbowl.sqlite.greendao.HumidityEntity;
import com.github.bzsy.fishbowl.sqlite.greendao.ImageEntity;
import com.github.bzsy.fishbowl.sqlite.greendao.LightEntity;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * Created by bzsy on 16/1/7.
 */
public class MainActivity extends AppCompatActivity {
    @Bind(R.id.chart_temperature)
    LineChart chartTemperature;
    @Bind(R.id.chart_humidity)
    LineChart chartHumidity;
    @Bind(R.id.chart_light)
    LineChart chartLight;
    @Bind(R.id.pull_to_refresh)
    PtrFrameLayout pullToRefresh;

    private final int MAX_QUERY_NUM_FORM_SERVER = 1000;
    private final int MAX_QUERY_NUM_FORM_DB = 500000;

    private final int SELECT_TODAY = 0;
    private final int SELECT_THIS_WEEK = 1;
    private final int SELECT_THIS_MONTH = 2;
    private final int SELECT_ALL = 3;
    @Bind(R.id.btn_today)
    TextView btnToday;
    @Bind(R.id.btn_this_week)
    TextView btnThisWeek;
    @Bind(R.id.btn_this_month)
    TextView btnThisMonth;
    @Bind(R.id.btn_all)
    TextView btnAll;
    @Bind(R.id.iv_image)
    ImageView ivImage;
    @Bind(R.id.btn_more_image)
    ImageView btnMoreImage;

    private int curSelect = SELECT_TODAY;

    @OnClick(R.id.btn_today)
    void selectToday() {
        if (curSelect != SELECT_TODAY) {
            updateSelectorUI(SELECT_TODAY);
            updateUI();
        }
    }

    @OnClick(R.id.btn_this_week)
    void selectThisWeek() {
        if (curSelect != SELECT_THIS_WEEK) {
            updateSelectorUI(SELECT_THIS_WEEK);
            updateUI();
        }
    }

    @OnClick(R.id.btn_this_month)
    void selectThisMonth() {
        if (curSelect != SELECT_THIS_MONTH) {
            updateSelectorUI(SELECT_THIS_MONTH);
            updateUI();
        }
    }

    @OnClick(R.id.btn_all)
    void selectAll() {
        if (curSelect != SELECT_ALL) {
            updateSelectorUI(SELECT_ALL);
            updateUI();
        }
    }

    @OnClick(R.id.btn_more_image)
    void clickImage() {
        startActivity(new Intent(this, ImagesActivity.class));
    }

    private void updateSelectorUI(int newSelect) {
        switch (curSelect) {
            case SELECT_TODAY:
                btnToday.setTextColor(getResources().getColor(R.color.colorPrimary));
                btnToday.setBackgroundResource(android.R.color.transparent);
                break;
            case SELECT_THIS_WEEK:
                btnThisWeek.setTextColor(getResources().getColor(R.color.colorPrimary));
                btnThisWeek.setBackgroundResource(android.R.color.transparent);
                break;
            case SELECT_THIS_MONTH:
                btnThisMonth.setTextColor(getResources().getColor(R.color.colorPrimary));
                btnThisMonth.setBackgroundResource(android.R.color.transparent);
                break;
            case SELECT_ALL:
                btnAll.setTextColor(getResources().getColor(R.color.colorPrimary));
                btnAll.setBackgroundResource(android.R.color.transparent);
                break;
        }
        curSelect = newSelect;
        switch (newSelect) {
            case SELECT_TODAY:
                btnToday.setTextColor(Color.WHITE);
                btnToday.setBackgroundResource(R.color.colorPrimary);
                break;
            case SELECT_THIS_WEEK:
                btnThisWeek.setTextColor(Color.WHITE);
                btnThisWeek.setBackgroundResource(R.color.colorPrimary);
                break;
            case SELECT_THIS_MONTH:
                btnThisMonth.setTextColor(Color.WHITE);
                btnThisMonth.setBackgroundResource(R.color.colorPrimary);
                break;
            case SELECT_ALL:
                btnAll.setTextColor(Color.WHITE);
                btnAll.setBackgroundResource(R.color.colorPrimary);
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        initLineChart();
        final MaterialHeader header = new MaterialHeader(this);
        header.setPadding(0, 70, 0, 70);
        header.setColorSchemeColors(new int[]{getResources().getColor(R.color.colorPrimary)});
        header.setPtrFrameLayout(pullToRefresh);
        pullToRefresh.addPtrUIHandler(header);
        pullToRefresh.setHeaderView(header);
        pullToRefresh.disableWhenHorizontalMove(true);
        pullToRefresh.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                new GetPiDataAsyncTask().execute();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });
        updateUI();
        pullToRefresh.autoRefresh();
    }

    private void updateUI() {
        List<AirTemperatureEntity> airTemperatureEntities = null;
        List<LightEntity> lightEntities = null;
        List<HumidityEntity> humidityEntities = null;
        switch (curSelect) {
            case SELECT_TODAY:
                airTemperatureEntities = AirTemperatureEntityDBHelper.getInstance(this).loadTodayData();
                lightEntities = LightEntityDBHelper.getInstance(this).loadTodayData();
                humidityEntities = HumidityEntityDBHelper.getInstance(this).loadTodayData();
                break;
            case SELECT_THIS_WEEK:
                airTemperatureEntities = AirTemperatureEntityDBHelper.getInstance(this).loadThisWeekData();
                lightEntities = LightEntityDBHelper.getInstance(this).loadThisWeekData();
                humidityEntities = HumidityEntityDBHelper.getInstance(this).loadThisWeekData();
                break;
            case SELECT_THIS_MONTH:
                airTemperatureEntities = AirTemperatureEntityDBHelper.getInstance(this).loadThisMonthData();
                lightEntities = LightEntityDBHelper.getInstance(this).loadThisMonthData();
                humidityEntities = HumidityEntityDBHelper.getInstance(this).loadThisMonthData();
                break;
            case SELECT_ALL:
                airTemperatureEntities = AirTemperatureEntityDBHelper.getInstance(this).loadData(MAX_QUERY_NUM_FORM_DB);
                lightEntities = LightEntityDBHelper.getInstance(this).loadData(MAX_QUERY_NUM_FORM_DB);
                humidityEntities = HumidityEntityDBHelper.getInstance(this).loadData(MAX_QUERY_NUM_FORM_DB);
                break;
            default:
                break;
        }
        setChartTemperatureData(airTemperatureEntities);
        setChartHumidityData(humidityEntities);
        setChartLightData(lightEntities);

        ImageEntity imageEntity = ImageEntityDBHelper.getInstance(this).loadLastData();
        if (imageEntity != null && ivImage != null) {
            ImageLoader.getInstance().displayImage(imageEntity.getThumbnail_url(), ivImage);
            if (btnMoreImage != null) {
                ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (wifiInfo.isConnected()) {
                    btnMoreImage.setVisibility(View.VISIBLE);
                } else {
                    btnMoreImage.setVisibility(View.GONE);
                }
            }
        }
    }

    private Object getLastDataTime() {
        long time = 0;
        time = Math.max(time, AirTemperatureEntityDBHelper.getInstance(this).getLastDataTime());
        time = Math.max(time, LightEntityDBHelper.getInstance(this).getLastDataTime());
        time = Math.max(time, HumidityEntityDBHelper.getInstance(this).getLastDataTime());
        DebugLog.i("last data time " + time);
        return time;
    }

    private void setChartTemperatureData(List<AirTemperatureEntity> entities) {
        if (chartTemperature == null) {
            return;
        }
        if (entities != null && entities.size() >= 0) {
            ArrayList<String> xVals = new ArrayList<String>();
            ArrayList<Entry> temperatureEntries = new ArrayList<Entry>();
            ArrayList<Entry> heatIndexEntries = new ArrayList<Entry>();

            int i = 0;
            for (AirTemperatureEntity entity : entities) {
                xVals.add(DateFormat.longToTimeString(entity.getTime()));
                heatIndexEntries.add(new Entry(entity.getHeat_index(), i));
                temperatureEntries.add(new Entry(entity.getTemperature(), i++));
            }

            LineDataSet temperature = new LineDataSet(temperatureEntries, "Temperature");
            initLineDataSetStyle(temperature, getResources().getColor(R.color.blue));
            LineDataSet heatIndex = new LineDataSet(heatIndexEntries, "Heat Index");
            initLineDataSetStyle(heatIndex, Color.WHITE);
            ArrayList<LineDataSet> temperatureDataSets = new ArrayList<LineDataSet>();
            temperatureDataSets.add(temperature);
            temperatureDataSets.add(heatIndex);
            chartTemperature.setData(new LineData(xVals, temperatureDataSets));
        } else {
            chartTemperature.clear();
        }
        chartTemperature.invalidate();
    }

    private void setChartHumidityData(List<HumidityEntity> entities) {
        if (chartHumidity == null) {
            return;
        }
        if (entities != null && entities.size() >= 0) {
            ArrayList<Entry> humidityEntries = new ArrayList<Entry>();
            ArrayList<String> xVals = new ArrayList<String>();
            int i = 0;
            for (HumidityEntity entity : entities) {
                xVals.add(DateFormat.longToTimeString(entity.getTime()));
                humidityEntries.add(new Entry(entity.getHumidity(), i++));
            }

            LineDataSet humidity = new LineDataSet(humidityEntries, "Humidity");
            initLineDataSetStyle(humidity, Color.WHITE);
            ArrayList<LineDataSet> humidityDataSets = new ArrayList<LineDataSet>();
            humidityDataSets.add(humidity);
            chartHumidity.setData(new LineData(xVals, humidityDataSets));
        } else {
            chartHumidity.clear();
        }
        chartHumidity.invalidate();
    }

    private void setChartLightData(List<LightEntity> entities) {
        if (chartLight == null) {
            return;
        }
        if (entities != null && entities.size() >= 0) {
            ArrayList<Entry> lightEntries = new ArrayList<Entry>();
            ArrayList<String> xVals = new ArrayList<String>();

            int i = 0;
            for (LightEntity entity : entities) {
                xVals.add(DateFormat.longToTimeString(entity.getTime()));
                lightEntries.add(new Entry(entity.getLight(), i++));
            }
            LineDataSet light = new LineDataSet(lightEntries, "Light");
            initLineDataSetStyle(light, Color.WHITE);
            ArrayList<LineDataSet> lightDataSets = new ArrayList<LineDataSet>();
            lightDataSets.add(light);
            chartLight.setData(new LineData(xVals, lightDataSets));
        } else {
            chartLight.clear();
        }
        chartLight.invalidate();
    }

    private void initLineDataSetStyle(LineDataSet set, int color) {
        set.setLineWidth(1.5f);
        set.setCircleSize(1.3f);
        set.setColor(color);
        set.setCircleColor(color);
        set.setHighLightColor(color);
        set.setCircleColorHole(color);
        set.setValueTextColor(color);
    }

    private void initLineChart() {
        Chart[] charts = new Chart[]{chartTemperature, chartHumidity, chartLight};
        int index = 0;
        for (Chart chart : charts) {
            if (chart instanceof LineChart) {
                LineChart lineChart = (LineChart) chart;
                lineChart.getXAxis().setTextColor(Color.WHITE);
                lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
                lineChart.getXAxis().setDrawAxisLine(false);
                lineChart.getXAxis().setDrawGridLines(false);
                lineChart.getAxisLeft().setStartAtZero(false);
                lineChart.getAxisLeft().setTextColor(Color.WHITE);
                lineChart.getAxisLeft().setGridColor(Color.WHITE);
                lineChart.getAxisLeft().setDrawAxisLine(false);
                lineChart.getAxisLeft().setSpaceTop(20);
                lineChart.getAxisLeft().setSpaceBottom(20);
                lineChart.getAxisRight().setEnabled(false);
                lineChart.setDrawGridBackground(false);
                lineChart.setScaleYEnabled(false);
                lineChart.setPinchZoom(false);
                lineChart.setSelected(false);
                lineChart.setDescription("");
                lineChart.animateX(1000);
                lineChart.setHighlightPerTapEnabled(false);
                lineChart.setHighlightPerDragEnabled(false);
                lineChart.setDoubleTapToZoomEnabled(false);
                lineChart.setHardwareAccelerationEnabled(true);
                lineChart.setNoDataText("无数据");
                switch (index++) {
                    case 0:
                        lineChart.getLegend().setTextColor(Color.WHITE);
                        lineChart.setViewPortOffsets(150, 0, 100, 100);
                        break;
                    case 1:
                        lineChart.getLegend().setEnabled(false);
                        lineChart.setViewPortOffsets(150, 50, 100, 50);
                        break;
                    case 2:
                        lineChart.getLegend().setEnabled(false);
                        lineChart.setViewPortOffsets(150, 50, 100, 50);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private class GetPiDataAsyncTask extends AsyncTask<Void, Void, Integer> {
        private final int ERROR_CODE_NONE = 0;
        private final int ERROR_CODE_GET_DATA_FAIL = 1;
        private final int ERROR_CODE_WRONG_DATA = 2;

        @Override
        protected Integer doInBackground(Void... voids) {
            AVQuery<AVObject> query = new AVQuery<AVObject>("PiData");
            query.whereGreaterThan("Time", getLastDataTime());
            query.orderByDescending("updatedAt");
            query.limit(MAX_QUERY_NUM_FORM_SERVER);
            List<AVObject> avObjects = null;
            try {
                avObjects = query.find();
            } catch (AVException e) {
                e.printStackTrace();
            }
            if (avObjects != null) {
                DebugLog.i("查询到" + avObjects.size() + " 条符合条件的数据");
                try {
                    for (AVObject avObject : avObjects) {
                        if (!avObject.has("Time")) {
                            continue;
                        }
                        long time = avObject.getLong("Time");
                        if (avObject.has("Humidity")) {
                            float humidity = Float.valueOf(avObject.getString("Humidity"));
                            HumidityEntityDBHelper.getInstance(MainActivity.this).save(
                                    new HumidityEntity(null, time, humidity,
                                            HumidityEntityDBHelper.DEVICE_BOWL)
                            );
                        }
                        if (avObject.has("Light")) {
                            int light = Integer.valueOf(avObject.getString("Light"));
                            LightEntityDBHelper.getInstance(MainActivity.this).save(
                                    new LightEntity(null, time, light, LightEntityDBHelper.DEVICE_BOWL)
                            );
                        }
                        if (avObject.has("Temperature")) {
                            float temperature = Float.valueOf(avObject.getString("Temperature"));
                            if (avObject.has("HeatIndex")) {
                                float heatIndex = Float.valueOf(avObject.getString("HeatIndex"));
                                AirTemperatureEntityDBHelper.getInstance(MainActivity.this).save(
                                        new AirTemperatureEntity(null, time, temperature, heatIndex,
                                                AirTemperatureEntityDBHelper.DEVICE_BOWL)
                                );
                            }
                        }
                        if (avObject.has("Image")) {
                            AVFile imageFile = avObject.getAVFile("Image");
                            ImageEntityDBHelper.getInstance(MainActivity.this).save(
                                    new ImageEntity(null, time, imageFile.getUrl(),
                                            imageFile.getThumbnailUrl(true, 1024 / 2, 768 / 2),
                                            ImageEntityDBHelper.DEVICE_BOWL)
                            );
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return ERROR_CODE_WRONG_DATA;
                }
                return ERROR_CODE_NONE;
            } else {
                return ERROR_CODE_GET_DATA_FAIL;
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            switch (integer) {
                case ERROR_CODE_NONE:
                    updateUI();
                    break;
                case ERROR_CODE_GET_DATA_FAIL:
                    Toast.makeText(MainActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                    break;
                case ERROR_CODE_WRONG_DATA:
                    Toast.makeText(MainActivity.this, "数据错误", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            if (pullToRefresh != null) {
                pullToRefresh.refreshComplete();
            }
        }
    }
}
