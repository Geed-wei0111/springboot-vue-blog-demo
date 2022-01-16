<template>
    <el-menu
            :default-active="flag"
            @close="handleClose"
            router="true"
            style="width: 200px; height: Calc(100vh - 50px);background-color: #E6E6FA;"
    >
        <el-sub-menu>
            <template #title>
                <el-icon><avatar /></el-icon>
                <span>用户管理</span>
            </template>
            <el-menu-item-group style="background-color: #E6E6FA;">
                <el-menu-item index="/home">
                    <el-icon><operation /></el-icon>
                    所有用户
                </el-menu-item>
                <el-menu-item index="/uploadTest">
                    <el-icon><Setting /></el-icon>
                    你谁啊你？
                </el-menu-item>
            </el-menu-item-group>
        </el-sub-menu>

        <el-menu-item index="/test?id=1">
            <el-icon><Location /></el-icon>
            <template #title>测试罢了！</template>
        </el-menu-item>

        <el-menu-item index="/news">
            <el-icon><star /></el-icon>
            <template #title>博客测试</template>
        </el-menu-item>

    </el-menu>
</template>

<script>
    import {Location, Document, Setting, Avatar, Operation, Star} from '@element-plus/icons';
    export default {
        name: "Aside",
        components:{Location, Document, Setting, Avatar, Operation, Star},
        data(){
            return {
                flag:this.$route.path
            }
        }
    }
</script>

<style scoped>

</style>