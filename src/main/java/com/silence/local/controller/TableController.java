package com.silence.local.controller;

import com.alibaba.fastjson.JSONObject;
import com.silence.module.common.enums.FolderFileEnum;
import com.silence.module.common.enums.OpCodeEnum;
import com.silence.module.common.model.DataResult;
import com.silence.module.common.model.FolderFile;
import com.silence.module.common.util.FileUtil;
import com.silence.module.common.util.MysqlDatabaseUtil;
import com.silence.module.db.api.model.Connection;
import com.silence.module.db.api.model.Table;
import com.silence.module.db.api.util.DatabaseMysqlUtil;
import com.silence.module.db.api.vo.ColumnVo;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author silence
 */
@Slf4j
@RestController
@RequestMapping("/table")
public class TableController {

    @GetMapping(value = {"", "/{page}"})
    public ModelAndView table(ModelAndView mav, @PathVariable(value = "page", required = false) String page) {

        mav.setViewName("table/" + (StringUtils.isEmpty(page) ? "index" : page));
        return mav;
    }


    @PostMapping()
    public DataResult table(@RequestBody Map param) throws Exception {

        String action = (String) param.getOrDefault("action", "");

        String url = (String) param.get("url");
        String username = (String) param.get("username");
        String password = (String) param.get("password");
        String tableCode = (String) param.get("tableCode");

        Connection database = new Connection() {{
            setUrl(url);
            setUsername(username);
            setPassword(password);
        }};

        MysqlDatabaseUtil util = new MysqlDatabaseUtil(url, username, password);

        switch (action) {
            case "database": {

                JSONObject obj = util.DBCatalogs();

                util.close();

                return new DataResult<>(obj);
            }
            case "tableList": {

                List<String> list = util.getTableNames(null);

                util.close();

                return new DataResult<>(list);
            }
            case "columnList": {


               util.close();

                List<ColumnVo> columnVoList = DatabaseMysqlUtil.getTableColumnVoList(database, tableCode);

                return new DataResult<>(columnVoList);
            }
            default:
                break;
        }


        List<Table> tableList = DatabaseMysqlUtil.getTableList(database, "", tableCode);

        List<ColumnVo> columnVoList = DatabaseMysqlUtil.getTableColumnVoList(database, tableCode);


        File root = new File("/Users/silence/projects/silence-codegenerator/src/main/resources/codetemplate-mc/package");
        FolderFile folderFile = FileUtil.getFolderFile(root, true, true);
        if (folderFile == null) {
            return new DataResult<>(OpCodeEnum.EXCEPTION.getKey(), "模板路径错误");
        }


        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("config", new JSONObject() {{
            put("symbol3", "#");
            put("symbol4", "$");
            put("author", "lrg");
            put("date", "2020-03-08");
        }});
        dataModel.put("table", tableList.get(0));
        dataModel.put("columnList", columnVoList);

        // 第一步：创建一个Configuration对象，直接new一个对象。构造方法的参数就是freemarker对于的版本号。
        Configuration configuration = new Configuration(Configuration.getVersion());
        // 第二步：设置模板文件所在的路径。
        configuration.setDirectoryForTemplateLoading(new File(folderFile.getBasePath()));
        // 第三步：设置模板文件使用的字符集。一般就是utf-8.
        configuration.setDefaultEncoding("utf-8");
        // 第四步：生成文件
        templateToCode(configuration, dataModel, folderFile, true, null);

        return new DataResult<>(columnVoList);

    }

    /**
     * 生成代码
     *
     * @param configuration Configuration对象
     * @param dataModel     模板变量数据对象
     * @param folderFile    要生成的模板目录
     * @param isReplace     是否递归
     * @param outFolder     输出位置 null:默认在模板相同的文件夹之下，！null : 位置必须存在
     */
    private void templateToCode(Configuration configuration, Map dataModel, FolderFile folderFile, boolean isReplace, File outFolder) throws IOException, TemplateException {

        if (configuration == null || CollectionUtils.isEmpty(dataModel) || folderFile == null) {
            return;
        }

        if (outFolder != null && !outFolder.exists()) {
            throw new RuntimeException("输出路径错误");
        }

        final File outFolderCopy = outFolder;

        boolean isFile = FolderFileEnum.Type.isFile(folderFile.getType());
        if (!isFile) {
            //判断是否递归
            if (isReplace && !CollectionUtils.isEmpty(folderFile.getNodes())) {
                //递归
                folderFile.getNodes().forEach(item -> {
                    try {
                        templateToCode(configuration, dataModel, item, isReplace, outFolderCopy);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            return;
        }

        Table table = (Table) dataModel.get("table");

        //输出文件名
        String name = folderFile.getName();
        if (name.indexOf("${table.codeHumpAll}") == 0) {
            name = name.replace("${table.codeHumpAll}", table.getCodeHumpAll());
        }

        //输出文件夹路径
        String outFolderPath = null;
        if (outFolder == null) {
            outFolderPath = folderFile.getBasePath() + "-out" + "/" + folderFile.getRelativePath() + "/";
        } else {
            outFolderPath = outFolder.getPath() + "/" + folderFile.getRelativePath() + "/";
        }
        outFolderPath = outFolderPath.replace("${table.codeHump}", table.getCodeHump());

        //输出文件
        File newFile = new File(outFolderPath + name + folderFile.getSuffix());

        //如果文件存在，是否替换
        if (newFile.exists() && !isReplace) {
            //不替换
            return;
        }

        //输出文件夹
        if (outFolder == null) {
            outFolder = new File(outFolderPath);
        }
        if (!outFolder.exists()) {
            outFolder.mkdirs();
        }

        Writer out = new FileWriter(newFile);
        //获取模板
        Template template = configuration.getTemplate(folderFile.getRelativePath() + "/" + folderFile.getName() + folderFile.getSuffix());
        //调用模板对象的process方法输出文件。
        template.process(dataModel, out);

        //关闭流。
        out.close();
    }

}
