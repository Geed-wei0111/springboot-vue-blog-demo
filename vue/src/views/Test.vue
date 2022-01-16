<template>
    <div>
        <el-descriptions
                title="测试展示个人信息"
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
    import request from "../../utils/request";

    export default {
        name: "Test",
        data(){
            return {
                userId:null,
                user:{}
            }
        },
        created(){
            //   接受参数
            let query = this.$route.query;
            this.userId = query.id;
        },
        mounted(){
            document.title="跳转查看个人信息";
            request.get("/user/id", {
                params:{
                    id:this.userId,
                }
            }).then(res=>{
                this.user = res.data;
            })
        }
    }
</script>

<style scoped>

</style>