<template>
    <div class="footer">
        <el-row :gutter="24" style="padding-top: 30px">
            <el-col :sm="8">
                <el-row :gutter="24" class="footer-title">
                    <el-col :span="4" :offset="7">联系站长</el-col>
                </el-row>
                <el-row :gutter="24" class="footer-content">
                    <el-col :sm="10" :offset="4">
                        QQ：66741631<br>
                        邮箱：66741631@qq.com
                    </el-col>
                    <el-col :sm="10">
<!--                        <el-image :src="src" style="width: 80px; height: 80px"></el-image>-->
                    </el-col>
                </el-row>
            </el-col>

            <el-col :sm="8">
                <el-row :gutter="24" class="footer-title">
                    <el-col :span="24">签名</el-col>
                </el-row>
                <el-row :gutter="24" class="footer-content">
                    <el-col :span="24" style="font-size: 15px">
                        青春是一个短暂的美梦, 当你醒来时, 它早已消失无踪
                    </el-col>
                </el-row>
            </el-col>
            <el-col :sm="8">
                <el-row :gutter="24" class="footer-title">
                    <el-col :span="24">网站信息</el-col>
                </el-row>
                <el-row :gutter="24" class="footer-content">
                    <el-col :span="24">
                        {{'总访问量: '+pv}} <br>
                        {{'访客量: '+uv}}
                    </el-col>
                </el-row>
            </el-col>

        </el-row>
        <el-divider ></el-divider>

        <el-row :gutter="24" style="height: 50px;margin-top: 25px;font-size: 12px">
            <el-col :span="24">
                Copyright&nbsp;&copy;&nbsp;2021-2022&nbsp;GEED's&nbsp;Blog.&nbsp;All&nbsp;rights&nbsp;reserved.&nbsp;
            </el-col>
        </el-row>

    </div>
</template>

<script>
    export default {
        name: "Footer"
    }
</script>

<style scoped>
    .footer-title {
        font-size: 13px;
        font-weight: bold;
        margin-bottom: 5px;
    }
    .footer-content {
        font-size: 13px;
    }
    .footer {
        margin: 0 auto;
        text-align: center;
        min-height: 20px;
        max-width: 100%;
        padding: 0 15px;
    }
</style>