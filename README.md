# FuYi District Parse

行政区划解析程序，输入shape文件，写入Postgresql.

完整的四级行政区划数据组织

## 数据情况

数据原源于网络，由于时间久远，我已经忘记了是如何获取到的

![China](http://img.zhoujian.site/knowledge-base/fuyi-weather/202208142058324.png)

![浙江省](http://img.zhoujian.site/knowledge-base/fuyi-weather/202208142106822.png)

## 名词解释

### 省级行政区

中国的一级行政区，或称国家一级行政区或省级行政区，是指直属中央政府管辖的行政区划，在历史上曾有不同的称呼。如：省、自治区、直辖市、特别行政区。

### 地级行政区

地级行政区即“地区级别行政区”，是现行中华人民共和国行政区划中常规的第二级行政区划单位，包括地级市、地区、盟、自治州等。地级行政区隶属于省、自治区、直辖市等省级行政区之下；下辖若干个县、区、县级市、旗等县级行政区。作为特例，东莞市、中山市、嘉峪关市、儋州市等四个地级市下辖街道办事处与乡镇，不辖县、区，因此也称作“直筒子（地级）市”。地级行政区的级别为正厅级，所以非正厅级的省直辖的行政区划不算作地级行政区，例如：湖北省辖的仙桃市、天门市、潜江市；河南省辖的济源市等等。直辖市下辖的区，虽然是正厅级，但未列入地级行政区的统计。

### 县级行政区

县为中华人民共和国行政区划单位之一，县级行政区指行政地位与“县”相同的行政区划单位的总称，其管辖乡级行政区。为乡、镇的上一级行政区划单位。中华人民共和国成立后，随着行政督察区名称的变更，除各直辖市均隶属于专区（行政督察专区）、地区或地级行政区，现除各直辖市、海南省直管县外均为地级行政区的下一级行政区。

- 按省、县、乡三级行政区划制度划分，县级行政区属于第二级行政区，为直辖市的下级行政区划单位。
- 按省、地、县、乡四级行政区划制度划分，县级行政区属于第三级行政区，属于省、自治区所辖地级行政区的下级行政区划单位。

### 乡级行政区

乡，中华人民共和国现行基层行政区划单位，区划层次介于县与村之间。“乡”为县、县级市下的主要行政区划类型之一。中国行政区划史上，“乡”一直为县的行政区划单元，因此现行处同一层次的区划单位归入乡级行政区。中国自改革开放以来，由于城市的快速扩张，行政区划制度出现了大的变革。1980年代以后“乡改镇”、“乡改街道”的现象越来越普遍。

在乡级行政区划中，乡（包括镇）设有一级人民政府，属于基层政权；乡的行政区划单位为村（含民族村）。但很多乡设有社区，乡的区划单位设置与镇、街道看不出实质性差异。

## 目的

解析所有数据文件，实现最终入库

使用GeoTools实现

数据库表结构：

- 行政区划信息（district_info）

  - id：自增Id（bigserial）
  - name：行政区划名称
  - grade：行政区划等级（省级行政区：1， 地级行政区：2， 县级行政区：3，）
  - code：行政区代码
  - center_point：中心点（geometry::point）
  - bounds：行政区边界（geometry）

注：

- 数据入库前审查，保证行政区代码唯一
- 使用grade区分省、市、县、乡镇
- 对于省、市、县code列，统一进行前6位截取（不满6位字符所在数据，直接丢弃），对于乡镇则统一进行前9位进行截取（不满9位字符所在数据，直接丢弃）

## 省级行政区

存在错乱数据，可以使用行政区代码识别（adcode）

![省级行政区-shapefile](http://img.zhoujian.site/knowledge-base/fuyi-weather/20220814173117.png)

## 地级行政区

存在错乱数据，可以使用行政区代码识别（code），需要截取前6位

![地级行政区](http://img.zhoujian.site/knowledge-base/fuyi-weather/20220814173132.png)

## 县级行政区

存在错乱数据，可以使用行政区代码识别（code），需要截取前6位

![县级行政区-shapefile](http://img.zhoujian.site/knowledge-base/fuyi-weather/20220814173141.png)

## 乡级行政区

![乡级行政区-shapefile](http://img.zhoujian.site/knowledge-base/fuyi-weather/20220814173148.png)

## 成果

记录数：46652

点数据为各个行政区中心点

![数据解析成果-shapefile_to_postgis](http://img.zhoujian.site/knowledge-base/fuyi-weather/202208141739133.png)

## 数据表

### PostGIS

```sql
CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;
```



### 行政区划

```sql
--
-- Name: district_info_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.district_info_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.district_info_id_seq OWNER TO postgres;

-- Table: public.district_info

-- DROP TABLE IF EXISTS public.district_info;

CREATE TABLE IF NOT EXISTS public.district_info
(
    name character varying COLLATE pg_catalog."default",
    grade integer,
    code character varying COLLATE pg_catalog."default",
    center_point geometry(Point,4326),
    bounds geometry(MultiPolygon,4326),
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    uid character varying COLLATE pg_catalog."default",
    id bigint NOT NULL DEFAULT nextval('district_info_id_seq'::regclass),
    CONSTRAINT district_info_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.district_info
    OWNER to postgres;

COMMENT ON TABLE public.district_info
    IS '行政区划信息表';

COMMENT ON COLUMN public.district_info.name
    IS '行政区名称';

COMMENT ON COLUMN public.district_info.grade
    IS '行政区等级，目前支持：（1：省级行政区，2；市级行政区，3：县级行政区，4：乡级行政区）';

COMMENT ON COLUMN public.district_info.code
    IS '行政区编码。其中，县级与县级以上行政区编码为6位，县级以下（即乡级）行政区编码为9位';

COMMENT ON COLUMN public.district_info.center_point
    IS '行政区中心点，数据坐标系：4326';

COMMENT ON COLUMN public.district_info.bounds
    IS '行政区边界，数据坐标系：4326';

COMMENT ON COLUMN public.district_info.uid
    IS '唯一标识，便于数据迁移';

--
-- Name: district_info_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.district_info_id_seq OWNED BY public.district_info.id;
```

## 日志

```t
...
org.fuyi.district.DistrictParseApplication
/home/fuyi/GeoDatabase/China
-------------------
/home/fuyi/GeoDatabase/China/重庆市/重庆市_市界.shp
current element size: 12
-------------------
/home/fuyi/GeoDatabase/China/重庆市/重庆市_省界.shp
current element size: 4
-------------------
/home/fuyi/GeoDatabase/China/重庆市/重庆市_县界.shp
current element size: 75
-------------------
/home/fuyi/GeoDatabase/China/重庆市/重庆市_乡镇边界.shp
current element size: 1218
-------------------
/home/fuyi/GeoDatabase/China/新疆自治区/新疆自治区_省界.shp
current element size: 4
-------------------
/home/fuyi/GeoDatabase/China/新疆自治区/新疆自治区_县界.shp
current element size: 111
-------------------
/home/fuyi/GeoDatabase/China/新疆自治区/新疆自治区_乡镇边界.shp
current element size: 1410
-------------------
/home/fuyi/GeoDatabase/China/新疆自治区/新疆自治区_市界.shp
current element size: 28
-------------------
/home/fuyi/GeoDatabase/China/湖北省/湖北省_市界.shp
current element size: 28
-------------------
/home/fuyi/GeoDatabase/China/湖北省/湖北省_省界.shp
current element size: 6
-------------------
/home/fuyi/GeoDatabase/China/湖北省/湖北省_县界.shp
current element size: 150
-------------------
/home/fuyi/GeoDatabase/China/湖北省/湖北省_乡镇边界.shp
current element size: 1679
-------------------
/home/fuyi/GeoDatabase/China/陕西省/陕西省_市界.shp
current element size: 24
-------------------
/home/fuyi/GeoDatabase/China/陕西省/陕西省_县界.shp
current element size: 158
-------------------
/home/fuyi/GeoDatabase/China/陕西省/陕西省_省界.shp
current element size: 7
-------------------
/home/fuyi/GeoDatabase/China/陕西省/陕西省_乡镇边界.shp
current element size: 1559
-------------------
/home/fuyi/GeoDatabase/China/广东省/广东省_省界.shp
current element size: 7
-------------------
/home/fuyi/GeoDatabase/China/广东省/广东省_县界.shp
current element size: 160
-------------------
/home/fuyi/GeoDatabase/China/广东省/广东省_市界.shp
current element size: 31
-------------------
/home/fuyi/GeoDatabase/China/广东省/广东省_乡镇边界.shp
current element size: 1902
-------------------
/home/fuyi/GeoDatabase/China/云南省/云南省_乡镇边界.shp
current element size: 1594
-------------------
/home/fuyi/GeoDatabase/China/云南省/云南省_省界.shp
current element size: 5
-------------------
/home/fuyi/GeoDatabase/China/云南省/云南省_县界.shp
current element size: 165
-------------------
/home/fuyi/GeoDatabase/China/云南省/云南省_市界.shp
current element size: 27
-------------------
/home/fuyi/GeoDatabase/China/宁夏省/宁夏省_市界.shp
current element size: 12
-------------------
/home/fuyi/GeoDatabase/China/宁夏省/宁夏省_乡镇边界.shp
current element size: 326
-------------------
/home/fuyi/GeoDatabase/China/宁夏省/宁夏省_县界.shp
current element size: 36
-------------------
/home/fuyi/GeoDatabase/China/宁夏省/宁夏省_省界.shp
current element size: 4
-------------------
/home/fuyi/GeoDatabase/China/内蒙古自治区/内蒙古自治区_乡镇边界.shp
current element size: 1498
-------------------
/home/fuyi/GeoDatabase/China/内蒙古自治区/内蒙古自治区_市界.shp
current element size: 35
-------------------
/home/fuyi/GeoDatabase/China/内蒙古自治区/内蒙古自治区_省界.shp
current element size: 7
-------------------
/home/fuyi/GeoDatabase/China/内蒙古自治区/内蒙古自治区_县界.shp
current element size: 173
-------------------
/home/fuyi/GeoDatabase/China/海南省/海南省_县界.shp
current element size: 27
-------------------
/home/fuyi/GeoDatabase/China/海南省/海南省_省界.shp
current element size: 1
-------------------
/home/fuyi/GeoDatabase/China/海南省/海南省_市界.shp
current element size: 19
-------------------
/home/fuyi/GeoDatabase/China/海南省/海南省_乡镇边界.shp
current element size: 214
-------------------
/home/fuyi/GeoDatabase/China/山东省/山东省_省界.shp
current element size: 5
-------------------
/home/fuyi/GeoDatabase/China/山东省/山东省_乡镇边界.shp
current element size: 1984
-------------------
/home/fuyi/GeoDatabase/China/山东省/山东省_市界.shp
current element size: 27
-------------------
/home/fuyi/GeoDatabase/China/山东省/山东省_县界.shp
current element size: 167
-------------------
/home/fuyi/GeoDatabase/China/浙江省/浙江省_省界.shp
current element size: 6
-------------------
/home/fuyi/GeoDatabase/China/浙江省/浙江省_市界.shp
current element size: 18
-------------------
/home/fuyi/GeoDatabase/China/浙江省/浙江省_县界.shp
current element size: 110
-------------------
/home/fuyi/GeoDatabase/China/浙江省/浙江省_乡镇边界.shp
current element size: 1431
-------------------
/home/fuyi/GeoDatabase/China/青海省/青海省_省界.shp
current element size: 5
-------------------
/home/fuyi/GeoDatabase/China/青海省/青海省_乡镇边界.shp
current element size: 470
-------------------
/home/fuyi/GeoDatabase/China/青海省/青海省_县界.shp
current element size: 72
-------------------
/home/fuyi/GeoDatabase/China/青海省/青海省_市界.shp
current element size: 19
-------------------
/home/fuyi/GeoDatabase/China/江苏省/江苏省_乡镇边界.shp
current element size: 1634
-------------------
/home/fuyi/GeoDatabase/China/江苏省/江苏省_省界.shp
current element size: 5
-------------------
/home/fuyi/GeoDatabase/China/江苏省/江苏省_市界.shp
current element size: 26
-------------------
/home/fuyi/GeoDatabase/China/江苏省/江苏省_县界.shp
current element size: 138
-------------------
/home/fuyi/GeoDatabase/China/江西省/江西省_市界.shp
current element size: 27
-------------------
/home/fuyi/GeoDatabase/China/江西省/江西省_乡镇边界.shp
current element size: 1927
-------------------
/home/fuyi/GeoDatabase/China/江西省/江西省_县界.shp
current element size: 137
-------------------
/home/fuyi/GeoDatabase/China/江西省/江西省_省界.shp
current element size: 7
-------------------
/home/fuyi/GeoDatabase/China/西藏自治区/西藏自治区_县界.shp
current element size: 91
-------------------
/home/fuyi/GeoDatabase/China/西藏自治区/西藏自治区_省界.shp
current element size: 5
-------------------
/home/fuyi/GeoDatabase/China/西藏自治区/西藏自治区_市界.shp
current element size: 14
-------------------
/home/fuyi/GeoDatabase/China/西藏自治区/西藏自治区_乡镇边界.shp
current element size: 737
-------------------
/home/fuyi/GeoDatabase/China/天津市/天津市_县界.shp
current element size: 32
-------------------
/home/fuyi/GeoDatabase/China/天津市/天津市_省界.shp
current element size: 2
-------------------
/home/fuyi/GeoDatabase/China/天津市/天津市_市界.shp
current element size: 4
-------------------
/home/fuyi/GeoDatabase/China/天津市/天津市_乡镇边界.shp
current element size: 360
-------------------
/home/fuyi/GeoDatabase/China/台湾省/台湾省_村名.shp
current element size: 0
-------------------
/home/fuyi/GeoDatabase/China/台湾省/台湾省_乡镇名称.shp
current element size: 0
-------------------
/home/fuyi/GeoDatabase/China/台湾省/台湾省_县名称.shp
current element size: 0
-------------------
/home/fuyi/GeoDatabase/China/广西省/广西省_省界.shp
current element size: 3
-------------------
/home/fuyi/GeoDatabase/China/广西省/广西省_市界.shp
current element size: 25
-------------------
/home/fuyi/GeoDatabase/China/广西省/广西省_县界.shp
current element size: 142
-------------------
/home/fuyi/GeoDatabase/China/广西省/广西省_乡镇边界.shp
current element size: 1433
-------------------
/home/fuyi/GeoDatabase/China/香港特别行政区/香港特别行政区_乡镇边界.shp
current element size: 8
-------------------
/home/fuyi/GeoDatabase/China/香港特别行政区/香港特别行政区_县界.shp
current element size: 21
-------------------
/home/fuyi/GeoDatabase/China/香港特别行政区/香港特别行政区_省界.shp
current element size: 2
-------------------
/home/fuyi/GeoDatabase/China/香港特别行政区/香港特别行政区_市界.shp
current element size: 1
-------------------
/home/fuyi/GeoDatabase/China/安徽省/安徽省_市界.shp
current element size: 34
-------------------
/home/fuyi/GeoDatabase/China/安徽省/安徽省_乡镇边界.shp
current element size: 1832
-------------------
/home/fuyi/GeoDatabase/China/安徽省/安徽省_省界.shp
current element size: 6
-------------------
/home/fuyi/GeoDatabase/China/安徽省/安徽省_县界.shp
current element size: 149
-------------------
/home/fuyi/GeoDatabase/China/河南省/河南省_市界.shp
current element size: 38
-------------------
/home/fuyi/GeoDatabase/China/河南省/河南省_乡镇边界.shp
current element size: 2722
-------------------
/home/fuyi/GeoDatabase/China/河南省/河南省_县界.shp
current element size: 209
-------------------
/home/fuyi/GeoDatabase/China/河南省/河南省_省界.shp
current element size: 7
-------------------
/home/fuyi/GeoDatabase/China/黑龙江省/黑龙江省_市界.shp
current element size: 20
-------------------
/home/fuyi/GeoDatabase/China/黑龙江省/黑龙江省_省界.shp
current element size: 3
-------------------
/home/fuyi/GeoDatabase/China/黑龙江省/黑龙江省_县界.shp
current element size: 150
-------------------
/home/fuyi/GeoDatabase/China/黑龙江省/黑龙江省_乡镇边界.shp
current element size: 1874
-------------------
/home/fuyi/GeoDatabase/China/澳门特别行政区/澳门特别行政区_市界.shp
current element size: 1
-------------------
/home/fuyi/GeoDatabase/China/澳门特别行政区/澳门特别行政区_省界.shp
current element size: 2
-------------------
/home/fuyi/GeoDatabase/China/澳门特别行政区/澳门特别行政区_乡镇边界.shp
current element size: 4
-------------------
/home/fuyi/GeoDatabase/China/澳门特别行政区/澳门特别行政区_县界.shp
current element size: 9
-------------------
/home/fuyi/GeoDatabase/China/吉林省/吉林省_省界.shp
current element size: 4
-------------------
/home/fuyi/GeoDatabase/China/吉林省/吉林省_县界.shp
current element size: 82
-------------------
/home/fuyi/GeoDatabase/China/吉林省/吉林省_乡镇边界.shp
current element size: 1134
-------------------
/home/fuyi/GeoDatabase/China/吉林省/吉林省_市界.shp
current element size: 19
-------------------
/home/fuyi/GeoDatabase/China/北京市/北京市_县界.shp
current element size: 32
-------------------
/home/fuyi/GeoDatabase/China/北京市/北京市_乡镇边界.shp
current element size: 395
-------------------
/home/fuyi/GeoDatabase/China/北京市/北京市_市界.shp
current element size: 4
-------------------
/home/fuyi/GeoDatabase/China/北京市/北京市_省界.shp
current element size: 3
-------------------
/home/fuyi/GeoDatabase/China/四川省/四川省_乡镇边界.shp
current element size: 4933
-------------------
/home/fuyi/GeoDatabase/China/四川省/四川省_市界.shp
current element size: 32
-------------------
/home/fuyi/GeoDatabase/China/四川省/四川省_省界.shp
current element size: 7
-------------------
/home/fuyi/GeoDatabase/China/四川省/四川省_县界.shp
current element size: 237
-------------------
/home/fuyi/GeoDatabase/China/上海市/上海市_省界.shp
current element size: 3
-------------------
/home/fuyi/GeoDatabase/China/上海市/上海市_乡镇边界.shp
current element size: 251
-------------------
/home/fuyi/GeoDatabase/China/上海市/上海市_县界.shp
current element size: 24
-------------------
/home/fuyi/GeoDatabase/China/上海市/上海市_市界.shp
current element size: 4
-------------------
/home/fuyi/GeoDatabase/China/甘肃省/甘肃省_县界.shp
current element size: 137
-------------------
/home/fuyi/GeoDatabase/China/甘肃省/甘肃省_市界.shp
current element size: 33
-------------------
/home/fuyi/GeoDatabase/China/甘肃省/甘肃省_省界.shp
current element size: 7
-------------------
/home/fuyi/GeoDatabase/China/甘肃省/甘肃省_乡镇边界.shp
current element size: 1601
-------------------
/home/fuyi/GeoDatabase/China/福建省/福建省_市界.shp
current element size: 18
-------------------
/home/fuyi/GeoDatabase/China/福建省/福建省_乡镇边界.shp
current element size: 1272
-------------------
/home/fuyi/GeoDatabase/China/福建省/福建省_省界.shp
current element size: 4
-------------------
/home/fuyi/GeoDatabase/China/福建省/福建省_县界.shp
current element size: 109
-------------------
/home/fuyi/GeoDatabase/China/河北省/河北省_市界.shp
current element size: 26
-------------------
/home/fuyi/GeoDatabase/China/河北省/河北省_乡镇边界.shp
current element size: 2574
-------------------
/home/fuyi/GeoDatabase/China/河北省/河北省_省界.shp
current element size: 8
-------------------
/home/fuyi/GeoDatabase/China/河北省/河北省_县界.shp
current element size: 225
-------------------
/home/fuyi/GeoDatabase/China/贵州省/贵州省_乡镇边界.shp
current element size: 1791
-------------------
/home/fuyi/GeoDatabase/China/贵州省/贵州省_县界.shp
current element size: 122
-------------------
/home/fuyi/GeoDatabase/China/贵州省/贵州省_市界.shp
current element size: 16
-------------------
/home/fuyi/GeoDatabase/China/贵州省/贵州省_省界.shp
current element size: 5
-------------------
/home/fuyi/GeoDatabase/China/湖南省/湖南省_省界.shp
current element size: 6
-------------------
/home/fuyi/GeoDatabase/China/湖南省/湖南省_市界.shp
current element size: 28
-------------------
/home/fuyi/GeoDatabase/China/湖南省/湖南省_乡镇边界.shp
current element size: 2662
-------------------
/home/fuyi/GeoDatabase/China/湖南省/湖南省_县界.shp
current element size: 169
-------------------
/home/fuyi/GeoDatabase/China/山西省/山西省_县界.shp
current element size: 154
-------------------
/home/fuyi/GeoDatabase/China/山西省/山西省_市界.shp
current element size: 28
-------------------
/home/fuyi/GeoDatabase/China/山西省/山西省_乡镇边界.shp
current element size: 1580
-------------------
/home/fuyi/GeoDatabase/China/山西省/山西省_省界.shp
current element size: 5
-------------------
/home/fuyi/GeoDatabase/China/辽宁省/辽宁省_县界.shp
current element size: 123
-------------------
/home/fuyi/GeoDatabase/China/辽宁省/辽宁省_市界.shp
current element size: 21
-------------------
/home/fuyi/GeoDatabase/China/辽宁省/辽宁省_省界.shp
current element size: 4
-------------------
/home/fuyi/GeoDatabase/China/辽宁省/辽宁省_乡镇边界.shp
current element size: 1659

Process finished with exit code 0

```



## 参考

- [wiki-中华人民共和国行政区划代码](https://zh.wikipedia.org/wiki/%E4%B8%AD%E5%8D%8E%E4%BA%BA%E6%B0%91%E5%85%B1%E5%92%8C%E5%9B%BD%E8%A1%8C%E6%94%BF%E5%8C%BA%E5%88%92%E4%BB%A3%E7%A0%81)
- [geotools-shapeplugin](https://zh.wikipedia.org/wiki/%E4%B8%AD%E5%8D%8E%E4%BA%BA%E6%B0%91%E5%85%B1%E5%92%8C%E5%9B%BD%E8%A1%8C%E6%94%BF%E5%8C%BA%E5%88%92%E4%BB%A3%E7%A0%81)

```roomsql

-- alter table district_info alter column center_point 
-- type Geometry(Point, 4326) USING ST_SetSRID(center_point, 4326);

-- ALTER TABLE district_info ALTER COLUMN bounds TYPE Geometry(MultiPolygon, 4326) USING ST_SetSRID(bounds, 4326);

select st_astext(center_point), * from district_info where grade = 1 order by id limit 1000 ;

select st_area(bounds) as area, * from district_info where grade = 1 and name like '%海南%';

-- delete from district_info;

select code, count(*) as _count from district_info group by code order by _count desc;

select * from district_info;
```
