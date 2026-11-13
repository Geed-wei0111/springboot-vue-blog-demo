<template>
    <div style="padding: 10px;background-color: #F0FFF0;">
        <div style="margin: 10px;">
            <!--      一些操作按钮-->
            <el-select
                    v-model="multiple"
                    filterable
                    reserve-keyword
                    clearable
                    placeholder="请选择标签检索"
                    style="width: 200px;"
                    @change="loadByTag"
            >
                <el-option
                        v-for="item in options"
                        :key="item"
                        :value="item"
                >
                </el-option>
            </el-select>
            <el-button type="primary" @click="load">加载数据</el-button>
            <el-button type="warning" @click="tagsVisible = true">管理标签</el-button>
        </div>

        <!--    表格展示数据-->
        <el-table
                :data="tableData"
                style="width: 100%"
                stripe="true"
                border="true"
                v-loading="loading"
        >
            <el-table-column prop="id" label="ID" sortable width="80"></el-table-column>
            <el-table-column prop="title" label="标题"></el-table-column>
            <el-table-column prop="tags" label="标签" sortable>
                <template #default="scope">
                    <el-tag v-if="scope.row.tags.length !== 0" :type="bindTag(item)" style="margin-right: 5px;" v-for="item in scope.row.tags">{{ item }}</el-tag>
                    <el-tag v-else type="error">该博客没有标签</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="author" label="作者"></el-table-column>
            <el-table-column prop="date" label="时间" width="100"></el-table-column>

            <el-table-column fixed="right" label="操作" width="300px">
                <template #default="scope">
                    <el-button @click="details(scope.row)" type="primary" size="small">查看博客</el-button>
                    <el-button @click="handleEdit(scope.row)" type="warning" size="small">编辑</el-button>

                    <el-popconfirm v-if="hasDelPower(scope.row)" title="这是一段内容确定删除吗？" @confirm="handleDelete(scope.row.id)">
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
                    width="50%"
                    @close="cancel"
            >
                <el-form :model="form" label-width="120px">
                    <!--          标题输入框-->
                    <el-form-item label="标题">
                        <el-input v-model="form.title" style="width: 80%"></el-input>
                    </el-form-item>

                    <!--            多选框-->
                    <span>标签：</span>
                    <el-select
                            v-model="multiple2"
                            multiple
                            filterable
                            reserve-keyword
                            clearable
                            placeholder="请选择一个标签（自创标签可以输入后按回车）"
                            style="width: 80%;margin: 5px;"
                    >
                        <el-option
                                v-for="item in options"
                                :key="item"
                                :value="item"
                                :disabled="item === '[无标签博客]'"
                        >
                        </el-option>
                    </el-select>

                    <!--            编辑器在这-->
                    <div id="div1"></div>

                </el-form>

                <template #footer>
                  <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">Cancel</el-button>
                    <el-button type="primary" @click="save">Confirm</el-button>
                  </span>
                </template>
            </el-dialog>

            <!--          -->
            <el-dialog v-model="tagsVisible">
                <el-table :data="Tags" max-height="500">
                    <el-table-column prop="tag" label="标签"></el-table-column>

                    <el-table-column fixed="right" label="操作" width="100px">
                        <template #default="scope">
                            <el-popconfirm title="这是一段内容确定删除吗？" @confirm="handleTagDelete(scope.row.tag)">
                                <template #reference>
                                    <el-button type="danger" size="small">删除</el-button>
                                </template>
                            </el-popconfirm>
                        </template>
                    </el-table-column>
                </el-table>
            </el-dialog>
        </div>
    </div>
</template>

