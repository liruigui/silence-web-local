package <%=config.javaPackages.enum%>;

import com.sprucetec.wms.enums.EnumBase;

/**
 * <%=table.name%> 枚举
 * 调用：EnumUtil.getList(枚举.class));
 *
 * @author <%=config.author%>.<%=config.date%>
 */
public class <%=table.codeHumpAll%>Enum {

    <% columns.filter(item=>item.options).forEach(item=>{ let {name,codeHump,codeHumpAll,options}=item;%>
    /**
     * <%=name%>
     */
    public enum <%=codeHumpAll%> implements EnumBase<Integer, String> {

        <%=options.map(o=>(o.code||o.id)+'('+o.id+', "'+o.name+'")').join(',\n        ') %>;

        <%=codeHumpAll%>(Integer key, String value) {
            this.key = key;
            this.value = value;
        }

        private Integer key;
        private String value;

        @Override
        public Integer getKey() {
            return this.key;
        }

        @Override
        public String getValue() {
            return this.value;
        }
    }
    <%})%>
}
