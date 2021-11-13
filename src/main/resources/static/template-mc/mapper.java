package <%=config.javaPackages.mapper%>;

import <%=config.javaPackages.model%>.<%=table.codeHumpAll%>;
import <%=config.javaPackages.query%>.<%=table.codeHumpAll%>Query;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <%=table.name%>
 *
 * @author <%=config.author%>.<%=config.date%>
 */
public interface <%=table.codeHumpAll%>Mapper {


    /**
     * 根据ID查询数据
     *
     * @param ids 主键ID集合
     * @return list ( size >= 0 )
     * @author <%=config.author%>.<%=config.date%>
     */
    List<<%=table.codeHumpAll%>> selectByIds(@Param("ids") List<Long> ids);

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
    List<<%=table.codeHumpAll%>> selectList(<%=table.codeHumpAll%>Query query);

    /**
     * 新增数据
     *
     * @param entityList 新增数据
     * @return >= 0
     * @author lrg.2020-14-07
     */
    int insert(List<<%=table.codeHumpAll%>> entityList);

    /**
     * 更新数据
     * 根据实体内的ID将不为NULL的字段进行更新，长度为0的String字段也会被更新
     *
     * @param entityList 更新数据
     * @return >= 0
     * @author <%=config.author%>.<%=config.date%>
     */
    int update(List<<%=table.codeHumpAll%>> entityList);

}
