package org.fuyi.district.core;

import org.locationtech.jts.geom.Geometry;

/**
 * 行政区实体，坐标系：4326
 */
public class DistrictInfo {

    /**
     * 主键，自增
     */
    private Long id;
    /**
     * 数据唯一标识，便于迁移数据
     */
    private String uid;
    /**
     * 行政区名称
     */
    private String name;
    /**
     * 行政区等级
     */
    private Integer grade;
    /**
     * 行政区编码
     */
    private String code;
    /**
     * 行政区中心点（空间数据），且保证数据一定在面上
     */
    private Geometry centerPoint;
    /**
     * 行政区边界数据（空间数据）
     */
    private Geometry bounds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Geometry getCenterPoint() {
        return centerPoint;
    }

    public void setCenterPoint(Geometry centerPoint) {
        this.centerPoint = centerPoint;
    }

    public Geometry getBounds() {
        return bounds;
    }

    public void setBounds(Geometry bounds) {
        this.bounds = bounds;
    }

    @Override
    public String toString() {
        return "DistrictInfo{" +
                "id=" + id +
                ", uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", grade=" + grade +
                ", code='" + code + '\'' +
                ", centerPoint=" + centerPoint +
                ", bounds=" + bounds +
                '}';
    }
}
