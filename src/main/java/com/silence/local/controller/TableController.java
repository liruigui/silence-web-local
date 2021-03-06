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
            return new DataResult<>(OpCodeEnum.EXCEPTION.getKey(), "??????????????????");
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

        // ????????????????????????Configuration???????????????new??????????????????????????????????????????freemarker?????????????????????
        Configuration configuration = new Configuration(Configuration.getVersion());
        // ????????????????????????????????????????????????
        configuration.setDirectoryForTemplateLoading(new File(folderFile.getBasePath()));
        // ???????????????????????????????????????????????????????????????utf-8.
        configuration.setDefaultEncoding("utf-8");
        // ????????????????????????
        templateToCode(configuration, dataModel, folderFile, true, null);

        return new DataResult<>(columnVoList);

    }

    /**
     * ????????????
     *
     * @param configuration Configuration??????
     * @param dataModel     ????????????????????????
     * @param folderFile    ????????????????????????
     * @param isReplace     ????????????
     * @param outFolder     ???????????? null:?????????????????????????????????????????????null : ??????????????????
     */
    private void templateToCode(Configuration configuration, Map dataModel, FolderFile folderFile, boolean isReplace, File outFolder) throws IOException, TemplateException {

        if (configuration == null || CollectionUtils.isEmpty(dataModel) || folderFile == null) {
            return;
        }

        if (outFolder != null && !outFolder.exists()) {
            throw new RuntimeException("??????????????????");
        }

        final File outFolderCopy = outFolder;

        boolean isFile = FolderFileEnum.Type.isFile(folderFile.getType());
        if (!isFile) {
            //??????????????????
            if (isReplace && !CollectionUtils.isEmpty(folderFile.getNodes())) {
                //??????
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

        //???????????????
        String name = folderFile.getName();
        if (name.indexOf("${table.codeHumpAll}") == 0) {
            name = name.replace("${table.codeHumpAll}", table.getCodeHumpAll());
        }

        //?????????????????????
        String outFolderPath = null;
        if (outFolder == null) {
            outFolderPath = folderFile.getBasePath() + "-out" + "/" + folderFile.getRelativePath() + "/";
        } else {
            outFolderPath = outFolder.getPath() + "/" + folderFile.getRelativePath() + "/";
        }
        outFolderPath = outFolderPath.replace("${table.codeHump}", table.getCodeHump());

        //????????????
        File newFile = new File(outFolderPath + name + folderFile.getSuffix());

        //?????????????????????????????????
        if (newFile.exists() && !isReplace) {
            //?????????
            return;
        }

        //???????????????
        if (outFolder == null) {
            outFolder = new File(outFolderPath);
        }
        if (!outFolder.exists()) {
            outFolder.mkdirs();
        }

        Writer out = new FileWriter(newFile);
        //????????????
        Template template = configuration.getTemplate(folderFile.getRelativePath() + "/" + folderFile.getName() + folderFile.getSuffix());
        //?????????????????????process?????????????????????
        template.process(dataModel, out);

        //????????????
        out.close();
    }

}
