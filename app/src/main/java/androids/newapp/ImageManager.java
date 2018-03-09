package androids.newapp;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.LinkedList;

public class ImageManager {

    public static final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=applicationsimage;AccountKey=7TwfoMD9uTnDDdn7sOAg5Z3JJKV8APSyQ8uxjKrTulUlv/2bIe0j0Lm72oQ9yKVtnMVF7b6IegEbJKP1N2zVAA==;EndpointSuffix=core.windows.net";

    private static CloudBlobContainer getContainer() throws Exception {
        CloudStorageAccount storageAccount = CloudStorageAccount
                .parse(storageConnectionString);
        CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
        CloudBlobContainer container = blobClient.getContainerReference("images");

        return container;
    }

    public static String UploadImage(InputStream image, int imageLength) throws Exception {
        CloudBlobContainer container = getContainer();
        container.createIfNotExists();
        String imageName = randomString(10);
        CloudBlockBlob imageBlob = container.getBlockBlobReference(imageName);
        imageBlob.upload(image, imageLength);
        return imageName;
    }

    public static void GetImage(String name, OutputStream imageStream, long imageLength) throws Exception {
        CloudBlobContainer container = getContainer();
        CloudBlockBlob blob = container.getBlockBlobReference(name);
        if(blob.exists()){
            blob.downloadAttributes();
            imageLength = blob.getProperties().getLength();
            blob.download(imageStream);
        }
    }

    static final String validChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static SecureRandom rnd = new SecureRandom();

    static String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( validChars.charAt( rnd.nextInt(validChars.length()) ) );
        return sb.toString();
    }

}

