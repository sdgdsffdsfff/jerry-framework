/**
 * 
 */
package com.hehua.framework.storage;

import java.io.InputStream;
import java.util.List;

import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.OSSObject;
import com.aliyun.openservices.oss.model.OSSObjectSummary;
import com.aliyun.openservices.oss.model.ObjectListing;
import com.aliyun.openservices.oss.model.ObjectMetadata;

/**
 * @author zhihua
 *
 */
public class Test {

    /**
     * @param args
     */
    public static void main(String[] args) {

        OSSClient client = new OSSClient("http://oss-cn-beijing.aliyuncs.com", "zZJwIvgDiTYyQ8w0",
                "SVeMcePunhEkhTMo5zfyC3J1W7IfNA");

        ObjectListing listObjects = client.listObjects("abtest");

        List<OSSObjectSummary> objectSummaries = listObjects.getObjectSummaries();
        for (OSSObjectSummary summary : objectSummaries) {
            System.out.println(summary);
        }

        OSSObject object = client.getObject("abtest", "2009223826584_2.jpg");

        ObjectMetadata metadata = object.getObjectMetadata();

        System.out.println(metadata);

        InputStream inputStream = object.getObjectContent();

    }

}
