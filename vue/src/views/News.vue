<template>
  <div style="padding: 10px;background-color: #FFFACD;">
    <div>
<!--      一些操作按钮-->
      <el-button type="primary" @click="add">
        新增数据
      </el-button>
      <el-button type="primary" @click="load">
        加载数据
      </el-button>
      <div style="padding-top: 10px;padding-bottom: 10px;">
        <el-input v-model="search" placeholder="请输入关键字" clearable style="width: 20%;"></el-input>
        <el-button type="primary" @click="load">搜索</el-button>
      </div>
    </div>

<!--    表格展示数据-->
    <el-table
            :data="tableData"
            style="width: 100%"
            :row-class-name="tableRowClassName"
            stripe="true"
            border="true"
            v-loading="loading"
    >
      <el-table-column prop="id" label="ID" sortable> </el-table-column>
      <el-table-column prop="title" label="标题" > </el-table-column>

      <el-table-column fixed="right" label="操作" width="300px">
        <template #default="scope">
          <el-button @click="details(scope.row)" type="primary" size="small">查看博客</el-button>
          <el-button @click="handleEdit(scope.row)" type="warning" size="small">编辑</el-button>

          <el-popconfirm title="这是一段内容确定删除吗？" @confirm="handleDelete(scope.row.id)">
            <template #reference>
              <el-button type="error" size="small">删除</el-button>
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
              :page-sizes="[5, 10, 20]"
              :page-size="pageSize"
              layout="total, sizes, prev, pager, next, jumper"
              :total="total"
      >
      </el-pagination>

<!--      增加数据的弹窗-->
      <el-dialog
              v-model="dialogVisible"
              title="请填写数据"
              width="30%"
      >
        <el-form :model="form" label-width="120px">
          <el-form-item label="标题">
            <el-input v-model="form.title" style="width: 80%"></el-input>
          </el-form-item>

          <div id="div1">
<!--            编辑器在这-->
          </div>
        </el-form>

        <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="save"
        >Confirm</el-button>
      </span>
        </template>
      </el-dialog>

        <el-dialog title="详情" v-model="contentVisible" width="50%">
            <el-card>
                <div v-html="detail.content" style="min-height: 100px"></div>
            </el-card>
        </el-dialog>

    </div>
  </div>
</template>

<script>
// @ is an alias to /src
import request from "../../utils/request";
import E from 'wangeditor'

export default {
  name: 'News',
    data(){
        return{
            form:{},
            detail:{},
            dialogVisible:false,
            contentVisible:false,
            search: '',
            currentPage: 1,
            pageSize:10,
            total: 10,
            tableData:[],
            editor: null,
            loading:false
        }
    },
  methods:{
    details(row){
        this.detail = row;
        this.contentVisible = true;
    },
    load(){
      this.loading = true;
      request.get("/news", {
          params:{
              pageNum:this.currentPage,
              pageSize:this.pageSize,
              search:this.search
          }
      }).then(res=>{
          this.tableData = res.data.records;
          this.total = res.data.total;

          this.$nextTick(()=>{
              this.loading = false;
          });
          // clearTimeout(this.timer);
          // this.timer = setTimeout(()=>{
          //     this.loading = false;
          // }, 300);
      })
    },
    add(){
      this.form = {};
      this.dialogVisible = true;

        this.$nextTick(()=>{
            if (!this.editor){
                this.editor = new E('#div1');

                //设置图片服务器端口
                this.editor.config.uploadImgServer = 'http://' + window.server.fileUploadIp + ":9090/files/editor/upload";
                this.editor.config.uploadFileName = "file";  // 设置上传参数名称

                this.editor.create();
            }
            this.editor.txt.html("");
        });
    },
    save(){
        //如果富文本编辑器存在，那么将编辑器里的文本存放到表单中
        if (this.editor){
            this.form.content = this.editor.txt.html();
        }

        if (this.form.id){  //如果表单中有id存在，则证明接下来要进行修改操作
            request.put("/news", this.form).then(res=>{
                if (res.code === '0'){
                    this.$message({
                        type:"success",
                        message:"更新成功"
                    });
                }else{
                    this.$message({
                        type:"error",
                        message:"更新失败"
                    });
                }
                this.dialogVisible = false;
                this.load();
            })
        }else { //没有id表示为新增操作
            request.post("/news", this.form).then(res=>{
                if (res.code === '0'){
                    this.$message({
                        type:"success",
                        message:"新增成功"
                    });
                }else{
                    this.$message({
                        type:"error",
                        message:"新增失败"
                    });
                }
                this.dialogVisible = false;
                this.load();
            });
        }
    },
    handleEdit(row){
        // 深拷贝，以免改动原表格的数据
        this.form = JSON.parse(JSON.stringify(row));
        this.dialogVisible = true;

        this.$nextTick(()=>{
            if (!this.editor){
                this.editor = new E('#div1');

                //设置图片服务器端口
                this.editor.config.uploadImgServer = 'http://' + window.server.fileUploadIp + ":9090/files/editor/upload";
                this.editor.config.uploadFileName = "file";  // 设置上传参数名称

                this.editor.create();
            }
            this.editor.txt.html(row.content)
        })

    },
    handleDelete(id){
        request.delete("/news/"+id).then(res=>{
            if (res.code === '0'){
                this.$message({
                    type:"success",
                    message:"删除成功"
                });
                this.load();
            }else{
                this.$message({
                    type:"error",
                    message:"删除失败"
                });
            }
        })
    },
      //改变每页个数触发
    handleSizeChange(pageSize){
        this.pageSize = pageSize;
        this.load();
    },
      //改变页码触发
    handleCurrentChange(pageNum){
        this.currentPage = pageNum;
        this.load();
    },
  },
  created(){
      this.load();
  },
  mounted(){
      document.title="博客测试";
  }
}
</script>
<style>
</style>