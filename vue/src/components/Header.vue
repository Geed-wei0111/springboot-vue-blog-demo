<template>
    <div style="height: 50px; line-height: 50px; border-bottom: 1px solid #ccc; display:flex;">
        <div style="width: 200px; background-color: #FFE4E1;">
            <router-link style="padding-left:50px;font-size: large;color: green;" to="/index">
                前 往 主 页
            </router-link>
        </div>
        <div style="width: Calc( 100vw - 400px );background-color: #FDF5E6;display: inline-flex">
            <div style="justify-content: center;display: flex;background-color: #FDF5E6;">
                <el-switch v-model="currentMenuState" @click="changeMenuCollapse" style="margin: auto 0;" />
            </div>
            <el-breadcrumb separator="/" style="display: inline-block;margin: auto 0 auto 10px;">
                <el-breadcrumb-item :to="'/home'">后台管理</el-breadcrumb-item>
                <el-breadcrumb-item>{{ currentPathName }}</el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div style="width: 200px;background-color: #D8BFD8;padding-left: 3%;">
            <el-dropdown>
                <span style="font-weight: bolder">
                  你好，{{ user.nickname }}
                    <i class="el-icon-arrow-down"></i>
                </span>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item disabled><img :src="user.avatar" style="width: 50px;height: 50px;"/></el-dropdown-item>
                        <el-dropdown-item @click="$router.push('/blogWrite')">博客编写</el-dropdown-item>
                        <el-dropdown-item @click="$router.push('/myFavorite')">收藏夹</el-dropdown-item>
                        <el-dropdown-item @click="$router.push('/home')" v-if="isAdmin">后台管理</el-dropdown-item>
                        <el-dropdown-item @click="toPerson">个人资料</el-dropdown-item>
                        <el-dropdown-item @click="handleExit">退出登录</el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </div>
    </div>
</template>

<script>
    import { ArrowDown,Phone } from "@element-plus/icons";
    export default {
        name: "Header",
        components: { ArrowDown,Phone },
        data(){
            return{
                user:{},
                isAdmin:false,
            }
        },
        computed:{
            currentPathName(){
                return this.$store.state.currentPathName;
            },
            currentMenuState(){
                return !this.$store.state.menuState;
            }
        },
        methods:{
            handleExit(){
                this.$router.push('/login');
                localStorage.removeItem("user");
            },
            changeMenuCollapse(){
                this.$nextTick(()=>{
                    this.$store.commit("changeMenuState");
                })
            },
            toPerson(){
                this.$router.push(`/test`);
            }
        },
        created(){
            const userStr =  localStorage.getItem("user");
            if (userStr !== null){
                this.user = JSON.parse(userStr);
                this.isAdmin = this.user.username.includes("admin");
            }else{
                this.$alert('尚未登录', 'WARNING', {
                    confirmButtonText: '确定',
                    callback: action => {
                        if (action === "confirm" || action === "cancel"){
                            this.$router.push("/login");
                        }
                    }
                });
            }
        }
    }
</script>

<style scoped>
</style>