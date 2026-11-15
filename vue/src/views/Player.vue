<template>
    <div style="width: 100vw;height: 100vh;background-color: #FFF0F5;position: fixed;">
        <div style="margin: 20% auto;width: 500px;">
            <el-select style="width: 50%" v-model="select" placeholder="请选择一首歌曲" @change="change">
                <el-option
                        v-for="item in audio"
                        :value="item.name"
                >
                    <span style="float: left">{{ item.name }}</span>
                    <span style="float: right;color: var(--el-text-color-secondary);font-size: 13px;">{{ item.artist }}</span>
                </el-option>
            </el-select>

            <el-button style="margin-left: 10px;" type="primary" @click="play">播放</el-button>
            <el-button type="primary" @click="pause">暂停</el-button>
            <!--        <el-progress :text-inside="true" :stroke-width="26" :percentage="percent" />-->
            <audio controls :src="nowPlaying" ref="player" style="width: 500px;" @canplay="get"></audio>
        </div>
    </div>
</template>

<script>
    import request from "../../utils/request";

    export default {
        name: "Player",
        components: {
        },
        data(){
            return{
                audio:[],
                nowPlaying:"",
                select:null,
                current:null,
                duration:null,
                isPlaying:false,
                timer:null,
            }
        },
        computed:{
            percent(){
                return parseInt(this.current / this.duration  * 100);
            }
        },
        methods:{
            loadMusics(){
                request.get("/music").then(res=>{
                    this.audio = res.data;
                })
            },
            change(){
                for (var i=0;i<this.audio.length;i++){
                    if (this.select === this.audio[i].name){
                        this.nowPlaying = this.audio[i].url;
                        break;
                    }
                }
                this.$refs.player.load();
                this.$refs.player.volume = 0.3;
            },
            play(){
                if (this.nowPlaying !== ""){
                    this.isPlaying = true;
                    this.$refs.player.play();
                    document.title="正在播放："+this.select;
                }else
                    this.$message.info("请先选择歌曲");
            },
            pause(){
                if (this.nowPlaying !== ""){
                    this.isPlaying = false;
                    this.$refs.player.pause();
                    document.title="正在播放："+this.select;
                }else
                    this.$message.info("请先选择歌曲");
            },
            // get(){
            //     if (this.isPlaying){
            //         this.current = this.$refs.player.currentTime;
            //         this.duration = this.$refs.player.duration;
            //         // console.log(this.$refs.player.volume)
            //     }
            // }
        },
        mounted (){
            this.loadMusics();

            // this.timer = setInterval(this.get(), 1000);
        },
        beforeDestroy () {
            // console.log("hahaha");
            // clearInterval(this.timer);
            // this.isPlaying = false;
        }
    }
</script>

<style scoped>

</style>