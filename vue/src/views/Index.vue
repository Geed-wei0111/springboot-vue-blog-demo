<template>
<!--  背景  -->
    <div style="width:100%;height: 100%;background-color: #E6E6FA;position: fixed;z-index:-1;">
        <el-image :src="require('@/assets/index-bg.png')"></el-image>
    </div>
<!--  导航栏  -->
    <div>
        <div style="height: 60px;background-color: #F8F8FF;position: fixed;z-index:100;left:calc((100vw - 1530px) / 2);">
            <Bside @scrollToMainContent="scrollToMainContent" style="width: calc(100vw - 400px);" />
        </div>
    </div>
    <div style="height: 60px;width: 100%"></div>

    <!--  elementPlus的轮播图  -->
    <div style="width: calc(100vw / 2);margin: 20px auto 0;" >
        <el-carousel @change="changeBackgroundImage" :interval="3000" height="540px" >
            <el-carousel-item v-for="(item, index) in imageList" :key="index">
                <el-image :src="item" :fit="fit" ></el-image>
            </el-carousel-item>
        </el-carousel>
    </div>

    <!--使用弹性盒子实现左中右结构-->
    <div class="index-main" id="index" >

        <!--左侧-->
        <div class="index-main-column-left">
            <el-card class="edgeCard">
                <template #header>
                    <h3>博客归档</h3>
                </template>
                <el-row v-for="arc in archives">
                    <u class="archives" @click="goToArchives(arc)">
                        {{arc.year}}年{{arc.month}}月&nbsp;&nbsp;({{arc.count}})
                    </u>
                </el-row>
            </el-card>
        </div>


        <!--中间-->
        <div class="index-main-column-middle">
            <el-card class="idea">
                <h2>欢迎来到XXX博客</h2>
                <p>Blog就是以网络作为载体，简易迅速便捷地发布自己的心得，及时有效轻松地与他人进行交流，再集丰富多彩的个性化展示于一体的综合性平台。</p>
                <p>欢迎大家来我这分享知识或见解~</p>
                <p>从<strong>顶部的菜单</strong>选择你所需要浏览的功能或博客！</p>
            </el-card>

            <el-card v-loading="loadings">
                <template #header>
                    <h3>最新博客</h3>
                </template>
                <el-card v-for="neo in latest">
                    <div style="display: inline-flex;">
                        <div style="width: 100px; height:100px;">
                            <img :src="showPic(neo)" alt="" style="width: 70%;height: 70%;margin-top: 7%;">
                        </div>
                        <div style="width: 600px;">
                            <h4 style="cursor:pointer;margin-bottom: 10px;" @click="$router.push(`/blog?id=${neo.id}`)">{{ neo.title }}</h4>
                            <div style="max-height: 88px;overflow: hidden;margin-bottom: 10px;">{{neo.content.replace(/<.*?>/g,"")}}</div>
                            <h6><i class="el-icon-user-solid"></i>{{neo.author}}&nbsp;&nbsp;&nbsp;<i class="el-icon-date"></i>{{neo.date}}</h6>
                        </div>
                    </div>
                </el-card>
            </el-card>

            <el-card v-loading="loadings">
                <template #header>
                    <h3>猜你喜欢</h3>
                </template>
                <el-card v-for="ran in random">
                    <div style="display: inline-flex;">
                        <div style="width: 100px; height:100px;">
                            <img :src="showPic(ran)" alt="" style="width: 70%;height: 70%;margin-top: 7%;">
                        </div>
                        <div style="width: 600px;">
                            <h4 style="cursor:pointer;margin-bottom: 10px;" @click="$router.push(`/blog?id=${ran.id}`)">{{ ran.title }}</h4>
                            <div style="max-height: 88px;overflow: hidden;margin-bottom: 10px;">{{ran.content.replace(/<.*?>/g,"")}}</div>
                            <h6><i class="el-icon-user-solid"></i>{{ran.author}}&nbsp;&nbsp;&nbsp;<i class="el-icon-date"></i>{{ran.date}}</h6>
                        </div>
                    </div>
                </el-card>
            </el-card>

            <el-card v-loading="loadings">
                <template #header>
                    <h3>热门博客</h3>
                </template>
                <el-card v-for="l in mostLike">
                    <div style="display: inline-flex;">
                        <div style="width: 100px; height:100px;">
                            <img :src="showPic(l)" alt="" style="width: 70%;height: 70%;margin-top: 7%;">
                        </div>
                        <div style="width: 600px;">
                            <h4 style="cursor:pointer;margin-bottom: 10px;" @click="$router.push(`/blog?id=${l.id}`)">{{ l.title }}</h4>
                            <div style="max-height: 88px;overflow: hidden;">{{l.content.replace(/<.*?>/g,"")}}</div>
                            <h6><i class="el-icon-user-solid"></i>{{l.author}}&nbsp;&nbsp;&nbsp;<i class="el-icon-date"></i>{{l.date}}</h6>
                        </div>
                    </div>
                </el-card>
            </el-card>

            <el-card v-loading="loadings">
                <template #header>
                    <h3>收藏最多</h3>
                </template>
                    <el-card v-for="co in mostCollect">
                        <div style="display: flex;">
                            <div style="width: 100px; height:100px;">
                                <img :src="showPic(co)" alt="" style="width: 70%;height: 70%;margin-top: 7%;">
                            </div>
                            <div style="width: 600px;">
                                <h4 style="cursor:pointer;margin-bottom: 10px;" @click="$router.push(`/blog?id=${co.id}`)">{{ co.title }}</h4>
                                <div style="max-height: 88px;overflow: hidden;">{{co.content.replace(/<.*?>/g,"")}}</div>
                                <h6><i class="el-icon-user-solid"></i>{{co.author}}&nbsp;&nbsp;&nbsp;<i class="el-icon-date"></i>{{co.date}}</h6>
                            </div>
                        </div>
                    </el-card>
            </el-card>
        </div>

        <!--右侧-->
        <div class="index-main-column-right">
            <el-card class="edgeCard">
                <template #header>
                    <h3>最新标签</h3>
                </template>
                <el-tag style="margin: 10px" v-for="tag in tags">{{ tag }}</el-tag>
            </el-card>
        </div>

    </div>

    <Footer class="index-foot"></Footer>
    <el-backtop />

