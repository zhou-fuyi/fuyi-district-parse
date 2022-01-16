package org.fuyi.district.core.source;

import org.fuyi.district.core.DistrictInfo;
import org.geotools.geometry.jts.WKTWriter2;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PostGISSource {

    private JdbcTemplate template;

    private static String DEFAULT_PORT = "5432";

    private static String DEFAULT_SCHEMA = "public";

    private static String QUERY_BY_CODE_SQL = "SELECT COUNT(*) FROM %s WHERE code = ?";

    private static String QUERY_BY_CODE_AND_AREA = "SELECT ST_AREA(bounds) FROM %s WHERE code = ?";

    private static String DELETE_BY_CODE = "DELETE FROM %s WHERE code = ?";

    private static String INSERT_SQL = "INSERT INTO %s (name, grade, code, center_point, bounds, uid) VALUES (?, ?, ?, ST_GeomFromText(?, 4326), ST_GeomFromText(?, 4326), ?)";

    private static WKTWriter2 WKT_WRITER = new WKTWriter2();

    public PostGISSource(String host, String port, String username, String password, String catalog, String schema) {
        DriverManagerDataSource managerDataSource = new DriverManagerDataSource();
        managerDataSource.setDriverClassName("org.postgresql.Driver");
        if (!StringUtils.hasText(port)) {
            port = DEFAULT_PORT;
        }
        if (StringUtils.hasText(schema)) {
            schema = DEFAULT_SCHEMA;
        }
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + catalog;
        managerDataSource.setUrl(url);
        managerDataSource.setCatalog(catalog);
        managerDataSource.setSchema(schema);
        managerDataSource.setUsername(username);
        managerDataSource.setPassword(password);
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(managerDataSource);
        String sql = "SELECT 1;";
        jdbcTemplate.execute(sql);
        this.template = jdbcTemplate;
    }

    public void flush(Stream<DistrictInfo> districtInfoStream, String table){
        List<DistrictInfo> collect = districtInfoStream.filter(districtInfo -> {
            String filterSql = String.format(QUERY_BY_CODE_SQL, table);
            Integer count = this.template.queryForObject(filterSql, Integer.class, districtInfo.getCode());
            // 根据行政区编码进行检查，避免重复插入数据
            if (count == 0){
                return true;
            }else {
                // 由于给定的shape数据中存在重复的数据，且部分的数据是错误的，所以这里进行矫正
                // 目前对于code相同的数据行，取面积最大的一行作为最终数据
                String filterByAreaSql = String.format(QUERY_BY_CODE_AND_AREA, table);
                Double area = this.template.queryForObject(filterByAreaSql, Double.class, districtInfo.getCode());
                if (area < districtInfo.getBounds().getArea()){
                    String deleteSql = String.format(DELETE_BY_CODE, table);
                    this.template.update(deleteSql, ps -> ps.setString(1, districtInfo.getCode()));
                    return true;
                }else {
                    return false;
                }
            }
        }).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(collect)){
            String insertSql = String.format(INSERT_SQL, table);
            this.template.batchUpdate(insertSql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement, int index) throws SQLException {
                    DistrictInfo info = collect.get(index);
                    preparedStatement.setString(1, info.getName());
                    preparedStatement.setInt(2, info.getGrade());
                    preparedStatement.setString(3, info.getCode());
                    preparedStatement.setString(4, WKT_WRITER.write(info.getCenterPoint()));
                    preparedStatement.setString(5, WKT_WRITER.write(info.getBounds()));
                    preparedStatement.setString(6, info.getUid());
                }

                @Override
                public int getBatchSize() {
                    return collect.size();
                }
            });
        }
    }

}
