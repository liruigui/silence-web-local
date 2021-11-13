package <%=config.javaPackages.managerImpl%>;

import com.alibaba.fastjson.JSON;
import <%=config.javaPackages.manager%>.<%=table.codeHumpAll%>Manager;
import <%=config.javaPackages.mapper%>.<%=table.codeHumpAll%>Mapper;
import <%=config.javaPackages.model%>.<%=table.codeHumpAll%>;
import <%=config.javaPackages.query%>.<%=table.codeHumpAll%>Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <%=table.name%> 管理接口实现类
 *
 * @author <%=config.author%>.<%=config.date%>
 */
@Component
public class <%=table.codeHumpAll%>ManagerImpl implements <%=table.codeHumpAll%>Manager {

    Logger logger = LoggerFactory.getLogger(<%=table.codeHumpAll%>ManagerImpl.class);

    @Autowired
    private <%=table.codeHumpAll%>Mapper <%=table.codeHump%>Mapper;

    /**
     * 根据ID查询数据
     *
     * @param idList 主键ID集合
     * @return ServiceResult
     * @author <%=config.author%>.<%=config.date%>
     */
    @Override
    public List<<%=table.codeHumpAll%>> selectByIds(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return new ArrayList<>();
        }
        return <%=table.codeHump%>Mapper.selectByIds(idList);
    }

    /**
     * 查询数据总条数
     *
     * @param query 查询参数对象
     * @return ServiceResult
     * @author <%=config.author%>.<%=config.date%>
     */
    @Override
    public long selectCount(<%=table.codeHumpAll%>Query query) {
        logger.info("调用<%=table.codeHump%>Mapper.selectCount param:{}", JSON.toJSONString(query));
        return <%=table.codeHump%>Mapper.selectCount(query);
    }

    /**
     * 查询数据
     *
     * @param query 查询参数对象
     * @return ServiceResult
     * @author <%=config.author%>.<%=config.date%>
     */
    @Override
    public List<<%=table.codeHumpAll%>> selectList(<%=table.codeHumpAll%>Query query) {
        logger.info("调用<%=table.codeHump%>Mapper.selectList param:{}", JSON.toJSONString(query));
        return <%=table.codeHump%>Mapper.selectList(query);
    }<%if(edit.has){%>

    /**
     * 编辑数据（新增或更新）
     * 不做任何逻辑，请正确传入数据
     * 不打印日志，请调用前打印日志
     * 慎重使用，建议仅为页面操作，若用于其它业务，天网恢恢，疏而不漏，后果自负。
     *
     * @param insertList       [非必填] 新增数据
     * @param updateList       [非必填] 更新数据，根据实体内的ID将不为NULL的字段进行更新，长度为0的String字段也会被更新
     * @param operationLogList [非必填] 操作日志
     * @return >= 0
     * @author <%=config.author%>.<%=config.date%>
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public int edit(List<<%=table.codeHumpAll%>> insertList, List<<%=table.codeHumpAll%>> updateList, List operationLogList) {

        int count = 0;
        count += CollectionUtils.isEmpty(insertList) ? 0 : <%=table.codeHump%>Mapper.insert(insertList);
        count += CollectionUtils.isEmpty(updateList) ? 0 : <%=table.codeHump%>Mapper.update(updateList);

        if (count > 0 && !CollectionUtils.isEmpty(operationLogList)) {
            // 操作日志
            // ...
        }
        return count;
    }<%}%>

}