</template>

<script>
    import request from "../../utils/request";
    import Bside from "../components/Bside";
    import Footer from "../components/Footer";

    export default {
        name: "Index",
        components: {
            Bside,Footer,
        },
        data() {
            return {
                //v-for来展示图片
                imageList: ["http://"+server.fileUploadIp+":9090/files/download/72cb0fd77b8d413bbf8651160a7fafb2.png",
                    "http://"+server.fileUploadIp+":9090/files/download/d14ff7e707c74540981bfd3b4bed4bc2.jpg",
                    "http://"+server.fileUploadIp+":9090/files/download/d46a81c2f037482898ecc51bf8a4b121.jpg"],
                fit:"cover",
                archives:[],
                tags:[],
                latest:[],
                random:[],
                mostLike:[],
                mostCollect:[],
                loadings:true,
            }
        },
        methods:{
            showPic(obj){
                let content = obj.content;
                if (content.includes("<img")){
                    return content.match('<img[^<>]*? src=\"([^<>]*?)\"')[1];
                }else{
                    // TODO 线上项目需要更换图片源
                    return "http://localhost:9090/files/download/504ac5333d824cb7a9fb1dd93b886cee.png";
                }
            },
            changeBackgroundImage(){
                if (this.imgListIndex + 1 === 3){
                    this.imgListIndex = 0;
                }else{
                    this.imgListIndex += 1;
                }
            },
            scrollToMainContent(){
                document.getElementById("index").scrollIntoView({
                    behavior: 'smooth',
                    block: 'start',
                    inline: 'nearest'
                });
            },
            goToArchives(arc){
                this.$router.push({name:"BlogSearch", params:{ year:arc.year, month:arc.month }});
            }
        },
        mounted(){
            document.title = "主页";

            //获取归档
            request.get('/news/archive').then(res=>{
                this.archives = res.data;
            });

            //展示最新的几个标签
            request.get('/tags/new').then(res=>{
                this.tags = res.data;
            });

            //展示最新的几个博客
            request.get('/news/latest').then(res=>{
                this.latest = res.data;

                this.$nextTick(()=>{
                    // console.log(this.latest);
                    for (let i=0;i<this.latest.length;i++){
                        // console.log(this.latest[i]);
                        let rst = this.latest[i].content.match('<img[^<>]*? src=\"([^<>]*?)\"');//通过正则匹配字符串
                        console.log(rst[1]);
                    }
                });
            });

            //随机推荐博客
            request.get('/news/random').then(res=>{
                this.random = res.data;
            });

            //展示最受欢迎的博客
            request.get('/news/mostlike').then(res=>{
                this.mostLike = res.data;
            });

            //展示收藏量高的博客
            request.get('/news/mostcollect').then(res=>{
                this.mostCollect = res.data;
            });

            this.$nextTick(()=>{
                this.loadings = false;
            })
        }
    }
</script>

<style scoped>
    .idea{
        font-size: 18px;
        border: 0;
        border-bottom: 1px solid #aaa;
        min-height: 118px;
        text-align: center;
    }
    .idea p,h2{
        padding: 10px;
    }
    .edgeCard{
        width: 250px;
        background-color: #B0C4DE;
        min-height: 250px;
        padding: 15px;
        position: sticky;
        top: 70px;
    }
    .index-main {
        display: flex;
        margin-top: 25px;
        margin-bottom: 40px;
        padding-top: 20px;
        margin-left: auto !important;
        margin-right: auto !important;
        width: 80%;
        opacity: 0.97;
    }
    .index-main-column-middle {
        flex: 1;
        /*width: 800px !important;*/
        min-height: 1000px;
    }
    .index-main-column-middle .el-card{
        margin: 25px;
        background-color: #B0C4DE;
    }
    .index-main-column-middle .el-card .el-card{
        background-color: #98B8E3;
    }
    .index-main-column-middle .el-card .el-card:hover{
        background-color: #9FBFE8;
    }
    .el-card h4:hover{
        color: #007aff;
        text-decoration:underline;
        transition: 0.25s;
    }
    u:hover{
        margin-left: 25px;
        transition: 0.25s;
    }
    h3:hover{
        color: darkcyan;
        transition: 0.25s;
    }
    h6:hover{
        color: #13ce66;
        transition: 0.25s;
    }
    .index-main-column-left {
        padding: 0;
        margin-right: 20px;
        margin-left: 30px;
    }
    .index-main-column-right {
        padding: 0;
        margin-left: 20px;
        margin-right: 30px;
    }
    .index-foot {
        background-color: #333333;
        color: white!important;
        opacity: 0.9;
    }
    .archives{
        margin: 5px;
        cursor: pointer;
    }
    .archives:hover{
        color: #007aff;
        text-decoration:underline;
    }
</style>