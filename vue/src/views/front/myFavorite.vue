<template>
    <Bside></Bside>
    <div style="padding: 10px;background-color: #FFFACD;height: calc(100vh - 61px);">
        <div>
            <div style="padding-top: 10px;padding-bottom: 10px;">
                <el-input v-model="search" placeholder="请输入关键字" @clear="load" @keyup.enter="load" clearable style="width: 20%;"></el-input>
                <el-select style="margin-left: 25px;" v-model="folder" filterable allow-create placeholder="无选择为默认收藏夹" clearable>
                    <el-option v-for="f in folders" :label="f.folder" :value="f.id">{{f.folder}}</el-option>
                </el-select>
                <el-select v-if="authors !== null" style="margin-left: 25px;" v-model="author" filterable placeholder="关注用户" clearable>
                    <el-option v-for="a in authors" :value="a">{{a}}</el-option>
                </el-select>
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
            <el-table-column align="center" prop="title" label="标题"></el-table-column>
            <el-table-column align="center"  prop="tags" label="标签">
                <template #default="scope">
                    <el-tag v-if="scope.row.tags.length !== 0" style="margin-right: 5px;" v-for="item in scope.row.tags">{{ item }}</el-tag>
                    <el-tag v-else type="error">该博客没有标签</el-tag>
                </template>
            </el-table-column>
            <el-table-column align="center"  prop="author" label="作者"></el-table-column>
            <el-table-column align="center"  prop="date" label="时间"></el-table-column>

            <el-table-column align="center"  fixed="right" label="操作" width="300px">
                <template #default="scope">
                    <el-button @click="details(scope.row)" type="primary" size="small">查看博客</el-button>

                    <el-popconfirm title="确定删除吗？" @confirm="deleteCollect(scope.row.id)">
                        <template #reference>
                            <el-button type="danger" size="small">取消收藏</el-button>
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
        </div>
    </div>
</template>

<script>
    // @ is an alias to /src
    import Bside from "../../components/Bside"
    import request from "../../../utils/request";

    export default {
        name: 'myFavorite',
        components: {Bside},
        data() {
            return {
                form: {},
                user:null,
                detail: {},
                search: '',
                currentPage: 1,
                pageSize: 10,
                total: 10,
                tableData: [],
                loading: false,
                folder:"",
                folders:null,
                author:"",
                authors:null,
            }
        },
        methods: {
            loadFollowAuthor(){
                request.get(`/sub/${this.user.id}`).then(res=>{
                    if (res.code === "0"){
                        this.authors = res.data;
                    }
                });
            },
            loadFolders(){
                request.get(`/folder/list/${this.user.id}`).then(res=>{
                    this.folders = res.data;
                });
            },
            deleteCollect(id){
                let user = this.user.id;
                request.delete(`/news/collect/${user}/${id}`).then(res=>{
                    if (res.code === "0"){
                        this.$message.success("删除成功");
                    }
                });
                this.load();
            },
            details(row) {
                this.$router.push(`/blog?id=${row.id}`);
            },
            load() {
                this.loading = true;
                if (this.author !== ""){
                    request.post("/news/getSub", this.author, {
                        params: {
                            pageNum: this.currentPage,
                            pageSize: this.pageSize,
                            search:this.search,
                        }
                    }).then(res=>{
                        this.tableData = res.data.records;
                        this.total = res.data.total;

                        this.$nextTick(() => {
                            this.loading = false;
                        });
                    });
                }else{
                    //加载博客
                    request.get("/news/getCollection", {
                        params: {
                            userId:this.user.id,
                            pageNum: this.currentPage,
                            pageSize: this.pageSize,
                            search:this.search,
                            folderId:this.folder,
                        }
                    }).then(res => {
                        this.tableData = res.data.records;
                        this.total = res.data.total;

                        this.$nextTick(() => {
                            this.loading = false;
                        });
                    })
                }
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
            this.loadFolders();
            this.loadFollowAuthor();
        },
        mounted() {
            document.title = "我的收藏夹";
        }
    }
</script>
<style>
</style>