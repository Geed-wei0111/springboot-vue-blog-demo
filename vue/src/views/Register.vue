<template>
    <div style="width: 100%;height: 100vh;overflow: hidden;background-color: darkcyan">
        <div style="margin: 400px auto; width: 500px;">
            <div style="text-align: center; font-size: 30px; color: #cccccc; padding: 10px 0">
                欢 迎 注 册
            </div>
            <el-form ref="form" :model="form" :rules="rules">
<!--                输入用户名-->
                <el-form-item prop="username" >
                    <el-input  v-model="form.username" clearable>
                        <template #prefix>
                            <el-icon class="el-input__icon"><User /></el-icon>
                        </template>
                    </el-input>
                </el-form-item>
<!--                    输入密码-->
                <el-form-item prop="password" >
                    <el-input v-model="form.password" show-password clearable>
                        <template #prefix>
                            <el-icon class="el-input__icon"><Lock /></el-icon>
                        </template>
                    </el-input>
                </el-form-item>
<!--                    确认密码-->
                <el-form-item prop="confirm">
                    <el-input v-model="form.confirm" show-password clearable>
                        <template #prefix>
                            <el-icon class="el-input__icon"><Lock /></el-icon>
                        </template>
                    </el-input>
                </el-form-item>
<!--                    按钮-->
                <el-form-item >
                    <div style="float: right;" >
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
    import { Lock,User } from "@element-plus/icons";
    export default {
        name: "Register",
        data(){
            return{
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
                    confirm:[
                        {
                            required: true,
                            message: '请确认密码',
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
            register(){
                if(this.form.password !== this.form.confirm){
                    this.$message({
                        type:"error",
                        message:"两次密码输入不一致"
                    });
                    return;
                }
                this.$refs['form'].validate((valid)=>{
                    if (valid){
                        request.post("/user/register", this.form).then(res=>{
                            if (res.code === '0'){
                                this.$message({
                                    type:"success",
                                    message:"注册成功"
                                });
                                this.$router.push("/login");
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
            document.title="注册界面";
        }
    }
</script>

<style scoped>

</style>