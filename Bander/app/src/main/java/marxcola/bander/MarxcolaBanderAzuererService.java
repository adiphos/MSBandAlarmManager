package marxcola.bander;

import android.app.IntentService;
import android.content.Intent;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.*;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

/**
 * Created by Arghyadip on 5/30/2015.
 */
public class MarxcolaBanderAzuererService extends IntentService {

    protected static CloudTableClient tableClient;
    protected static CloudTable table;
    protected static boolean azureInitialized;

    protected final static String tableName = "marxcolabanderbodytemparaturetable";

    public MarxcolaBanderAzuererService() {
        super("MarxcolaBanderAzuererService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Initialize Azure.
        String connectionString = intent.getExtras().getString("Conn_String");

        try{
            initializeAzure(connectionString);
        }
        catch (Throwable t){

        }

        return super.onStartCommand(intent,flags,startId);
    }
    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        // Initialize Azure.
        String connectionString = intent.getExtras().getString("Conn_String");
        String temparatureAsString = intent.getExtras().getString("Current_Body_temp");

        try{
            initializeAzure(connectionString);
            insertCurrentTemparature(temparatureAsString);
        }
        catch (Throwable t){

        }
    }

    private static void initializeAzure(String connectionString) throws StorageException, URISyntaxException, InvalidKeyException
    {
        if(!azureInitialized)
        {
            CloudStorageAccount account = CloudStorageAccount.parse(connectionString);

            // Create a table service client.
            tableClient = account.createCloudTableClient();

            // Retrieve a reference to a table.
            table = tableClient.getTableReference(tableName);

            // Create the table if it doesn't already exist.
            table.createIfNotExists();

            azureInitialized = true;
        }
    }

    private static void insertCurrentTemparature(String currentTemp)throws StorageException {

        BodyTemparatureEntity bodyTemp = new BodyTemparatureEntity(currentTemp);

        TableOperation insertTemparature = TableOperation.insert(bodyTemp);

        // Submit the operation to the table service.
        table.execute(insertTemparature);
    }
}
