<template>
    <div>
        <!--    头部-->
        <Header></Header>
    </div>
    <!--    如果在模板这不改变width，样式将乱套    -->
    <div style="display: flex;">
        <!--    侧边栏-->
        <Aside v-bind:class="{'aside':!isCollapse}"></Aside>
        <!--    内容-->
        <router-view v-bind:class="{'content':!isCollapse}" style="flex: 1;" />

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
        },
        computed:{
            isCollapse(){
                return this.$store.state.menuState;
            }
        }
    }
</script>

<style scoped>
.content{
    width: calc(100vw - 200px);
}
.aside{
    width: 200px;
}
</style>