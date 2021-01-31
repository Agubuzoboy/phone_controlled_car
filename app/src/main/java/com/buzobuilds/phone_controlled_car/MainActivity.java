package com.buzobuilds.phone_controlled_car;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    //ui componets
    Button goButton;
    Button stopButton;
    Button turnButton;
    Button discoverButton;
    Button refreshButton;
    ListView discoverList;
    ListView pairedList;



    //bluetooth adapter
    BluetoothAdapter bluetoothAdapter;

    ArrayList<BluetoothDevice> pairedDevices = new ArrayList<BluetoothDevice>();
    ArrayList<String> pairedDeviceNames = new ArrayList<String>();
    ArrayAdapter<String> pairedAdapter;

    ArrayList<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();
    ArrayList<String> discoveredDevicesNames = new ArrayList<String>();
    ArrayAdapter<String> discoveredAdapter;
    //socket and stream
    BluetoothSocket socket;
    OutputStream deviceOS;

    private final BroadcastReceiver recevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                discoveredDevices.add(device);
                if(device.getName() == null){
                    discoveredDevicesNames.add(device.getAddress());
                }
                else{
                    discoveredDevicesNames.add(device.getName());
                }
                discoveredAdapter.notifyDataSetChanged();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up ui ref
        goButton =  findViewById(R.id.goButton);
        stopButton = findViewById(R.id.stopButton);
        turnButton = findViewById(R.id.turnButton);
        discoverButton = findViewById(R.id.discoveryButton);
        refreshButton = findViewById(R.id.refreshButton);
        discoverList = findViewById(R.id.discoverList);
        pairedList = findViewById(R.id.pairedList);

        //get bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //get and list paired devices
        Set<BluetoothDevice> pairedDeviceSet = bluetoothAdapter.getBondedDevices();
        for(BluetoothDevice device : pairedDeviceSet){
            pairedDevices.add(device);
            pairedDeviceNames.add(device.getName());
        }
        pairedAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,
                pairedDeviceNames);
        pairedList.setAdapter(pairedAdapter);

        pairedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice deviceToConectTo = pairedDevices.get(position);
                try{
                    socket = deviceToConectTo.createInsecureRfcommSocketToServiceRecord(deviceToConectTo.getUuids()[0].getUuid());
                    bluetoothAdapter.cancelDiscovery();
                    socket.connect();
                    deviceOS = socket.getOutputStream();

                    Toast.makeText(getApplicationContext(), "Connected to " + deviceToConectTo.getName(), Toast.LENGTH_LONG).show();
                } catch(IOException e){
                    Toast.makeText(getApplicationContext(), "Failed to connected to " + deviceToConectTo.getName(), Toast.LENGTH_LONG).show();
                    try{
                        socket.close();
                    }catch(IOException e2){
                        Toast.makeText(getApplicationContext(), "Failed to close socket", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        //I am typing in dummy code to make it look like I am programming, because i didnt record my self actually programming
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(recevier, filter);

        discoveredAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, discoveredDevicesNames);
        discoverList.setAdapter(discoveredAdapter);

        discoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothAdapter.startDiscovery();
                Toast.makeText(getApplicationContext(), "Discovery Started", Toast.LENGTH_SHORT).show();
            }
        });

       //enable user to pair with car
       discoverList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @RequiresApi(api = Build.VERSION_CODES.KITKAT)
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              BluetoothDevice deviceToPairTo  = discoveredDevices.get(position);

              boolean res = deviceToPairTo.createBond();
              if(res){
                  Toast.makeText(getApplicationContext(),"pairing to "+ deviceToPairTo.getName(), Toast.LENGTH_LONG).show();
              }
              else{
                  Toast.makeText(getApplicationContext(), "pairing failed", Toast.LENGTH_LONG).show();
              }
          }
      });
//Ill just write another comment over here lol




        
       refreshButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
               Set<BluetoothDevice> pairedDeviceSet = bluetoothAdapter.getBondedDevices();
               pairedDevices.clear();
               pairedDeviceNames.clear();

               for(BluetoothDevice device : pairedDeviceSet){
                   pairedDevices.add(device);
                   pairedDeviceNames.add(device.getName());
               }
               pairedAdapter.notifyDataSetChanged();
           }
       });

       goButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               try{
                   deviceOS.write(1);
                   Toast.makeText(getApplicationContext(), "1 sent", Toast.LENGTH_SHORT).show();
               }
               catch(IOException e){
                   Toast.makeText(getApplicationContext(), "failed to send 1", Toast.LENGTH_SHORT).show();
               }
           }
       });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    deviceOS.write(0);
                    Toast.makeText(getApplicationContext(), "0 sent", Toast.LENGTH_SHORT).show();
                }
                catch(IOException e){
                    Toast.makeText(getApplicationContext(), "failed to send 0", Toast.LENGTH_SHORT).show();
                }
            }
        });

        turnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    deviceOS.write(2);
                    Toast.makeText(getApplicationContext(), "2 sent", Toast.LENGTH_SHORT).show();
                }
                catch(IOException e){
                    Toast.makeText(getApplicationContext(), "failed to send 2", Toast.LENGTH_SHORT).show();
                }
            }
        });






    }
}