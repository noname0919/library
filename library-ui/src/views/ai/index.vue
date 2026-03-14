<template>
  <div class="app-container">
    <el-card class="chat-container">
      <template #header>
        <div class="card-header">
          <span>智能问答</span>
          <el-button type="primary" size="small" @click="clearChat" plain>
            清空对话
          </el-button>
        </div>
      </template>
      
      <div class="chat-content" ref="chatContent">
        <div 
          v-for="(message, index) in messages" 
          :key="index"
          :class="['chat-message', message.role === 'user' ? 'user-message' : 'ai-message']"
        >
          <div class="message-avatar">
            <el-avatar :size="32" :src="message.avatar">{{ message.avatarText }}</el-avatar>
          </div>
          <div class="message-content">
            <div class="message-name">{{ message.name }}</div>
            <div class="message-text" v-html="formatMessage(message.text)"></div>
            <div class="message-time">{{ message.time }}</div>
          </div>
        </div>
        <div v-if="isLoading" class="chat-message ai-message">
          <div class="message-avatar">
            <el-avatar :size="32">AI</el-avatar>
          </div>
          <div class="message-content">
            <div class="message-name">智能助手</div>
            <div class="message-text">
              <i class="el-icon-loading"></i> 正在思考...
            </div>
          </div>
        </div>
      </div>
      
      <div class="chat-input">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="3"
          placeholder="请输入您的问题..."
          @keyup.enter.exact="sendMessage"
        ></el-input>
        <div class="input-actions">
          <el-button type="primary" @click="sendMessage" :loading="isLoading" style="width: 100px; height: 40px; font-size: 14px;">
            发送
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { sendMessage } from '@/api/ai/chat'

export default {
  name: 'SmartQnA',
  data() {
    return {
      messages: [
        {
          role: 'assistant',
          name: '智能助手',
          text: '嗨～欢迎来到图书管理系统！我是你的智能小助手，有什么可以帮你的呀？',
          time: this.getFormattedTime(),
          avatar: '',
          avatarText: 'AI'
        }
      ],
      inputMessage: '',
      isLoading: false
    }
  },
  methods: {
    async sendMessage() {
      if (!this.inputMessage.trim() || this.isLoading) return
      
      const userMessageText = this.inputMessage.trim()
      
      // 添加用户消息
      const userMessage = {
        role: 'user',
        name: '你',
        text: userMessageText,
        time: this.getFormattedTime(),
        avatar: '',
        avatarText: '我'
      }
      this.messages.push(userMessage)
      this.inputMessage = ''
      
      // 滚动到底部
      this.scrollToBottom()
      
      // 调用后端AI接口
      this.isLoading = true
      try {
        // 构建消息历史
        const apiMessages = this.messages
          .filter(msg => msg.role === 'user' || msg.role === 'assistant')
          .map(msg => ({
            role: msg.role,
            content: msg.text
          }))
        
        const response = await sendMessage({ messages: apiMessages })
        
        if (response.code === 200) {
          const aiMessage = {
            role: 'assistant',
            name: '智能助手',
            text: response.data,
            time: this.getFormattedTime(),
            avatar: '',
            avatarText: 'AI'
          }
          this.messages.push(aiMessage)
        } else {
          // 显示后端返回的错误信息
          this.$message.error(response.msg || 'AI服务调用失败')
        }
      } catch (error) {
        console.error('AI服务调用失败:', error)
        this.$message.error('AI服务调用失败: ' + (error.message || '未知错误'))
      } finally {
        this.isLoading = false
        this.scrollToBottom()
      }
    },
    clearChat() {
      this.messages = [
        {
          role: 'assistant',
          name: '智能助手',
          text: '嗨～欢迎来到图书管理系统！我是你的智能小助手，有什么可以帮你的呀？',
          time: this.getFormattedTime(),
          avatar: '',
          avatarText: 'AI'
        }
      ]
    },
    getFormattedTime() {
      const now = new Date()
      const hours = now.getHours().toString().padStart(2, '0')
      const minutes = now.getMinutes().toString().padStart(2, '0')
      return `${hours}:${minutes}`
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const chatContent = this.$refs.chatContent
        if (chatContent) {
          chatContent.scrollTop = chatContent.scrollHeight
        }
      })
    },
    formatMessage(text) {
      // 简单的文本格式化，将换行符转换为<br>
      return text.replace(/\n/g, '<br>')
    }
  }
}
</script>

<style scoped>
.app-container {
  height: 100vh;
  padding: 20px;
  box-sizing: border-box;
}

.chat-container {
  max-width: 800px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  border-bottom: 1px solid #e8e8e8;
  max-height: calc(100vh - 200px);
}

.chat-message {
  display: flex;
  margin-bottom: 20px;
}

.user-message {
  flex-direction: row-reverse;
}

.message-avatar {
  margin: 0 10px;
}

.message-content {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 18px;
  background-color: #f5f7fa;
  position: relative;
}

.user-message .message-content {
  background-color: #409eff;
  color: white;
}

.message-name {
  font-size: 12px;
  margin-bottom: 4px;
  opacity: 0.7;
}

.message-text {
  font-size: 14px;
  line-height: 1.5;
  word-wrap: break-word;
}

.message-time {
  font-size: 11px;
  margin-top: 4px;
  opacity: 0.5;
  text-align: right;
}

.chat-input {
  padding: 20px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.input-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 5px;
}

/* 滚动条样式 */
.chat-content::-webkit-scrollbar {
  width: 8px;
}

.chat-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.chat-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.chat-content::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 兼容IE和Firefox */
.chat-content {
  scrollbar-width: thin;
  scrollbar-color: #c1c1c1 #f1f1f1;
}
</style>
