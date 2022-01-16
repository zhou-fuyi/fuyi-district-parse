package org.fuyi.district;

import org.fuyi.district.core.ShapefilePlugin;
import org.fuyi.district.core.source.PostGISSource;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

/**
 * 行政区划解析入口类
 */
public class DistrictParseApplication {

    private static final String DEFAULT_PATH = "/home/fuyi/Repository/Resources/GeographicInfo/areas";
    private static final String SUPPORTED_SUFFIX = ".shp";
    private static final String EXCLUDED_PATH = "Project";
    private static final String EXCLUDED_NAME = "_中华人民共和国.shp";
    private static final String DEFAULT_TABLE_NAME = "district_info";
    private static final ShapefilePlugin SHAPEFILE_PLUGIN = new ShapefilePlugin();
    private static final PostGISSource SOURCE;

    static {
        SOURCE = new PostGISSource("localhost", "5432", "postgres", "root", "fuyi_weather_db", null);
    }

    public static void main(String[] args) {
        // 如果给定的是一个目录，那么需要进行目录递归
        String path = DEFAULT_PATH;
        File file = new File(path);
        System.out.println(file);
        recursionLoad(file);
    }

    /**
     * 递归调用
     * @param file
     */
    private static void recursionLoad(File file) {
        if (file.exists() && file.isFile()) {
            System.out.println("-------------------");
            System.out.println(file);
            try {
                SOURCE.flush(SHAPEFILE_PLUGIN.access(file), DEFAULT_TABLE_NAME);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        // 对数据进行过滤，必须是 .shp后缀的文件才会被读取，EXCLUDED_PATH和EXCLUDED_NAME也是过滤条件
        Stream.of(file.listFiles()).filter(item -> {
            if ((item.isFile() && item.getName().endsWith(SUPPORTED_SUFFIX) && !item.getName().endsWith(EXCLUDED_NAME)) ||
                    (item.isDirectory() && !item.getName().endsWith(EXCLUDED_PATH))) {
                return true;
            } else {
                return false;
            }
        }).forEach(DistrictParseApplication::recursionLoad);
    }

}
