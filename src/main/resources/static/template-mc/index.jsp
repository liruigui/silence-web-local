<<%=config.symbolPercent%>@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" <%=config.symbolPercent%>>
<<%=config.symbolPercent%>@ page isELIgnored="false" <%=config.symbolPercent%>>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title><%=table.name%></title>
    <!-- <%=config.author%>.<%=config.date%> -->
    <jsp:include page="<%=system.include.style%>"/>
    <style type="text/css">

    </style>
</head>
<body>
<div id="app" >
    <el-form size="small" :inline="true" label-width="80px" class="form-query"><%if(query.hasWarehouse){%>
        <el-form-item label="仓库">
            <el-select v-model="queryParam.warehouseId" filterable placeholder="仓库" style="width:200px;">
                <el-option v-for="item in warehouseList" :key="'q_warehouse_'+item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
        </el-form-item><%} if(query.hasClass1){%>
        <el-form-item label="一级分类">
            <el-select :disabled="queryParam.searchSku?true:false" v-model="queryParam.class1Id" filterable clearable placeholder="一级分类" >
                <el-option v-for="item in class1List" :key="'q_class1_'+item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
        </el-form-item><%} if(query.hasClass2){%>
        <el-form-item label="二级分类">
            <el-select :disabled="queryParam.searchSku?true:false" v-model="queryParam.class2Id" filterable clearable placeholder="二级分类" >
                <el-option v-for="item in class2List.filter(item=>item.class1Id===queryParam.class1Id)" :key="'q_class2_'+item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
        </el-form-item><%} if(query.hasClass3){%>
        <el-form-item label="三级分类">
            <el-select :disabled="queryParam.searchSku?true:false" v-model="queryParam.class3Id" filterable clearable placeholder="三级分类" >
                <el-option v-for="item in class3List" :key="'q_class3_'+item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
        </el-form-item><%} if(query.hasDeliveryDate){%>
        <el-form-item label="配送日期" >
            <el-date-picker v-model="queryParam.deliveryDateArr" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" :picker-options="pickerOptions2" ></el-date-picker>
        </el-form-item><%} query.conditions.filter(item=>item.has).forEach(field=>{ %>
        <el-form-item label="<%=field.name%>"><%if(field.controlId===0){%>
            <label v-html="<%=field.value||'无控件，显示值'%>"></label><%}%><%if(field.controlId===1){%>
            <el-input clearable v-model="queryParam.<%=field.codeHump%>" placeholder="<%=field.placeholder||field.name||''%>" ></el-input><%}%><%if(field.controlId===2){%>
            <el-input clearable v-model="queryParam.<%=field.codeHump%>" placeholder="<%=field.placeholder||field.name||''%>" type="textarea" :rows="2" ></el-input><%}%><%if(field.controlId===3){%>
            <el-input-number clearable v-model="queryParam.<%=field.codeHump%>" placeholder="<%=field.placeholder||field.name||''%>" controls-position="right" :min="1" :max="99999999" ></el-input-number><%}%><%if(field.controlId===4){%>
            <el-select clearable filterable v-model="queryParam.<%=field.codeHump%>" placeholder="<%=field.placeholder||field.name||''%>" >
                <el-option v-for="item in <%=field.codeHump%>List" :key="'q_<%=field.codeHump%>_'+item.key" :label="item.value" :value="item.key"></el-option>
            </el-select><%}%><%if(field.controlId===5){%>
            <el-select clearable filterable multiple v-model="queryParam.<%=field.codeHump%>" placeholder="<%=field.placeholder||field.name||''%>" >
                <el-option v-for="item in <%=field.codeHump%>List" :key="'q_<%=field.codeHump%>_'+item.key" :label="item.value" :value="item.key"></el-option>
            </el-select><%}%><%if(field.controlId===6){%>
            <el-date-picker clearable v-model="queryParam.<%=field.codeHump%>" placeholder="<%=field.placeholder||field.name||''%>" type="date"></el-date-picker><%}%><%if(field.controlId===7){%>
            <el-date-picker clearable v-model="queryParam.<%=field.codeHump%>DateArr" type="daterange" range-separator="至" start-placeholder="开始" end-placeholder="结束"></el-date-picker><%}%><%if(field.controlId===8){%>
            <el-time-picker clearable v-model="queryParam.<%=field.codeHump%>" placeholder="<%=field.placeholder||field.name||''%>"></el-time-picker><%}%><%if(field.controlId===9){%>
            <el-time-picker clearable v-model="queryParam.<%=field.codeHump%>TimeArr" is-range range-separator="至" start-placeholder="开始" end-placeholder="结束" ></el-time-picker><%}%>
        </el-form-item><% }) %>
        <el-form-item label=" " >
            <el-button type="primary" plain @click="handleQuery(1)" v-loading.fullscreen.lock="fullscreenLoading"><span v-text="'查询'"></span></el-button>
            <el-button type="warning" plain @click="handleQuery('reset')"><span v-text="'重置'"></span></el-button>
        </el-form-item>
    </el-form>
    <el-row>
        <el-button size="small" type="primary"  @click="handleEdit('open', {} )" ><span v-text="'新增'"></span></el-button>
    </el-row>
    <el-table border size="small" :data="queryResult.data" stripe tooltip-effect="dark">
        <el-table-column header-align="center" align="center" type="index" width="50" label="#"></el-table-column>
        <el-table-column header-align="center" align="center" label="操作" width="50">
            <template slot-scope="scope">
                <el-button size="mini" type="text" @click="handleEdit('open', scope.row ,scope.$index)" ><span v-text="'编辑'"></span></el-button>
            </template>
        </el-table-column><%for(let i = 0; i < query.columns.length; i++) {let {has,name,codeHump,codeHumpAll,typeJava,typeSqlSize,options}=query.columns[i];if(!has){continue;}%>
        <el-table-column header-align="center" align="center" prop="<%=codeHump%><%=options?'Str':''%>" label="<%=name%>"></el-table-column><%}%>
    </el-table><%if(query.pageType===1){%>
    <div class="block">
        <el-pagination
                :hide-on-single-page="false" layout="total, sizes, prev, pager, next, jumper" :page-sizes="[10,20,50,100, 200]"
                :page-size="queryParam.pageSize" :current-page="queryParam.pageNumber" :total="queryResult.count"
                @size-change="val=>handleQuery('size',val)" @current-change="handleQuery">
        </el-pagination>
    </div><%} if(edit.has){%>

    <!-- 数据编辑 -->
    <el-dialog :close-on-click-modal="false" :title="edit.title" :visible.sync="edit.show" @close="handleEdit('close')" width="680px" >
        <el-form size="small" :inline="true" :ref="edit.formref" :model="edit.data" :rules="edit.rules" label-width="110px" class="form-query"><% edit.columns.filter(item=>item.has).forEach(field=>{ if(({id:true,warehouseId:true,warehouseName:true,cu:true,uu:true,ct:true,ut:true,creater:true,createrName:true,updater:true,updaterName:true})[field.codeHump]){return true;} %>
            <el-form-item label="<%=field.name%>"  prop="<%=field.codeHump%>" ><%if(field.controlId===0){%>
                <label v-html="<%=field.value||'无控件，显示值'%>"></label><%}%><%if(field.controlId===1){%>
                <el-input clearable v-model="edit.data.<%=field.codeHump%>" placeholder="<%=field.placeholder||field.name||''%>" ></el-input><%}%><%if(field.controlId===2){%>
                <el-input clearable v-model="edit.data.<%=field.codeHump%>" placeholder="<%=field.placeholder||field.name||''%>" type="textarea" :rows="2" ></el-input><%}%><%if(field.controlId===3){%>
                <el-input-number clearable v-model="edit.data.<%=field.codeHump%>" placeholder="<%=field.placeholder||field.name||''%>" controls-position="right" :min="1" :max="99999999" ></el-input-number><%}%><%if(field.controlId===4){%>
                <el-select clearable filterable v-model="edit.data.<%=field.codeHump%>" placeholder="<%=field.placeholder||field.name||''%>" >
                    <el-option v-for="item in <%=field.codeHump%>List" :key="'e_<%=field.codeHump%>_'+item.key" :label="item.value" :value="item.key"></el-option>
                </el-select><%}%><%if(field.controlId===5){%>
                <el-select clearable filterable multiple v-model="edit.data.<%=field.codeHump%>" placeholder="<%=field.placeholder||field.name||''%>" >
                    <el-option v-for="item in <%=field.codeHump%>List" :key="'e_<%=field.codeHump%>_'+item.key" :label="item.value" :value="item.key"></el-option>
                </el-select><%}%><%if(field.controlId===6){%>
                <el-date-picker clearable v-model="edit.data.<%=field.codeHump%>" placeholder="<%=field.placeholder||field.name||''%>" type="date"></el-date-picker><%}%><%if(field.controlId===7){%>
                <el-date-picker clearable v-model="edit.data.<%=field.codeHump%>DateArr" type="daterange" range-separator="至" start-placeholder="开始" end-placeholder="结束"></el-date-picker><%}%><%if(field.controlId===8){%>
                <el-time-picker clearable v-model="edit.data.<%=field.codeHump%>" placeholder="<%=field.placeholder||field.name||''%>"></el-time-picker><%}%><%if(field.controlId===9){%>
                <el-time-picker clearable v-model="edit.data.<%=field.codeHump%>TimeArr" is-range range-separator="至" start-placeholder="开始" end-placeholder="结束" ></el-time-picker><%}%>
            </el-form-item><% }) %>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button size="small" type="primary" @click="handleEdit('submit')"><span v-text="'保存'"></span></el-button>
            <el-button size="small" type="warning" @click="handleEdit('reset')"><span v-text="'重置'"></span></el-button>
            <el-button size="small" @click="edit.show=false"><span v-text="'取消'"></span></el-button>
        </div>
    </el-dialog><%}%>

