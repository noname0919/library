<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="图书名称" prop="bookName">
        <el-input
          v-model="queryParams.bookName"
          placeholder="请输入图书名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="借阅时间">
        <el-date-picker
          v-model="dateRange"
          size="small"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['library:borrow:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="borrowList">
      <el-table-column label="借阅ID" align="center" prop="id" />
      <el-table-column label="图书名称" align="center" prop="bookName" />
      <el-table-column label="ISBN号" align="center" prop="isbn" />
      <el-table-column label="读者账号" align="center" prop="userName" />
      <el-table-column label="读者姓名" align="center" prop="nickName" />
      <el-table-column label="借阅时间" align="center" prop="borrowTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.borrowTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="应还日期" align="center" prop="dueDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.dueDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="续借次数" align="center" prop="renewCount" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <el-tag type="success" v-if="scope.row.status === '0'">借阅中</el-tag>
          <el-tag type="danger" v-else-if="scope.row.status === '2'">已逾期</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >详情</el-button>
          <el-button
            v-if="isReader && scope.row.status === '0' && scope.row.renewCount < 3"
            size="mini"
            type="text"
            icon="el-icon-refresh"
            @click="handleRenew(scope.row)"
            v-hasPermi="['library:borrow:renew']"
          >续借</el-button>
          <el-button
            v-if="isReader"
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handleReturn(scope.row)"
            v-hasPermi="['library:book:return']"
          >归还</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 详情对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" label-width="100px">
        <el-form-item label="借阅ID：">
          <span>{{ form.id }}</span>
        </el-form-item>
        <el-form-item label="图书名称：">
          <span>{{ form.bookName }}</span>
        </el-form-item>
        <el-form-item label="ISBN号：">
          <span>{{ form.isbn }}</span>
        </el-form-item>
        <el-form-item label="读者账号：">
          <span>{{ form.userName }}</span>
        </el-form-item>
        <el-form-item label="读者姓名：">
          <span>{{ form.nickName }}</span>
        </el-form-item>
        <el-form-item label="借阅时间：">
          <span>{{ parseTime(form.borrowTime) }}</span>
        </el-form-item>
        <el-form-item label="应还日期：">
          <span>{{ parseTime(form.dueDate) }}</span>
        </el-form-item>
        <el-form-item label="续借次数：">
          <span>{{ form.renewCount }}</span>
        </el-form-item>
        <el-form-item label="状态：">
          <el-tag type="success" v-if="form.status === '0'">借阅中</el-tag>
          <el-tag type="danger" v-else-if="form.status === '2'">已逾期</el-tag>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listBorrow, getBorrow, renewBorrow } from "@/api/library/borrow";
import { returnBook } from "@/api/library/book";

export default {
  name: "Borrow",
  computed: {
    // 判断当前用户是否是读者角色
    isReader() {
      const roles = this.$store.state.user.roles;
      return roles && roles.includes('reader');
    }
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 借阅记录表格数据
      borrowList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 日期范围
      dateRange: [],
      // 查询参数 - 只查询借阅中(0)和逾期(2)的记录
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        bookName: null,
        userName: null,
        status: null
      },
      // 表单参数
      form: {}
    };
  },
  created() {
    this.getList();
  },
  activated() {
    this.getList();
  },
  methods: {
    /** 查询借阅记录列表 */
    getList() {
      this.loading = true;
      listBorrow(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
        this.borrowList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        bookName: null,
        isbn: null,
        userName: null,
        nickName: null,
        borrowTime: null,
        dueDate: null,
        renewCount: null,
        status: null
      };
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('library/borrow/export', {
        ...this.queryParams
      }, `borrow_${new Date().getTime()}.xlsx`)
    },
    /** 详情按钮操作 */
    handleView(row) {
      this.reset();
      const id = row.id || this.ids
      getBorrow(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "借阅详情";
      });
    },
    /** 续借按钮操作 */
    handleRenew(row) {
      this.$modal.confirm('确认续借图书《' + row.bookName + '》吗？').then(function() {
        return renewBorrow(row.id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("续借成功");
      }).catch(() => {});
    },
    /** 归还按钮操作 */
    handleReturn(row) {
      this.$modal.confirm('确认归还图书《' + row.bookName + '》吗？').then(function() {
        return returnBook(row.id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("归还成功");
      }).catch(() => {});
    }
  }
};
</script>
