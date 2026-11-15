<template>
    <div style="padding: 10px;background-color: #FAEBD7;">
        <div>
            <!--      一些操作按钮-->
            <el-button type="success" @click="add">
                新增数据
                <i class="el-icon-plus"></i>
            </el-button>

            <el-upload :action="uploadUrl" style="display: inline-block;padding: 0 10px;" accept="xlsx" :show-file-list="false" :on-success="handleUploadSuccess">
                <el-button type="primary">
                    导入Excel
                    <i class="el-icon-top"></i>
                </el-button>
            </el-upload>

            <el-button type="primary" @click="exportExcel">
                导出Excel
                <i class="el-icon-bottom"></i>
            </el-button>

            <el-button type="info" @click="load" >
                加载数据
                <i class="el-icon-refresh"></i>
            </el-button>

            <el-button type="danger" @click="batchDelete" >
                批量删除用户
                <i class="el-icon-delete"></i>
            </el-button>

            <div style="padding-top: 10px;padding-bottom: 10px;">
                <el-input v-model="search" placeholder="请输入关键字筛选用户名" clearable style="width: 20%;" @clear="load" @keyup.enter="load"></el-input>
                <el-button type="primary" @click="load">搜索<i class="el-icon-search"></i></el-button>
            </div>
        </div>

        <!--    表格展示数据-->
        <el-table
                :data="tableData"
                style="width: 100%"
                stripe="true"
                border="true"
                v-loading="loading"
                @selection-change="handleSelectionChange"
        >
            <el-table-column type="selection" width="40" />
            <el-table-column prop="id" label="ID" sortable width="80"></el-table-column>
            <el-table-column prop="ban" label="封禁" width="80">
                <template #default="scope">
                    <el-switch v-model="scope.row.ban" :active-value="1" :inactive-value="0" @change="handleBan(scope.row)"></el-switch>
                </template>
            </el-table-column>
            <el-table-column prop="username" label="用户名"></el-table-column>
            <el-table-column prop="nickname" label="昵称"></el-table-column>
            <el-table-column prop="password" label="密码">
                <template #default="scope">
                    <el-input show-password disabled v-model="scope.row.password"></el-input>
                </template>
            </el-table-column>
            <el-table-column prop="sex" label="性别"></el-table-column>
            <el-table-column prop="age" label="年龄" width="60"></el-table-column>
            <el-table-column prop="address" label="地址"></el-table-column>
            <el-table-column label="角色列表" width="250">
                <template #default="scope">
                    <el-select v-model="scope.row.roles" multiple placeholder="请选择" style="width: 80%">
                        <el-option v-for="item in roles" :key="item.id" :label="item.comment" :value="item.id"></el-option>
                    </el-select>
                </template>
            </el-table-column>
            <el-table-column fixed="right" label="操作" width="280px">
                <template #default="scope">
                    <el-button size="small" type="primary" @click="handleRoleChange(scope.row)">保存角色信息</el-button>
<!--                    <el-button @click="$router.push('/person?id='+scope.row.id)" type="primary" size="small">跳转查看</el-button>-->
                    <el-button @click="handleEdit(scope.row)" type="warning" size="small">编辑</el-button>
                    <el-popconfirm title="这是一段内容确定删除吗？" @confirm="handleDelete(scope.row.id)">
                        <template #reference>
                            <el-button type="danger" size="small">删除</el-button>
                        </template>
                    </el-popconfirm>
                </template>
            </el-table-column>
        </el-table>

        <!--    分页显示-->
        <div style="margin: 10px 0;">
            <el-pagination
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                    :current-page="currentPage"
                    :page-sizes="[5, 10, 20]"
                    :page-size="pageSize"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="total"
            >
            </el-pagination>

            <!--      增加数据的弹窗-->
            <el-dialog
                    v-model="dialogVisible"
                    title="请填写数据"
                    width="30%"
            >
                <el-form :model="form" label-width="120px">
                    <el-form-item label="用户名">
                        <el-input v-model="form.username" style="width: 80%"></el-input>
                    </el-form-item>
                    <el-form-item label="昵称">
                        <el-input v-model="form.nickname" style="width: 80%"></el-input>
                    </el-form-item>
                    <el-form-item label="密码">
                        <el-input show-password v-model="form.password" style="width: 80%"></el-input>
                    </el-form-item>
                    <el-form-item label="性别">
                        <el-radio v-model="form.sex" label="男">男</el-radio>
                        <el-radio v-model="form.sex" label="女">女</el-radio>
                        <el-radio v-model="form.sex" label="未知">未知</el-radio>
                    </el-form-item>
                    <el-form-item label="年龄">
                        <el-input v-model="form.age" style="width: 25%"></el-input>
                    </el-form-item>
                    <el-form-item label="地址">
                        <el-input type="textarea" v-model="form.address" style="width: 80%"></el-input>
                    </el-form-item>
                </el-form>

                <template #footer>
                  <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="save">确认</el-button>
                  </span>
                </template>
            </el-dialog>
        </div>
    </div>
