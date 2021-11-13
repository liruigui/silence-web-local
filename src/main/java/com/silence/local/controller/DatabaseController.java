package com.silence.local.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.silence.module.common.enums.EnumFactory;
import com.silence.module.common.model.DataResult;
import com.silence.module.common.util.BuildSqlUtil;
import com.silence.module.common.util.CommonUtil;
import com.silence.module.common.util.MysqlDatabaseUtil;
import com.silence.module.db.api.enums.ConnectionEnum;
import com.silence.module.db.api.model.Connection;
import com.silence.module.db.api.query.ConnectionQuery;
import com.silence.module.db.api.service.ConnectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.*;


/**
 * 数据库 页面
 *
 * @author lrg.2020-12-20
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/database")
public class DatabaseController {

    @Resource
    private ConnectionService connectionService;

    /**
     * 进入页面
     *
     * @author lrg.2020-12-20
     */
    @RequestMapping(value = {"/index"}, method = RequestMethod.GET)
    public ModelAndView index(ModelAndView mav) {

        //进入页面
        mav.setViewName("database/index");

        // 枚举
        List list = EnumFactory.getList(ConnectionEnum.Type.class);
        mav.addObject("typeList", JSON.toJSON(JSON.toJSONString(list)));

        return mav;
    }

    /**
     * 进入页面
     *
     * @author lrg.2020-12-20
     */
    @RequestMapping(value = {"/page"}, method = RequestMethod.GET)
    public ModelAndView page(ModelAndView mav) {

        //进入页面
        mav.setViewName("database/page");

        return mav;
    }


    /**
     * 查询数据
     *
     * @author lrg.2020-12-20
     */
    @RequestMapping(value = "/index", method = RequestMethod.POST)
    public Object index(@RequestParam(value = "action", defaultValue = "") String action,
                        @RequestParam(value = "database", defaultValue = "") String database,
                        @RequestParam(value = "tableCode", defaultValue = "") String tableCode,
                        @RequestParam(value = "primaryKey", defaultValue = "") String primaryKey,
                        @RequestParam(value = "primaryKeyValue", defaultValue = "") String primaryKeyValue,
                        @RequestParam(value = "sqlSelectColumnText", defaultValue = "*") String sqlSelectColumnText,
                        @RequestParam(value = "jsonParamStr", defaultValue = "") String jsonParamStr,
                        @RequestParam(value = "connectionId", required = false) Long connectionId,
                        @ModelAttribute ConnectionQuery connectionQuery) throws Exception {

        // 获取连接
        Connection connection;
        if (connectionId != null) {
            DataResult<Connection> dr = connectionService.selectById(connectionId);
            if (!dr.getSuccess()) {
                return dr;
            }
            connection = dr.getData();
            Assert.isTrue(connection != null, "连接ID(" + connectionId + ")不存在");
        } else {
            connection = new Connection() {{
                setUrl(connectionQuery.getUrl());
                setUsername(connectionQuery.getUsername());
                setPassword(connectionQuery.getPassword());
            }};
        }

        // 连接数据库
        MysqlDatabaseUtil util = new MysqlDatabaseUtil(connection.getUrl(), connection.getUsername(), connection.getPassword()).setDatabase(database);

        // 分发请求
        switch (action) {
            case "databaseList": {

                JSONObject obj = util.DBCatalogs();

                util.close();

                return new DataResult<>(obj);
            }
            case "tableList": {
                List<Map<String, Object>> list = util.setDatabase(database).getTableList();

                list.forEach(item -> {
                    item.put("uuid", UUID.randomUUID());
                    item.put("name", (String) item.getOrDefault("tableName", "") +" "+ item.getOrDefault("tableComment", ""));
                });

                util.close();
                return new DataResult<>(list);
            }
            case "tableInfo": {

                List<Map<String, Object>> list = util.setDatabase(database).getColumnList(tableCode);

                list.forEach(item -> {
                    item.put("name", item.getOrDefault("columnName", ""));
                });


                String createTableSql = util.getCreateTableSql(tableCode);

                // 获取表描述
                String[] sqlArr = createTableSql.split(" ");
                String tableComment = sqlArr[sqlArr.length - 1];
                if (tableComment.contains("COMMENT")) {
                    tableComment = tableComment.replace("COMMENT=", "").replace("'", "");
                } else {
                    tableComment = "";
                }

                JSONObject json = new JSONObject();
                json.put("tableCode", tableCode);
                json.put("tableComment", tableComment);
                json.put("createTableSql", createTableSql);
                json.put("columnList", list);

                util.close();
                return new DataResult<>(json);
            }
            case "count": {
                long count = util.executeCount(tableCode + BuildSqlUtil.where(jsonParamStr));
                util.close();
                return new DataResult<>(count);
            }
            case "updateByPrimaryKey": {
                Assert.isTrue(StringUtils.hasLength(primaryKey), "主键字段不能为空");
                Assert.isTrue(StringUtils.hasLength(primaryKeyValue), "主键字段值不能为空");
                //String sqlSet = this.getSqlUpdateSet(jsonParamStr).toString();
                int count = util.executeUpdate(BuildSqlUtil.update(tableCode,jsonParamStr,new HashMap<String,Object>(1){{
                    put(primaryKey,primaryKeyValue);
                }}));
                util.close();
                return new DataResult<>(count);
            }
            default: {
                break;
            }
        }

        //String sqlWhere = this.getSqlWhere(jsonParamStr).toString();
        //List<Map<String, Object>> list = util.executeQuery("select " + sqlSelectColumnText + " from " + tableCode + sqlWhere + " order by " + primaryKey + " desc limit " + ((connectionQuery.getPageNumber() - 1) * connectionQuery.getPageLength()) + " , " + connectionQuery.getPageLength());
        Map<String, Object> mapParam = CommonUtil.toMap(jsonParamStr);
        mapParam.put("pageNumber",connectionQuery.getPageNumber());
        mapParam.put("pageLength",connectionQuery.getPageLength());
        mapParam.put("_orderBy",new ArrayList<JSONObject>(){{
            add(new JSONObject(){{
                put("name",primaryKey);
                put("value","DESC");
            }});
        }});
        List<Map<String, Object>> list = util.executeQuery(BuildSqlUtil.selectList(tableCode,sqlSelectColumnText,mapParam));
        util.close();
        list.forEach(item -> {
            Iterator<Map.Entry<String, Object>> it = item.entrySet().iterator();
            while (it.hasNext()) {

                Map.Entry<String, Object> entry = it.next();

                Object value = entry.getValue();
                // 解决前端Number丢精度问题
                if (value instanceof Long) {
                    String valueStr = String.valueOf(value);
                    int valueStrLen = valueStr.length();
                    // js number 最大值 9007199254740992 (16位)
                    // 数据库存时间戳(ms)是13位
                    if (valueStrLen > 13) {
                        item.put(entry.getKey(), valueStr);
                    }
                }
            }
        });
        return new DataResult<>(list);
    }
}
