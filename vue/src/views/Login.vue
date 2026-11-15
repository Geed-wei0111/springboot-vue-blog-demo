<template>
    <div>
        <el-image :src="backgroundImageUrl" style="position: absolute;z-index: -1;height: 100%;width: 100%"></el-image>
    </div>
    <div style="width: 100%;height: 100vh;overflow: hidden;">
        <div style="margin: 400px auto; width: 500px;">
            <div style="text-align: center; font-size: 30px; color: #E6E6FA; padding: 10px 0">
                欢 迎 登 录
            </div>
            <el-form ref="form" :model="form" :rules="rules">
                <!--                输入用户名-->
                <el-form-item prop="username">
                    <el-input v-model="form.username" @keyup.enter="handleLogin" clearable >
                        <template #prefix>
                            <i class="el-icon-user"></i>
                        </template>
                    </el-input>
                </el-form-item>
                <!--                    输入密码-->
                <el-form-item prop="password">
                    <el-input v-model="form.password" show-password @keyup.enter="handleLogin" clearable>
                        <template #prefix>
                            <i class="el-icon-lock"></i>
                        </template>
                    </el-input>
                </el-form-item>

                <div style="display: flex;margin: 10px 0;">
                    <el-input prefix-icon="el-icon-key" v-model="form.validCode" style="width: 50%;" placeholder="请输入验证码" @keyup.enter="handleLogin"></el-input>
                    <ValidCode style="margin-left: 10px;" @input="createValidCode"></ValidCode>
                </div>

                <el-form-item>
                    <div style="float: right;">
                        <!--                    登录按钮-->
                        <el-button style="width: 245px;" type="primary" @click="login">登录</el-button>
                        <!--                前面注册页面-->
                        <el-button style="width: 245px;" type="primary" @click="$router.push('/register')">前往注册
                        </el-button>
                    </div>
                </el-form-item>
            </el-form>

            <el-dialog
                    v-model="dialogVisible"
                    title="Tips"
                    width="30%"
            >
                <div>博客系统毕设实践demo——————by Geed</div>
                <div>你可能会看到：1.无逻辑的跳转 2.奇怪的bug 3.未完成的功能等</div>
<!--                <div>（估计也就开发了2分之1？？）</div>-->
                <template #footer>
                <span class="dialog-footer">
                    <el-button type="primary" @click="goToHome">确认</el-button>
                </span>
                </template>
            </el-dialog>
        </div>
    </div>
</template>

<script>
    import request from "../../utils/request";
    import ValidCode from "../components/ValidCode";
    import {activeRouter} from "../../utils/permission";

    export default {
        name: "Login",
        data() {
            return {
                dialogVisible: false,
                form: {},
                rules: {
                    username: [
                        {
                            required: true,
                            message: '请输入用户名',
                            trigger: 'blur',
                        },
                        {
                            min: 3,
                            message: '用户名长度至少3位',
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
                },
                isLogin: false,
                backgroundImageUrl:"http://"+window.server.fileUploadIp+":9090/files/download/589e8ccd97424bc2bdfd6afee68100bc.jpg",
                validCode:null,
            }
        },
        components: {
            ValidCode,
        },
        methods: {
            createValidCode(data) {
                this.validCode = data
            },
            goToHome() {
                this.$router.push("/index");
            },
            handleLogin() {
                if (!this.isLogin) {
                    this.login();
                }else{
                    this.$nextTick(()=>{
                        this.goToHome();
                    })
                }
            },
            login() {
                this.$refs['form'].validate((valid) => {
                    if (valid) {
                        if (!this.form.validCode) {
                            this.$message.error("请填写验证码");
                            return;
                        }
                        if (this.form.validCode.toLowerCase() !== this.validCode.toLowerCase()){
                            this.form.validCode = '';
                            this.$message.error("验证码输入错误");
                            return;
                        }
                        request.post("/user/login", this.form).then(res => {
                            if (res.code === '0') {
                                this.$message({
                                    type: "success",
                                    message: "登录成功"
                                });
                                localStorage.setItem("user", JSON.stringify(res.data));  // 缓存用户信息
                                localStorage.removeItem("ip");
                                activeRouter();//动态路由
                                // this.dialogVisible = true;
                                // this.isLogin = true;
                                let roles = res.data.roles;
                                if (roles.find(p => p === 1 || p === 3 || p === 4 || p === 5)) {
                                    this.$router.push('/home');
                                }else{
                                    this.$router.push('/index');
                                }
                            } else {
                                this.$message.error(res.msg);
                                this.form.password = '';
                            }
                        })
                    }
                })
            }
        },
        mounted() {
            document.title = "用户登录";
        }
    }
</script>

<style scoped>

</style>