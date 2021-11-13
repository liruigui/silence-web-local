package <%=config.javaPackages.query%>;

import <%=config.javaPackages.model%>.<%=table.codeHumpAll%>;<%if(system.lombok){%>
import lombok.Data;
<%}%>
import java.io.Serializable;

/**
 * <%=table.name%> 查询类
 *
 * @author <%=config.author%>.<%=config.date%>
 */<%if(system.lombok){%>
@Data<%}%>
public class <%=table.codeHumpAll%>Query extends <%=table.codeHumpAll%> implements Serializable {

    /**
     * 基本查询条件
     */
    private int startRecord;
    private int pageSize;
    private int pageNumber;
    private String sortName;
    private String sortOrder;

    public void setStartRecord(int startRecord) {
        this.startRecord = this.pageSize * (this.pageNumber - 1);
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        this.setStartRecord(0);
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        this.setStartRecord(0);
    }

}