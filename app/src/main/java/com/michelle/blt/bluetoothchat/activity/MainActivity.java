package com.michelle.blt.bluetoothchat.activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.michelle.blt.bluetoothchat.adapter.RecyclerBlueToothAdapter;
import com.michelle.blt.bluetoothchat.bean.BlueTooth;
import com.michelle.blt.bluetoothchat.receiver.BlueToothReceiver;
import com.michelle.blt.bluetoothchat.service.BluetoothChatService;
import com.michelle.blt.bluetoothchat.util.ToastUtil;
import com.michelle.blt.bluetoothchat.vinterface.BlueToothInterface;
import com.rdc.zzh.bluetoothchat.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, BlueToothInterface, RecyclerBlueToothAdapter.OnItemClickListener {
    private static final String TAG = "MainActivity";
    public static final int BLUE_TOOTH_DIALOG = 0x111;
    public static final int BLUE_TOOTH_TOAST = 0x123;
    public static final int BLUE_TOOTH_WRAITE = 0X222;
    public static final int BLUE_TOOTH_READ = 0X333;
    public static final int BLUE_TOOTH_SUCCESS = 0x444;

    private RecyclerView recyclerView;
    private Switch st;
    private BluetoothAdapter mBluetoothAdapter;
    private Timer timer;
    private WifiTask task;
    private RecyclerBlueToothAdapter recyclerAdapter;
    private List<BlueTooth> list = new ArrayList<>();
    private ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    BlueToothReceiver mReceiver;

    private BluetoothChatService mBluetoothChatService;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BluetoothAdapter.STATE_ON:
                case BluetoothAdapter.STATE_OFF: {
                    if (msg.what == BluetoothAdapter.STATE_ON) {
                        st.setText("蓝牙已开启");
                        Log.e(TAG, "onCheckedChanged: startIntent");
                        //自动刷新
                        swipeRefreshLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(true);
                                onRefreshListener.onRefresh();
                            }
                        }, 300);
                        //开启socket监听
                        mBluetoothChatService = BluetoothChatService.getInstance(handler);
                        mBluetoothChatService.start();
                    } else if (msg.what == BluetoothAdapter.STATE_OFF) {
                        st.setText("蓝牙已关闭");
                        recyclerAdapter.setWifiData(null);
                        recyclerAdapter.notifyDataSetChanged();
                        mBluetoothChatService.stop();
                    }
                    timer.cancel();
                    timer = null;
                    task = null;
                    st.setClickable(true);
                }
                break;
                case BLUE_TOOTH_DIALOG: {
                    showProgressDialog((String) msg.obj);
                }
                break;
                case BLUE_TOOTH_TOAST: {
                    dismissProgressDialog();
                    ToastUtil.showText(MainActivity.this, (String) msg.obj);
                }
                break;
                case BLUE_TOOTH_SUCCESS: {
                    dismissProgressDialog();
                    ToastUtil.showText(MainActivity.this, "连接设备" + (String) msg.obj + "成功");
                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                    intent.putExtra(ChatActivity.DEVICE_NAME_INTENT, (String) msg.obj);
                    startActivity(intent);
                    //关闭其他资源
                    close();

                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        st = (Switch) findViewById(R.id.st);

        st.setOnCheckedChangeListener(this);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        recyclerAdapter = new RecyclerBlueToothAdapter(this);
        recyclerAdapter.setWifiData(list);
        recyclerAdapter.setOnItemClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdapter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); //获取本地蓝牙实例
        //判断蓝牙是否开启来设置状态
        if (mBluetoothAdapter.isEnabled()) {
            //已经开启
            st.setChecked(true);
            st.setText("蓝牙已开启");
        } else {
            st.setChecked(false);
            st.setText("蓝牙已关闭");
        }
        mReceiver = new BlueToothReceiver(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PAIRING_REQUEST);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        if (mBluetoothAdapter.isEnabled()) {
            Log.e(TAG, "onResume: resumeStart");
            mBluetoothChatService = BluetoothChatService.getInstance(handler);
            mBluetoothChatService.start();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked == true) {
            if (mBluetoothAdapter.getState() != BluetoothAdapter.STATE_ON) {
                mBluetoothAdapter.enable();  //打开蓝牙
                st.setText("正在开启蓝牙");
                ToastUtil.showText(this, "正在开启蓝牙");
            }
        } else {
            if (mBluetoothAdapter.getState() != BluetoothAdapter.STATE_OFF) {
                mBluetoothAdapter.disable();  //打开蓝牙
                st.setText("正在关闭Wifi");
                ToastUtil.showText(this, "正在关闭蓝牙");
            }
        }
        st.setClickable(false);
        if (timer == null || task == null) {
            timer = new Timer();
            task = new WifiTask();
            task.setChecked(isChecked);
            timer.schedule(task, 0, 1000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        close();
        mBluetoothChatService.stop();
    }

    private void close() {
        if (timer != null)
            timer.cancel();
        //取消扫描
        mBluetoothAdapter.cancelDiscovery();
        swipeRefreshLayout.setRefreshing(false);
        unregisterReceiver(mReceiver);
    }

    /**
     * RecyclerView Item 点击处理
     *
     * @param position
     */
    @Override
    public void onItemClick(int position) {
        showProgressDialog("正在进行连接");
        BlueTooth blueTooth = list.get(position);
        connectDevice(blueTooth.getMac());
    }

    /**
     * 根据mac地址，连接目标蓝牙
     *
     * @param mac
     */
    String pwd = "1234";

    private void connectDevice(String mac) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mac);
