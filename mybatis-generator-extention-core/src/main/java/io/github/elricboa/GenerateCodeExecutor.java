package io.github.elricboa;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.XMLParserException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * @author shentongzhou on 2019-09-03
 */
public class GenerateCodeExecutor {

    private List<String> warnings = Lists.newArrayList();

    public void execute() throws IOException, XMLParserException {
        URL generatorConfig = this.getClass().getClassLoader().getResource("generatorConfig.xml");
        Preconditions.checkNotNull(generatorConfig, "无法找到generator的配置文件,默认使用resources下的generatorConfig.xml文件");
        //  解析器
        ConfigurationParser parser = new ConfigurationParser(warnings);
        Configuration configuration = parser.parseConfiguration(new File(generatorConfig.getFile()));
    }
}
