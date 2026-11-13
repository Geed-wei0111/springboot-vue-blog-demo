<template>
    <!--  Background Image  -->
    <div>
        <el-image :src="backgroundImage" style="width: 100vw;height:100vh;z-index: -1;position: fixed;"></el-image>
    </div>
    <Bside/>
    <div class="center" style="margin-top: 5px;">
        <el-input v-model="title" placeholder="标题" clearable></el-input>
    </div>
    <div class="center" style="margin-top: 5px;">
<!--        <div id="toolbar"></div>-->
        <div id="editor"></div>
    </div>
    <div class="center" style="margin-top: 10px;">
        <el-tag
                v-for="tag in dynamicTags"
                :key="tag"
                class="mx-1"
                closable
                :disable-transitions="false"
                @close="handleCloseTag(tag)"
        >
            {{ tag }}
        </el-tag>
        <el-input
                v-if="inputVisible"
                ref="InputRef"
                v-model="inputValue"
                style="width: 150px;"
                size="small"
                @keyup.enter="handleInputConfirm"
                @blur="handleInputConfirm"
                placeholder="输入完后按回车确认"
        />
        <el-button v-else class="button-new-tag ml-1" size="normal" @click="showInput">
            增加标签
        </el-button>

        <el-select style="width: 100px;margin-left: 25px;" v-model="selType" placeholder="选择类型" clearable>
            <el-option v-for="t in types" :value="t">{{t}}</el-option>
        </el-select>

        <div style="float: right;">
            <el-popconfirm title="确定要上传博客吗？" @confirm="saveDone">
                <template #reference>
                    <el-button type="success">上传博客</el-button>
                </template>
            </el-popconfirm>

            <el-popconfirm title="确定要读取上一次保存的草稿吗？" @confirm="loadDraft">
                <template #reference>
                    <el-button type="primary" >读取草稿</el-button>
                </template>
            </el-popconfirm>

            <el-popconfirm title="确定要覆盖上一次保存的草稿吗？" @confirm="saveDraft">
                <template #reference>
                    <el-button type="warning">保存草稿</el-button>
                </template>
            </el-popconfirm>

            <el-popconfirm title="确定要删除当前已写的博客吗？" @confirm="flushDraft">
                <template #reference>
                    <el-button type="danger">清空内容</el-button>
                </template>
            </el-popconfirm>
        </div>
    </div>
</template>

<script>
    import router from "../../router";
    import request from "../../../utils/request";
    import Bside from "../../components/Bside";
    import E from 'wangeditor';

    export default {
        name: "blogWrite",
        components:{
            Bside,
        },
        data(){
            return{
                user:localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
                backgroundImage:"http://"+server.fileUploadIp+":9090/files/avatar/9f21627a22e54fae83161009477a5847.jpg",
                dynamicTags:[],
                inputValue:null,
                inputVisible:false,
                title:null,
                form:{},
                form2:{},
                types:["游戏","科技","娱乐","资讯","学习"],
                selType:null,
            }
        },
        methods: {
            //async和await合用可以同步发送请求
            beforeunloadHandler(e) {
                // 用户退出后，保存最新一次的博客信息，避免丢失信息
                if (this.$route.path === "/blogWrite") {
                    var e = window.event||e;
                    e.returnValue=("确定离开吗？");
                }else{
                    window.removeEventListener('beforeunload', e => this.beforeunloadHandler(e));
                }
            },
            showInput(){
                this.inputVisible = true;
                this.$nextTick(() => {
                    this.$refs.InputRef.focus();
                })
            },
            handleInputConfirm(){//添加标签
                if (this.dynamicTags.length === 5){
                    this.$message.warning("标签最多有5个");
                }
                else if (this.inputValue && !this.dynamicTags.includes(this.inputValue)) {
                    this.dynamicTags.push(this.inputValue);
                }
                this.inputVisible = false;
                this.inputValue = '';
            },
            handleCloseTag(tag){//去掉自定义标签
                this.dynamicTags.splice(this.dynamicTags.indexOf(tag), 1);
                console.log(this.dynamicTags);
            },
            saveDone(){
                //如果富文本编辑器存在，那么将编辑器里的文本存放到表单中
                if (this.editor) {
                    this.form.content = this.editor.txt.html();
                }
                this.form.author = this.user.username;
                this.form.title = this.title;
                this.form.type = this.selType;
                request.post("/news", this.form, {
                    params: {
                        arr: encodeURI(this.dynamicTags),
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
                });
            },
            saveDraft(){
                //如果富文本编辑器存在，那么将编辑器里的文本存放到表单中
                if (this.editor) {
                    this.form2.content = this.editor.txt.html();
                }
                this.form2.userId = this.user.id;
                this.form2.title = this.title;
                request.post("/draft", this.form2, {
                    params: {
                        arr: encodeURI(this.dynamicTags),
                    }
                }).then(res => {
                    if (res.code === '0') {
                        this.$message({
                            type: "success",
                            message: "保存成功"
                        });
                    } else {
                        this.$message({
                            type: "error",
                            message: "保存失败"
                        });
                    }
                });
            },
            loadDraft(){
                request.get("/draft").then(res=>{
                    if (res.code === "0"){
                        this.editor.txt.html(res.data.content); //将之前填写的内容填入编辑器中
                        this.title = res.data.title;
                        if (res.data.tags !== ""){
                            this.dynamicTags = res.data.tags.split(",");
                        }
                    }
                })
            },
            flushDraft(){
                this.title = "";
                this.editor.txt.html("");
                this.dynamicTags = [];
            }
        },
        beforeRouteLeave(to, from, next){
            this.$alert('博客可能未保存或发布，确定要离开吗？', 'warning', {
                confirmButtonText: '确定',
                showCancelButton:true,
                cancelButtonText:"取消",
                callback: action => {
                    if (action === "confirm"){
                        window.removeEventListener('beforeunload', e => this.beforeunloadHandler(e));
                        this.editor.destroy();
                        this.editor = null;
                        next();
                    }
                }
            });
        },
        mounted() {
            //渲染编辑器！！！！
            this.$nextTick(() => {
                if (!this.editor) {
                    this.editor = new E('#editor');
                    this.editor.config.height = 720; //设置编辑器高度，分离后可以自适应
                    this.editor.config.zIndex = 10; //设置层优先级，默认10000
                    // this.editor.config.menus = []; //设置菜单可选项，因为该页面是查看，无需编辑，所以将菜单分离后，清空可选项
                    this.editor.config.uploadImgMaxSize = 5 * 1024 * 1024; // 图片大小5M
                    this.editor.config.uploadImgServer = 'http://' + window.server.fileUploadIp + ":9090/files/editor/upload";//设置图片服务器端口
                    this.editor.config.uploadFileName = "file";  // 设置上传参数名称
                    this.editor.create(); //创建编辑器
                    // this.editor.disable(); //禁用编辑器，只可用于查看
                }
            });

            window.addEventListener('beforeunload', e => this.beforeunloadHandler(e))
        },
        destroyed() {
            window.removeEventListener('beforeunload', e => this.beforeunloadHandler(e))
        },
    }
</script>

<style scoped>
    .center{
        margin: 0 auto;
        width: calc( 100vw / 2 );
    }
</style>