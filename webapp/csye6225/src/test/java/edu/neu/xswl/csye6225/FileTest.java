package edu.neu.xswl.csye6225;

import org.junit.Test;

import java.io.File;

public class FileTest {
    @Test
    public void deleteFile() {
        File file = new File("/Users/zhaoxiaohan/vagrant-workstation/data/csye6225-spring2019/webapp/csye6225/src/main/resources/static/100k.jpg");
        if (file.isFile() && file.exists())
            file.delete();

    }
}
