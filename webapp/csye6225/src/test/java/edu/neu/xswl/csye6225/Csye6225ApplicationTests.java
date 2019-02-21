package edu.neu.xswl.csye6225;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import edu.neu.xswl.csye6225.utils.S3uploadUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class Csye6225ApplicationTests {


    @Test
    public void contextLoads() {
        AmazonS3 s3client = new AmazonS3Client(DefaultAWSCredentialsProviderChain.getInstance());
        String bucketName = s3client.listBuckets().get(0).getName();
        System.out.println(bucketName);
    }
    @Test
    public void deleteS3(){
        S3uploadUtil.deleteObject("t.sh",S3uploadUtil.bucketName);
    }

}

