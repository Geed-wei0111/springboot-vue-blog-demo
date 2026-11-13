<template>
    <!--  网页内容  -->
    <div style="padding: 10px;" v-bind:class="{'collapse':isCollapse, 'expand':!isCollapse}">
        <div>
            <!--      一些操作按钮-->
            <el-upload ref="upload" :action="fileUploadUrl" :on-success="fileUploadSuccess" :show-file-list="false" style="display: inline-block;padding-right: 10px;">
                <el-button type="primary">
                    上传文件
                    <i class="el-icon-plus"></i>
                </el-button>
            </el-upload>

            <el-popover
                    placement="top-start"
                    trigger="hover"
                    content="重新刷新表格数据"
            >
                <template #reference>
                    <el-button type="info" @click="load" >
                        加载数据
                        <i class="el-icon-refresh"></i>
                    </el-button>
                </template>
            </el-popover>

            <el-button type="danger" @click="batchDelete" :disabled="batchDelPower" >
                批量删除文件
                <i class="el-icon-delete-solid"></i>
            </el-button>

            <el-alert
                    title="禁止传播黄赌毒暴等相关资源，一经发现后果自负！"
                    type="error"
                    style="width: 400px;display: inline;margin-left: 5px;"
                    :closable="false"
            ></el-alert>
            <el-alert
                    title="暂时只支持上传100MB以内的文件。"
                    type="warning"
                    style="width: 400px;display: inline;margin-left: 5px;"
                    :closable="false"
            ></el-alert>

            <div style="padding-top: 10px;padding-bottom: 10px;">
                <el-input v-model="search" placeholder="请输入关键字筛选文件名" clearable style="width: 20%;" @clear="load" @keyup.enter="load"></el-input>
                <el-button type="primary" @click="load">搜索<i class="el-icon-search"></i></el-button>

                <!--     筛选单选组    -->
                <el-radio-group v-model="radio" size="large" style="margin: 0 10px;" @change="selectType">
                    <el-radio label="不筛选"></el-radio>
                    <el-radio label="音乐"></el-radio>
                    <el-radio label="视频"></el-radio>
                    <el-radio label="图片"></el-radio>
                    <el-radio label="压缩包"></el-radio>
                </el-radio-group>
                <el-input v-model="search2" placeholder="请输入关键字筛选‘ 当前页 ’的资源（实验内容）" clearable style="width: 20%;" @clear="load"></el-input>
            </div>
        </div>

        <!--    表格展示数据-->
        <el-table
                :data="tableData"
                style="width: 100%"
                stripe="true"
                border="true"
                v-loading="loading"
                @selection-change="handleSelectionChange"
        >
            <el-table-column type="selection" width="40" />
            <el-table-column prop="id" label="ID" sortable width="60"></el-table-column>
            <el-table-column prop="name" label="名称"></el-table-column>
            <el-table-column prop="size" label="大小(Kb)" width="100"></el-table-column>
            <el-table-column prop="type" label="类型"></el-table-column>
            <el-table-column prop="enabled" label="公开">
                <template #default="scope">
                    <el-switch v-if="hasDelPower(scope.row)" v-model="scope.row.enabled" :active-value="1" :inactive-value="0" @change="handleEnabled(scope.row)"></el-switch>
                    <el-alert v-else type="warning" :closable="false">您没有权限更改该文件的公开权限</el-alert>
                </template>
            </el-table-column>
            <el-table-column prop="user" label="上传用户"></el-table-column>
            <el-table-column prop="remarks" label="备注"></el-table-column>

            <el-table-column fixed="right" label="操作" width="280px">
                <template #default="scope">
                    <el-button @click="downloadFile(scope.row.url)" type="primary" size="small">下载</el-button>
                    <el-button @click="handlePreview(scope.row)" type="success" size="small">
                        预览
                    </el-button>
                    <el-popconfirm v-if="hasDelPower(scope.row)" title="这是一段内容确定删除吗？" @confirm="handleDelete(scope.row.id)">
                        <template #reference>
                            <el-button type="danger" size="small">
                                删除
                                <i class="el-icon-delete"></i>
                            </el-button>
                        </template>
                    </el-popconfirm>
                </template>
            </el-table-column>
        </el-table>

        <!--    分页显示-->
        <div style="margin: 10px 0;">
            <el-pagination
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                    :current-page="currentPage"
                    :page-sizes="[5, 10]"
                    :page-size="pageSize"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="total"
            >
            </el-pagination>
        </div>
    </div>
    <el-drawer v-model="dialogVisible" direction="rtl" :before-close="handleDrawerClose">
        <template #title>
            <h1>文件预览</h1>
        </template>
        <el-image v-if="type === 'image'" :src="imageUrl" :preview-src-list="[imageUrl]" fit="fill"></el-image>
        <audio @canplay="$refs.au.volume = 0.3" ref="au" v-if="type === 'music'" :src="musicUrl" controls style="width: 400px;"></audio>
        <video ref="pr" v-if="type === 'video'" :src="videoUrl" controls style="width: 100%;"></video>
    </el-drawer>