</template>

<script>
    // @ is an alias to /src
    import request from "../../utils/request";
    import {Edit, Delete, Search, Plus, Bottom, Top, Loading, Refresh } from '@element-plus/icons';

    export default {
        name: 'User',
        components:{
            Edit, Delete, Search, Plus, Bottom, Top, Loading, Refresh
        },
        data() {
            return {
                form: {},
                dialogVisible: false,
                search: '',
                currentPage: 1,
                pageSize: 10,
                total: 10,
                tableData: [],
                loading: true,
                uploadUrl:"http://"+window.server.fileUploadIp+":9090/user/import",
                multipleSelection:[],
                idBatch:[],
                roles:[],
            }
        },
        methods: {
            handleBan(row){
                request.put("/user/ban", row).then(res=>{
                    if (res.code === '0') {
                        if (row.ban === 1)
                            this.$message.success("封禁成功");
                        else
                            this.$message.success("解封成功");
                    }
                })
            },
            handleRoleChange(row) {
                request.put("/user/changeRole", row).then(res => {
                    if (res.code === '0') {
                        this.$message.success("更新成功");
                        if (res.data) {
                            localStorage.removeItem("user");
                            this.$router.push("/login");
                        }
                    }
                })
            },
            batchDelete(){
                this.$messageBox({
                    showCancelButton:true,
                    confirmButtonText:"确定",
                    cancelButtonText:"取消",
                    message:"你确定要批量删除吗",
                    title:"WARNING",
                    center: true,
                    callback:action=>{
                        if (action === "confirm"){
                            request.delete("/user/batch", {
                                params:{
                                    array:encodeURI(this.idBatch),
                                }
                            }).then((res)=>{
                                this.$message.info(res.msg);
                                this.$nextTick(()=>{
                                    this.load();
                                });
                            })
                        }else{
                            this.$message.info("好的呢~");
                        }
                    }
                })
            },
            handleSelectionChange(val){
                this.idBatch = [];
                this.multipleSelection = val;
                for (let i = 0;i<this.multipleSelection.length;i++){
                    this.idBatch.push(this.multipleSelection[i].id);
                }
                // console.log(this.idBatch);
            },
            load() {
                this.loading = true;
                request.get("/user", {
                    params: {
                        pageNum: this.currentPage,
                        pageSize: this.pageSize,
                        search: this.search
                    }
                }).then(res => {
                    this.tableData = res.data.records;
                    this.total = res.data.total;
                    // this.currentPage = res.data.page;

                    this.$nextTick(() => {
                        this.loading = false;
                    });
                });
                //加载角色
                request.get("/role/all").then(res => {
                    this.roles = res.data
                })
            },
            add() {
                this.form = {};
                this.dialogVisible = true;
            },
            save() {
                if (this.form.id) {
                    request.put("/user", this.form).then(res => {
                        if (res.code === '0') {
                            this.$message({
                                type: "success",
                                message: "更新成功"
                            });
                            this.dialogVisible = false;
                            this.load();
                        } else {
                            this.$message({
                                type: "error",
                                message: "更新失败"
                            });
                        }
                    });
                } else {
                    request.post("/user", this.form).then(res => {
                        if (res.code === '0') {
                            this.$message({
                                type: "success",
                                message: "新增成功"
                            });
                            this.dialogVisible = false;
                            this.load();
                        } else {
                            this.$message({
                                type: "error",
                                message: "新增失败"
                            });
                        }
                    });
                }
            },
            handleEdit(row) {
                // 深拷贝
                this.form = JSON.parse(JSON.stringify(row));
                this.dialogVisible = true;
            },
            handleDelete(id) {
                request.delete("/user/" + id).then(res => {
                    if (res.code === '0') {
                        this.$message({
                            type: "success",
                            message: "删除成功"
                        });
                        this.load();
                    } else {
                        this.$message({
                            type: "error",
                            message: "删除失败"
                        });
                    }
                })
            },
            //改变每页个数触发
            handleSizeChange(pageSize) {
                this.pageSize = pageSize;
                this.load();
            },
            //改变页码触发
            handleCurrentChange(pageNum) {
                this.currentPage = pageNum;
                this.load();
            },
            exportExcel(){
                window.open("http://"+window.server.fileUploadIp+":9090/user/export");
            },
            handleUploadSuccess(){
                this.$message.success("上传成功");
            }
        },
        created() {
            this.load();
        },
        mounted() {
            document.title = "用户管理";
        }
    }
</script>
<style>
</style>