</div>

<jsp:include page="<%=system.include.script%>"/>
<script>
    const vm = new Vue({
        el: '#app',
        data() {
            return {
                fullscreenLoading: false,
                // 枚举<% columns.filter(item=>item.options).forEach(item=>{ %>
                <%<%=item.codeHump%>List:${<%=item.codeHump%>List},<%=item.codeHump%>Map:{}, %>
                <% }) %>
                <%=query.hasWarehouse?'\n                warehouseList: [],':''%><%=query.hasClass1?'\n                class1List: [],':''%><%=query.hasClass2?'\n                class2List: [],':''%><%=query.hasClass3?'\n                class3List: [],':''%>

                queryParam: {
                    pageNumber: 1, pageSize: 10,
                    <%=query.hasWarehouse?'warehouseId: null,':''%><%=query.hasDeliveryDate?'deliveryDateArr:[new Date(),new Date()],':''%><%=query.hasClass1?'class1Id: null,':''%><%=query.hasClass2?'class2Id: null,':''%><%=query.hasClass3?'class3Id: null,':''%>
                },
                queryResult: {
                    count: 0,
                    data: []
                },<%if(edit.has){%>
                edit: {
                    show: false,
                    title: '',
                    formref: 'editFormref',
                    sourceData: null,
                    data: {
                        <%=edit.columns.filter(item=>item.has).filter(item=>!({id:true,ct:true,ut:true})[item.codeHump]).map(item=>item.codeHump+':null').join()%>
                    },
                    rules: {<%for(let i = 0; i < edit.columns.length; i++) {let {has,name,codeHump,codeHumpAll,typeJava,typeSqlSize,options}=edit.columns[i];if(!has){continue;}; if(({id:true,ct:true,ut:true})[codeHump]){continue;} if(typeJava==='String'){%>
                        <%=codeHump%>: [
                            {required: true, message: '请输入<%=name%>', trigger: 'blur'},
                            {min: 1, max: <%=typeSqlSize%>, message: '长度在 1 到 <%=typeSqlSize%> 个字符', trigger: 'blur'}
                        ],<%} else if(options){%>
                        <%=codeHump%>: [
                            {required: true, message: '请选择<%=name%>', trigger: 'change'},
                        ],<%} else if(typeJava==='Integer'){%>
                        <%=codeHump%>: [
                            {required: true, type:'number', message: '<%=name%>必须为数字', trigger: 'blur'},
                        ],<%} }%>
                    }
                },<%}%>

            }
        },
        beforeCreate() {

        },
        created() {
            //vue创建成功，此时html元素尚未挂载

        },
        mounted() {
            [<%=columns.filter(item=>item.options).map(item=>"'"+item.codeHump+"'").join() %>].forEach(field => {
                this[field + 'List'].forEach(item => {
                    this[field + 'Map'][item.key] = item;
                });
            });
            <%=query.hasClass1?'\n            this.axiosClass();':''%>
            this.handleQuery(1);
        },
        watch: {
            //监控数据变化

        },
        computed: {
            //计算属性

        },
        methods: {
            handleQuery(action, val){

                const queryParam = this.queryParam;
                const queryResult = this.queryResult;

                switch (action) {
                    // 重置
                    case 'reset': {
                        let not={
                            pageSize:true,
                            <%=query.hasDeliveryDate?'deliveryDateArr:true,':''%>
                        };
                        for (let key in queryParam) {
                            if(not[key]){
                                continue;
                            }
                            queryParam[key] = null;
                        }
                        queryParam.pageNumber = 1;
                        return;
                    }
                    case 'size': {
                        queryParam.pageSize = val;
                        break;
                    }
                    // 默认查询
                    default: {
                        if (typeof action === 'number' && action > 0) {
                            queryParam.pageNumber = action;
                        }
                        break;
                    }
                }
                <% if(query.hasDeliveryDate){ %>
                // 配送日期
                if (queryParam.deliveryDateArr && queryParam.deliveryDateArr.length >= 2) {
                    queryParam.deliveryDateStart = Number(new Date(queryParam.deliveryDateArr[0].format('yyyy-MM-dd') + ' 00:00:00').getTime() / 1000);
                    queryParam.deliveryDateEnd = Number(new Date(queryParam.deliveryDateArr[1].format('yyyy-MM-dd') + ' 00:00:00').getTime() / 1000);
                } else {
                    queryParam.deliveryDateStart = null;
                    queryParam.deliveryDateEnd = null;
                }<%}%>

                //清空数据
                queryResult.data = [];

                //参数设置
                let axiosParam = null;
                try {
                    axiosParam = mc.toFormData(queryParam);
                } catch (e) {
                    this.$message({showClose: true, type: 'error', message: e});
                    return;
                }

                //请求服务数据
                this.fullscreenLoading = true;
                axios.post("index.mc", axiosParam).then(({data:result}=res) => {

                    //处理请求回来的数据
                    (result.data || []).forEach(item => {

                        item.utStr = McUtils.getDateTimeStr(item.ut, "yyyy-MM-dd HH:mm:ss");

                        // 转换数据
                        [<%=columns.filter(item=>item.options).map(item=>"'"+item.codeHump+"'").join() %>].forEach(field => {

                            const map = this[field + 'Map'] || {};

                            let fieldValue = item[field];
                            item[field + 'Str'] = (map[fieldValue] || {}).value || fieldValue;
                        });
                    });
                    //返回数据
                    return result;
                }).then((result => {
                    //表格数据
                    queryResult.data = result.data || [];
                    queryResult.count = result.total_records || 0;
                    //判断请求服务结果
                    if (result.error_code !== 0) {
                        this.$message({showClose: true, type: 'warning', message: result.error.msg || result.error_code});
                    }
                })).catch(err => {
                    //表格数据，如果进方法时已经清空，可省略此行
                    queryResult.data = [];
                    queryResult.count = 0;
                    //发生错误时的提示框
                    this.$message({showClose: true, type: 'error', message: err});
                }).finally(() => {
                    //最终总会执行的函数，没有参数
                    this.fullscreenLoading = false;
                });
            },<%if(edit.has){%>

            handleEdit(action,value, index) {

                //获取要操作的数据
                const edit = this.edit;
                const sourceData = edit.sourceData || {};
                const data = edit.data || {};

                switch (action) {
                    //打开
                    case 'open': {
                        let title = '新增';

                        edit.sourceData = value;
                        McUtils.setData(this.edit.data,JSON.parse(JSON.stringify(value)));
                        if (edit.sourceData.id) {
                            title = '更新  ' + (data.code || '') + ' ' + (data.name || '');
                        }

                        edit.title = title;
                        edit.show = true;

                        return;
                    }
                    //关闭
                    case 'close': {
                        edit.show = false;
                        edit.title = '';
                        edit.sourceData = null;
                        for (let key in edit.data) {
                            edit.data[key] = null;
                        }
                        this.$refs[edit.formref].clearValidate();

                        return;
                    }
                    //重置
                    case 'reset': {
                        this.$refs[edit.formref].resetFields();
                        return;
                    }
                    case 'submit':{
                        // 放行
                        break;
                    }
                    default:{
                        return;
                    }
                }

                //----------【提交数据功能】-----------------------------------------

                //检查数据
                let checkSuccess = true;
                this.$refs[edit.formref].validate((valid) => {
                    checkSuccess = valid;
                });
                if (!checkSuccess) {
                    return;
                }

                //获取参数（发送给后台的数据）
                const params = {};

                Object.keys(data).forEach(item => {
                    let value = data[item];
                    let sourceValue = sourceData[item];

                    if (value !== sourceValue) {
                        params[item] = value;
                    }
                });

                if (Object.keys(params).length == 0) {
                    this.$message({showClose: true, type: 'warning', message: "没有任何更改"});
                    return;
                }

                // 其它参数
                params.id=sourceData.id||null;

                this.fullscreenLoading = true;

                axios.post("edit", mc.toFormData(params)).then(({data:result}=res) => {

                    if (result.error_code !== 0) {

                        this.$message({
                            showClose: true,
                            type: 'warning',
                            message: result.message || (result.error || {}).msg || '操作失败'
                        });
                        return;
                    }

                    //重新查询数据
                    this.handleQuery();
                    //关闭模态框
                    edit.show = false;
                    //提示信息
                    this.$message({showClose: true, type: 'success', message: '操作成功' });

                }).catch(err => {
                    //发生错误时的提示框
                    this.$message({showClose: true, type: 'error', message: err});
                }).finally(() => {
                    //最终总会执行的函数，没有参数
                    this.fullscreenLoading = false;
                });
            },<%}%>

        }
    });
</script>
</body>
</html>
