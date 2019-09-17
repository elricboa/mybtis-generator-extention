package io.github.elricboa;

import io.github.elricboa.constant.GeneratorConstant;
import io.github.elricboa.util.MethodUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
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

    @Override
    public void saveStarted(int i) {
        try {
            Class myBatisGeneratorClazz = myBatisGenerator.getClass();

            Field generatedJavaFilesField = myBatisGeneratorClazz.getDeclaredField(GENERATEDJAVAFILES_NAME);
            generatedJavaFilesField.setAccessible(true);

            List<GeneratedJavaFile> generatedJavaFiles = (List<GeneratedJavaFile>) generatedJavaFilesField.get(myBatisGenerator);

            if (CollectionUtils.isNotEmpty(generatedJavaFiles)) {

                for (GeneratedJavaFile generatedJavaFile : generatedJavaFiles) {

                    if (generatedJavaFile.getCompilationUnit() instanceof TopLevelClass) {

                        TopLevelClass topLevelClass = (TopLevelClass) generatedJavaFile.getCompilationUnit();

                        String javaModelName = topLevelClass.getType().getShortName().replaceAll("Example", "");

                        IntrospectedTable introspectedTable = getIntrospectedTableByName(javaModelName);

                        if (null != introspectedTable) {
                            MethodUtil.setSuperClass(topLevelClass, introspectedTable);
                        }
                    } else if (generatedJavaFile.getCompilationUnit() instanceof Interface) {

                        Interface interfaceClazz = (Interface) generatedJavaFile.getCompilationUnit();

                        String javaModelName = interfaceClazz.getType().getShortName().replaceAll("Mapper", "");

                        IntrospectedTable introspectedTable = getIntrospectedTableByName(javaModelName);

                        if (null != introspectedTable) {
                            String interfaceName = MethodUtil.getPropertyValueByName(introspectedTable, GeneratorConstant.INTERFACE_NAME);

                            if (StringUtils.isNotBlank(interfaceName)) {
                                reflectAndSetField(interfaceClazz.getType(), "baseShortName", interfaceName);
                            }

                            if (CollectionUtils.isNotEmpty(interfaceClazz.getSuperInterfaceTypes())
                                    && interfaceClazz.getSuperInterfaceTypes().size() == 1) {

                                FullyQualifiedJavaType type = (FullyQualifiedJavaType) interfaceClazz.getSuperInterfaceTypes().toArray()[0];

                                String javaModelExampleName = String.format("%sExample", javaModelName);
                                String baseShortName = String.format("%s<%s,%s>", type.getShortName(), javaModelName, javaModelExampleName);
                                reflectAndSetField(type, "baseShortName", baseShortName);
                            }

                            //  实现类方式
                            String pattern = MethodUtil.getPropertyValueByName(introspectedTable, GeneratorConstant.IMPLEMENT_PATTERN_STATUS_NAME);
                            if (StringUtils.isNotBlank(pattern) && Boolean.parseBoolean(pattern)) {
                                TopLevelClass topLevelClassTest = MethodUtil.convertInterfaceToTopLevelClass(interfaceClazz);
                                reflectAndSetField(generatedJavaFile, "compilationUnit", topLevelClassTest);
                            }
                        }
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(generatedJavaFiles)) {

                Iterator<GeneratedJavaFile> javaFileIterator = generatedJavaFiles.iterator();
                while (javaFileIterator.hasNext()) {
                    GeneratedJavaFile generatedJavaFile = javaFileIterator.next();
                    boolean existFileStatus = existFile(generatedJavaFile);
                    if (existFileStatus) {
                        javaFileIterator.remove();
                    }
                }
            }

            Field generatedXmlFilesField = myBatisGeneratorClazz.getDeclaredField(GENERATEDXMLFILES_NAME);
            generatedXmlFilesField.setAccessible(true);

            List<GeneratedXmlFile> generatedXmlFileList = (List<GeneratedXmlFile>) generatedXmlFilesField.get(myBatisGenerator);

            if (CollectionUtils.isNotEmpty(generatedXmlFileList)) {
                for (GeneratedXmlFile file : generatedXmlFileList) {

                    String prefixXmlFileName = file.getFileName().replaceAll(".xml", "");
                    String javaModelName = prefixXmlFileName.replaceAll("Mapper", "");

                    IntrospectedTable introspectedTable = getIntrospectedTableByName(javaModelName);

                    if (null != introspectedTable) {
                        String mapperXMLName = MethodUtil.getPropertyValueByName(introspectedTable, GeneratorConstant.MAPPER_XML_NAME);
                        if (StringUtils.isNotBlank(mapperXMLName)) {
                            mapperXMLName = String.format("%s.xml", mapperXMLName);
                            reflectAndSetField(file, "fileName", mapperXMLName);
                        }

                        String interfaceName = MethodUtil.getPropertyValueByName(introspectedTable, GeneratorConstant.INTERFACE_NAME);
                        if (StringUtils.isNotBlank(interfaceName)) {

                            Document document = (Document) reflectAndGetField(file, "document");

                            if (CollectionUtils.isNotEmpty(document.getRootElement().getAttributes())) {
                                Attribute attribute = document.getRootElement().getAttributes().get(0);
                                if ("namespace".equals(attribute.getName())) {

                                    String nameSpaceValue = attribute.getValue().replace(prefixXmlFileName,
                                            interfaceName);
                                    reflectAndSetField(attribute, "value", nameSpaceValue);
                                }
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

    public Object reflectAndGetField(Object sourceObject, String fieldName) {
        Object reflectObject = null;
        try {
            if (null != sourceObject) {
                Class clazz = sourceObject.getClass();
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                reflectObject = field.get(sourceObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reflectObject;
    }

    private IntrospectedTable getIntrospectedTableByName(String name) {

        if (StringUtils.isBlank(name)) {
            return null;
        }
        Map<String, IntrospectedTable> introspectedTableMap = getIntrospectedTable();

        if (introspectedTableMap.containsKey(name)) {
            return introspectedTableMap.get(name);
        } else {
            return null;
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

    private boolean existFile(GeneratedJavaFile generatedJavaFile) {
        boolean result = false;

        FullyQualifiedJavaType javaType = generatedJavaFile.getCompilationUnit().getType();
        String rootPath = generatedJavaFile.getTargetProject();

        String javaFilePath = String.format("%s.%s", javaType.getPackageName(), javaType.getShortName());

//        if (generatedJavaFile.getCompilationUnit() instanceof TopLevelClass) {
//            javaFilePath = generatedJavaFile.getCompilationUnit().getType().getFullyQualifiedName();
//        } else if (generatedJavaFile.getCompilationUnit() instanceof Interface) {
//        }

        if (StringUtils.isNotBlank(javaFilePath)) {
            javaFilePath = javaFilePath.replaceAll("\\.", "/");
        }
        String fullJavaFilePath = String.format("%s%s.java", rootPath, javaFilePath);
        File file = new File(fullJavaFilePath);
        if (file.exists()) {
            result = true;
        }
        return result;
    }
}
