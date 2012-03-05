package fr.kaplux;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class HelloAndroidActivity extends Activity {
	
	// Create a BroadcastReceiver for ACTION_FOUND
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    public void onReceive(Context context, Intent intent) {

	        String action = intent.getAction();
	        // When discovery finds a device
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            // Get the BluetoothDevice object from the Intent
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            // Add the name and address to an array adapter to show in a ListView
	            Toast.makeText(getApplicationContext(), device.getName() + " "+ intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI), Toast.LENGTH_SHORT).show();

	        }
	    }
	};
	
    private static final int REQUEST_ENABLE_BT = 3;

    private static String TAG = "bluetoothRadar";
    
    
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after 
     * previously being shut down then this Bundle contains the data it most 
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
        setContentView(R.layout.main);
     
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "no bluetooth",  Toast.LENGTH_LONG).show();
        }
        else{
        	if (!mBluetoothAdapter.isEnabled()) {
        	    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        	    startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
        	}

        	// Register the BroadcastReceiver
        	IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        	registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
        	
        	ensureDiscoverable();
        	new AsyncTask() {


				@Override
				protected Object doInBackground(Object... params) {
				
					while(true){
					mBluetoothAdapter.cancelDiscovery();
	        		mBluetoothAdapter.startDiscovery();
	        		
	        		try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {

		        		Toast.makeText(getApplicationContext(), "scan",  Toast.LENGTH_LONG).show();
					}
	        	
					}
	        	//return null;
				}
			}.execute(null);
        
        }
    }
    
    private void ensureDiscoverable() {
     
        if (mBluetoothAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}
    
    

}

