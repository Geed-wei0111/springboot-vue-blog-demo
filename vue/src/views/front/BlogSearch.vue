<template>
    <!--  背景色  -->
    <div>
        <el-image :src="backgroundImage" style="width: 100vw;z-index: -1;position: fixed;"></el-image>
    </div>
    <!--  导航栏  -->
    <div style="height: 61px;padding-top:5px;">
        <Bside class="center"></Bside>
    </div>
    <div class="center" style="margin-top: 10px;padding: 30px;background-color: rgba(230,230,250,0.8);">
        <el-input v-model="search" placeholder="根据标题模糊搜索" clearable style="width: 50%;margin-left: 10px;"></el-input><el-button @click="load" style="margin-left: 25px;" type="success">搜索</el-button>
        <el-radio-group v-model="radio" style="margin-left: 25px;">
            <el-radio label="asc" >发布最久</el-radio>
            <el-radio label="desc">最新发布</el-radio>
        </el-radio-group>
        <div style="margin-top: 15px;margin-left: 20px;display: inline-flex;">
            <h5 style="padding-right: 30px;">标签筛选：
                <el-select style="width: 100px;" v-model="selTag" placeholder="tag" clearable>
                    <el-option v-for="t in tags" :value="t">{{t}}</el-option>
                </el-select>
            </h5>
            <h5 style="padding-right: 30px;">类型筛选：
                <el-select style="width: 100px;" v-model="selType" placeholder="type" clearable>
                    <el-option v-for="t in types" :value="t">{{t}}</el-option>
                </el-select>
            </h5>
            <h5 style="padding-right: 30px;">作者筛选：
                <el-input v-model="author" clearable style="width: 100px;" placeholder="author"></el-input>
            </h5>
            <h5 style="padding-right: 50px;">时间筛选：
                <el-select style="width: 100px;" v-model="arc" placeholder="archive" clearable>
                    <el-option v-for="a in archives" :value="`${a.year}-${a.month}`">{{ a.year }}-{{ a.month }}</el-option>
                </el-select>
            </h5>
        </div>
    </div>
    <el-card v-loading="loadings" class="center blog" v-for="b in blogs" style="background-color: rgba(230,230,250,0.75);">
        <el-divider></el-divider>
        <el-button type="text" style="font-size:20px;" @click="$router.push(`/blog?id=${b.id}`)">{{ b.title }}</el-button>
        <!--  将HTML标签去掉预览博客内容  -->
        <div style="max-height: 88px;overflow: hidden;">{{b.content.replace(/<.*?>/g,"")}}</div>
        <h6><i class="el-icon-user-solid"></i>{{b.author}}&nbsp;&nbsp;&nbsp;<i class="el-icon-date"></i>{{b.date}}</h6>
        <el-tag size="small">标签</el-tag>
        <el-tag v-if="b.tags.length!==0" type="success" size="small" style="margin-left: 5px;margin-top: 5px;" v-for="t in b.tags">
            <i class="el-icon-collection-tag"></i>{{t}}
        </el-tag>
        <el-tag v-else size="small" type="danger" style="margin-left: 5px;margin-top: 5px;">无标签</el-tag>
        <el-divider></el-divider>
    </el-card>
    <div class="center" style="margin: 10px auto;">
        <el-pagination
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                :current-page="currentPage"
                :page-sizes="[5, 10, 20]"
                :page-size="pageSize"
                layout="total, sizes, prev, pager, next, jumper"
                :total="total"
        ></el-pagination>
    </div>
</template>

<script>
    import Bside from "../../components/Bside";
    import request from "../../../utils/request";

    export default {
        name: "BlogSearch",
        components: {Bside},
        data(){
            return{
                selTag:null,
                selType:null,
                author:null,
                types:["游戏","科技","娱乐","资讯","学习"],
                tags:[],
                currentPage:1,
                pageSize:10,
                total:0,
                search:null,
                backgroundImage:"http://"+server.fileUploadIp+":9090/files/download/4f1a3624e1ed4b058938ff3067c7d7e6.png",
                blogs:[
                    {
                        title:"title",
                        content:"content",
                        author:"author",
                        tags:"",
                    }
                    ],
                radio:"desc",
                arc:"",
                archives:[],
                loadings:true,
            }
        },
        methods:{
            async load() {
                this.loadings = true;
                //获取博客预览
                let year, month;
                if (this.arc !== '') {
                    let param = this.arc.split('-');
                    year = parseInt(param[0]);
                    month = parseInt(param[1]) - 1;
                } else {
                    year = -1;
                    month = -1;
                }

                await request.get("/news", {
                    params: {
                        pageNum: this.currentPage,
                        pageSize: this.pageSize,
                        search: this.search,
                        order: this.radio,
                        tag: this.selTag,
                        author: this.author,
                        type:this.selType,
                        year: year,
                        month: month,
                    }
                }).then(res => {
                    if (res.code === "0") {
                        this.blogs = res.data.records;
                        this.total = res.data.total;
                    }
                });

                //获取所有博客的标签
                await request.get("/tags").then(res => {
                    if (res.code === "0") {
                        this.tags = res.data;
                    }
                });

                //获取归档数据
                await request.get('/news/archive').then(res => {
                    this.archives = res.data;
                });

                this.loadings = false;
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
        mounted(){
            this.load();
        },
        created(){
        },
        //keep-alive的独有钩子函数，切换当前页面将执行activated，切换其他执行deactivated
        deactivated(){
            console.log("deactivated");
        },
        activated(){
            let str = JSON.stringify(this.$route.params);
            console.log(str);
            if (str !== "{}"){
                this.selTag = null;
                this.arc = '';

                if (this.$route.params.tag){
                    this.selTag = this.$route.params.tag;
                }
                if (this.$route.params.year){
                    let year = this.$route.params.year;
                    let month = this.$route.params.month;
                    this.arc = `${year}-${month}`;
                }

                this.$nextTick(()=>{
                    this.load();
                });
            }
            console.log("activated");
        }
    }
</script>

<style scoped>
    .center{
        width: calc(100vw / 2);
        margin: 0 auto;
    }
    .blog{
        margin: 5px auto;
        padding: 10px;
    }
    .blog div{
        margin: 10px 0;
    }
</style>