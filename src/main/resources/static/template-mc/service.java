package <%=config.javaPackages.service%>;


import com.sprucetec.bone.common.ServiceResult;
import <%=config.javaPackages.model%>.<%=table.codeHumpAll%>;
import <%=config.javaPackages.query%>.<%=table.codeHumpAll%>Query;

import java.util.List;

/**
 * <%=table.name%> 服务接口
 *
 * @author <%=config.author%>.<%=config.date%>
 */
public interface <%=table.codeHumpAll%>Service {

    /**
     * 根据ID查询数据
     *
     * @param idList 主键ID集合
     * @return ServiceResult
     * @author <%=config.author%>.<%=config.date%>
     */
    ServiceResult<List<<%=table.codeHumpAll%>>> selectByIds(<%=system.multipleDatabase?'Long warehouseId, ':''%>List<Long> idList);

    /**
     * 查询数据总条数
     *
     * @param query 查询参数对象
     * @return ServiceResult
     * @author <%=config.author%>.<%=config.date%>
     */
    ServiceResult<Long> selectCount(<%=table.codeHumpAll%>Query query);

    /**
     * 查询数据
     *
     * @param query 查询参数对象
     * @return ServiceResult
     * @author <%=config.author%>.<%=config.date%>
     */
    ServiceResult<List<<%=table.codeHumpAll%>>> selectList(<%=table.codeHumpAll%>Query query);<%if(edit.has){%>

    /**
     * 编辑数据（新增或更新）
     * 仅为页面操作可用，若用于其它业务，天网恢恢，疏而不漏，后果自负。
     *
     * @param entityList 编辑数据
     * @return ServiceResult
     * @author <%=config.author%>.<%=config.date%>
     */
    ServiceResult<Integer> editByWeb(<%=system.multipleDatabase?'Long warehouseId, ':''%>List<<%=table.codeHumpAll%>> entityList);<%}%>

}
