package <%=config.javaPackages.controller%>;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.sprucetec.bone.common.ServiceResult;
import <%=config.javaPackages.enum%>.<%=table.codeHumpAll%>Enum;
import <%=config.javaPackages.model%>.<%=table.codeHumpAll%>;
import <%=config.javaPackages.query%>.<%=table.codeHumpAll%>Query;
import <%=config.javaPackages.service%>.<%=table.codeHumpAll%>Service;
import com.sprucetec.wms.enums.EnumUtil;
import com.sprucetec.wms.model.DataResult;
import com.sprucetec.wms.model.ListResult;
import com.sprucetec.wms.sso.model.UserInfo;
import com.sprucetec.wms.util.ConstantUtil;
import com.sprucetec.wms.utils.SSOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

/**
 * <%=table.name%> 页面
 *
 * @author <%=config.author%>.<%=config.date%>
 */
@RestController
@RequestMapping("/<%=table.codeHump%>")
public class <%=table.codeHumpAll%>Controller {

    @Autowired
    private <%=table.codeHumpAll%>Service <%=table.codeHump%>Service;

    /**
     * 进入页面
     *
     * @author <%=config.author%>.<%=config.date%>
     */
    @RequestMapping(value = "/index.mc", method = RequestMethod.GET)
    public ModelAndView index(ModelAndView mav) {

        //进入页面
        mav.setViewName("<%=table.codeHump%>/index");

        // 枚举<% columns.filter(item=>item.options).forEach(item=>{ %>
        <%mav.addObject("<%=item.codeHump%>List", JSON.toJSON(EnumUtil.getList(<%=table.codeHumpAll%>Enum.<%=item.codeHumpAll%>.class))); %>
        <% }) %>

        return mav;
    }

    /**
     * 查询数据
     *
     * @author <%=config.author%>.<%=config.date%>
     */
    @RequestMapping(value = "/index.mc", method = RequestMethod.POST)
    public Map<?, ?> index(@ModelAttribute <%=table.codeHumpAll%>Query query) {

        ServiceResult<Long> srCount = <%=table.codeHump%>Service.selectCount(query);
        Assert.isTrue(srCount.getSuccess(), srCount.getMessage());

        long count = srCount.getBody() == null ? 0L : srCount.getBody();
        if (count == 0) {
            return new ListResult(ConstantUtil.OpCode.HANDLE_SUCCESS, count, query.getPageSize(), query.getPageNumber(), new ArrayList<>()).toMap();
        }

        ServiceResult<List<<%=table.codeHumpAll%>>> sr = <%=table.codeHump%>Service.selectList(query);
        Assert.isTrue(sr.getSuccess(), sr.getMessage());
        List<<%=table.codeHumpAll%>> list = sr.getBody();

        return new ListResult(ConstantUtil.OpCode.HANDLE_SUCCESS, count, query.getPageSize(), query.getPageNumber(), list).toMap();

    }<%if(edit.has){%>

    /**
     * 编辑数据
     *
     * @author <%=config.author%>.<%=config.date%>
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Map<?, ?> edit(@ModelAttribute <%=table.codeHumpAll%> entity) {

        UserInfo userInfo = SSOUtils.getCurrentUserInfo();<%if(system.multipleDatabase){%>
        Long warehouseId = Long.valueOf(userInfo.getWarehouseId());<%}%>

        //设置操作人<%columns.filter(item=>(({cu:true,uu:true,creater:true,createrName:true,updater:true,updaterName:true})[item.codeHump])).forEach(item=>{ let {name,codeHump,codeHumpAll,typeJava,typeSqlSize}=item; if(typeJava==='String'){%>
        entity.set<%=codeHumpAll%>(userInfo.getUser().getName());<%}else{%>
        entity.set<%=codeHumpAll%>(userInfo.getUser().getId());<%} })%>

        ServiceResult<Integer> sr = <%=table.codeHump%>Service.editByWeb(<%=system.multipleDatabase?'warehouseId, ':''%>Lists.newArrayList(entity));
        Assert.isTrue(sr.getSuccess(), sr.getMessage());

        return new DataResult<>(ConstantUtil.OpCode.HANDLE_SUCCESS, sr.getBody()).toMap();
    }<%}%>

}
