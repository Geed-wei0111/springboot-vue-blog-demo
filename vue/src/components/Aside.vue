<template>
    <el-menu
            :default-active="loadPath"
            router="true"
            style="height: Calc(100vh - 50px);background-color: #E6E6FA;"
            :collapse="isCollapse"
            :collapse-transition="false"
    >

        <el-menu-item v-for="m in user.permissions" :index="`/${m.path}`" :key="m.id" >
            <i :class="m.icon"></i>
            <template #title>{{m.comment}}</template>
        </el-menu-item>

    </el-menu>
</template>

<script>
    export default {
        name: "Aside",
        components:{
        },
        data(){
            return {
                flag:this.$route.path,
                user:{},
            }
        },
        computed:{
            isCollapse(){
                return this.$store.state.menuState;
            },
            loadPath(){
                return this.$store.state.currentPath;
            },
        },
        methods:{
            changeMenuCollapse(){
                this.$store.commit("changeMenuState");
            },
        },
        created(){
            this.user = JSON.parse(localStorage.getItem("user"));
        }
    }
</script>

<style scoped>

</style>