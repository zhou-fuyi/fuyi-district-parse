# FuYi District Parse

行政区划解析程序，输入shape文件，写入Postgresql.

完整的四级行政区划数据组织

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

![省级行政区-shapefile](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/a761c947-f307-4791-a115-6bc690a96008/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20220116%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20220116T140057Z&X-Amz-Expires=86400&X-Amz-Signature=70ec28d8b1828d9daeb99fadec49e586b16c8c392fb05b117eb4c388a3b0706e&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22&x-id=GetObject)

## 地级行政区

存在错乱数据，可以使用行政区代码识别（code），需要截取前6位

![地级行政区](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/030f569a-5f95-4d0f-aa98-393347175b9a/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20220116%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20220116T140243Z&X-Amz-Expires=86400&X-Amz-Signature=95804284a7c913ee44e8186126dd300de3e733c6ae24d06670f83910ff110791&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22&x-id=GetObject)

## 县级行政区

存在错乱数据，可以使用行政区代码识别（code），需要截取前6位

![县级行政区-shapefile](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/22714973-832d-4e1f-b305-7d1c472c5fc0/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20220116%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20220116T140340Z&X-Amz-Expires=86400&X-Amz-Signature=01f3a171d446afede57815a96796d2abd28089da884944d0adaf405e55453aff&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22&x-id=GetObject)

## 乡级行政区

![乡级行政区-shapefile](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/4b555a5a-2de0-471e-91da-019098daefc1/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20220116%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20220116T140429Z&X-Amz-Expires=86400&X-Amz-Signature=8ae3cec9129b97174cdf79f403ad9f566b586464e2fef329d86a198734aacb30&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22&x-id=GetObject)

## 成果

点数据为各个行政区中心点

![数据解析成果-shapefile_to_postgis](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/771d9359-e3b7-41a3-991c-f1be1172baff/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20220116%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20220116T140505Z&X-Amz-Expires=86400&X-Amz-Signature=a39f9df4cff94d1fcbd16e6cb1336ff6c4fc039daa0dd0e5b383bff46f216572&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22&x-id=GetObject)

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
