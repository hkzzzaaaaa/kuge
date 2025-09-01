<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import type { ComponentSize } from 'element-plus'
import { getFavouriteService, deleteFavouriteService } from '@/api/music'
import { Star, VideoPlay, Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { putFavouriteService } from '@/api/music'

const tableData = ref([])
const emit = defineEmits(['play-song'])

const handleClick = function(music_url: string, rowdata: any) {
  emit('play-song', music_url, rowdata)
}

const currentPage = ref(1)
const pageSize = ref(10)
const size = ref<ComponentSize>('default')
const pagesize = ref('')
const handleSizeChange = (val: number) => {
  pageSize.value = val
  currentPage.value = 1
  getfavourite()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  getfavourite() 
}

const getfavourite = async function() {
  const parms = {
    pageNum: currentPage.value,
    pagesize: pageSize.value
  }
  const result = await getFavouriteService(parms)
  tableData.value = result.data.items
  pagesize.value = result.data.total
}

const putFavourite = async function(music_id) {
  console.log(music_id);
  let result = await putFavouriteService(music_id)
  if (result.code === 0) {
    ElMessage({
      message: '操作成功',
      type: 'success'
    })
    getfavourite()
  } else {
    ElMessage({
      message: '操作失败',
      type: 'error'
    })
  }
}
onMounted(() => {
  getfavourite()
})

</script>

<template>
  <div class="subpage-container">
    <el-container class="main-container">
      <el-main class="table-content">
        <el-table 
          :data="tableData" 
          stripe 
          style="width: 100%"
        >
          <el-table-column prop="music_name" label="音乐名称" width="200" />
          <el-table-column prop="user_name" label="作者" width="200" />
          <el-table-column prop="album_name" label="专辑" width="200" />
          <el-table-column prop="vip" label="vip">
            <template #default="scope">
              <span>{{ scope.row.vip === 1 ? '是' : '否' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button :icon="VideoPlay" circle size="small" @click="handleClick(scope.row.music_url, scope.row)" />
              <el-button :icon="Star" circle size="small" @click="putFavourite(scope.row.music_id)" />
              <el-button :icon="Plus" circle size="small" />
            </template>
          </el-table-column>
        </el-table>
      </el-main>
      <el-footer class="fixed-footer">
        <div class="demo-pagination-block">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :size="size"
            :disabled="false"
            :background="false"
            layout="jumper,prev, pager, next"
            :total="pagesize"
            @current-change="handleCurrentChange"
            @size-change="handleSizeChange"
          />
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<style scoped>
.subpage-container {
  width: 100%;
  display: flex;
  flex-direction: column;
}

.main-container {
  display: flex;
  flex-direction: column;
  width: 100%;
}
.table-content {
  padding: 20px;
  box-sizing: border-box;
  min-height: 300px;
  overflow: auto;
}

.fixed-footer {
  height: 80px;
  min-height: 80px;
  max-height: 80px;
  padding: 20px;
  box-sizing: border-box;
  border-top: 1px solid #e5e7eb;
  background-color: #fff;
  width: 100%;
  margin-top: 10px;
}

.demo-pagination-block {
  display: flex;
  justify-content: end;
  height: 100%;
  align-items: center;
}
</style>