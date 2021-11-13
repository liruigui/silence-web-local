package <%=config.javaPackages.serviceImpl%>;

import com.alibaba.fastjson.JSON;
import com.sprucetec.bone.common.ServiceResult;
import <%=config.javaPackages.manager%>.<%=table.codeHumpAll%>Manager;
import <%=config.javaPackages.model%>.<%=table.codeHumpAll%>;
import <%=config.javaPackages.query%>.<%=table.codeHumpAll%>Query;
import <%=config.javaPackages.service%>.<%=table.codeHumpAll%>Service;
import com.sprucetec.wms.util.ConstantUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <%=table.name%> 服务接口实现类
 *
 * @author <%=config.author%>.<%=config.date%>
 */
@Service
public class <%=table.codeHumpAll%>ServiceImpl implements <%=table.codeHumpAll%>Service {

    private Logger logger = LoggerFactory.getLogger(<%=table.codeHumpAll%>ServiceImpl.class);

    @Autowired
    private <%=table.codeHumpAll%>Manager <%=table.codeHump%>Manager;

    /**
     * 根据ID查询数据
     *
     * @param idList 主键ID集合
     * @return ServiceResult
     * @author <%=config.author%>.<%=config.date%>
     */
    @Override
    public ServiceResult<List<<%=table.codeHumpAll%>>> selectByIds(<%=system.multipleDatabase?'Long warehouseId, ':''%>List<Long> idList) {

        idList = idList == null ? null : idList.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());

        if (CollectionUtils.isEmpty(idList)) {
            return new ServiceResult<>(new ArrayList<>());
        }

        final int maxSize = 2000;

        if (idList.size() > maxSize) {
            return new ServiceResult<>(ConstantUtil.OpCode.BUSINESS_EXCEPTION, "最大可查询 " + maxSize + " 条数据");
        }

        return new ServiceResult<>(<%=table.codeHump%>Manager.selectByIds(idList));
    }

    @Override
    public ServiceResult<Long> selectCount(<%=table.codeHumpAll%>Query query) {
        return new ServiceResult<>(<%=table.codeHump%>Manager.selectCount(query));
    }

    @Override
    public ServiceResult<List<<%=table.codeHumpAll%>>> selectList(<%=table.codeHumpAll%>Query query) {
        return new ServiceResult<>(<%=table.codeHump%>Manager.selectList(query));
    }<%if(edit.has){%>

    /**
     * 编辑数据（新增或更新）
     * 仅为页面操作可用，若用于其它业务，天网恢恢，疏而不漏，后果自负。
     *
     * @param editList 编辑数据
     * @return ServiceResult 结果
     * @author lrg.2020-15-07
     */
    @Override
    public ServiceResult<Integer> editByWeb(<%=system.multipleDatabase?'Long warehouseId, ':''%>List<<%=table.codeHumpAll%>> editList) {

        if (CollectionUtils.isEmpty(editList)) {
            return new ServiceResult<>(0);
        }

        // 数据统一检查
        editList.forEach(item -> {<%columns.filter(item=>(({cu:true,uu:true,creater:true,createrName:true,updater:true,updaterName:true})[item.codeHump])).forEach(item=>{ let {name,codeHump,codeHumpAll,typeJava,typeSqlSize}=item; if(typeJava==='String'){%>
            Assert.isTrue(StringUtils.hasLength(item.get<%=codeHumpAll%>()), "<%=name%>必填");
            Assert.isTrue(item.get<%=codeHumpAll%>().length() <= <%=typeSqlSize%>, "<%=name%>长度不允许大于<%=typeSqlSize%>个字符");<%}else{%>
            Assert.isTrue(item.get<%=codeHumpAll%>() != null, "<%=name%>不允许为空");<%} })%>
        });

        // 新增数据
        final List<<%=table.codeHumpAll%>> insertList = editList.stream().filter(item -> item.getId() == null).collect(Collectors.toList());
        // 更新数据
        final List<<%=table.codeHumpAll%>> updateList = editList.stream().filter(item -> item.getId() != null).collect(Collectors.toList());

        // 查询要更新的源数据，并映射为Map<ID,数据库实体> 用于操作日志对比
        final List<Long> updateIds = updateList.stream().map(<%=table.codeHumpAll%>::getId).collect(Collectors.toList());
        final List<<%=table.codeHumpAll%>> dbList = CollectionUtils.isEmpty(updateIds) ? new ArrayList<>() : <%=table.codeHump%>Manager.selectByIds(updateIds);
        final Map<Long, <%=table.codeHumpAll%>> idDatabaseEntity = dbList.stream().collect(Collectors.toMap(<%=table.codeHumpAll%>::getId, Function.identity()));

        // 新增数据检查
        insertList.forEach(item -> {
            // 检查数据合法性<%columns.filter(item=>!(({id:true,isDeleted:true,ct:true,ut:true,cu:true,uu:true,creator:true,creatorName:true,creater:true,createrName:true,updater:true,updaterName:true})[item.codeHump])).forEach(item=>{ let {name,codeHump,codeHumpAll,typeJava,typeSqlSize}=item; if(typeJava==='String'){%>
            Assert.isTrue(StringUtils.hasLength(item.get<%=codeHumpAll%>()), "<%=name%>必填");
            Assert.isTrue(item.get<%=codeHumpAll%>().length() <= <%=typeSqlSize%>, "<%=name%>长度不允许大于<%=typeSqlSize%>个字符");<%}else{%>
            Assert.isTrue(item.get<%=codeHumpAll%>() != null, "<%=name%>不允许为空");<%} })%>
        });

        // 更新数据检查
        updateList.forEach(item -> {
            // 检查数据合法性<%columns.filter(item=>!(({cu:true,uu:true,creater:true,createrName:true,updater:true,updaterName:true})[item.codeHump])).forEach(item=>{ let {name,codeHump,codeHumpAll,typeJava,typeSqlSize}=item; if(typeJava==='String'){%>
            Assert.isTrue(item.get<%=codeHumpAll%>() == null || item.get<%=codeHumpAll%>().length() <= <%=typeSqlSize%>, "<%=name%>长度不允许大于<%=typeSqlSize%>个字符");<%} })%>
        });

        // 组装操作日志
        List operationLogList = new ArrayList();
        // ...

        logger.info("调用编辑功能Manager.edit({}, {}, {})", JSON.toJSONString(insertList), JSON.toJSONString(updateList), JSON.toJSONString(operationLogList));
        int count = <%=table.codeHump%>Manager.edit(insertList, updateList, operationLogList);
        logger.info("调用编辑功能Manager.edit 成功条数:{}", count);

        return new ServiceResult<>(count);
    }<%}%>

}
