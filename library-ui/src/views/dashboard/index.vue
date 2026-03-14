<template>
  <div class="app-container">
    <!-- 核心数据统计卡片 -->
    <el-row :gutter="20" class="panel-group">
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel" @click="handleClick('book')">
          <div class="card-panel-icon-wrapper icon-book">
            <i class="el-icon-reading card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">图书总量</div>
            <count-to :start-val="0" :end-val="statistics.totalBooks || 0" :duration="2600" class="card-panel-num" />
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel" @click="handleClick('user')">
          <div class="card-panel-icon-wrapper icon-user">
            <i class="el-icon-user card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">用户总数</div>
            <count-to :start-val="0" :end-val="statistics.totalUsers || 0" :duration="2600" class="card-panel-num" />
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel" @click="handleClick('todayBorrow')">
          <div class="card-panel-icon-wrapper icon-borrow">
            <i class="el-icon-s-order card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">今日借阅</div>
            <count-to :start-val="0" :end-val="statistics.todayBorrow || 0" :duration="2600" class="card-panel-num" />
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel" @click="handleClick('todayReturn')">
          <div class="card-panel-icon-wrapper icon-return">
            <i class="el-icon-refresh-left card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">今日归还</div>
            <count-to :start-val="0" :end-val="statistics.todayReturn || 0" :duration="2600" class="card-panel-num" />
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel" @click="handleClick('overdue')">
          <div class="card-panel-icon-wrapper icon-overdue">
            <i class="el-icon-warning card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">逾期未还</div>
            <count-to :start-val="0" :end-val="statistics.overdueCount || 0" :duration="2600" class="card-panel-num" />
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :lg="12">
        <el-card class="chart-card">
          <div slot="header" class="flex items-center justify-between">
            <span>图书分类占比</span>
            <el-button size="small" type="primary" icon="el-icon-download" @click="handleExport('categoryStats')">导出</el-button>
          </div>
          <div ref="categoryChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 今日借阅记录弹窗 -->
    <el-dialog title="今日借阅记录" :visible.sync="todayBorrowDialogVisible" width="900px">
      <el-table :data="todayBorrowRecords" v-loading="dialogLoading">
        <el-table-column label="图书名称" prop="bookName" />
        <el-table-column label="ISBN" prop="isbn" />
        <el-table-column label="读者账号" prop="userName" />
        <el-table-column label="读者姓名" prop="nickName" />
        <el-table-column label="借阅时间" prop="borrowTime" width="160" />
        <el-table-column label="应还日期" prop="dueDate" width="100">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.dueDate, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="80">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === '0'" type="success">借阅中</el-tag>
            <el-tag v-else-if="scope.row.status === '1'" type="info">已归还</el-tag>
            <el-tag v-else-if="scope.row.status === '2'" type="danger">已逾期</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" icon="el-icon-download" @click="handleExport('todayBorrow')">导出</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 今日归还记录弹窗 -->
    <el-dialog title="今日归还记录" :visible.sync="todayReturnDialogVisible" width="900px">
      <el-table :data="todayReturnRecords" v-loading="dialogLoading">
        <el-table-column label="图书名称" prop="bookName" />
        <el-table-column label="ISBN" prop="isbn" />
        <el-table-column label="读者账号" prop="userName" />
        <el-table-column label="读者姓名" prop="nickName" />
        <el-table-column label="借阅时间" prop="borrowTime" width="160" />
        <el-table-column label="归还时间" prop="returnTime" width="160" />
        <el-table-column label="操作" width="80">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" icon="el-icon-download" @click="handleExport('todayReturn')">导出</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 逾期未还记录弹窗 -->
    <el-dialog title="逾期未还记录" :visible.sync="overdueDialogVisible" width="900px">
      <el-table :data="overdueRecords" v-loading="dialogLoading">
        <el-table-column label="图书名称" prop="bookName" />
        <el-table-column label="ISBN" prop="isbn" />
        <el-table-column label="读者账号" prop="userName" />
        <el-table-column label="读者姓名" prop="nickName" />
        <el-table-column label="借阅时间" prop="borrowTime" width="160" />
        <el-table-column label="应还日期" prop="dueDate" width="100">
          <template slot-scope="scope">
            <span style="color: #f56c6c; font-weight: bold;">{{ parseTime(scope.row.dueDate, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template slot-scope="scope">
            <el-tag type="danger">已逾期</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" icon="el-icon-download" @click="handleExport('overdue')">导出</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import CountTo from 'vue-count-to'
import { getDashboardStatistics, exportTodayBorrow, exportTodayReturn, exportOverdue, exportCategoryStats } from "@/api/library/dashboard"
import { listAllBorrow, listReturn, listBorrow } from "@/api/library/borrow"
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
        totalUsers: 0,
        todayBorrow: 0,
        todayReturn: 0,
        overdueCount: 0,
        categoryStats: []
      },
      isAdmin: false,
      categoryChart: null,
      // 弹窗相关
      todayBorrowDialogVisible: false,
      todayReturnDialogVisible: false,
      overdueDialogVisible: false,
      dialogLoading: false,
      todayBorrowRecords: [],
      todayReturnRecords: [],
      overdueRecords: []
    }
  },
  created() {
    this.isAdmin = this.checkIsAdmin()
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
    // 检查是否是超级管理员
    checkIsAdmin() {
      const roles = this.$store.getters.roles
      return roles && roles.includes('admin')
    },
    // 点击卡片
    handleClick(type) {
      switch (type) {
        case 'book':
          this.$router.push('/library/book')
          break
        case 'user':
          if (this.isAdmin) {
            this.$router.push('/system/user')
          } else {
            this.$modal.msgWarning('只有超级管理员才能查看用户管理')
          }
          break
        case 'todayBorrow':
          this.showTodayBorrowRecords()
          break
        case 'todayReturn':
          this.showTodayReturnRecords()
          break
        case 'overdue':
          this.showOverdueRecords()
          break
      }
    },
    // 显示今日借阅记录（包括已归还的）
    showTodayBorrowRecords() {
      this.todayBorrowDialogVisible = true
      this.dialogLoading = true
      const today = this.parseTime(new Date(), '{y}-{m}-{d}')
      listAllBorrow({
        params: { beginTime: today, endTime: today }
      }).then(response => {
        this.todayBorrowRecords = response.rows
        this.dialogLoading = false
      }).catch(() => {
        this.dialogLoading = false
      })
    },
    // 显示今日归还记录
    showTodayReturnRecords() {
      this.todayReturnDialogVisible = true
      this.dialogLoading = true
      const today = this.parseTime(new Date(), '{y}-{m}-{d}')
      listReturn({
        params: { beginTime: today, endTime: today }
      }).then(response => {
        this.todayReturnRecords = response.rows
        this.dialogLoading = false
      }).catch(() => {
        this.dialogLoading = false
      })
    },
    // 显示逾期未还记录（查询已逾期的，后端已实时计算status）
    showOverdueRecords() {
      this.overdueDialogVisible = true
      this.dialogLoading = true
      // 查询所有借阅中的记录，后端已实时计算status，直接过滤status='2'的即可
      listBorrow({}).then(response => {
        this.overdueRecords = response.rows.filter(record => {
          // 后端已实时计算status，逾期记录status='2'
          return record.status === '2'
        })
        this.dialogLoading = false
      }).catch(() => {
        this.dialogLoading = false
      })
    },
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
    },
    // 处理导出
    handleExport(type) {
      let exportFunc, fileName
      switch (type) {
        case 'todayBorrow':
          exportFunc = exportTodayBorrow
          fileName = `今日借阅记录_${this.parseTime(new Date(), '{y}{m}{d}')}.xlsx`
          break
        case 'todayReturn':
          exportFunc = exportTodayReturn
          fileName = `今日归还记录_${this.parseTime(new Date(), '{y}{m}{d}')}.xlsx`
          break
        case 'overdue':
          exportFunc = exportOverdue
          fileName = `逾期未还记录_${this.parseTime(new Date(), '{y}{m}{d}')}.xlsx`
          break
        case 'categoryStats':
          exportFunc = exportCategoryStats
          fileName = `图书分类占比_${this.parseTime(new Date(), '{y}{m}{d}')}.xlsx`
          break
        default:
          return
      }

      this.$modal.loading('导出中，请稍候...')
      exportFunc().then(response => {
        this.$modal.closeLoading()
        this.downloadFile(response, fileName)
      }).catch(() => {
        this.$modal.closeLoading()
        this.$message.error('导出失败，请重试')
      })
    },
    // 下载文件
    downloadFile(response, fileName) {
      const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = fileName
      link.click()
      window.URL.revokeObjectURL(url)
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

      .icon-user {
        background: #36a2eb;
      }

      .icon-borrow {
        background: #ff6384;
      }

      .icon-return {
        background: #4bc0c0;
      }

      .icon-overdue {
        background: #ff9f40;
      }
    }

    .icon-book {
      color: #40c9c6;
    }

    .icon-user {
      color: #36a2eb;
    }

    .icon-borrow {
      color: #ff6384;
    }

    .icon-return {
      color: #4bc0c0;
    }

    .icon-overdue {
      color: #ff9f40;
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

    .card-panel-export {
      position: absolute;
      top: 10px;
      right: 10px;
      z-index: 10;
    }
  }
}

.flex {
  display: flex;
}

.items-center {
  align-items: center;
}

.justify-between {
  justify-content: space-between;
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
