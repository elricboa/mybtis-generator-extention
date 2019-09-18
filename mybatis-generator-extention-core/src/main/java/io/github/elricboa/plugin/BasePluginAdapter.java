package io.github.elricboa.plugin;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.jvm.hotspot.debugger.Page;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author shentongzhou on 2019-09-19
 */
public class BasePluginAdapter extends PluginAdapter {

    private final static Logger logger = LoggerFactory.getLogger(BasePluginAdapter.class);

    protected FullyQualifiedJavaType pageClass;

    protected Map<FullyQualifiedTable, List<XmlElement>> tableAndElementInfoList = new HashMap<>();

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        super.initialized(introspectedTable);
        //  构建分页类
        this.pageClass = new FullyQualifiedJavaType(Page.class.getName());
    }

    /**
     * 获取表名称
     *
     * @param introspectedTable
     * @return
     * @throws Exception
     */
    protected String getTableName(IntrospectedTable introspectedTable) throws Exception {
        try {
            return introspectedTable.getFullyQualifiedTable().getIntrospectedTableName();
        } catch (Exception e) {
            logger.error("获取表名信息列表异常", e);
            throw e;
        }
    }

    /**
     * 获取表主键信息
     *
     * @param introspectedTable
     * @return
     * @throws Exception
     */
    protected IntrospectedColumn getPrimaryKeyColumn(IntrospectedTable introspectedTable) throws Exception {
        try {

            IntrospectedColumn primaryKeyColumn = null;

            if (null != introspectedTable) {
                if (CollectionUtils.isNotEmpty(introspectedTable.getPrimaryKeyColumns())
                        && introspectedTable.getPrimaryKeyColumns().size() == 1) {
                    primaryKeyColumn = introspectedTable.getPrimaryKeyColumns().get(0);
                }
            }
            return primaryKeyColumn;
        } catch (Exception e) {
            logger.error("获取表名信息列表异常", e);
            throw e;
        }
    }

    /**
     * 获取表相关信息
     *
     * @return
     * @throws Exception
     */
    protected List<IntrospectedTable> getIntrospectedTableList() throws Exception {
        try {
            Field field = Context.class.getDeclaredField("introspectedTables");
            field.setAccessible(true);
            List<IntrospectedTable> introspectedTables = (List<IntrospectedTable>) field.get(super.getContext());

            //  判断表名信息列表是否为空
            if (CollectionUtils.isEmpty(introspectedTables)) {//  为空
                throw new Exception("表信息列表为空");
            }
            return introspectedTables;
        } catch (Exception e) {
            logger.error("获取表信息列表异常", e);
            throw e;
        }
    }

    /**
     * 添加到xml文件中
     *
     * @param element
     * @param fullyQualifiedTable
     */
    public void addElementToRootElement(XmlElement element, FullyQualifiedTable fullyQualifiedTable) {
        try {
            //  判断 子元素或表对象 是否为空
            if (null != element && null != fullyQualifiedTable) {//  非空
                //  获取 元素信息列表
                List<XmlElement> elementList = tableAndElementInfoList.get(fullyQualifiedTable);
                //  判断 元素信息列表  是否为空
                if (elementList == null) {//  非空
                    elementList = new ArrayList<XmlElement>();
                }

                //	添加子元素信息
                elementList.add(element);
                //  重置 表信息和元素信息集合
                tableAndElementInfoList.put(fullyQualifiedTable, elementList);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Parameter createPageParameter() {
        return new Parameter(pageClass, "page", "@Param(\"page\")");
    }

    @Override
    public boolean validate(List<String> list) {
        return true;
    }
}
