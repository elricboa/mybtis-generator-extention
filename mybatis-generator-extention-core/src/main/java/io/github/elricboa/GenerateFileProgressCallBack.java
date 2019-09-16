package io.github.elricboa;

import io.github.elricboa.constant.GeneratorConstant;
import io.github.elricboa.util.MethodUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shentongzhou on 2019-09-04
 */
public class GenerateFileProgressCallBack implements ProgressCallback {
    private MyBatisGenerator myBatisGenerator;

    private static final String GENERATEDJAVAFILES_NAME = "generatedJavaFiles";
    private static final String GENERATEDXMLFILES_NAME = "generatedXmlFiles";

    private Map<String, IntrospectedTable> domainAndTableNameAndIntrospectedTableMap = new HashMap<>();

    public GenerateFileProgressCallBack(MyBatisGenerator myBatisGenerator) {
        this.myBatisGenerator = myBatisGenerator;
    }

    @Override
    public void introspectionStarted(int i) {

    }

    @Override
    public void generationStarted(int i) {
        Map<String, IntrospectedTable> introspectedTableMap = getIntrospectedTable();

        if (MapUtils.isNotEmpty(introspectedTableMap)) {

            for (IntrospectedTable introspectedTable : introspectedTableMap.values()) {

                FullyQualifiedTable fullyQualifiedTable = introspectedTable.getFullyQualifiedTable();

                String multiTable = MethodUtil.getPropertyValueByName(introspectedTable, GeneratorConstant.MULTI_TABLE_STATUS_NAME);
                if (StringUtils.isNotBlank(multiTable) && Boolean.parseBoolean(multiTable)) {
                    reflectAndSetField(fullyQualifiedTable, "introspectedTableName", GeneratorConstant.MULTI_TABLE_NAME);
                }
            }
        }
    }

    private Map<String, IntrospectedTable> getIntrospectedTable() {
        try {
            Class myBatisGeneratorClazz = myBatisGenerator.getClass();

            Field configurationField = myBatisGeneratorClazz.getDeclaredField("configuration");
            configurationField.setAccessible(true);

            Configuration configuration = (Configuration) configurationField.get(myBatisGenerator);

            if (null != configuration && CollectionUtils.isNotEmpty(configuration.getContexts())) {
                for (Context context : configuration.getContexts()) {
                    if (null != context) {
                        Class contextClazz = context.getClass();

                        Field introspectedTablesField = contextClazz.getDeclaredField("introspectedTables");
                        introspectedTablesField.setAccessible(true);

                        List<IntrospectedTable> introspectedTables = (List<IntrospectedTable>)
                                introspectedTablesField.get(context);

                        if (CollectionUtils.isNotEmpty(introspectedTables)) {

                            for (IntrospectedTable introspectedTable : introspectedTables) {

                                String domainObjectName = introspectedTable.getFullyQualifiedTable()
                                        .getDomainObjectName();

                                String tableName = introspectedTable.getFullyQualifiedTable()
                                        .getIntrospectedTableName();
                                tableName = tableName.replaceAll("_", "");

                                domainAndTableNameAndIntrospectedTableMap.put(domainObjectName, introspectedTable);
                                domainAndTableNameAndIntrospectedTableMap.put(tableName, introspectedTable);
                            }
                        }
                    }
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return domainAndTableNameAndIntrospectedTableMap;
    }

    public Object reflectAndSetField(Object sourceObject, String fieldName, Object newValue) {
        Object reflectObject = null;
        try {
            if (null != sourceObject) {
                Class clazz = sourceObject.getClass();
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                reflectObject = field.get(sourceObject);
                //设置私有域的值
                field.set(sourceObject, newValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reflectObject;
    }

    @Override
    public void saveStarted(int i) {

    }

    @Override
    public void startTask(String s) {

    }

    @Override
    public void done() {

    }

    @Override
    public void checkCancel() throws InterruptedException {

    }
}
