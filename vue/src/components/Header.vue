<template>
    <div style="height: 50px; line-height: 50px; border-bottom: 1px solid #ccc; display:flex;">
        <div style="width: 200px;padding-left: 50px; font-weight: bold; background-color: #FFE4E1;">后台管理</div>
        <div style="flex: 1;background-color: #FDF5E6;"></div>
        <div style="width: 100px;background-color: #D8BFD8;padding-left: 20px;">
            <el-dropdown>
                <span style="font-weight: bolder">
                  {{ user.username }}
                    <el-icon><ArrowDown /></el-icon>
                </span>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item @click="$router.push('/person')">个人资料</el-dropdown-item>
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
                user:{}
            }
        },
        methods:{
            handleExit(){
                this.$router.push('/login');
                sessionStorage.removeItem("user");
            }
        },
        created(){
            const userStr =  sessionStorage.getItem("user");
            this.user = JSON.parse(userStr);
        }
    }
</script>

<style scoped>
</style>