<script>
    // @ is an alias to /src
    import request from "../../utils/request";
    import E from 'wangeditor'

    export default {
        name: 'News',
        data() {
            return {
                user:null,
                form: {},
                detail: {},
                dialogVisible: false,
                tagsVisible: false,
                search: '',
                currentPage: 1,
                pageSize: 10,
                total: 10,
                tableData: [],
                editor: null,
                loading: false,
                options: [], //多选框中的所有值
                multiple: [],//按标签筛选的多选的值
                multiple2: [],//小弹窗的多选值
                Tags: {} //所有的标签
            }
        },
        computed:{
        },
        methods: {
            hasDelPower(row){
                let roles = this.user.roles;
                return roles.find(p=>(p === 3 || p === 1)) || this.user.username === row.author;
            },
            bindTag(item){//标签筛选时，高亮所选标签
                if (!this.multiple)//未选择筛选时默认蓝色
                    return "";
                if (item === this.multiple)//两值相等说明标签为选中的，反之则不是
                    return "success";
                else
                    return "info";
            },
            loadByTag(val) {
                console.log(val);
                this.loading = true;
                this.currentPage = 1;
                //TODO 无博客标签
                this.load();
            },
            details(row) {
                this.$router.push(`/blog?id=${row.id}`);
            },
            cancel() {
                this.dialogVisible = false;
                this.editor.destroy();
                this.editor = null;
            },
            load() {
                if (this.multiple === ""){
                    this.loading = true;
                    request.get("/news", {
                        params: {
                            pageNum: this.currentPage,
                            pageSize: this.pageSize,
                            search: this.search
                        }
                    }).then(res => {
                        this.tableData = res.data.records;
                        this.total = res.data.total;

                        this.$nextTick(() => {
                            this.loading = false;
                        });
                    })
                }else{
                    this.loading = true;
                    request.get("/news/tag", {
                        params: {
                            pageNum: this.currentPage,
                            pageSize: this.pageSize,
                            tag: encodeURI(this.multiple)
                        }
                    }).then((res) => {
                        this.tableData = res.data.records;
                        this.total = res.data.total;

                        this.$nextTick(() => {
                            this.loading = false;
                        });
                    })
                }
            },
            loadTags(){
                request.get("/tags").then((res) => {
                    this.options = res.data;
                });
                request.get("/tags/list").then(res=>{
                    this.Tags = res.data;
                });
            },
            save() {
                //如果富文本编辑器存在，那么将编辑器里的文本存放到表单中
                if (this.editor) {
                    this.form.content = this.editor.txt.html();
                }

                if (this.form.id) {  //如果表单中有id存在，则证明接下来要进行修改操作
                    request.put("/news", this.form, {
                        params: {
                            arr: encodeURI(this.multiple2),
                        }
                    }).then(res => {
                        if (res.code === '0') {
                            this.$message({
                                type: "success",
                                message: "更新成功"
                            });
                        } else {
                            this.$message({
                                type: "error",
                                message: "更新失败"
                            });
                        }
                        this.dialogVisible = false;
                        if (this.multiple)
                            this.loadByTag(this.multiple);
                        else
                            this.load();
                    })
                }
            },
            handleEdit(row) {
                // 深拷贝，以免改动原表格的数据
                this.form = JSON.parse(JSON.stringify(row));

                this.multiple2 = this.form.tags;

                this.dialogVisible = true;

                this.$nextTick(() => {
                    if (!this.editor) {
                        this.editor = new E('#div1');

                        //设置图片服务器端口
                        this.editor.config.uploadImgServer = 'http://' + window.server.fileUploadIp + ":9090/files/editor/upload";
                        this.editor.config.uploadFileName = "file";  // 设置上传参数名称

                        this.editor.create();
                    }
                    this.editor.txt.html(row.content)
                })
            },
            handleDelete(id) {
                request.delete("/news/" + id).then(res => {
                    if (res.code === '0') {
                        this.$message({
                            type: "success",
                            message: "删除成功"
                        });
                        this.$nextTick(()=>{
                            this.load();
                        });
                    } else {
                        this.$message({
                            type: "error",
                            message: "删除失败"
                        });
                    }
                })
            },
            handleTagDelete(tag){
                request.post("/tags/del", tag).then((res)=>{
                    if (res.code === "0"){
                        this.$message({//删除成功
                            type:"success",
                            message:res.msg,
                        });
                        this.$nextTick(()=>{
                            this.load();
                        });
                    }else if (res.code === "-1"){//标签有关联，不能删除
                        this.$message({
                            type:"error",
                            message:res.msg
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
        },
        created() {
            // let str = JSON.stringify(this.$route.params);
            // if (str !== "{}"){
            //     this.loadTags();
            //     this.loadByTag(this.$route.params.tag);
            //     this.multiple = this.$route.params.tag;
            // }else{
            //     this.loadTags();
            //     this.load();
            // }
            this.loadTags();
            this.load();
            this.user = JSON.parse(localStorage.getItem("user"));
        },
        mounted() {
            document.title = "标签管理";
        }
    }
</script>
<style>
</style>