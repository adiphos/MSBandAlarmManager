package marxcola.bander;

import com.microsoft.azure.storage.table.TableServiceEntity;
/**
 * Created by Arghyadip on 5/30/2015.
 */
public class BodyTemparatureEntity extends TableServiceEntity {

    public BodyTemparatureEntity(String temparature) {
        this.partitionKey = "marxcolabanderbodytemp";
        this.rowKey = temparature;
    }

    public BodyTemparatureEntity() {
    }
}
