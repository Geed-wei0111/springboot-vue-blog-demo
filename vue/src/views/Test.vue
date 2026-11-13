<template>
    <div>
        <el-card style="width: 500px;margin: 200px auto;">
            <el-form label-width="80px" size="small">
                <el-upload
                        class="avatar-uploader"
                        :action="fileUploadUrl"
                        :headers="{
                            token:user.token,
                        }"
                        :show-file-list="false"
                        :on-success="handleAvatarSuccess"
                >
                    <img v-if="form.avatar" :src="form.avatar" class="avatar" alt="">
                    <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                </el-upload>

                <el-form-item label="用户名">
                    <el-input v-model="form.username" disabled autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="昵称">
                    <el-input v-model="form.nickname" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="性别">
                    <el-input v-model="form.sex" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="年龄">
                    <el-input v-model="form.age" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="地址">
                    <el-input type="textarea" v-model="form.address" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="save">确 定</el-button>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<script>
    import request from "../../utils/request";

    export default {
        name: "Test",
        data() {
            return {
                userId: null,
                user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
                fileUploadUrl:'http://' + server.fileUploadIp + ':9090/files/avatar',
                form: {},
            }
        },
        methods:{
            async getUser() {
                return (await request.get("/user/username/" + this.user.username)).data
            },
            save(){
                request.put("/user", this.form).then(res => {
                    if (res.code === '0') {
                        this.$message.success("保存成功");

                        // 触发父级更新User的方法
                        // this.$emit("refreshUser");
                        // 更新浏览器存储的用户信息
                        this.$nextTick(()=>{
                            this.getUser().then(res => {
                                res.token = JSON.parse(localStorage.getItem("user")).token;
                                res.roles = this.user.roles;
                                res.permissions = this.user.permissions;
                                localStorage.setItem("user", JSON.stringify(res));

                                //刷新页面，重新加载user信息
                                this.$router.go(0);
                            });
                        });
                    } else {
                        this.$message.error("保存失败");
                    }
                })
            },
            handleAvatarSuccess(res) {
                if (res.code === "0"){
                    this.form.avatar = res.url;//保存当前的头像
                }else if (res.code === "-1"){
                    this.$message.warning(res.msg);
                }
            }
        },
        created() {
            // //   接受参数
            // let query = this.$route.query;
            // this.userId = query.id;
            // if (!this.userId){
            //     this.userId = 1;
            // }
            this.form = this.user;
        },
        mounted() {
            document.title = "个人信息";
        }
    }
</script>

<style scoped>
    .avatar-uploader {
        text-align: center;
        padding-bottom: 10px;
    }
    .avatar-uploader .el-upload {
        border: 1px dashed #d9d9d9;
        border-radius: 6px;
        cursor: pointer;
        position: relative;
        overflow: hidden;
    }
    .avatar-uploader .el-upload:hover {
        border-color: #409EFF;
    }
    .avatar-uploader-icon {
        font-size: 28px;
        color: #8c939d;
        width: 138px;
        height: 138px;
        line-height: 138px;
        text-align: center;
    }
    .avatar {
        width: 138px;
        height: 138px;
        display: block;
    }
</style>