package io.github.elricboa.constant;

import io.github.elricboa.enums.MethodEnum;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author shentongzhou on 2019-09-17
 */
public class GeneratorConstant {
    //  多表状态名称
    public static final String MULTI_TABLE_STATUS_NAME = "multiTable";
    //  分表的表名
    public static final String MULTI_TABLE_NAME = "${@io.github.elricboa.common.dkeeper.SQLInfo@getTable()}";

    //  分页名称
    public static final String PAGE_OFFSET = "pageOffSet";
    public static final String PAGE_SIZENAME = "pageSize";
    public static final String ROOT_CLASS_NAME = "rootClass";
    //  接口名称
    public static final String INTERFACE_NAME = "interfaceName";
    //  根接口类的Key名称
    public static final String ROOT_INTERFACE_NAME = "rootInterface";
    //  实现方式状态名称
    public static final String IMPLEMENT_PATTERN_STATUS_NAME = "implementPattern";
    //  mapper文件名称
    public static final String MAPPER_XML_NAME = "mapperXMLName";
    //  key：文件前缀 同一小写  value mapper文件的元素名称列表
    public static Map<String, Set<MethodEnum>> existElementForMapperMap = new HashMap<>();
}
