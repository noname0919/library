<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="推荐类型" prop="recommendType">
        <el-select v-model="queryParams.recommendType" placeholder="请选择推荐类型" clearable @change="handleRecommendChange">
          <el-option label="随机推荐" value="random" />
          <el-option label="热门推荐" value="hot" />
          <el-option label="关键词推荐" value="keyword" />
          <el-option label="综合推荐" value="all" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-refresh" size="mini" @click="handleQuery">刷新推荐</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-refresh-right"
          size="mini"
          @click="getRandomRecommend"
          v-hasPermi="['library:recommend:random']"
        >随机推荐</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-s-data"
          size="mini"
          @click="getHotRecommend"
          v-hasPermi="['library:recommend:hot']"
        >热门推荐</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-search"
          size="mini"
          @click="getKeywordRecommend"
          v-hasPermi="['library:recommend:keyword']"
        >关键词推荐</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-menu"
          size="mini"
          @click="getMixedRecommend"
          v-hasPermi="['library:recommend:all']"
        >综合推荐</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="bookList">
      <el-table-column label="图书ID" align="center" prop="id" />
      <el-table-column label="ISBN号" align="center" prop="isbn" />
      <el-table-column label="图书名称" align="center" prop="bookName" :show-overflow-tooltip="true" />
      <el-table-column label="作者" align="center" prop="authorName" />
      <el-table-column label="出版社" align="center" prop="publisherName" :show-overflow-tooltip="true" />
      <el-table-column label="分类" align="center" prop="categoryId">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.library_category" :value="scope.row.categoryId"/>
        </template>
      </el-table-column>
      <el-table-column label="价格" align="center" prop="price">
        <template slot-scope="scope">
          <span>¥{{ scope.row.price }}</span>
        </template>
      </el-table-column>
      <el-table-column label="借阅次数" align="center" prop="borrowCount" sortable />
      <el-table-column label="可借数量" align="center" prop="availableQuantity">
        <template slot-scope="scope">
          <el-tag :type="scope.row.availableQuantity > 0 ? 'success' : 'danger'" size="small">
            {{ scope.row.availableQuantity }}
          </el-tag>
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
            v-if="scope.row.availableQuantity > 0"
            size="mini"
            type="text"
            icon="el-icon-reading"
            @click="handleBorrow(scope.row)"
            v-hasPermi="['library:book:borrow']"
          >借阅</el-button>
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

    <!-- 图书详情对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" label-width="80px">
        <el-form-item label="图书名称">
          <span>{{ form.bookName }}</span>
        </el-form-item>
        <el-form-item label="ISBN号">
          <span>{{ form.isbn }}</span>
        </el-form-item>
        <el-form-item label="作者">
          <span>{{ form.authorName }}</span>
        </el-form-item>
        <el-form-item label="出版社">
          <span>{{ form.publisherName }}</span>
        </el-form-item>
        <el-form-item label="分类">
          <dict-tag :options="dict.type.library_category" :value="form.categoryId"/>
        </el-form-item>
        <el-form-item label="价格">
          <span>¥{{ form.price }}</span>
        </el-form-item>
        <el-form-item label="借阅次数">
          <span>{{ form.borrowCount || 0 }}</span>
        </el-form-item>
        <el-form-item label="可借数量">
          <el-tag :type="form.availableQuantity > 0 ? 'success' : 'danger'">
            {{ form.availableQuantity }}
          </el-tag>
        </el-form-item>
        <el-form-item label="图书封面" v-if="form.image">
          <el-image :src="form.image" style="width: 200px; height: 280px;" fit="cover" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitBorrow" v-if="form.availableQuantity > 0" v-hasPermi="['library:book:borrow']">立即借阅</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { randomRecommend, hotRecommend, keywordRecommend, mixedRecommend } from "@/api/library/recommend"
import { borrowBook } from "@/api/library/book"

export default {
  name: "Recommend",
  dicts: ['library_category'],
  data() {
    return {
      loading: false,
      showSearch: true,
      bookList: [],
      total: 0,
      title: "",
      open: false,
      form: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        recommendType: 'random'
      }
    }
  },
  created() {
    this.getRandomRecommend()
  },
  methods: {
    getList() {
      // 根据当前推荐类型获取数据
      switch(this.queryParams.recommendType) {
        case 'random':
          this.getRandomRecommend()
          break
        case 'hot':
          this.getHotRecommend()
          break
        case 'keyword':
          this.getKeywordRecommend()
          break
        case 'all':
          this.getMixedRecommend()
          break
        default:
          this.getRandomRecommend()
      }
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    handleRecommendChange(val) {
      this.queryParams.recommendType = val
      this.handleQuery()
    },
    getRandomRecommend() {
      this.loading = true
      this.queryParams.recommendType = 'random'
      randomRecommend(this.queryParams.pageSize).then(response => {
        this.bookList = response.data
        this.total = response.data.length
        this.loading = false
      })
    },
    getHotRecommend() {
      this.loading = true
      this.queryParams.recommendType = 'hot'
      hotRecommend(this.queryParams.pageSize).then(response => {
        this.bookList = response.data
        this.total = response.data.length
        this.loading = false
      })
    },
    getKeywordRecommend() {
      this.loading = true
      this.queryParams.recommendType = 'keyword'
      keywordRecommend(this.queryParams.pageSize).then(response => {
        this.bookList = response.data
        this.total = response.data.length
        this.loading = false
      })
    },
    getMixedRecommend() {
      this.loading = true
      this.queryParams.recommendType = 'all'
      mixedRecommend(this.queryParams.pageSize).then(response => {
        this.bookList = response.data
        this.total = response.data.length
        this.loading = false
      })
    },
    handleView(row) {
      this.form = row
      this.title = "图书详情"
      this.open = true
    },
    handleBorrow(row) {
      this.form = row
      this.title = "借阅图书"
      this.open = true
    },
    submitBorrow() {
      borrowBook(this.form.id).then(() => {
        this.$modal.msgSuccess("借阅成功")
        this.open = false
        this.getList()
      })
    },
    cancel() {
      this.open = false
      this.form = {}
    }
  }
}
</script>
