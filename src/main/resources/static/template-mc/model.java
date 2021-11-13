package <%=config.javaPackages.model%>;

<%if(system.lombok){%>
import lombok.Data;
<%}%>
import java.io.Serializable;

/**
 * <%=table.name%>
 *
 * @author <%=config.author%>.<%=config.date%>
 */<%if(system.lombok){%>
@Data<%}%>
public class <%=table.codeHumpAll%> implements Serializable {

<%columns.forEach(item=>{%>
    /**
     * <%=item.name%>
     */
    private <%=item.typeJava%> <%=item.codeHump%>;
<% }) %><%if(!system.lombok){columns.forEach(item=>{%>
    public <%:=item.typeJava%> get<%=item.codeHumpAll%>() {
        return <%=item.codeHump%>;
    }

    public void set<%=item.codeHumpAll%>(<%=item.typeJava%> <%=item.codeHump%>) {
        this.<%=item.codeHump%> = <%=item.codeHump%>;
    }
<% })} %>
}