<template>
        <!-- vue3.0配置 -->
        <router-view v-slot="{ Component }">
                <keep-alive>
                        <component :is="Component" :key="$route.name"  v-if="$route.meta.keepAlive"/>
                </keep-alive>
                <component :is="Component" :key="$route.name"  v-if="!$route.meta.keepAlive"/>
        </router-view>
</template>

<style>
</style>

<script>
    export default {
    name:"App",
    components:{
    },
    data(){
      return {
      }
    }
  }
</script>