//       if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
//          ClsUtils.pair(mac, pwd);
//       }
        //传入要连接的蓝牙mac地址和配对码，利用反射方法调用进行配对
        mBluetoothChatService.connectDevice(device);
    }

    /**
     * 进度对话框
     *
     * @param msg
     */
    public void showProgressDialog(String msg) {
        if (progressDialog == null)
            progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * 计时器，去发送是否要开启或关闭蓝牙
     */
    private class WifiTask extends TimerTask {
        private boolean isChecked;

        public void setChecked(boolean isChecked) {
            this.isChecked = isChecked;
        }

        @Override
        public void run() {
            if (isChecked) {
                if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON)
                    handler.sendEmptyMessage(BluetoothAdapter.STATE_ON);
            } else {
                if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF)
                    handler.sendEmptyMessage(BluetoothAdapter.STATE_OFF);
            }
        }
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {

            if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                list.clear();
                //扫描的是已配对的
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    list.add(new BlueTooth("已配对的设备", BlueTooth.TAG_TOAST));
                    for (BluetoothDevice device : pairedDevices) {
                        Log.e(TAG, device.getName() + "\n" + device.getAddress());
                        list.add(new BlueTooth(device.getName(), device.getAddress(), ""));
                    }
                    list.add(new BlueTooth("已扫描的设备", BlueTooth.TAG_TOAST));
                } else {
                    ToastUtil.showText(getApplicationContext(), "没有找到已匹对的设备！");
                    list.add(new BlueTooth("已扫描的设备", BlueTooth.TAG_TOAST));
                }
                recyclerAdapter.notifyDataSetChanged();
                //开始扫描设备
                mBluetoothAdapter.startDiscovery();
                ToastUtil.showText(MainActivity.this, "开始扫描设备");
            } else {
                swipeRefreshLayout.setRefreshing(false);
                ToastUtil.showText(MainActivity.this, "请开启蓝牙");
            }
        }
    };

    //数据保存
    public void save(View view) {
     /*   if(list  != null){
            SQLiteDatabase db = sqlHelper.getWritableDatabase();
            int row = Integer.parseInt(etRow.getText().toString());
            int line = Integer.parseInt(etLine.getText().toString());
            //数据保存格式
            StringBuffer sb = new StringBuffer();
            sb.append("(");
            for(BlueTooth blueTooth : list){
                sb.append(blueTooth.getName() + " : " + blueTooth.getRssi());
                sb.append(" , ");
            }
            sb.replace(sb.toString().length() - 2 , sb.toString().length() - 1 , ")");
            //是否有对应的记录
            Cursor cursor = db.query("blue_tooth_table", null, "id=?", new String[]{line + ""}, null, null, null);
            //表示一开始没有数据，则插入一条数据
            if(!cursor.moveToNext()){
                ContentValues contentValues = new ContentValues();
                contentValues.put("id" , line);
                contentValues.put("i" + row , sb.toString());
                db.insert("blue_tooth_table" , null , contentValues);
            }else{
                ContentValues contentValues = new ContentValues();
                contentValues.put("i" + row, sb.toString());
                String [] whereArgs = {String.valueOf(line)};
                db.update("blue_tooth_table" , contentValues , "id=?" , whereArgs);
            }
            Toast.makeText(MainActivity.this , "保存成功" , Toast.LENGTH_SHORT).show();
        }*/
    }

    /**
     * 扫描设备回调监听
     *
     * @param device
     * @param rssi
     */
    @Override
    public void getBlutToothDevices(BluetoothDevice device, int rssi) {
        if (device != null && !TextUtils.isEmpty(device.getName())) {
            Log.e("device found==", device.getName());
            if (!list.contains(device.getAddress())) {
                list.add(new BlueTooth(device.getName(), device.getAddress(), rssi + ""));
            }
            //更新UI
            recyclerAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void searchFinish() {
        swipeRefreshLayout.setRefreshing(false);
        ToastUtil.showText(MainActivity.this, "扫描完成");
    }


}
