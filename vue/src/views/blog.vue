<template>
    <!--  Header  -->
    <div style="height: 60px;width:100%;position: fixed;z-index: 20;">
        <Bside></Bside>
    </div>
    <div style="height: 60px;width: 100%"></div>

    <!--  Background Image  -->
    <div>
        <el-image :src="backgroundImage" style="width: 100vw;height: calc(100vh - 60px);z-index: -1;position: fixed;"></el-image>
    </div>
    <!-- BlogContent  -->
    <div ref="box" style="margin: 10px auto;width:calc(100vw - 800px);">

        <div style="min-height: 720px;">

            <!--     博客内容    -->
            <el-card v-loading="cardLoading">
                <template #header>
                    <div class="card-header" style="text-align: center">
                        <h1>{{detail.title}}</h1>
                        <h4 @click="subAuthor" id="subscribe">作者：{{detail.author}}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;时间:{{detail.date}}</h4>
                        <h6>访问量：{{recommend.visits}}&nbsp;&nbsp;点赞量：{{recommend.likes}}&nbsp;&nbsp;收藏量：{{recommend.favorites}}</h6>
                    </div>
                </template>

                <div id="toolbar"></div>
                <div id="editor"></div>

                <!-- Blog's tags-->
                <el-divider></el-divider>
                <div v-if="hasTags">
                    <el-popover
                            placement="top-start"
                            title="Tip"
                            :width="200"
                            trigger="hover"
                            content="点击右边标签可搜索相关标签的博客"
                    >
                        <template #reference>
                            <el-tag size="large" class="tag" type="info">标签</el-tag>
                        </template>
                    </el-popover>

                    <el-tag size="large" class="tag" type="success" v-for="item in tags" @click="toSearch(item)">{{item}}</el-tag>
                </div>
                <el-tag v-else>无标签</el-tag>

                <div style="margin-top: 10px;">
                    <el-button @click="collectDialog = true" v-if="!isCollect">
                        <i class="el-icon-star-on"></i>
                        收藏文章
                    </el-button>
                    <el-button @click="deleteCollect" v-else>
                        <i class="el-icon-star-on star"></i>
                        取消收藏
                    </el-button>

                    <el-button @click="likeBlog" v-if="!isLike">
                        <i class="el-icon-share"></i>
                        点赞文章</el-button>
                    <el-button @click="deleteLike" v-else>
                        <i class="el-icon-share like"></i>
                        取消点赞
                    </el-button>
                </div>

                <!--  博客评论  -->
                <div style="margin: 10px;">
                    <el-card v-loading="cardLoading">
                        <template #header>
                            <div class="card-header">
                                <h3 style="text-align: center" v-if="message.length!==0">
                                    评论区
                                    <el-button type="text" @click="giveComment">评论</el-button>
                                </h3>
                                <h3 style="text-align: center" v-if="message.length===0">
                                    暂时没有评论哦~
                                    <el-button type="text" @click="giveComment">评论</el-button>
                                </h3>
                            </div>
                        </template>

                        <div style="border: solid 1px #B0C4DE;border-radius: 10px;">
                            <div style="border-radius: 10px;border: solid 1px black;margin: 15px;min-height: 50px;padding: 10px;" v-for="item in message">
                                <span style="font-size: 18px;color: #8cc5ff;">{{item.username}}:</span>
                                <span style="padding-right: 10px;">{{item.content}}</span>
                                <el-button type="text" @click="openReplyDialog(item.id)" style="color: #7FFF00">回复</el-button>
                                <el-button type="text" @click="deleteComment(item.id)" style="color: #FA8072;" v-if="user.username === item.username ">删除</el-button>

                                <div style="background-color: #eee; padding: 10px" v-if="item.parentMessage">回复@ {{ item.username }}：{{ item.parentMessage.content }}</div>
                            </div>
                        </div>
                    </el-card>
                </div>
            </el-card>
        </div>
    </div>

    <!--  放在最外层，这样显示弹窗时比其他内容更靠前  -->

        <!--  评论窗口  -->
    <el-dialog title="回复信息" v-model="dialogFormVisible" width="30%" close="cancel">
        <el-input v-model="content" autocomplete="off" type="textarea" :rows="3"></el-input>
        <template #footer>
            <el-button @click="cancel">取 消</el-button>
            <el-button type="primary" @click="reply">确 定</el-button>
        </template>
    </el-dialog>

        <!--  收藏窗口  -->
    <el-dialog title="收藏博客" v-model="collectDialog" width="30%">
        <h3 style="text-align: center;">
            请选择收藏夹:
            <el-select v-model="folder" filterable allow-create placeholder="无选择为默认收藏夹" clearable>
                <el-option v-for="f in folders" :value="f.folder">{{f.folder}}</el-option>
            </el-select>
        </h3>

        <template #footer>
            <el-button @click="collectDialog = false">取 消</el-button>
            <el-button type="primary" @click="collectBlog">确 定</el-button>
        </template>
    </el-dialog>

    <el-backtop />
