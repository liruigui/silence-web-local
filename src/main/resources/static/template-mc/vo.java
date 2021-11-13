package <%=config.javaPackages.vo%>;

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
public class <%=table.codeHumpAll%>Vo extends <%=table.codeHumpAll%> implements Serializable {

<%columns.forEach(item=>{if(item.typeJava==='Integer'){%>
    /**
     * <%=item.name%>
     */
    private String <%=item.codeHump%>Str;
<% }}) %><%if(!system.lombok){columns.forEach(item=>{ if(item.typeJava==='Integer'){%>
    public String get<%=item.codeHumpAll%>Str() {
        return <%=item.codeHump%>Str;
    }

    public void set<%=item.codeHumpAll%>Str(String <%=item.codeHump%>Str) {
        this.<%=item.codeHump%>Str = <%=item.codeHump%>Str;
    }
<% }})} %>
}