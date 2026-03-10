<template>
  <div class="app-container">
    <!-- 核心数据统计卡片 -->
    <el-row :gutter="20" class="panel-group">
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel">
          <div class="card-panel-icon-wrapper icon-book">
            <i class="el-icon-reading card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">图书总量</div>
            <count-to :start-val="0" :end-val="statistics.totalBooks || 0" :duration="2600" class="card-panel-num" />
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :lg="12">
        <el-card class="chart-card">
          <div slot="header">
            <span>图书分类占比</span>
          </div>
          <div ref="categoryChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import CountTo from 'vue-count-to'
import { getDashboardStatistics } from "@/api/library/dashboard"
import * as echarts from 'echarts'

export default {
  name: "Dashboard",
  components: {
    CountTo
  },
  data() {
    return {
      statistics: {
        totalBooks: 0,
        categoryStats: []
      },
      categoryChart: null
    }
  },
  created() {
    this.getStatistics()
  },
  mounted() {
    this.initCategoryChart()
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    if (this.categoryChart) {
      this.categoryChart.dispose()
    }
  },
  methods: {
    getStatistics() {
      getDashboardStatistics().then(response => {
        this.statistics = response.data
        this.updateCategoryChart()
      })
    },
    initCategoryChart() {
      this.categoryChart = echarts.init(this.$refs.categoryChart)
      this.updateCategoryChart()
    },
    updateCategoryChart() {
      if (!this.categoryChart || !this.statistics.categoryStats) return

      const data = this.statistics.categoryStats.map(item => ({
        name: item.name || '未分类',
        value: item.value
      }))

      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c}本 ({d}%)'
        },
        legend: {
          type: 'scroll',
          orient: 'horizontal',
          bottom: 10,
          left: 'center',
          textStyle: {
            fontSize: 11
          },
          itemWidth: 12,
          itemHeight: 12
        },
        series: [
          {
            name: '图书分类',
            type: 'pie',
            radius: ['40%', '65%'],
            center: ['50%', '45%'],
            avoidLabelOverlap: true,
            itemStyle: {
              borderRadius: 6,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 13,
                fontWeight: 'bold',
                formatter: '{b}\n{c}本 ({d}%)'
              },
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            },
            labelLine: {
              show: false
            },
            data: data
          }
        ]
      }

      this.categoryChart.setOption(option)
    },
    handleResize() {
      if (this.categoryChart) {
        this.categoryChart.resize()
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.panel-group {
  margin-top: 18px;

  .card-panel-col {
    margin-bottom: 32px;
  }

  .card-panel {
    height: 108px;
    cursor: pointer;
    font-size: 12px;
    position: relative;
    overflow: hidden;
    color: #666;
    background: #fff;
    box-shadow: 4px 4px 40px rgba(0, 0, 0, .05);
    border-color: rgba(0, 0, 0, .05);

    &:hover {
      .card-panel-icon-wrapper {
        color: #fff;
      }

      .icon-book {
        background: #40c9c6;
      }
    }

    .icon-book {
      color: #40c9c6;
    }

    .card-panel-icon-wrapper {
      float: left;
      margin: 14px 0 0 14px;
      padding: 16px;
      transition: all 0.38s ease-out;
      border-radius: 6px;
    }

    .card-panel-icon {
      float: left;
      font-size: 48px;
    }

    .card-panel-description {
      float: right;
      font-weight: bold;
      margin: 26px;
      margin-left: 0px;

      .card-panel-text {
        line-height: 18px;
        color: rgba(0, 0, 0, 0.45);
        font-size: 16px;
        margin-bottom: 12px;
      }

      .card-panel-num {
        font-size: 20px;
      }
    }
  }
}

.chart-card {
  margin-bottom: 20px;

  .chart-container {
    width: 100%;
    height: 350px;
  }
}

@media (max-width:550px) {
  .card-panel-description {
    display: none;
  }

  .card-panel-icon-wrapper {
    float: none !important;
    width: 100%;
    height: 100%;
    margin: 0 !important;

    .svg-icon {
      display: block;
      margin: 14px auto !important;
      float: none !important;
    }
  }
}
</style>
