<template>
    <div id="nav">
        <!--    头部-->
        <Header />
    </div>
    <div style="display: flex;">
        <!--    侧边栏-->
        <Aside />
        <!--    内容-->
        <router-view style="flex: 1;" />
    </div>
</template>

<script>
    import Header from "../components/Header";
    import Aside from "../components/Aside";
    export default {
        name: "Layout",
        components:{
            Header,
            Aside
        }
    }
</script>

<style scoped>

</style>