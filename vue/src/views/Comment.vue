<template>

    <el-card style="background-color: #FFE4E1;">
        <div style="padding: 20px; color: #888;">
            <div>
                <el-input
                        v-model="entity.content"
                        :rows="4"
                        type="textarea"
                        placeholder="输入文字吧~"
                        size="large"
                        resize="none"
                />
                <div style="justify-content: right; padding: 10px; display: flex;"><el-button type="primary" @click="send">留言</el-button></div>
            </div>
        </div>

        <div style="border: solid 1px #B0C4DE;border-radius: 10px;">
            <el-scrollbar max-height="600px">
                <div style="border-radius: 10px;border: solid 1px black;margin: 15px;min-height: 50px;padding: 10px;" v-for="item in message">
                    <span style="font-size: 18px;color: #8cc5ff;">{{item.username}}:</span>
                    <span style="padding-right: 10px;">{{item.content}}</span>
                    <el-button type="text" @click="openReplyDialog(item.id)" style="color: #7FFF00">回复</el-button>
                    <el-button type="text" @click="deleteComment(item.id)" style="color: #FA8072;" v-if="user.username === item.username ">删除</el-button>

                    <div style="background-color: #eee; padding: 10px" v-if="item.parentMessage">回复@ {{ item.username }}：{{ item.parentMessage.content }}</div>
                </div>
            </el-scrollbar>
        </div>

        <el-dialog title="回复信息" v-model="dialogFormVisible" width="30%" close="cancel">
            <el-input v-model="content" autocomplete="off" type="textarea" :rows="3"></el-input>
            <template #footer>
                <el-button @click="cancel">取 消</el-button>
                <el-button type="primary" @click="reply">确 定</el-button>
            </template>
        </el-dialog>

    </el-card>

</template>

<script>
    import request from "../../utils/request";
    import { ElLoading } from 'element-plus'

    export default {
        name: "Comment",
        data() {
            return {
                dialogFormVisible:false,
                content:"",
                entity:{},
                user:{},
                message:{},
                loading:null
            }
        },
        methods : {
            send(){
                this.entity.username = this.user.username;
                this.entity.foreignId = 0;
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
                })
                this.$nextTick(()=>{
                    this.entity = {};
                })
            },
            loadComment(){
                this.fullscreenLoading();
                request.get("/message/foreign/0").then((res)=>{
                    if (res.data !== []){
                        this.message = res.data;
                    }else{
                        this.$message({
                            type:"error",
                            message:"没有留言",
                        });
                    }
                });
                this.loading.close();
            },
            deleteComment(id){
                request.delete("/message/delete/"+id).then((res)=>{
                    this.$message({
                        type:"warning",
                        message:res.msg,
                    });
                    this.loadComment();
                })
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
            fullscreenLoading(){
                this.loading = ElLoading.service({
                    lock: true,
                    text: 'Loading',
                    background: 'rgba(0, 0, 0, 0.7)',
                });
            }
        },
        mounted() {
            this.user = JSON.parse(localStorage.getItem("user"));
            this.fullscreenLoading();
            this.loadComment();

            document.title="留言测试";

            this.$messageBox("欢迎留言~~~");
        }
    }
</script>

<style scoped>

</style>