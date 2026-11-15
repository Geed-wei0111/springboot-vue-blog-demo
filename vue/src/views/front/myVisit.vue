<template>
    <Bside></Bside>
    <div style="padding: 10px;background-color: #FFFACD;height: calc(100vh - 61px);">
        <div>
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
        name: 'myVisit',
        components: {Bside},
        data() {
            return {
                user:null,
                search: '',
                currentPage: 1,
                pageSize: 10,
                total: 10,
                tableData: [],
                loading: false,
            }
        },
        methods: {
            details(row) {
                this.$router.push(`/blog?id=${row.id}`);
            },
            load() {
                this.loading = true;
                //加载博客
                request.get(`/news/getVisits/${this.user.id}`, {
                    params: {
                        pageNum: this.currentPage,
                        pageSize: this.pageSize,
                        search: this.search,
                    }
                }).then(res => {
                    this.tableData = res.data.records;
                    this.total = res.data.total;

                    this.$nextTick(() => {
                        this.loading = false;
                    });
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
            document.title = "观看记录";
        }
    }
</script>
<style>
</style>