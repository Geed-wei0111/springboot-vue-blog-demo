<template>
    <Bside></Bside>
    <div style="padding: 10px;background-color: #FFFACD;height: calc(100vh - 61px);">
        <div>
            <!--      一些操作按钮-->
            <el-button type="success" @click="$router.push('/blogWrite')">
                新增博客
            </el-button>


            <el-button type="primary" @click="load">
                刷新表格
            </el-button>

            <div style="padding-top: 10px;padding-bottom: 10px;">
                <el-input v-model="search" placeholder="请输入关键字" @clear="load" @keyup.enter="load" clearable style="width: 20%;"></el-input>
                <el-button type="primary" @click="load" >搜索</el-button>
            </div>
        </div>

        <!--    表格展示数据-->
        <el-table
                :data="tableData"
                style="width: 100%"
                stripe="true"
                border="true"
                v-loading="loading"
        >
<!--            <el-table-column prop="id" label="ID" sortable width="80"></el-table-column>-->
            <el-table-column align="center" prop="title" label="标题"></el-table-column>
            <el-table-column align="center" prop="tags" label="标签">
                <template #default="scope">
                    <el-tag v-if="scope.row.tags.length!==0" style="margin-right: 5px;" v-for="item in scope.row.tags">{{ item }}</el-tag>
                    <el-tag v-else type="error">该博客没有标签</el-tag>
                </template>
            </el-table-column>
            <el-table-column align="center" prop="author" label="作者"></el-table-column>
            <el-table-column align="center" prop="date" label="时间"></el-table-column>

            <el-table-column align="center" fixed="right" label="操作" width="300px">
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
                            v-model="multiple"
                            multiple
                            filterable
                            reserve-keyword
                            clearable
                            allow-create
                            default-first-option
                            placeholder="请选择标签（自创标签可以输入后按回车）"
                            style="width: 80%;margin: 5px;"
                    >
                        <el-option
                                v-for="item in options"
                                :key="item"
                                :value="item"
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
        </div>
    </div>
</template>

<script>
    // @ is an alias to /src
    import Bside from "../../components/Bside"
    import request from "../../../utils/request";
    import E from 'wangeditor'

    export default {
        name: 'myBlogs',
        components: {Bside},
        data() {
            return {
                form: {},
                user:null,
                detail: {},
                dialogVisible: false,
                contentVisible: false,
                search: '',
                currentPage: 1,
                pageSize: 10,
                total: 10,
                tableData: [],
                editor: null,
                loading: false,
                options: [],
                multiple: [],
            }
        },
        methods: {
            hasDelPower(row){
                let roles = this.user.roles;
                return roles.find(p=>(p === 3 || p === 1)) || this.user.username === row.author;
            },
            details(row) {
                this.$router.push(`/blog?id=${row.id}`);
            },
            cancel() {
                //当对话框关闭时，销毁富文本编辑器，否则切换页面后会导致对象丢失
                this.dialogVisible = false;
                this.editor.destroy();
                this.editor = null;
            },
            loadTags() {
                //获取标签
                request.get("/tags").then((res) => {
                    this.options = res.data;
                })
            },
            load() {
                this.loading = true;
                this.loadTags();
                //加载博客
                request.get("/news", {
                    params: {
                        pageNum: this.currentPage,
                        pageSize: this.pageSize,
                        search: this.search,
                        author: this.user.username,
                    }
                }).then(res => {
                    this.tableData = res.data.records;
                    this.total = res.data.total;

                    this.$nextTick(() => {
                        this.loading = false;
                    });
                })
            },
            // add() {
            //     //清空表单
            //     this.form = {};
            //     // 清空多选器
            //     this.multiple = [];
            //     //将对话框弹出
            //     this.dialogVisible = true;
            //
            //     this.$nextTick(() => {
            //         if (!this.editor) {
            //             this.editor = new E('#div1');
            //
            //             //设置图片服务器端口
            //             this.editor.config.uploadImgServer = 'http://' + window.server.fileUploadIp + ":9090/files/editor/upload";
            //             this.editor.config.uploadFileName = "file";  // 设置上传参数名称
            //
            //             this.editor.create();
            //         }
            //         this.editor.txt.html("");
            //     });
            // },
            save() {
                //如果富文本编辑器存在，那么将编辑器里的文本存放到表单中
                if (this.editor) {
                    this.form.content = this.editor.txt.html();
                }

                if (this.form.id) {  //如果表单中有id存在，则证明接下来要进行修改操作
                    request.put("/news", this.form, {
                        params: {
                            arr: encodeURI(this.multiple),
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
                        this.load();
                    })
                } else { //没有id表示为新增操作
                    this.form.author = this.user.username;
                    request.post("/news", this.form, {
                        params: {
                            arr: encodeURI(this.multiple),
                        }
                    }).then(res => {
                        if (res.code === '0') {
                            this.$message({
                                type: "success",
                                message: "新增成功"
                            });
                        } else {
                            this.$message({
                                type: "error",
                                message: "新增失败"
                            });
                        }
                        this.dialogVisible = false;
                        this.load();
                    });
                }
            },
            handleEdit(row) {
                // 深拷贝，以免改动原表格的数据
                this.form = JSON.parse(JSON.stringify(row));

                this.multiple = this.form.tags;


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
        },
        created() {
            this.user = JSON.parse(localStorage.getItem("user"));
            this.load();
        },
        mounted() {
            document.title = "博客测试";
        }
    }
</script>
<style>
</style>