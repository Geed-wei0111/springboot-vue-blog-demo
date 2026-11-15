<template>
    <div>
        <el-image :src="backgroundImageUrl" style="position: absolute;z-index: -1;height: 100%;width: 100%"></el-image>
    </div>
    <div style="width: 100%;height: 100vh;overflow: hidden;">
        <div style="margin: 300px auto; width: 500px;">
            <div style="text-align: center; font-size: 30px; color: #F8F8FF; padding: 10px 0">
                欢 迎 注 册
            </div>
            <el-form ref="form" :model="form" :rules="rules">
                <!--                输入用户名-->
                <el-form-item prop="username">
                    <el-input v-model="form.username" clearable>
                        <template #prefix>
                            <i class="el-icon-user"></i>
                        </template>
                    </el-input>
                </el-form-item>
                <!--                    输入密码-->
                <el-form-item prop="password">
                    <el-input v-model="form.password" show-password clearable>
                        <template #prefix>
                            <i class="el-icon-lock"></i>
                        </template>
                    </el-input>
                </el-form-item>
                <!--                    确认密码-->
                <el-form-item prop="confirm">
                    <el-input v-model="form.confirm" show-password clearable>
                        <template #prefix>
                            <i class="el-icon-lock"></i>
                        </template>
                    </el-input>
                </el-form-item>

                <!--  发送邮箱验证码   -->
                <el-input v-model="mail" placeholder="输入邮箱以获取验证码" clearable style="width: 60%;"></el-input>
                <el-button style="margin-left: 10px;" @click="sendCheckCode" v-bind:disabled="disable" type="primary">
                    <p v-if="!disable">发送验证码</p>
                    <p v-if="disable">{{count}}</p>
                </el-button>
                <el-input v-model="checkCode" placeholder="输入邮箱验证码" clearable style="width: 60%;margin: 10px 0;"></el-input>

                <!--  确认普通验证码   -->
<!--                <div style="display: flex;margin: 10px 0;">-->
<!--                    <el-input prefix-icon="el-icon-key" v-model="form.validCode" style="width: 50%;" placeholder="请输入验证码" @keyup.enter="register"></el-input>-->
<!--                    <ValidCode @input="createValidCode" />-->
<!--                </div>-->

                <!--                    按钮-->
                <el-form-item>
                    <div style="float: right;">
                        <!--                    注册按钮-->
                        <el-button style="width: 245px;" type="primary" @click="register">注册</el-button>
                        <!--                前面登录页面-->
                        <el-button style="width: 245px;" type="primary" @click="$router.push('/login')">已有账号</el-button>
                    </div>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script>
    import request from "../../utils/request";
    import ValidCode from "../components/ValidCode";

    export default {
        name: "Register",
        data() {
            return {
                form: {},
                rules: {
                    username: [
                        {
                            required: true,
                            message: '请输入用户名',
                            trigger: 'blur',
                        },
                        {
                            pattern: /^[A-Za-z0-9\u4e00-\u9fa5]+$/,
                            message: "不允许输入空格等特殊符号",
                        },
                    ],
                    password: [
                        {
                            required: true,
                            message: '请输入密码',
                            trigger: 'blur',
                        },
                        {
                            min: 3,
                            max: 12,
                            message: '密码长度至少3位最多12位',
                            trigger: 'blur',
                        },
                        {
                            pattern: /^[A-Za-z0-9\u4e00-\u9fa5]+$/,
                            message: "不允许输入空格等特殊符号",
                        },
                    ],
                    confirm: [
                        {
                            required: true,
                            message: '请确认密码',
                            trigger: 'blur',
                        },
                        {
                            min: 3,
                            max: 12,
                            message: '密码长度至少3位最多12位',
                            trigger: 'blur',
                        },
                        {
                            pattern: /^[A-Za-z0-9\u4e00-\u9fa5]+$/,
                            message: "不允许输入空格等特殊符号",
                        },
                    ],
                },
                backgroundImageUrl:"http://"+window.server.fileUploadIp+":9090/files/download/63a04298833141378ea49396913b22bd.jpg",
                validCode:null,
                mail:"",
                checkCode:"",
                count:null,
                timer:null,
                disable:false,
            }
        },
        components: {
            ValidCode
        },
        methods: {
            createValidCode(data){
                this.validCode = data;
            },
            register() {
                this.$refs['form'].validate((valid) => {
                    if (this.form.password !== this.form.confirm) {
                        this.$notify({
                            type: "error",
                            message: "两次密码输入不一致"
                        });
                        return;
                    }
                    if (this.checkCode !== localStorage.getItem("checkCode")){
                        this.$notify({
                            type: "error",
                            message: "邮箱验证码不正确"
                        });
                        return;
                    }

                    if (valid) {
                        // if (!this.form.validCode) {
                        //     this.$notify.error("请填写验证码");
                        //     return
                        // }
                        // if (this.form.validCode.toLowerCase() !== this.validCode.toLowerCase()){
                        //     this.form.validCode = '';
                        //     this.$notify.error("验证码输入错误");
                        //     return
                        // }
                        request.post("/user/register", this.form).then(res => {
                            if (res.code === '0') {
                                this.$notify({
                                    type: "success",
                                    message: "注册成功"
                                });
                                localStorage.removeItem("checkCode");
                                this.$router.push("/login");
                            } else {
                                this.$notify({
                                    type: "error",
                                    message: res.msg
                                });
                            }
                        })
                    }
                })
            },
            sendCheckCode(){
                if (this.mail !== ""){
                    console.log(this.mail);
                    request.post("/mail/getCheckCode", this.mail).then(res=>{
                        if (res.code === "0"){
                            this.$message.success("发送成功，请查收~");
                            localStorage.setItem("checkCode", res.data);
                        }
                    });

                    if (!this.timer) {
                        this.count = 60;
                        this.disable = true;
                        this.timer = setInterval(() => {
                            if (this.count > 0 && this.count <= 60) {
                                this.count--;
                            } else {
                                this.disable = false;
                                clearInterval(this.timer);
                                this.timer = null;
                            }
                        }, 1000)
                    }
                }else {
                    this.$message.error("邮箱不能为空");
                }
            }
        },
        mounted() {
            document.title = "账号注册";
        }
    }
</script>

<style scoped>

</style>