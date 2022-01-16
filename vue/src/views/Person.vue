<template>
    <div>
        <el-descriptions
                title="你的个人信息"
                direction="vertical"
                :column="3"
                size="large"
                border
                style="width: 500px;margin: 200px auto;"
        >
            <el-descriptions-item label="用户名">
                {{user.username}}
            </el-descriptions-item>
            <el-descriptions-item label="昵  称">
                {{user.nickname}}
            </el-descriptions-item>
            <el-descriptions-item label="密  码">
                {{user.password}}
            </el-descriptions-item>
            <el-descriptions-item label="地  址">
                {{user.address}}
            </el-descriptions-item>
            <el-descriptions-item label="性  别">
                {{user.sex}}
            </el-descriptions-item>
            <el-descriptions-item label="年  龄">
                {{user.age}}
            </el-descriptions-item>
        </el-descriptions>
    </div>
</template>

<script>
    export default {
        name: "Person",
        data(){
            return{
                user:{}
            }
        },
        created(){
            const userStr =  sessionStorage.getItem("user");
            if (userStr !== ""){
                this.user = JSON.parse(userStr);
            }
        }
    }
</script>

<style scoped>

</style>