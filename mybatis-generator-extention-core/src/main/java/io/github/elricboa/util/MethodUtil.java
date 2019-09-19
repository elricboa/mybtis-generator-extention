package io.github.elricboa.util;

import io.github.elricboa.constant.GeneratorConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.Context;

import java.util.Properties;

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

    /**
     * 填充父类
     *
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    public static FullyQualifiedJavaType setSuperClass(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType superClass = getSuperClass(introspectedTable);
        if (superClass != null) {
            topLevelClass.setSuperClass(superClass);
            topLevelClass.addImportedType(superClass);
        }
        return superClass;
    }

    /**
     * 获取父类
     *
     * @param introspectedTable
     * @return
     */
    public static FullyQualifiedJavaType getSuperClass(IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType superClass;
        if (null != introspectedTable && null != introspectedTable.getRules() && introspectedTable.getRules().generatePrimaryKeyClass()) {
            superClass = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
        } else {
            String rootClass = getRootClass(introspectedTable, introspectedTable.getContext());
            if (rootClass != null) {
                superClass = new FullyQualifiedJavaType(rootClass);
            } else {
                superClass = null;
            }
        }
        return superClass;
    }

    /**
     * 获取根类
     *
     * @param introspectedTable
     * @param context
     * @return
     */
    public static String getRootClass(IntrospectedTable introspectedTable, Context context) {
        String rootClass = introspectedTable.getTableConfigurationProperty(GeneratorConstant.ROOT_CLASS_NAME);
        if (rootClass == null) {
            Properties properties = context.getJavaModelGeneratorConfiguration().getProperties();
            rootClass = properties.getProperty(GeneratorConstant.ROOT_CLASS_NAME);
        }
        return rootClass;
    }

    /**
     * 接口类转换成 普通类
     *
     * @param interfaceClazz
     * @return
     */
    public static TopLevelClass convertInterfaceToTopLevelClass(Interface interfaceClazz) {

        TopLevelClass topLevelClass = new TopLevelClass(interfaceClazz.getType());
        topLevelClass.addImportedTypes(interfaceClazz.getImportedTypes());
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        //  父类接口是否为空
        if (CollectionUtils.isNotEmpty(interfaceClazz.getSuperInterfaceTypes())) {
            topLevelClass.setSuperClass(interfaceClazz.getSuperInterfaceTypes().iterator().next());
        }
        //  生成GetSqlSession方法
        Method method = new Method();
        method.setName("getSqlSession");
        method.getBodyLines().add("  return null; ");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType(SqlSession.class.getName()));
        topLevelClass.addImportedType(new FullyQualifiedJavaType(SqlSession.class.getName()));
        topLevelClass.getMethods().add(method);
        return topLevelClass;
    }

    public static boolean checkExistMethodElement(IntrospectedTable introspectedTable, MethodEnum methodEnum) {
        return false;
    }
}
