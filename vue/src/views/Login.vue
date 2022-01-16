<template>
    <div style="width: 100%;height: 100vh;overflow: hidden;background-color: darkcyan">
        <div style="margin: 400px auto; width: 500px;">
            <div style="text-align: center; font-size: 30px; color: #cccccc; padding: 10px 0">
                欢 迎 登 录
            </div>
            <el-form ref="form" :model="form" :rules="rules">
<!--                输入用户名-->
                <el-form-item prop="username">
                    <el-input v-model="form.username" @keyup.enter="handleLogin">
                        <template #prefix>
                            <el-icon class="el-input__icon"><User /></el-icon>
                        </template>
                    </el-input>
                </el-form-item>
<!--                    输入密码-->
                <el-form-item prop="password">
                    <el-input v-model="form.password" show-password @keyup.enter="handleLogin">
                        <template #prefix>
                            <el-icon class="el-input__icon"><Lock /></el-icon>
                        </template>
                    </el-input>
                </el-form-item>

                <el-form-item >
                    <div style="float: right;" >
                        <!--                    登录按钮-->
                        <el-button style="width: 245px;" type="primary" @click="login">登录</el-button>
                        <!--                前面注册页面-->
                        <el-button style="width: 245px;" type="primary" @click="$router.push('/register')">前往注册</el-button>
                    </div>
                </el-form-item>
            </el-form>

            <el-dialog
                    v-model="dialogVisible"
                    title="Tips"
                    width="30%"
            >
                <span>目前未适配移动端</span>
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
    import { Lock,User } from "@element-plus/icons";
    export default {
        name: "Login",
        data(){
            return{
                dialogVisible:false,
                form:{},
                rules:{
                    username:[
                        {
                            required: true,
                            message: '请输入用户名',
                            trigger: 'blur',
                        }
                    ],
                    password:[
                        {
                            required: true,
                            message: '请输入密码',
                            trigger: 'blur',
                        }
                    ],
                }
            }
        },
        components:{
            Lock,User
        },
        methods:{
            goToHome(){

                this.$router.push("/home");
            },
            handleLogin(){
                this.login();
            },
            login(){
                this.$refs['form'].validate((valid)=>{
                    if (valid){
                        request.post("/user/login", this.form).then(res=>{
                            if (res.code === '0'){
                                this.$message({
                                    type:"success",
                                    message:"登录成功"
                                });
                                sessionStorage.setItem("user", JSON.stringify(res.data))  // 缓存用户信息
                                this.dialogVisible = true;
                            }else{
                                this.$message({
                                    type:"error",
                                    message:res.msg
                                });
                            }
                        })
                    }
                })
            }
        },
        mounted(){
            document.title="登录界面";
        }
    }
</script>

<style scoped>

</style>