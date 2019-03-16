package edu.neu.xswl.csye6225.utils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.io.*;

/**
 * SDK接口方法
 * Created by wangwl on 2017/7/13.
 */
public class S3uploadUtil {
    @Autowired
    public static Environment env;


    public final static AmazonS3 s3client = new AmazonS3Client(DefaultAWSCredentialsProviderChain.getInstance());

    public final static String bucketName = getBucketNameStartwithCSYE6225(s3client.listBuckets());
           // s3client.listBuckets().get(0).getName();

    public static String getBucketNameStartwithCSYE6225(List<Bucket> bucketList){
        for(Bucket bucket : bucketList){
            if(bucket.getName().startsWith("csye6225-spring2019"))
                return bucket.getName();
        }
        return null;
    }

    public static String getpublicurl(String key_name,String bucketname){
        String keyName = key_name;

        //获取一个request
        GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(
                bucketname, keyName);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 5);
        Date expiredDate = c.getTime();

        urlRequest.setExpiration(expiredDate);
        //生成公用的url
        URL url = s3client.generatePresignedUrl(urlRequest);
        System.out.println("=========URL=================" + url);
        if (url == null) {
            System.out.println("can't get s3 file url!");
        }
        return url.toString();
    }

    public static File renamefile(File file,String filename,String filepath,String profix){
        File dir=new File(filepath+"/pictures");
        File file1=new File(filepath+"/pictures/"+filename+profix);
        File filejpg=new File(filepath+"/pictures/"+filename+".jpg");
        File filepng=new File(filepath+"/pictures/"+filename+".png");
        File filejpeg=new File(filepath+"/pictures/"+filename+".jpeg");
        if(!dir.exists()){
            dir.mkdir();
            if(filejpg.exists()){filejpg.delete();}
            else if(filepng.exists()){filepng.delete();}
            else if(filejpeg.exists()){filejpeg.delete();}
            file.renameTo(file1);
        }
        else {
            if(filejpg.exists()){filejpg.delete();}
            else if(filepng.exists()){filepng.delete();}
            else if(filejpeg.exists()){filejpeg.delete();}
            file.renameTo(file1);
        }
        return file1;
    }

    public static File convertMultiPartToFile(MultipartFile file, HttpServletRequest request) throws IOException {
        String contenttype = file.getContentType();
        String fileName = file.getOriginalFilename();
        String filePath = request.getSession().getServletContext().getRealPath("imgupload/");
        File dir=new File(filePath);
        if(!dir.exists()){dir.mkdirs();}
        File convFile=new File(filePath+fileName);
        InputStream ins =file.getInputStream();// new File(file.getOriginalFilename());
        OutputStream os = new FileOutputStream(convFile);
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
        ins.close();
        return convFile;
    }


    public static void upload(String key_name,File file,String bucketname) {
        String keyName = key_name;
//        String uploadFileName = path;

        try {
            System.out.println("Uploading a new object to S3 from a file\n");
//            File file = new File(uploadFileName);
            System.out.println(file);
            s3client.putObject(new PutObjectRequest(bucketname, keyName, file));
            System.out.println("Uploading finished\n");

        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " +
                    "means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " +
                    "means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }

    /**
     * ##########################################
     *   bucket的操作
     * ##########################################
     */

    /**
     * 获取bucket
     * @param bucket_name
     * @return
     */
    public static Bucket getBucket(String bucket_name) {
        Bucket named_bucket = null;
        List<Bucket> buckets = s3client.listBuckets();
        for (Bucket b : buckets) {
            if (b.getName().equals(bucket_name)) {
                named_bucket = b;
            }
        }
        return named_bucket;
    }

    /**
     * 创建bucket
     * @param bucket_name
     * @return
     */
    public static void createBucket(String bucket_name) {
        Bucket b = null;
        if (s3client.doesBucketExistV2(bucket_name)) {
            System.out.format("Bucket %s already exists.\n", bucket_name);
            b = getBucket(bucket_name);
        } else {
            try {
                b = s3client.createBucket(bucket_name);
            } catch (AmazonS3Exception e) {
                System.err.println(e.getErrorMessage());
            }
        }
    }

    /**
     * 列出所有的buckets
     * @return
     */
    public static List<Bucket> listBuckets() {
        List<Bucket> buckets = s3client.listBuckets();
        System.out.println("Your Amazon S3 buckets:");
        for (Bucket b2 : buckets) {
            System.out.println("* " + b2.getName());
        }
        return buckets;
    }

    /**
     * 删除不受版本控制的存储桶之前从中删除对象.
     * 要在删除不受版本控制的存储桶之前从中删除对象，
     * 可以使用 AmazonS3 客户端的 listObjects 方法检索对象列表，使用 deleteObject 删除各个对象。
     * @param bucket_name
     */
    public static void deleteObjectsNoVersion(String bucket_name){
        try {
            System.out.println(" - removing objects from bucket");
            //获取该bucket下的所有对象
            ObjectListing object_listing = s3client.listObjects(bucket_name);
            while (true) {
                for (Iterator<?> iterator = object_listing.getObjectSummaries().iterator(); iterator.hasNext();) {
                    S3ObjectSummary summary = (S3ObjectSummary)iterator.next();
                    s3client.deleteObject(bucket_name, summary.getKey());
                }

                // more object_listing to retrieve?
                if (object_listing.isTruncated()) {
                    object_listing = s3client.listNextBatchOfObjects(object_listing);
                } else {
                    break;
                }
            };
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }

    }

    /**
     * 删除受版本控制的存储桶之前从中删除对象
     * 如果您使用受版本控制的存储桶，还需要先删除存储桶中存储的所有受版本控制对象，然后才能删除存储桶。
     * 使用在删除存储桶中对象时使用的相似方法，通过使用 AmazonS3 客户端的 listVersions 方法列出所有受版本控制的对象，
     * 然后使用 deleteVersion 删除各个对象。
     * @param bucket_name
     */
    public static void deleteObjectsVersion(String bucket_name) {
        try {
            System.out.println(" - removing objects from bucket");
            ObjectListing object_listing = s3client.listObjects(bucket_name);
            while (true) {
                for (Iterator<?> iterator = object_listing.getObjectSummaries().iterator(); iterator.hasNext();) {
                    S3ObjectSummary summary = (S3ObjectSummary)iterator.next();
                    s3client.deleteObject(bucket_name, summary.getKey());
                }

                // more object_listing to retrieve?
                if (object_listing.isTruncated()) {
                    object_listing = s3client.listNextBatchOfObjects(object_listing);
                } else {
                    break;
                }
            };

            System.out.println(" - removing versions from bucket");
            VersionListing version_listing = s3client.listVersions(
                    new ListVersionsRequest().withBucketName(bucket_name));
            while (true) {
                for (Iterator<?> iterator = version_listing.getVersionSummaries().iterator(); iterator.hasNext();) {
                    S3VersionSummary vs = (S3VersionSummary)iterator.next();
                    s3client.deleteVersion(bucket_name, vs.getKey(), vs.getVersionId());
                }

                if (version_listing.isTruncated()) {
                    version_listing = s3client.listNextBatchOfVersions(version_listing);
                } else {
                    break;
                }
            }
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }

    /**
     * 删除一个空的bucket
     * @param bucket_name
     */
    public static void deleteEmptyBucket(String bucket_name) {
        try {
            s3client.deleteBucket(bucket_name);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }

    }

    /**
     * ##########################################
     *   object的操作
     * ##########################################
     */

    /**
     * 上传对象到bucket
     * @param bucket_name
     * @param file_path 文件对象路径
     */
    public static void putObject(String bucket_name, String file_path) {
        String key_name = Paths.get(file_path).getFileName().toString();
        try {
            s3client.putObject(bucket_name, key_name, new java.io.File(file_path));
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }

    /**
     * 列出bucket里的所有对象。
     * listObjects 方法返回一个 ObjectListing 对象，该对象提供有关存储桶中对象的信息。
     * @param bucket_name
     */
    public static void listObjects(String bucket_name) {
        ObjectListing ol = s3client.listObjects(bucket_name);
        //使用 getObjectSummaries 方法获取 S3ObjectSummary 对象的列表
        List<S3ObjectSummary> objects = ol.getObjectSummaries();
        for (S3ObjectSummary os: objects) {
            //调用其 getKey 方法以检索对象名称
            System.out.println("*对象名称： " + os.getKey());
        }
    }

    //check
    public static boolean checkObject( String key_name) {
        boolean flag = false;
        ObjectListing objectListing = s3client.listObjects(new ListObjectsRequest().withBucketName(bucketName));
        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            if(objectSummary.getKey().equals(key_name)) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 复制对象（从一个bucket到另一个bucket）。
     * 您可以将 copyObject 与 deleteObject 配合使用来移动或重命名对象，
     * 方式是先将对象复制到新名称 (您可以使用与源和目标相同的存储桶)，然后从对象的旧位置删除对象。
     * @param from_bucket
     * @param object_key 对象的名称
     * @param to_bucket
     */
    public static void copyObject(String from_bucket, String object_key, String to_bucket) {
        try {
            s3client.copyObject(from_bucket, object_key, to_bucket, object_key);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }

    /**
     * 删除对象
     * @param object_key 对象的名称
     */
    public static void deleteObject( String object_key,String bucketname) {
        try {
            s3client.deleteObject(bucketname, object_key);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }

    /**
     * 批量删除对象
     * @param object_keys 对象名称的数组
     */
    public static void deleteObjects(String object_keys) {
        try {
            DeleteObjectsRequest dor = new DeleteObjectsRequest(bucketName).withKeys(object_keys);
            s3client.deleteObjects(dor);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }
    //get keyname
    public static String getKeyname(String str){
        int last = str.indexOf("?");
        int first = str.substring(0,last).lastIndexOf("/");
        return str.substring(first+1, last);
    }

}
