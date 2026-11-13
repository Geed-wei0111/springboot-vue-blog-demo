<template>
    <div style="display:flex;background-color: #B0C4DE">
        <el-menu
                router="true"
                :default-active="loadPath"
                mode="horizontal"
                style="flex: 1;width: Calc( 100vw - 200px );height: 60px;"
                text-color="#4169E1"
                background-color="#B0C4DE"
        >
            <el-menu-item style="margin-left: 50px;;" index="/index" @click="startRead"> 主 页</el-menu-item>
            <el-menu-item v-if="user!==null" index="/blogSearch">博客搜索</el-menu-item>
            <el-menu-item v-if="user!==null" index="/myBlogs">我的博客</el-menu-item>
            <el-menu-item v-if="user!==null" index="/resource">资源分享</el-menu-item>
            <el-menu-item v-if="user!==null" index="/blogWrite">博客编写</el-menu-item>
            <el-menu-item v-if="user!==null" index="/myFavorite">收藏夹</el-menu-item>
            <el-menu-item v-if="user!==null" index="" @click="toComment">留 言 区</el-menu-item>
            <el-menu-item v-if="user===null">~登录后可查看更多功能~</el-menu-item>
        </el-menu>
        <div style="width: 200px;line-height: 60px;padding-left: 40px;">
            <el-dropdown>
                <span v-if="user !== null" style="font-weight: bolder">
                  {{ user.username }}
                    <i class="el-icon-arrow-down"></i>
                </span>

                <span v-else style="font-weight: bolder">
                  您尚未登录
                    <i class="el-icon-arrow-down"></i>
                </span>
                <template #dropdown>
                    <el-dropdown-menu v-if="user !== null">
                        <el-dropdown-item @click="$router.push('/myVisit')">浏览记录</el-dropdown-item>
                        <el-dropdown-item @click="$router.push('/home')">后台管理</el-dropdown-item>
                        <el-dropdown-item @click="toPerson">个人资料</el-dropdown-item>
                        <el-dropdown-item @click="toMyBilibili">关于网站作者</el-dropdown-item>
                        <el-dropdown-item @click="toOpenSource">系统开源 </el-dropdown-item>
                        <el-dropdown-item @click="handleExit">退出登录</el-dropdown-item>
                    </el-dropdown-menu>
                    <el-dropdown-menu v-else>
                        <el-dropdown-item @click="$router.push('/login')">登录</el-dropdown-item>
                        <el-dropdown-item @click="$router.push('/register')">注册</el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </div>
    </div>
</template>

<script>
    export default {
        name: "Bside",
        components:{
        },
        data(){
            return{
                user:null,
            }
        },
        computed:{
            isIndex(){
                return this.$route.name === "Index";
            },
            loadPath(){
                return this.$route.path;
            },
        },
        methods:{
            startRead(){
                if (this.$route.path === "/index") {
                    this.$nextTick(()=>{
                        this.$emit("scrollToMainContent");
                    })
                }
            },
            toOpenSource(){
                window.open("https://github.com/Geed-wei0111/springboot-vue-blog-demo");
            },
            toMyBilibili(){
                window.open("https://space.bilibili.com/4398856");
            },
            toComment(){
                this.$store.commit("setMenuCollapse");
                this.$router.push('/comment');
            },
            handleExit(){
                this.$router.push('/login');
                localStorage.removeItem("user");
            },
            toPerson(){
                this.$router.push(`/test?id=${this.user.id}`);
                this.$store.commit("setMenuCollapse");
            },
        },
        created(){
            //获取本地存储的登录信息
            const userStr =  localStorage.getItem("user");
            if (userStr !== null){
                this.user = JSON.parse(userStr);
            }
        },
        activated(){
            this.user = JSON.parse(localStorage.getItem("user"));
        }
    }
</script>

<style scoped>

</style>