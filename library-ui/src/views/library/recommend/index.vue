<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="当前推荐类型" prop="recommendType" label-width="100px">
        <el-input v-model="currentRecommendTypeText" disabled style="width: 120px;" />
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

      <el-col :span="1.5" style="margin-left: auto;">
        <el-button
          type="default"
          plain
          icon="el-icon-info"
          size="mini"
          @click="openRuleDialog"
        >推荐规则</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="bookList">
      <el-table-column label="ISBN号" align="center" prop="isbn" />
      <el-table-column label="图书名称" align="center" prop="bookName" :show-overflow-tooltip="true" />
      <el-table-column label="作者姓名" align="center" prop="authorName" />
      <el-table-column label="出版社名称" align="center" prop="publisherName" :show-overflow-tooltip="true" />
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
      <!-- 热门推荐时显示借阅次数（所有角色） -->
      <el-table-column v-if="isHotRecommend" label="借阅次数" align="center" prop="borrowCount" sortable />
      <!-- 非热门推荐且非读者角色显示借阅次数 -->
      <el-table-column v-if="!isHotRecommend && !isReader" label="借阅次数" align="center" prop="borrowCount" sortable />
      <!-- 非读者角色显示可借数量 -->
      <el-table-column v-if="!isReader" label="可借数量" align="center" prop="availableQuantity">
        <template slot-scope="scope">
          <el-tag :type="scope.row.availableQuantity > 0 ? 'success' : 'danger'" size="small">
            {{ scope.row.availableQuantity }}
          </el-tag>
        </template>
      </el-table-column>
      <!-- 读者角色显示可借状态（包括热门推荐时） -->
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
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >详情</el-button>
          <el-button
            v-if="scope.row.availableQuantity > 0 && !scope.row.isBorrowedByCurrentUser"
            size="mini"
            type="text"
            icon="el-icon-reading"
            @click="handleBorrow(scope.row)"
            v-hasPermi="['library:book:borrow']"
          >借阅</el-button>
          <el-button
            v-if="scope.row.isBorrowedByCurrentUser"
            size="mini"
            type="text"
            icon="el-icon-check"
            disabled
          >已借阅</el-button>
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
        <el-form-item label="借阅状态" v-if="form.isBorrowedByCurrentUser !== undefined">
          <el-tag :type="form.isBorrowedByCurrentUser ? 'info' : 'success'">
            {{ form.isBorrowedByCurrentUser ? '已借阅' : '未借阅' }}
          </el-tag>
        </el-form-item>
        <el-form-item label="图书封面" v-if="form.image">
          <el-image :src="form.image" style="width: 200px; height: 280px;" fit="cover" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitBorrow" v-if="form.availableQuantity > 0 && !form.isBorrowedByCurrentUser" v-hasPermi="['library:book:borrow']">立即借阅</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 推荐规则对话框 -->
    <el-dialog title="推荐规则说明" :visible.sync="ruleDialogVisible" width="600px" append-to-body>
      <div class="rule-content">
        <h4>1. 随机推荐</h4>
        <p>从所有上架图书中随机选择，适合发现新图书。</p>
        
        <h4>2. 热门推荐</h4>
        <p>基于图书的借阅次数排序，推荐最受欢迎的图书。</p>
        
        <h4>3. 关键词推荐</h4>
        <p>根据您的搜索历史，推荐与您常搜索的关键词相关的图书。</p>

      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="ruleDialogVisible = false">我知道了</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { randomRecommend, hotRecommend, keywordRecommend } from "@/api/library/recommend"
import { borrowBook } from "@/api/library/book"

export default {
  name: "Recommend",
  dicts: ['library_category'],
  computed: {
    // 判断当前用户是否是读者角色
    isReader() {
      const roles = this.$store.state.user.roles;
      return roles && roles.includes('reader');
    },
    // 判断是否是热门推荐
    isHotRecommend() {
      return this.queryParams.recommendType === 'hot';
    },
    // 当前推荐类型文本
    currentRecommendTypeText() {
      const typeMap = {
        'random': '随机推荐',
        'hot': '热门推荐',
        'keyword': '关键词推荐'
      };
      return typeMap[this.queryParams.recommendType] || '随机推荐';
    }
  },
  data() {
    return {
      loading: false,
      showSearch: true,
      bookList: [],
      total: 0,
      title: "",
      open: false,
      ruleDialogVisible: false,
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
    },
    openRuleDialog() {
      this.ruleDialogVisible = true
    }
  }
};
</script>
