package org.fuyi.district.core;

import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;

import java.util.Objects;
import java.util.UUID;

public class DistrictInfoFactory {

    private static String GRADE_FIELD = "grade";
    private static String NAME_FIELD = "Name";
    private static String CODE_FIELD = "code";
    private static String CODE_EXTRA_FIELD = "adcode";
    private static String BOUNDS_FIELD = "the_geom";
    private static int DEFAULT_SRID = 4326;

    public static DistrictInfo build(SimpleFeature feature) {
        DistrictInfo info = new DistrictInfo();
        Integer grade = Integer.parseInt(feature.getAttribute(GRADE_FIELD).toString());
        int position = 6;
        String codeField = CODE_FIELD;
        if (grade == 4) {
            position = 9;
        }
        if (grade == 1) {
            // 针对现有数据进行处理
            codeField = CODE_EXTRA_FIELD;
        }
        info.setGrade(grade);
        String code = feature.getAttribute(codeField).toString().substring(0, position);
        info.setCode(code);
        Geometry bounds = (Geometry) feature.getAttribute(BOUNDS_FIELD);
        if (Objects.isNull(bounds)) {
            bounds = (Geometry) feature.getDefaultGeometryProperty().getValue();
        }
        info.setBounds(bounds);
        info.setCenterPoint(bounds.getInteriorPoint());
        info.setUid(UUID.randomUUID().toString());
        info.setName(feature.getAttribute(NAME_FIELD).toString());
        return info;
    }

    private static Geometry setSRID(Geometry g, int SRID) {
        if (SRID != 0) {
            g.setSRID(SRID);
        }
        return g;
    }

    public static void main(String[] args) {
        System.out.println(UUID.randomUUID().toString());
    }

}
