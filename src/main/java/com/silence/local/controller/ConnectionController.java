package com.silence.local.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.silence.module.common.enums.EnumFactory;
import com.silence.module.common.enums.OpCodeEnum;
import com.silence.module.common.model.DataResult;
import com.silence.module.common.util.MysqlDatabaseUtil;
import com.silence.module.db.api.enums.ConnectionEnum;
import com.silence.module.db.api.model.Connection;
import com.silence.module.db.api.query.ConnectionQuery;
import com.silence.module.db.api.service.ConnectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

;

/**
 * 连接表 页面
 *
 * @author lrg.2020-09-10
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/connection")
public class ConnectionController {

    @Resource
    private ConnectionService connectionService;

    /**
     * 进入页面
     *
     * @author lrg.2020-09-10
     */
    @RequestMapping(value = {"/index"}, method = RequestMethod.GET)
    public ModelAndView index(ModelAndView mav) {

        //进入页面
        mav.setViewName("connection/index");

        // 枚举
        List list = EnumFactory.getList(ConnectionEnum.Type.class);
        mav.addObject("typeList", JSON.toJSON(JSON.toJSONString(list)));

        return mav;
    }

    /**
     * 查询数据
     *
     * @author lrg.2020-09-10
     */
    @RequestMapping(value = "/index", method = RequestMethod.POST)
    public Map<?, ?> index(@ModelAttribute ConnectionQuery query) {

        DataResult<Long> srCount = connectionService.selectCount(query);
        Assert.isTrue(srCount.getSuccess(), srCount.getMessage());

        long count = srCount.getData() == null ? 0L : srCount.getData();
        List<Connection> list = null;
        if (count > 0) {
            DataResult<List<Connection>> sr = connectionService.selectList(query);
            Assert.isTrue(sr.getSuccess(), sr.getMessage());
            list = sr.getData() == null ? new ArrayList<>() : sr.getData();
            list.forEach(item -> item.setPassword(""));
            list.sort((Comparator.comparingInt(Connection::getSequence)));
        }

        return new DataResult<>(count, list).toMap();
    }

    /**
     * 编辑数据
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Map<?, ?> edit(Connection entity) {


        //设置操作人
        entity.setUpdater(0L);
        entity.setUpdaterName("1234");

        if (StringUtils.isEmpty(entity.getPassword())) {
            entity.setPassword(null);
        }

        DataResult<Integer> sr = connectionService.editByWeb(Lists.newArrayList(entity));
        Assert.isTrue(sr.getSuccess(), sr.getMessage());

        return new DataResult<>(sr.getData()).toMap();
    }

    /**
     * 连接测试
     *
     * @param id ID
     */
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public Map<?, ?> test(@RequestParam("id") Long id) {

        DataResult<Connection> dr = connectionService.selectById(id);
        if (!dr.getSuccess()) {
            return dr.toMap();
        }

        Connection connection = dr.getData();
        Assert.isTrue(connection != null, "数据不存在");

        ConnectionEnum.Type typeEnum = EnumFactory.getByKey(ConnectionEnum.Type.class, connection.getType());
        switch (typeEnum) {
            case MYSQL: {
                return this.testMySql(connection).toMap();
            }
            default: {
                return new DataResult<>(OpCodeEnum.SUCCESS.getKey(), "连接类型错误").toMap();
            }
        }
    }

    /**
     * 测试MySql连接
     *
     * @param connection 连接
     * @return DataResult
     */
    private DataResult testMySql(Connection connection) {
        MysqlDatabaseUtil util = new MysqlDatabaseUtil(connection.getUrl(), connection.getUsername(), connection.getPassword());
        try {
            util.setDatabase(null);
            List<Map<String, Object>> list = util.executeQuery("SHOW GRANTS FOR "+connection.getUsername()+"@'localhost'");
            System.out.println(list);
            util.close();
            return new DataResult<>(OpCodeEnum.SUCCESS.getKey(), "连接成功");
        } catch (SQLException e) {
            return new DataResult<>(e, "连接失败");
        }
    }

}
