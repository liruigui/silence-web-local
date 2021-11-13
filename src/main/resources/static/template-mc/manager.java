package <%=config.javaPackages.manager%>;

import <%=config.javaPackages.model%>.<%=table.codeHumpAll%>;
import <%=config.javaPackages.query%>.<%=table.codeHumpAll%>Query;

import java.util.List;

/**
 * <%=table.name%> 管理接口
 *
 * @author <%=config.author%>.<%=config.date%>
 */
public interface <%=table.codeHumpAll%>Manager {

    /**
     * 根据ID查询数据，最多仅查询 2000 条数据
     *
     * @param idList 主键ID集合
     * @return list ( size >= 0 )
     * @author <%=config.author%>.<%=config.date%>
     */
    List<<%=table.codeHumpAll%>> selectByIds(List<Long> idList);

    /**
     * 查询数据总条数
     *
     * @param query 查询参数对象
     * @return >= 0
     * @author <%=config.author%>.<%=config.date%>
     */
    long selectCount(<%=table.codeHumpAll%>Query query);

    /**
     * 查询数据
     *
     * @param query 查询参数对象
     * @return list ( size >= 0 )
     * @author <%=config.author%>.<%=config.date%>
     */
    List<<%=table.codeHumpAll%>> selectList(<%=table.codeHumpAll%>Query query);<%if(edit.has){%>

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
     * @author lrg.2020-14-07
     */
    int edit(List<<%=table.codeHumpAll%>> insertList, List<<%=table.codeHumpAll%>> updateList, List operationLogList);<%}%>

}