</template>

<script>
    // @ is an alias to /src
    import request from "../../utils/request";

    export default {
        name: 'Files',
        components:{},
        data() {
            return {
                form: {},
                user:null,
                dialogVisible: false,
                search: '',
                currentPage: 1,
                pageSize: 10,
                total: 0,
                tableData: [],
                loading: true,
                fileUploadUrl:"http://"+window.server.fileUploadIp+":9090/files/upload3",
                multipleSelection:[],
                idBatch:[],
                imageUrl:null,
                musicUrl:null,
                type:"none",
                upEntity:{},
                radio:"不筛选",
                radioType:null,
                batchDelPower:false,
                search2:'',//实验内容，根据用户名筛选（filter）
                currentPageFilesState:true,//T代表一页里的文件全公开了
            }
        },
        computed:{
            isCollapse(){
                return this.$store.state.menuState;
            },
            // load2(){
            //     //过滤掉未公开的资源文件
            //     let newTable = this.tableData.filter(item=>{
            //         let user = item.user;
            //         const currentUser =  JSON.parse(localStorage.getItem("user")).username;
            //         return item.enabled === 1 || currentUser.includes("admin") || user === currentUser;
            //     });
            //     //如果筛选用户名有则加上
            //     if (this.search2 !== ''){
            //         this.currentPageFilesState = false;
            //         newTable = newTable.filter(item=>{
            //             return item.user.includes(this.search2);
            //         })
            //     }
            //     //提醒用户该页有隐藏文件
            //     this.$nextTick(()=>{
            //         if (this.currentPageFilesState){
            //             if (this.tableData.length > newTable.length){
            //                 this.currentPageFilesState = false;
            //                 this.$notify.info({
            //                     message: '当前页的某些资源文件已被上传用户或管理员隐藏',
            //                     offset: 150,
            //                 });
            //             }
            //         }
            //     });
            //     //返回数据
            //     return newTable;
            // },
        },
        methods: {
            handleEnabled(row){
                request.put(`/files/update`, row).then(res=>{
                    if (res.code === "0"){
                        this.$notify({
                            type:"warning",
                            title: 'Success',
                            message: "修改"+res.msg,
                            offset: 100,
                            duration:1000,
                        });
                    }
                })
            },
            hasDelPower(row){
                let roles = this.user.roles;
                return roles.find(p=>p === 1 || p === 4) || this.user.username === row.user;
            },
            selectType(){
                switch (this.radio) {
                    case "视频":
                        this.radioType = "video";
                        break;
                    case "音乐":
                        this.radioType = "music";
                        break;
                    case "图片":
                        this.radioType = "image";
                        break;
                    case "压缩包":
                        this.radioType = "package";
                        break;
                    case "不筛选":
                        this.radioType = "none";
                        break;
                }
                this.load();
            },
            handleDrawerClose(){
                if (this.type === "music"){
                    if (this.$refs.au.ended || this.$refs.au.paused){
                        this.dialogVisible = false;
                        return;
                    }
                    this.$messageBox({
                        title: '取消预览',
                        message: "是否后台播放音乐",
                        showCancelButton: true,
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        callback:action=>{
                            if (action === "confirm"){
                                this.$message.success("音乐后台播放中，切换页面或文件将停止播放");
                            }
                            if (action === "cancel"){
                                this.$refs.au.pause();
                            }
                            this.dialogVisible = false;
                        }
                    })
                }else{
                    this.dialogVisible = false;
                }
            },
            handlePreview(row){
                if (row.type === "jpg" || row.type === "png"){
                    this.type = "image";
                    this.imageUrl = row.url;
                    this.dialogVisible = true;
                }
                else if (row.type === "mp3" || row.type === "wav"){
                    this.type = "music";
                    this.musicUrl = row.url;
                    this.dialogVisible = true;
                }else{
                    this.type = "none";
                    this.$messageBox("该类型不支持预览哦");
                }
            },
            downloadFile(url){
                window.open(url);
            },
            fileUploadSuccess(res) {
                if (res.code === "0"){
                    this.$notify.success("上传成功");

                    this.$messageBox.prompt('请输入备注', 'Tip', {
                        confirmButtonText: 'OK',
                        cancelButtonText: 'Cancel',
                    }).then(({ value }) => {
                        if (value !== ""){
                            this.upEntity = {};
                            this.upEntity.id = res.data;
                            this.upEntity.remarks = value;

                            const userStr =  localStorage.getItem("user");
                            if (userStr !== null){
                                this.upEntity.user = JSON.parse(userStr).username;
                            }

                            request.put(`/files/update`, this.upEntity).then(res=>{
                                if (res.code === "0"){
                                    this.$notify.success("备注"+res.msg);
                                }
                            });
                        }
                        this.$nextTick(()=>{
                            this.load();
                        })
                     });
                }else{
                    this.$message.error("上传失败");
                }
            },
            batchDelete(){
                this.$messageBox({
                    showCancelButton:true,
                    confirmButtonText:"确定",
                    cancelButtonText:"取消",
                    message:"你确定要批量删除吗",
                    title:"WARNING",
                    center: true,
                    callback:action=>{
                        if (action === "confirm"){
                            request.delete("/files/batch", {
                                params:{
                                    array:encodeURI(this.idBatch),
                                }
                            }).then((res)=>{
                                this.$message.info(res.msg);
                                this.$nextTick(()=>{
                                    this.load();
                                });
                            })
                        }else{
                            this.$message.info("好的呢~");
                        }
                    }
                })
            },
            handleSelectionChange(val){
                const localUsername =  JSON.parse(localStorage.getItem("user")).username;
                this.idBatch = [];
                this.multipleSelection = val;
                this.batchDelPower = false;
                for (let i = 0;i<this.multipleSelection.length;i++){
                    if (this.multipleSelection[i].user === localUsername || localUsername.includes("admin")){
                        this.idBatch.push(this.multipleSelection[i].id);
                    }else {
                        if(!this.batchDelPower){
                            this.batchDelPower = true;
                            this.$notify.warning("你选中的文件中，有些不是你所上传的，待会将不能删除");
                        }
                    }
                }
                // console.log(this.idBatch);
            },
            load() {
                this.currentPageFilesState = true;
                this.loading = true;
                request.get("/files/getList", {
                    params: {
                        pageNum: this.currentPage,
                        pageSize: this.pageSize,
                        search: this.search,
                        type:this.radioType
                    }
                }).then(res => {
                    this.tableData = res.data.records;
                    this.total = res.data.total;

                    this.$nextTick(() => {
                        this.loading = false;
                    });
                })
            },
            handleDelete(id) {
                request.delete("/files/" + id).then(res => {
                    if (res.code === '0') {
                        this.$message({
                            type: "success",
                            message: "删除成功"
                        });
                        this.load();
                    } else {
                        this.$message({
                            type: "error",
                            message: "删除失败"
                        });
                    }
                })
            },
            //改变每页个数触发
            handleSizeChange(pageSize) {
                this.pageSize = pageSize;
                this.load();
            },
            //改变页码触发
            handleCurrentChange(pageNum) {
                this.currentPage = pageNum;
                this.load();
            },
        },
        created() {
            this.load();
            this.user = JSON.parse(localStorage.getItem("user"));
        },
        mounted() {
            document.title = "文件资源共享";
        }
    }
</script>
<style>
    .collapse{
        width: calc((100vw - 60px) );
        height: calc((100vh - 50px) );
    }
    .expand{
        width: calc((100vw - 200px) );
        height: calc((100vh - 50px) );
    }
</style>