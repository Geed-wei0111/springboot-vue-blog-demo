<template>
    <el-card v-bind:class="{'collapse':isCollapse, 'expand':!isCollapse}" style="background-color: 	#F5F5DC;">
        <template #header>
            <h1>Welcome '{{nickname}}'! ~~~这里是系统动向：</h1>
            <h1>没有相应的管理员权限左侧菜单是无法使用的~</h1>
            <el-button type="text" @click="dialog1 = true">网页更新日志</el-button>
            <el-button type="text" @click="dialog2 = true">网站所用技术</el-button>
            <el-button type="text" @click="openChart">图表统计测试</el-button>
        </template>
        <el-table
                :data="tableData"
                style="width: 100%"
                stripe="true"
                border="true"
                v-loading="loading"
        >
            <el-table-column prop="id" label="ID" width="80"></el-table-column>
            <el-table-column prop="log" label="标题"></el-table-column>
            <el-table-column prop="time" label="时间"></el-table-column>
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
    </el-card>
    <el-dialog v-model="dialog1">
        <scroll :data="listData" :class-option="classOption" class="seamless-warp">
            <div style="color: #7FFF00;">目前未适配移动端（时间够的话。。）</div>
            <div>已完成的内容如下：</div>
            <div>1.用户管理，注册登录</div>
            <div>2.文件上传共享，音乐视频播放图片预览</div>
            <div>3.富文本编辑器（博客文章）</div>
            <div>4.跳转查看网页</div>
            <div>5.轮播图</div>
            <div>6.标签存储</div>
            <div>7.excel导入导出</div>
            <div>8.留言，回复，评论</div>
            <div>9.集成JWT，保留token</div>
            <div>10.滚动公告</div>
            <div>11.RBAC--角色管理菜单管理</div>
            <div>12.增加菜单的管理员权限</div>
            <div>未完待续……beta version</div>
            <div style="color: darkred;">欢迎帮忙测试捏，可以在留言区留下bug</div>
        </scroll>
    </el-dialog>
    <el-dialog v-model="dialog2">
        <el-row>
            <el-col :span="12" style="line-height: 30px">
                <div><b>后端</b></div>
                <div>SpringBoot2</div>
                <div>Hutool</div>
                <div>JWT</div>
                <div>Lombok</div>
                <div>MybatisPlus</div>
            </el-col>

            <el-col :span="12" style="line-height: 30px">
                <b>前端</b>
                <div>Vue3</div>
                <div>Vue-Router</div>
                <div>VueX</div>
                <div>ElementPlus</div>
                <div>Axios</div>
            </el-col>
        </el-row>
    </el-dialog>
    <el-dialog v-model="dialog3" style="width: 400px">
        <div id="chart" style="width: 500px; height: 400px"></div>
    </el-dialog>
</template>

<script>
    import scroll from 'vue-seamless-scroll/src'
    import request from "../../utils/request";
    import * as echarts from 'echarts'

    export default {
        name: "Home",
        components:{
            scroll,
        },
        data(){
            return{
                loading:false,
                dialog1:false,
                dialog2:false,
                dialog3:false,
                total:10,
                currentPage:1,
                pageSize:10,
                tableData:[],
                nickname: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).nickname : {},
                listData: [{
                    'a': '',
                    'b': ''
                }, {
                    'a': '',
                    'b': ''
                }, {
                    'a': '',
                    'b': ''
                }, {
                    'a': '',
                    'b': ''
                }, {
                    'a': '',
                    'b': ''
                }],
            }
        },
        computed: {
            classOption () {
                return {
                    direction: 1,
                    hoverStop: false,
                    singleHeight: 30,
                }
            },
            isCollapse(){
                return this.$store.state.menuState;
            },
        },
        methods:{
            openChart(){
                this.dialog3 = true;
                this.$nextTick(()=>{
                    var option = {
                        title: {
                            text: '各季度博客数量统计',
                            subtext: '趋势图',
                            left: 'center'
                        },
                        xAxis: {
                            type: 'category',
                            data: ["第一季度", "第二季度", "第三季度", "第四季度"]
                        },
                        yAxis: {
                            type: 'value'
                        },
                        series: [
                            {
                                data: [],
                                type: 'line'
                            },
                            {
                                data: [],
                                type: 'bar'
                            }
                        ]
                    };

                    var myChart = echarts.init(document.getElementById('chart'));

                    request.get("/echarts/blogs").then(res => {
                        // 填空
                        // option.xAxis.data = res.data.x
                        option.series[0].data = res.data;
                        option.series[1].data = res.data;
                        // 数据准备完毕之后再set
                        myChart.setOption(option);

                    });
                });
            },
            loadLogs(){
                this.loading = true;
                request.get("/log/list", {
                    params:{
                        pageNum:this.currentPage,
                        pageSize:this.pageSize,
                    }
                }).then(res=>{
                    this.tableData = res.data.records;
                    this.total = res.data.total;
                    this.$nextTick(()=>{
                        this.loading = false;
                    });
                })
            },
            //改变每页个数触发
            handleSizeChange(pageSize) {
                this.pageSize = pageSize;
                this.loadLogs();
            },
            //改变页码触发
            handleCurrentChange(pageNum) {
                this.currentPage = pageNum;
                this.loadLogs();
            },
        },
        mounted(){
            this.loadLogs();
        }
    }
</script>

<style scoped>
    .seamless-warp {
        height: 300px;
        overflow: hidden;
        margin: 0 auto;
    }
    .seamless-warp div{
        padding: 5px 0;
        text-align: center;
    }

    .collapse{
        width: calc((100vw - 60px) );
        height: calc((100vh - 50px) );
    }
    .expand{
        width: calc((100vw - 200px) );
        height: calc((100vh - 50px) );
    }
</style>