</template>

<script>
    import request from "../../utils/request";
    import Bside from "../components/Bside";
    import E from 'wangeditor';
    import router from "../router";

    export default {
        name: "blog",
        components: {
            Bside
        },
        data() {
            return {
                detail: {},
                tags: [],
                cardLoading:true,
                backgroundImage:"http://"+server.fileUploadIp+":9090/files/avatar/9f21627a22e54fae83161009477a5847.jpg",
                dialogFormVisible:false,
                collectDialog:false,
                blogId:null,
                entity:{},
                user:{},
                message:[],
                content:"",
                editor:null,
                recommend:{},
                isCollect:false,
                isLike:false,
                isScrolled:false,
                folder:"",
                folders:null,
            }
        },
        computed:{
            hasTags(){
                return this.tags.length !== 0;
            }
        },
        methods:{
            subAuthor(){
                this.$messageBox({
                    title: '提示',
                    message: '确定要关注这个作者吗',
                    showCancelButton: true,
                    callback:action=>{
                        if (action === "confirm"){
                            let userId = this.user.id;
                            let author = this.detail.author;
                            request.post(`/sub/${author}/${userId}`).then((res)=>{
                                if (res.code === "0"){
                                    this.$message.success("关注成功~");
                                }else if (res.code !== "0"){
                                    this.$message.info(res.msg);
                                }
                            });
                        }
                    }
                });
            },
            send(){
                this.entity.username = this.user.username;
                this.entity.foreignId = this.blogId;
                if (!this.entity.parentId){
                    this.entity.parentId = 0;
                }

                request.post("/message", this.entity).then((res)=>{
                    if (res.code === "-1"){
                        this.$message({
                            type:"error",
                            message:res.msg,
                        });
                    }else if (res.code === "0"){
                        this.$message({
                            type:"success",
                            message:"发送成功",
                        });
                        this.loadComment();
                    }
                });
                this.$nextTick(()=>{
                    this.entity = {};
                })
            },
            loadComment(){
                request.get("/message/foreign/"+this.blogId).then((res)=>{
                    console.log(res.data, this.message, this.message.length);
                    if (res.data !== []){
                        this.message = res.data;
                    }else{
                        this.$message({
                            type:"error",
                            message:"没有留言",
                        });
                    }
                });
            },
            deleteComment(id){
                this.$messageBox({
                    title:"Warning",
                    message:"你确定要删除该评论吗？",
                    showCancelButton:true,
                    confirmButtonText:"确定",
                    cancelButtonText:"取消",
                    callback:action=>{
                        if (action === "confirm"){
                            request.delete("/message/delete/"+id).then((res)=>{
                                this.$message({
                                    type:"warning",
                                    message:res.msg,
                                });
                                this.loadComment();
                            })
                        }else{
                            this.$message.info("取消删除");
                        }
                    }
                })
            },
            giveComment(){
                this.dialogFormVisible = true;
                this.entity.parentId = 0;
            },
            reply(){
                this.entity.content = this.content;
                this.dialogFormVisible = false;
                this.send();
                this.content = "";
            },
            openReplyDialog(id){
                this.dialogFormVisible = true;
                this.entity.parentId = id;
            },
            cancel(){
                this.dialogFormVisible = false;
                this.entity = {};
                this.content = "";
            },
            toSearch(tag){
                this.$router.push({ name: 'BlogSearch', params: { tag: tag } })
            },
            loadFolders(){
                request.get(`/folder/list/${this.user.id}`).then(res=>{
                    this.folders = res.data;
                });
            },
            collectBlog(){
                let user = this.user.id;
                let blog = parseInt(this.blogId);
                request.post(`/news/collect/${user}/${blog}`, this.folder).then(res=>{
                    if (res.code === "0"){
                        this.$message.success("收藏成功");
                        this.isCollect = true;
                        this.recommend.favorites += 1;
                    }
                });
                this.collectDialog = false;
                this.loadFolders();
            },
            deleteCollect(){
                let user = this.user.id;
                let blog = parseInt(this.blogId);
                request.delete(`/news/collect/${user}/${blog}`).then(res=>{
                    if (res.code === "0"){
                        this.$message.success("删除成功");
                        this.isCollect = false;
                        this.recommend.favorites -= 1;
                    }
                });
            },
            likeBlog(){
                let user = this.user.id;
                let blog = parseInt(this.blogId);
                request.put(`/news/recommend/like/${blog}`).then(res=>{
                    if (res.code === "0"){
                        this.$message.success("点赞成功");
                        this.isLike = true;
                    }
                    this.$nextTick(()=>{
                        this.recommend.likes += 1;
                    });
                });
            },
            deleteLike(){
                let user = this.user.id;
                let blog = parseInt(this.blogId);
                request.put(`/news/minus/like/${blog}`).then(res=>{
                    if (res.code === "0"){
                        this.$message.success("取消成功");
                        this.isLike = false;
                    }
                    this.$nextTick(()=>{
                        this.recommend.likes -= 1;
                    });
                });
            },
            handleScrollBottom(){
                //滑动窗口到底部说明浏览完毕，增加访问量
                //变量scrollTop是滚动条滚动时，距离顶部的距离
                var scrollTop = document.documentElement.scrollTop||document.body.scrollTop;
                //变量windowHeight是可视区的高度
                var windowHeight = document.documentElement.clientHeight || document.body.clientHeight;
                //变量scrollHeight是滚动条的总高度
                var scrollHeight = document.documentElement.scrollHeight||document.body.scrollHeight;
                //滚动条到底部的一半时！
                if(scrollTop+windowHeight===scrollHeight){
                    // console.log("距顶部"+scrollTop+"可视区高度"+windowHeight+"滚动条总高度"+scrollHeight);
                    if (!this.isScrolled){
                        request.put(`/news/recommend/visits/${this.blogId}`);
                        console.log("浏览完毕！增加访问量~");
                        this.isScrolled = true;
                    }
                }
            }
        },
        mounted() {
            this.user = JSON.parse(localStorage.getItem("user"));

            let query = this.$route.query;
            this.blogId = query.id;
            console.log(this.blogId);

            request.get(`/news/recommend/${this.blogId}`).then(res=>{
                this.recommend = res.data;
                console.log(this.recommend);
                if (this.recommend.isFavor === 1){
                    this.isCollect = true;
                }
                if (this.recommend.isLike === 1){
                    this.isLike = true;
                }
            });
            this.loadFolders();
            this.loadComment();
            request.get("/news/" + this.blogId, {}).then(res => {
                if (res.code !== '0') {
                    this.$message({
                        type: "error",
                        message: res.msg,
                    })
                } else if (res.code === "0") {
                    this.detail = res.data;

                    //渲染编辑器！！！！
                    this.$nextTick(() => {
                        if (!this.editor) {
                            this.editor = new E('#toolbar', '#editor');
                            // this.editor.config.height = 1000; 设置编辑器高度，分离后可以自适应
                            this.editor.config.zIndex = 10; //设置层优先级，默认10000
                            this.editor.config.menus = []; //设置菜单可选项，因为该页面是查看，无需编辑，所以将菜单分离后，清空可选项
                            this.editor.create(); //创建编辑器
                            this.editor.disable(); //禁用编辑器，只可用于查看
                        }
                        this.editor.txt.html(this.detail.content); //将之前填写的内容填入编辑器中
                    });
                    this.tags = this.detail.tags;

                    this.$nextTick(()=>{
                        this.cardLoading = false;
                        document.title=this.detail.title+"——"+this.detail.author;
                    })
                }
            });

            this.$nextTick(() => {
                // this.initScroll()
                window.onscroll = this.handleScrollBottom
            });
        },
        created() {},
        beforeRouteLeave (to,from,next){//利用路由守卫离开页面时，销毁编辑器，避免切换页面后编辑器还存在 TODO 浏览器的返回前进不起作用
            if (this.editor !== null){
                this.editor.destroy();
                this.editor = null;
                console.log("destroy");
            }
            window.onscroll = null;
            next();
        }
    }
</script>

<style scoped>
    .tag{
        margin-right:10px;
        margin-top: 10px;
    }
    /*.recommend{*/
    /*    margin: 0 10px;*/
    /*    float: right;*/
    /*    width: calc(100vw - 1400px);*/
    /*}*/
    .el-icon-share.like{
        color: red;
    }
    .el-icon-star-on.star{
        color: 	#FFD700;
    }
    #subscribe:hover{
        color: blue;
        text-decoration:underline;
        cursor: pointer;
    }
</style>