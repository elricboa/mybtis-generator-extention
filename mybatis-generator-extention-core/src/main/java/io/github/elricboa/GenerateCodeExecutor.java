package io.github.elricboa;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.config.*;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

/**
 * @author shentongzhou on 2019-09-03
 */
public class GenerateCodeExecutor {

    private static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";

    private String databaseUrl;
    private String databaseUsername;
    private String databasePassword;

    private String javaCodeBasePath = System.getProperty("user.dir") + "/";
    private String resourcesBasePath = System.getProperty("user.dir") + "/";
    private String domainPackage;
    private String mapperPackage;
    private String mapperXmlFolder;

    private List<String> warnings = Lists.newArrayList();
    private boolean overwrite;

    public void execute() throws IOException, XMLParserException, InvalidConfigurationException, SQLException, InterruptedException {
        URL generatorConfig = this.getClass().getClassLoader().getResource("generatorConfig.xml");
        Preconditions.checkNotNull(generatorConfig, "无法找到generator的配置文件,默认使用resources下的generatorConfig.xml文件");
        //  解析器
        ConfigurationParser parser = new ConfigurationParser(warnings);
        Configuration configuration = parser.parseConfiguration(new File(generatorConfig.getFile()));
        for (Context context : configuration.getContexts()) {
            JDBCConnectionConfiguration jdbcConfig = context.getJdbcConnectionConfiguration();
            jdbcConfig.setDriverClass(MYSQL_DRIVER);
            if (StringUtils.isNotEmpty(databaseUrl)) {
                jdbcConfig.setConnectionURL(databaseUrl);
            }
            if (StringUtils.isNotEmpty(databaseUsername)) {
                jdbcConfig.setUserId(databaseUsername);
            }
            if (StringUtils.isNotEmpty(databasePassword)) {
                jdbcConfig.setPassword(databasePassword);
            }
        }

        for (Context context : configuration.getContexts()) {
            JavaModelGeneratorConfiguration javaModelConfig = context.getJavaModelGeneratorConfiguration();
            if (StringUtils.isNotEmpty(javaCodeBasePath)) {
                javaModelConfig.setTargetProject(javaCodeBasePath + javaModelConfig.getTargetProject());
            }
            if (StringUtils.isNotEmpty(domainPackage)) {
                javaModelConfig.setTargetPackage(domainPackage);
            }
        }

        for (Context context : configuration.getContexts()) {
            SqlMapGeneratorConfiguration mapperXmlConfig = context.getSqlMapGeneratorConfiguration();
            if (StringUtils.isNotEmpty(resourcesBasePath)) {
                mapperXmlConfig.setTargetProject(resourcesBasePath + mapperXmlConfig.getTargetProject());
            }
            if (StringUtils.isNotEmpty(mapperXmlFolder)) {
                mapperXmlConfig.setTargetPackage(mapperXmlFolder);
            }
        }

        for (Context context : configuration.getContexts()) {
            JavaClientGeneratorConfiguration mapperConfig = context.getJavaClientGeneratorConfiguration();
            if (StringUtils.isNotEmpty(javaCodeBasePath)) {
                mapperConfig.setTargetProject(javaCodeBasePath + mapperConfig.getTargetProject());
            }
            if (StringUtils.isNotEmpty(mapperPackage)) {
                mapperConfig.setTargetPackage(mapperPackage);
            }
        }


        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(configuration, callback, warnings);
        ProgressCallback progressCallback = new GenerateFileProgressCallBack(myBatisGenerator);
        myBatisGenerator.generate(progressCallback);
    }
}
