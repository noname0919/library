<template>
  <div class="app-container">
    <!-- 图书分类借阅统计 -->
    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :lg="12">
        <el-card class="chart-card">
          <div slot="header" class="flex items-center justify-between">
            <span>图书分类借阅统计</span>
            <el-radio-group v-model="categoryDays" size="small" @change="getCategoryBorrowStats">
              <el-radio-button label="0">全部</el-radio-button>
              <el-radio-button label="7">7天</el-radio-button>
              <el-radio-button label="30">30天</el-radio-button>
              <el-radio-button label="90">90天</el-radio-button>
            </el-radio-group>
          </div>
          <div ref="categoryChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="12">
        <el-card class="chart-card">
          <div slot="header" class="flex items-center justify-between">
            <span>TOP10 热门图书</span>
            <el-radio-group v-model="topBooksDays" size="small" @change="getTopBorrowedBooks">
              <el-radio-button label="0">全部</el-radio-button>
              <el-radio-button label="7">7天</el-radio-button>
              <el-radio-button label="30">30天</el-radio-button>
              <el-radio-button label="90">90天</el-radio-button>
            </el-radio-group>
          </div>
          <div ref="topBooksChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getCategoryBorrowStats, getTopBorrowedBooks } from "@/api/library/dashboard"
import * as echarts from 'echarts'

export default {
  name: "DashboardRanking",
  data() {
    return {
      categoryChart: null,
      categoryData: [],
      categoryDays: 0,
      topBooksChart: null,
      topBooksData: [],
      topBooksDays: 0
    }
  },
  created() {
    this.getCategoryBorrowStats()
    this.getTopBorrowedBooks()
  },
  mounted() {
    this.initCategoryChart()
    this.initTopBooksChart()
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    if (this.categoryChart) {
      this.categoryChart.dispose()
    }
    if (this.topBooksChart) {
      this.topBooksChart.dispose()
    }
  },
  methods: {
    // 获取图书分类借阅统计
    getCategoryBorrowStats() {
      getCategoryBorrowStats(this.categoryDays).then(response => {
        this.categoryData = response.data
        this.updateCategoryChart()
      })
    },
    // 初始化分类图表
    initCategoryChart() {
      this.categoryChart = echarts.init(this.$refs.categoryChart)
      this.updateCategoryChart()
    },
    // 更新分类图表
    updateCategoryChart() {
      if (!this.categoryChart || !this.categoryData || this.categoryData.length === 0) return

      const categories = this.categoryData.map(item => item.categoryName)
      const counts = this.categoryData.map(item => item.borrowCount)

      const option = {
        tooltip: {
          trigger: 'axis',
          formatter: '{b}: {c}次'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: categories,
          axisLabel: {
            fontSize: 11,
            rotate: 30
          }
        },
        yAxis: {
          type: 'value',
          minInterval: 1
        },
        series: [
          {
            name: '借阅次数',
            type: 'bar',
            data: counts,
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#67C23A' },
                { offset: 1, color: '#85CE61' }
              ])
            }
          }
        ]
      }

      this.categoryChart.setOption(option)
    },
    // 获取TOP10热门图书
    getTopBorrowedBooks() {
      getTopBorrowedBooks(this.topBooksDays).then(response => {
        this.topBooksData = response.data
        this.updateTopBooksChart()
      })
    },
    // 初始化热门图书图表
    initTopBooksChart() {
      this.topBooksChart = echarts.init(this.$refs.topBooksChart)
      this.updateTopBooksChart()
    },
    // 更新热门图书图表
    updateTopBooksChart() {
      if (!this.topBooksChart || !this.topBooksData || this.topBooksData.length === 0) return

      const books = this.topBooksData.map(item => item.bookName)
      const counts = this.topBooksData.map(item => item.borrowCount)

      const option = {
        tooltip: {
          trigger: 'axis',
          formatter: '{b}: {c}次'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: books,
          axisLabel: {
            fontSize: 10,
            rotate: 45
          }
        },
        yAxis: {
          type: 'value',
          minInterval: 1
        },
        series: [
          {
            name: '借阅次数',
            type: 'bar',
            data: counts,
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#E6A23C' },
                { offset: 1, color: '#F0C78A' }
              ])
            }
          }
        ]
      }

      this.topBooksChart.setOption(option)
    },
    handleResize() {
      if (this.categoryChart) {
        this.categoryChart.resize()
      }
      if (this.topBooksChart) {
        this.topBooksChart.resize()
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.chart-card {
  margin-bottom: 20px;

  .chart-container {
    width: 100%;
    height: 400px;
  }
}
</style>
