package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.threads.BluetoothConnectionThread;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
{
    private BluetoothAdapter bluetoothAdapter = null;
    private BluetoothSocket bluetoothSocket = null;

    private static final int ENABLE_BLUETOOTH_INTENT_REQUEST = 1;
    private static final UUID BLUETOOTH_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static String address = null;
    private static boolean activate =  false;

    private Button mConnectButton;
    private Button mDisconnectButton;

    private ImageView mColorPicker;
    private TextView mColorValues;
    private View mColorPreview;

    private Bitmap bitmap;

    private static BluetoothConnectionThread connectionThread;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        for(BluetoothDevice device : devices)
        {
            if (device.getName().equals("HC05"))
            {
                address = device.getAddress();
            }
        }

        Toast.makeText(this, address, Toast.LENGTH_SHORT).show();

        mConnectButton = findViewById(R.id.connectButton);
        mDisconnectButton = findViewById(R.id.disconnectButton);

        mColorPicker = findViewById(R.id.colorPicker);
        mColorPreview = findViewById(R.id.colorPreview);
        mColorValues = findViewById(R.id.displayValues);

        mConnectButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(MainActivity.this, "Connect click", Toast.LENGTH_LONG).show();
                activate = true;
                onResume();
            }
        });



        mDisconnectButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(MainActivity.this, "Disconnect click", Toast.LENGTH_LONG).show();

                try
                {
                    bluetoothSocket.close();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        });

        mColorPicker.setDrawingCacheEnabled(true);
        mColorPicker.buildDrawingCache(true);
        mColorPicker.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE)
                {
                    bitmap = mColorPicker.getDrawingCache();
                    int pixel = bitmap.getPixel((int) event.getX(), (int) event.getY());

                    int r = Color.red(pixel);
                    int g = Color.green(pixel);
                    int b = Color.blue(pixel);

                    String hex = "#" + Integer.toHexString(pixel);
                    mColorPreview.setBackgroundColor(Color.rgb(r, g, b));
                    mColorValues.setText("RGB: " + r + ", " + g + ", " + b + "\nHEX: " + hex);

                    updateColor(r, g, b);
                }

                return true;
            }
        });

        checkBluetooth();
    }

    private void updateColor(int r, int g, int b)
    {
        connectionThread.write("R");
        connectionThread.write(r);
        connectionThread.write("G");
        connectionThread.write(g);
        connectionThread.write("B");
        connectionThread.write(b);
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException
    {
        return device.createRfcommSocketToServiceRecord(BLUETOOTH_MODULE_UUID);
    }

    private void checkBluetooth()
    {
        if (!bluetoothAdapter.isEnabled())
        {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, ENABLE_BLUETOOTH_INTENT_REQUEST);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(activate)
        {
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
            try
            {
                bluetoothSocket = createBluetoothSocket(device);
            }
            catch(IOException e)
            {
                Toast.makeText(getBaseContext(), "Something went wrong while creating the socket", Toast.LENGTH_LONG).show();
            }

            try
            {
                bluetoothSocket.connect();
            }
            catch(IOException e)
            {
                try
                {
                    bluetoothSocket.close();
                }
                catch(IOException ignored)
                {

                }
            }

            connectionThread = new BluetoothConnectionThread(bluetoothSocket, this);
            connectionThread.start();
        }
    }
}