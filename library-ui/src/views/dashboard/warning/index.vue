<template>
  <div class="app-container">
    <!-- 库存不足预警 -->
    <el-card class="chart-card">
      <div slot="header">
        <span>库存不足预警（可借数量 &lt; 5）</span>
      </div>
      <template v-if="warningList && warningList.length > 0">
        <el-table :data="warningList" style="width: 100%" stripe>
          <el-table-column prop="bookName" label="图书名称" min-width="180" />
          <el-table-column prop="isbn" label="ISBN" min-width="130" />
          <el-table-column prop="authorName" label="作者" min-width="120" />
          <el-table-column prop="totalQuantity" label="总馆藏" min-width="100">
            <template slot-scope="scope">
              <span v-if="!scope.row.editing">{{ scope.row.totalQuantity }}</span>
              <el-input-number
                v-else
                v-model="scope.row.totalQuantity"
                :min="scope.row.borrowedQuantity"
                :step="1"
                size="small"
                style="width: 120px;"
              />
            </template>
          </el-table-column>
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
          <el-table-column label="操作" min-width="120">
            <template slot-scope="scope">
              <el-button
                v-if="!scope.row.editing"
                type="primary"
                size="small"
                @click="scope.row.editing = true"
              >
                编辑馆藏
              </el-button>
              <el-button
                v-else
                type="success"
                size="small"
                @click="handleSaveQuantity(scope.row)"
                style="margin-right: 5px;"
              >
                保存
              </el-button>
              <el-button
                v-else
                type="info"
                size="small"
                @click="cancelEdit(scope.row)"
              >
                取消
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </template>
      <div v-else class="empty-message">
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
        this.warningList = response.data.map(book => ({
          ...book,
          editing: false,
          originalTotalQuantity: book.totalQuantity
        }))
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
    },
    // 处理保存馆藏数量
    handleSaveQuantity(row) {
      const oldTotal = row.originalTotalQuantity
      const newTotal = row.totalQuantity
      const quantityChange = newTotal - oldTotal
      
      if (quantityChange === 0) {
        row.editing = false
        return
      }
      
      // 自动更新可借数量
      row.availableQuantity = row.availableQuantity + quantityChange
      
      updateBook(row).then(() => {
        this.$message.success(`图书 ${row.bookName} 馆藏数量已更新，增加了 ${quantityChange} 本`)
        row.editing = false
        row.originalTotalQuantity = newTotal
      }).catch(() => {
        this.$message.error('馆藏数量更新失败，请重试')
        // 恢复原状态
        row.totalQuantity = row.originalTotalQuantity
        row.availableQuantity = row.availableQuantity - quantityChange
      })
    },
    // 取消编辑
    cancelEdit(row) {
      row.totalQuantity = row.originalTotalQuantity
      row.editing = false
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
