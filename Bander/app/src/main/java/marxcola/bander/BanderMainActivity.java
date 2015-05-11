package marxcola.bander;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.BandIOException;
import com.microsoft.band.BandPendingResult;
import com.microsoft.band.ConnectionState;


public class BanderMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bander_main);

        final Button button = (Button) findViewById(R.id.insideTempButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                new BandCommunicator().execute();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bander_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class BandCommunicator extends AsyncTask<String, String, String> {
        BandClient bandClient = null;
        Integer outsideTemp;
        Integer bodytemp;

        @Override
        protected void onPreExecute() {
            BandInfo[] pairedBands = BandClientManager.getInstance().getPairedBands();
            if(pairedBands.length > 0) {
                bandClient = BandClientManager.getInstance().create(BanderMainActivity.this, pairedBands[0]);
            }
        }

        @Override
        protected String doInBackground(String... arg0) {
            if(bandClient != null)
            {
                try
                {
                    BandPendingResult<ConnectionState> pendingResult = bandClient.connect();

                    try {
                        ConnectionState state = pendingResult.await();

                        if(state == ConnectionState.CONNECTED) {
                            bodytemp = 35;

                            this.publishProgress(bodytemp.toString());
                        }
                        else {
                         // do work on failure
                        }
                    }
                    catch(InterruptedException ex) {
                        // handle InterruptedException
                    }
                    catch(BandException ex) {
                        // handle BandException
                    }

                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            return null;
        }

        protected void onPostExecute(Void result) {

        }

        protected void onProgressUpdate(String... values){
            super.onProgressUpdate(values);

            TextView display = (TextView) findViewById(R.id.bodyTempField);

            display.setText(values[0]);

        }
    }
}
