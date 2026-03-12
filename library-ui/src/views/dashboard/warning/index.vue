<template>
  <div class="app-container">
    <!-- 库存不足预警 -->
    <el-card class="chart-card">
      <div slot="header">
        <span>库存不足预警（可借数量 &lt; 5）</span>
      </div>
      <el-table :data="warningList" style="width: 100%" stripe>
        <el-table-column prop="bookName" label="图书名称" min-width="180" />
        <el-table-column prop="isbn" label="ISBN" min-width="130" />
        <el-table-column prop="authorName" label="作者" min-width="120" />
        <el-table-column prop="totalQuantity" label="总馆藏" min-width="80" />
        <el-table-column prop="availableQuantity" label="可借数量" min-width="80" />
        <el-table-column prop="borrowedQuantity" label="已借数量" min-width="80" />
        <el-table-column prop="status" label="状态" min-width="120">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === '0'" type="success">上架</el-tag>
            <el-tag v-else type="danger">下架</el-tag>
            <el-switch
              v-model="scope.row.status"
              active-value="0"
              inactive-value="1"
              @change="handleStatusChange(scope.row)"
              style="margin-left: 10px;"
            />
          </template>
        </el-table-column>
      </el-table>
      <div v-if="warningList.length === 0" class="empty-message">
        <el-empty description="暂无库存不足的图书" />
      </div>
    </el-card>
  </div>
</template>

<script>
import { getStockWarning } from "@/api/library/dashboard"
import { updateBook } from "@/api/library/book"

export default {
  name: "DashboardWarning",
  data() {
    return {
      warningList: []
    }
  },
  created() {
    this.getStockWarning()
  },
  methods: {
    // 获取库存不足预警
    getStockWarning() {
      getStockWarning().then(response => {
        this.warningList = response.data
      })
    },
    // 处理状态切换
    handleStatusChange(row) {
      const statusText = row.status === '0' ? '上架' : '下架'
      updateBook(row).then(() => {
        this.$message.success(`图书 ${row.bookName} 已${statusText}`)
      }).catch(() => {
        this.$message.error('状态切换失败，请重试')
        // 恢复原状态
        row.status = row.status === '0' ? '1' : '0'
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.chart-card {
  margin-bottom: 20px;

  .empty-message {
    margin: 40px 0;
  }
}
</style>
