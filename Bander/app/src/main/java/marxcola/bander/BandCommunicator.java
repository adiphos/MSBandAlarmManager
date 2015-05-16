package marxcola.bander;

import android.os.AsyncTask;
import android.widget.TextView;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.BandPendingResult;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.sensors.BandSkinTemperatureEvent;
import com.microsoft.band.sensors.BandSkinTemperatureEventListener;

/**
 * Created by Arghyadip on 5/16/2015.
 */
public class BandCommunicator  extends AsyncTask<String, String, String> implements BandSkinTemperatureEventListener {
    BandClient bandClient = null;
    Integer outsideTemp;
    BanderMainActivity mainActivity;

    public BandCommunicator(BanderMainActivity activity)
    {
        mainActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        BandInfo[] pairedBands = BandClientManager.getInstance().getPairedBands();
        if(pairedBands.length > 0) {
            bandClient = BandClientManager.getInstance().create(mainActivity, pairedBands[0]);
        }
    }

    public void onBandSkinTemperatureChanged(BandSkinTemperatureEvent temperatureEvent)
    {
        float bodyTemp = temperatureEvent.getTemperature();
        String temparatureAsString = Float.toString(bodyTemp);
        this.publishProgress(temparatureAsString);
    }

    @Override
    protected String doInBackground(String... arg0) {
        if (bandClient != null) {
            try {
                BandPendingResult<ConnectionState> pendingResult = bandClient.connect();

                try {
                    ConnectionState state = pendingResult.await();

                    if (state == ConnectionState.CONNECTED) {
                        bandClient.getSensorManager().registerSkinTemperatureEventListener(this);
                    } else {
                        this.publishProgress("Not Connected");
                    }
                } catch (InterruptedException ex) {
                    // handle InterruptedException
                } catch (BandException ex) {
                    // handle BandException
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }

    protected void onPostExecute(Void result) {

    }

    protected void onProgressUpdate(String... values){
        super.onProgressUpdate(values);

        TextView display = (TextView)mainActivity.findViewById(R.id.bodyTempField);

        display.setText(values[0]);

        //try {
        //bandClient.getSensorManager().unregisterSkinTemperatureEventListeners();
        //}
        //catch (BandException ex) {
        // handle BandException
        //}
    }
}
