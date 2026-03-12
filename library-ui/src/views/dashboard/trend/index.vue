<template>
  <div class="app-container">
    <!-- 借阅趋势图表 -->
    <el-card class="chart-card">
      <div slot="header" class="flex items-center justify-between">
        <span>借阅趋势</span>
        <el-radio-group v-model="borrowTrendDays" size="small" @change="getBorrowTrend">
          <el-radio-button label="7">7天</el-radio-button>
          <el-radio-button label="30">30天</el-radio-button>
          <el-radio-button label="90">90天</el-radio-button>
        </el-radio-group>
      </div>
      <div ref="borrowTrendChart" class="chart-container"></div>
    </el-card>
  </div>
</template>

<script>
import { getBorrowTrend } from "@/api/library/dashboard"
import * as echarts from 'echarts'

export default {
  name: "DashboardTrend",
  data() {
    return {
      borrowTrendChart: null,
      borrowTrendDays: 7,
      borrowTrendData: []
    }
  },
  created() {
    this.getBorrowTrend()
  },
  mounted() {
    this.initBorrowTrendChart()
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    if (this.borrowTrendChart) {
      this.borrowTrendChart.dispose()
    }
  },
  methods: {
    // 获取借阅趋势数据
    getBorrowTrend() {
      getBorrowTrend(this.borrowTrendDays).then(response => {
        this.borrowTrendData = response.data
        this.updateBorrowTrendChart()
      })
    },
    // 初始化借阅趋势图表
    initBorrowTrendChart() {
      this.borrowTrendChart = echarts.init(this.$refs.borrowTrendChart)
      this.updateBorrowTrendChart()
    },
    // 更新借阅趋势图表
    updateBorrowTrendChart() {
      if (!this.borrowTrendChart || !this.borrowTrendData || this.borrowTrendData.length === 0) return

      const dates = this.borrowTrendData.map(item => item.date)
      const counts = this.borrowTrendData.map(item => item.count)

      const option = {
        tooltip: {
          trigger: 'axis',
          formatter: '{b}: {c}本'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: dates,
          axisLabel: {
            fontSize: 11,
            rotate: 45
          }
        },
        yAxis: {
          type: 'value',
          minInterval: 1
        },
        series: [
          {
            name: '借阅量',
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 6,
            lineStyle: {
              width: 3,
              color: '#409EFF'
            },
            itemStyle: {
              color: '#409EFF'
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                {
                  offset: 0,
                  color: 'rgba(64, 158, 255, 0.3)'
                },
                {
                  offset: 1,
                  color: 'rgba(64, 158, 255, 0.1)'
                }
              ])
            },
            data: counts
          }
        ]
      }

      this.borrowTrendChart.setOption(option)
    },
    handleResize() {
      if (this.borrowTrendChart) {
        this.borrowTrendChart.resize()
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
