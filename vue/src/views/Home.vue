<template>
  <div style="padding: 10px;background-color: #FFFACD;">
    <div>
<!--      一些操作按钮-->
      <el-button type="primary"
      @click="add">
        新增数据
      </el-button>
      <el-button type="primary" disabled>
        导入数据
      </el-button>
      <el-button type="primary" disabled>
        导出数据
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
            stripe="true"
            border="true"
            v-loading="loading"
    >
      <el-table-column prop="id" label="ID" sortable> </el-table-column>
      <el-table-column prop="username" label="用户名" > </el-table-column>
      <el-table-column prop="nickname" label="昵称" > </el-table-column>
      <el-table-column prop="password" label="密码" > </el-table-column>
      <el-table-column prop="sex" label="性别" > </el-table-column>
      <el-table-column prop="age" label="年龄" > </el-table-column>
      <el-table-column prop="address" label="地址"> </el-table-column>
      <el-table-column fixed="right" label="操作" width="250px">
        <template #default="scope">
            <el-button @click="$router.push('/test?id='+scope.row.id)" type="primary" size="small">跳转查看</el-button>
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
          <el-form-item label="用户名">
            <el-input v-model="form.username" style="width: 80%"></el-input>
          </el-form-item>
          <el-form-item label="昵称">
            <el-input v-model="form.nickname" style="width: 80%"></el-input>
          </el-form-item>
          <el-form-item label="性别">
            <el-radio v-model="form.sex" label="男">男</el-radio>
            <el-radio v-model="form.sex" label="女">女</el-radio>
            <el-radio v-model="form.sex" label="未知">未知</el-radio>
          </el-form-item>
          <el-form-item label="年龄">
            <el-input v-model="form.age" style="width: 25%"></el-input>
          </el-form-item>
          <el-form-item label="地址">
            <el-input type="textarea" v-model="form.address" style="width: 80%"></el-input>
          </el-form-item>
        </el-form>

        <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">确认</el-button>
      </span>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script>
// @ is an alias to /src
import request from "../../utils/request";

export default {
  name: 'Home',
    data(){
        return{
            form:{},
            dialogVisible:false,
            search: '',
            currentPage: 1,
            pageSize:10,
            total: 10,
            tableData:[],
            loading:true
        }
    },
  methods:{
    load(){
      this.loading = true;
      request.get("/user", {
          params:{
              pageNum:this.currentPage,
              pageSize:this.pageSize,
              search:this.search
          }
      }).then(res=>{
          this.tableData = res.data.records;
          this.total = res.data.total;
          // this.currentPage = res.data.page;

          this.$nextTick(()=>{
              this.loading = false;
          });
      })
    },
    add(){
      this.form = {};
      this.dialogVisible = true;
    },
    save(){
        if (this.form.id){
            request.put("/user", this.form).then(res=>{
                if (res.code === '0'){
                    this.$message({
                        type:"success",
                        message:"更新成功"
                    });
                    this.dialogVisible = false;
                    this.load();
                }else{
                    this.$message({
                        type:"error",
                        message:"更新失败"
                    });
                }
            });
        }else {
            request.post("/user", this.form).then(res=>{
                if (res.code === '0'){
                    this.$message({
                        type:"success",
                        message:"新增成功"
                    });
                    this.dialogVisible = false;
                    this.load();
                }else{
                    this.$message({
                        type:"error",
                        message:"新增失败"
                    });
                }
            });
        }
    },
    handleEdit(row){
        // 深拷贝
        this.form = JSON.parse(JSON.stringify(row));
        this.dialogVisible = true;
    },
    handleDelete(id){
        request.delete("/user/"+id).then(res=>{
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
      document.title="用户管理";
  }
}
</script>
<style>
</style>