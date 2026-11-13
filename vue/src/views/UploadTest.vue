<template>
    <div class="demo-image">
        <el-image
                style="max-width: 640px; max-height: 360px;"
                :src="url"
                :preview-src-list="[url]"
                :initial-index="1"
        >
        </el-image>

        <el-upload ref="upload" :action="fileUploadUrl" :on-success="fileUploadSuccess">
            <el-button type="primary" style="width: 200px">点击上传文件</el-button>
        </el-upload>
    </div>
</template>

<script>
    export default {
        name: "UploadTest",
        methods: {
            fileUploadSuccess(res) {
                this.$refs['upload'].clearFiles();    //清空上传历史记录
                if ( res.type === ".jpg" || res.type === ".png" ){
                    this.url = res.url;
                }else if (res.type === ".mp3" || res.type === ".wav"){
                    this.$message({
                        type:"info",
                        message:"音乐文件可以去音乐播放界面试听"
                    });
                }
                else{
                    this.$message({
                        type:"error",
                        message:"文件不是JPG和PNG格式，无法预览"
                    });
                }
            }
        },
        data() {
            return {
                url: "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.jj20.com%2Fup%2Fallimg%2Ftp05%2F1Z9291TIBZ6-0-lp.jpg&refer=http%3A%2F%2Fimg.jj20.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1644732632&t=819943c02f5f61b58c689d42f4d5a4e7",
                fileUploadUrl: "http://" + window.server.fileUploadIp + ":9090/files/upload2"
            }
        },
        mounted() {
            document.title = "上传测试";
            this.$messageBox("如果上传的文件为图片格式，可以预览~");
        }
    }
</script>
<style scoped>
</style>