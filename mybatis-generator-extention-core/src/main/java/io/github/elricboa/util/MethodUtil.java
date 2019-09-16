package io.github.elricboa.util;

import org.mybatis.generator.api.IntrospectedTable;

/**
 * @author shentongzhou on 2019-09-17
 */
public class MethodUtil {
    /**
     * 从Properties 获取key对应的value
     *
     * @param introspectedTable
     * @param keyName
     * @return
     */
    public static String getPropertyValueByName(IntrospectedTable introspectedTable, String keyName) {
        return introspectedTable.getTableConfigurationProperty(keyName);
    }
}
