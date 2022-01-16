package org.fuyi.district.core;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.stream.Stream;

/**
 * Shapefile插件
 */
public class ShapefilePlugin {

    public Stream<DistrictInfo> access(File file) throws IOException {
        Map<String, Object> params = new HashMap<>();
        try {
            params.put("url", file.toURI().toURL());
            params.put("charset", "GBK");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        DataStore dataStore = null;
        try {
            dataStore = DataStoreFinder.getDataStore(params);
            String typeName = dataStore.getTypeNames()[0];
            FeatureSource<SimpleFeatureType, SimpleFeature> source = dataStore.getFeatureSource(typeName);
            return infoWrapper(source.getFeatures().features());
        }finally {
            if (Objects.nonNull(dataStore)) {
                // 手动释放资源
                dataStore.dispose();
            }
        }
    }

    private Stream<DistrictInfo> infoWrapper(FeatureIterator<SimpleFeature> featureFeatureIterator){
        List<DistrictInfo> currentInfoList = new ArrayList<>();
        // 循环创建DistrictInfo，完成后自动释放资源（FeatureIterator）
        try (FeatureIterator<SimpleFeature> access = featureFeatureIterator) {
            while (access.hasNext()){
                currentInfoList.add(DistrictInfoFactory.build(access.next()));
            }
            System.out.println("current element size: " + currentInfoList.size());
        }
        return currentInfoList.stream();
    }

    public static void main(String[] args) throws IOException {
        ShapefilePlugin shapefilePlugin = new ShapefilePlugin();
//        File file = new File("/home/fuyi/Repository/Resources/GeographicInfo/areas/海南省/海南省_省界.shp");
        File file = new File("/home/fuyi/Repository/Resources/GeographicInfo/areas/安徽省/安徽省_省界.shp");
//        File file = new File("/home/fuyi/Repository/Resources/GeographicInfo/areas/安徽省/安徽省_市界.shp");
        System.out.println(file);
        Stream<DistrictInfo> collection = shapefilePlugin.access(file);
        collection.forEach(item -> {
            System.out.println(item);
        });
    }

}
