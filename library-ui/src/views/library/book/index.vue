<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="ISBN号" prop="isbn">
        <el-input
          v-model="queryParams.isbn"
          placeholder="请输入ISBN号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="图书名称" prop="bookName">
        <el-input
          v-model="queryParams.bookName"
          placeholder="请输入图书名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="分类" prop="categoryId">
        <el-select v-model="queryParams.categoryId" placeholder="请选择分类" clearable>
          <el-option
            v-for="dict in dict.type.library_category"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['library:book:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['library:book:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['library:book:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['library:book:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="bookList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" v-if="!isReader" />
      <el-table-column label="图书ID" align="center" prop="id" v-if="!isReader" />
      <el-table-column label="ISBN号" align="center" prop="isbn" />
      <el-table-column label="图书名称" align="center" prop="bookName" />
      <el-table-column label="作者姓名" align="center" prop="authorName" />
      <el-table-column label="出版社名称" align="center" prop="publisherName" />
      <el-table-column label="分类" align="center" prop="categoryId">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.library_category" :value="scope.row.categoryId"/>
        </template>
      </el-table-column>
      <el-table-column label="出版日期" align="center" prop="publishDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.publishDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="图书价格" align="center">
        <template slot-scope="scope">
          <span>¥{{ scope.row.price }}</span>
        </template>
      </el-table-column>
      <el-table-column label="封面图片" align="center" prop="image" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.image" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <!-- 只有非读者角色才显示馆藏数量相关信息 -->
      <el-table-column v-if="!isReader" label="总馆藏数量" align="center" prop="totalQuantity" />
      <el-table-column v-if="!isReader" label="可借数量" align="center" prop="availableQuantity" />
      <el-table-column v-if="!isReader" label="已借数量" align="center" prop="borrowedQuantity" />
      <el-table-column v-if="!isReader" label="图书状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.book_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <!-- 读者角色只显示可借状态 -->
      <el-table-column v-if="isReader" label="可借状态" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.availableQuantity > 0 ? 'success' : 'danger'">
            {{ scope.row.availableQuantity > 0 ? '可借' : '不可借' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['library:book:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['library:book:remove']"
          >删除</el-button>
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

    <!-- 添加或修改图书基本信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="ISBN号" prop="isbn">
          <el-input v-model="form.isbn" placeholder="请输入ISBN号" />
        </el-form-item>
        <el-form-item label="图书名称" prop="bookName">
          <el-input v-model="form.bookName" placeholder="请输入图书名称" />
        </el-form-item>
        <el-form-item label="作者姓名" prop="authorName">
          <el-input v-model="form.authorName" placeholder="请输入作者姓名" />
        </el-form-item>
        <el-form-item label="出版社" prop="publisherName" >
          <el-input v-model="form.publisherName" placeholder="请输入出版社名称" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类">
            <el-option
              v-for="dict in dict.type.library_category"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="出版日期" prop="publishDate">
          <el-date-picker clearable
            v-model="form.publishDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择出版日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="图书价格" prop="price">
          <el-input v-model="form.price" placeholder="请输入图书价格" />
        </el-form-item>
        <el-form-item label="封面图片" prop="image">
          <image-upload v-model="form.image"/>
        </el-form-item>
        <el-form-item label="馆藏数量" prop="totalQuantity">
          <el-input v-model="form.totalQuantity" placeholder="请输入总馆藏数量" />
        </el-form-item>
        <el-form-item v-if="form.id != null" label="可借数量" prop="availableQuantity">
          <el-input v-model="form.availableQuantity" placeholder="请输入可借数量" />
        </el-form-item>
        <el-form-item v-if="form.id != null" label="已借数量" prop="borrowedQuantity">
          <el-input v-model="form.borrowedQuantity"
                    :disabled="true"
                    placeholder="请输入已借数量" />
        </el-form-item>
        <el-form-item v-if="form.id != null" label="图书状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in dict.type.book_status"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listBook, getBook, delBook, addBook, updateBook } from "@/api/library/book"
import { checkRole } from "@/utils/permission"
import store from "@/store"

export default {
  name: "Book",
  dicts: ['library_category', 'book_status'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 图书基本信息表格数据
      bookList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否是读者角色
      isReader: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        isbn: null,
        bookName: null,
        categoryId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        isbn: [
          { required: true, message: "ISBN号不能为空", trigger: "blur" }
        ],
        bookName: [
          { required: true, message: "图书名称不能为空", trigger: "blur" }
        ],
        authorName: [
          { required: true, message: "作者姓名不能为空", trigger: "blur" }
        ],
        publisherName: [
          { required: true, message: "出版社名称不能为空", trigger: "blur" }
        ],
        categoryId: [
          { required: true, message: "分类不能为空", trigger: "change" }
        ],
        publishDate: [
          { required: true, message: "出版日期不能为空", trigger: "blur" }
        ],
        price: [
          { required: true, message: "图书价格不能为空", trigger: "blur" }
        ],
        image: [
          { required: true, message: "封面图片不能为空", trigger: "blur" }
        ],
        totalQuantity: [
          { required: true, message: "总馆藏数量不能为空", trigger: "blur" }
        ],
        availableQuantity: [
          { required: true, message: "可借数量不能为空", trigger: "blur" }
        ],
        borrowedQuantity: [
          { required: true, message: "已借数量不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "图书状态不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    // 检查当前用户是否是读者角色
    this.isReader = store.getters.roles && store.getters.roles.includes('reader')
    this.getList()
  },
  methods: {
    /** 查询图书基本信息列表 */
    getList() {
      this.loading = true
      listBook(this.queryParams).then(response => {
        this.bookList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        isbn: null,
        bookName: null,
        authorName: null,
        publisherName: null,
        categoryId: null,
        publishDate: null,
        price: null,
        image: null,
        totalQuantity: null,
        availableQuantity: null,
        borrowedQuantity: null,
        status: null,
        createTime: null,
        updateTime: null
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加图书基本信息"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getBook(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改图书基本信息"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateBook(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            // 新增时：可借数量与馆藏数量一致，已借数量为0，图书状态默认为下架(1)
            this.form.availableQuantity = this.form.totalQuantity
            this.form.borrowedQuantity = 0
            this.form.status = '1'
            addBook(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除图书基本信息编号为"' + ids + '"的数据项？').then(function() {
        return delBook(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('library/book/export', {
        ...this.queryParams
      }, `book_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
