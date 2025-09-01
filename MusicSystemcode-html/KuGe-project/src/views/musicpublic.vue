<script lang="ts" setup>
import { ref,reactive } from 'vue'
import type { ComponentSize } from 'element-plus'
import {Edit} from '@element-plus/icons-vue'
import { addMusicService,findmusicpublicService,findMusicByNameService} from '@/api/music'
import { ElMessage } from 'element-plus'
const currentPage3 = ref(1)
const pageSize3 = ref(5)
const size = ref<ComponentSize>('default')
const dialogVisible = ref(false)
const musicsearch = reactive({
  musicname: '',
})
const handleSizeChange = (val: number) => {
   pageSize3.value = val;
  currentPage3.value = 1;
  if (musicsearch.musicname) {
    findMusicByNameImpl(); 
  } else {
    findmusicpublicServiceImpl(); 
  }
}
const handleCurrentChange = (val: number) => {
  currentPage3.value = val;
  if (musicsearch.musicname) {
    findMusicByNameImpl();
  } else {
    findmusicpublicServiceImpl();
  }
}
const tableData=ref([])
const addmusic=reactive({
  musicname:'',
  albumname:'',
  vip:'0',
})
const replace=function(){
  addmusic.musicname=''
  addmusic.albumname=''
  addmusic.vip='0'
  selectedFile.value=null
  selectedFileName.value=''
}
const addMusicServiceImpl=async function() {
  if (!selectedFile.value) {
    ElMessage.warning('请选择要上传的音频文件');
    return;
  }
  if (!addmusic.musicname) {
    ElMessage.warning('请输入歌曲名称');
    return;
  }
  if (!addmusic.musicname) {
    ElMessage.warning('请输入专辑名称');
    return;
  }
  if (!addmusic.musicname) {
    ElMessage.warning('是否需要vip');
    return;
  }
  const formData = new FormData();
  formData.append('file', selectedFile.value);
  Object.keys(addmusic).forEach(key => {
    if (addmusic[key] !== undefined && addmusic[key] !== null) {
      formData.append(key, addmusic[key]);
    }
  });
  let result=await addMusicService(formData);
  if(result.code===0){
     ElMessage({
        message:"歌曲发布成功",
        type:"success"
      })
    replace();
    findmusicpublicServiceImpl();
  }
  else{
     ElMessage({
        message:result.data.message,
        type:"error"
      })
  }
}
const findMusicByNameImpl=async function(){
  let parms={
    pageNum:currentPage3.value,
    pagesize:pageSize3.value,
    music_name:musicsearch.musicname
  }
  let result=await findMusicByNameService(parms);
  tableData.value=result.data.items;
}
const pagesize=ref('')
const findmusicpublicServiceImpl=async function(){
  let parms={
    pageNum:currentPage3.value,
    pagesize:pageSize3.value
  }
  let result=await findmusicpublicService(parms);
  tableData.value=result.data.items;
  pagesize.value=result.data.total;
}
const selectedFileName=ref('');
const selectedFile = ref(null);
const emit = defineEmits(['fileSelected']);
const handleFileChange = (e) => {
  const file = e.target.files[0];
  if (file) {
    selectedFileName.value = file.name;
    if (file.size > 50 * 1024 * 1024) {
      ElMessage.error('文件大小不能超过50MB');
      e.target.value = '';
      selectedFileName.value = '';
      selectedFile.value = null;
      return;
    }
    selectedFile.value = file;
  } else {
    selectedFileName.value = '';
    selectedFile.value = null;
  }
};
findmusicpublicServiceImpl();
</script>
<template>
<div class="common-layout">
    <el-container>
      <el-header style="display: flex;justify-content: space-between;">
        歌曲管理
         <el-button type="primary" size="large" @click="dialogVisible = true">发布歌曲</el-button>
      </el-header>
      <el-main>
        <el-card style="max-width: 100%">
        <template #header>
          <div class="card-header">
            <el-form :inline="true" :model="musicsearch" class="demo-form-inline">
               <el-form-item label="歌曲搜索">
                  <el-input v-model="musicsearch.musicname" placeholder="Search" clearable />
                </el-form-item>
              <el-form-item> 
                <el-button type="primary" v-on:click="findMusicByNameImpl">搜索</el-button>
                <el-button type="default" v-on:click="musicsearch.musicname=''">重置</el-button>
              </el-form-item>
            </el-form>
          </div>
        </template>
        <el-table style="width: 100%" :data="tableData">
          <el-table-column prop="music_name" label="音乐名称" width="180" />
          <el-table-column prop="user_name" label="作者" width="180" />
          <el-table-column prop="album_name" label="专辑" />
          <el-table-column prop="vip" label="vip"  :formatter="(row) => row.vip === 1 ? '是' : '否'"/>
           <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button type="primary" :icon="Edit" circle size="small"/>
            </template>
          </el-table-column>
        </el-table>
        <template #footer>
        <div class="demo-pagination-block" style="display: flex;justify-content: end;">
          <el-pagination
            v-model:current-page="currentPage3"
            v-model:page-size="pageSize3"
            :size="size"
            :disabled=false
            :background=false
            layout="jumper,prev, pager, next"
            :total=pagesize
            @current-change="handleCurrentChange"
            @size-change="handleSizeChange"
          />
        </div>
        </template>
      </el-card>
        <el-dialog
          v-model="dialogVisible"
          title="添加歌曲"
          width="500">
          <template #footer>
            <div class="dialog-footer">
              <el-form label-width="auto" style="max-width: 600px">
                 <el-form-item label="音乐名称">
                    <el-input v-model="addmusic.musicname"/>
                 </el-form-item>
                 <el-form-item label="专辑">
                    <el-input v-model="addmusic.albumname"/>
                 </el-form-item>
                 <el-form-item label="是否需要vip">
                  <el-radio-group  v-model="addmusic.vip">
                    <el-radio value="1">是</el-radio>
                    <el-radio value="0">否</el-radio>
                  </el-radio-group>
                 </el-form-item>
              <el-form-item label="上传文件">
                <input 
                  type="file" 
                  id="fileInput" 
                  accept="*" 
                  class="file-input"
                  @change="handleFileChange"
                >
                <label for="fileInput" class="file-label">
                  <i class="el-icon-upload"></i>
                  <span v-if="!selectedFileName">点击上传或拖拽文件到此处</span>
                  <span v-else class="selected-file-name">{{ selectedFileName }}</span>
                </label>
              </el-form-item>  
              </el-form>
              <div style="display: flex; justify-content: center" gap="100px">
                <el-button type="primary" size="large" style="width: 100px;" v-on:click="addMusicServiceImpl">确认</el-button>
                <el-button size="large" style="width: 100px;" v-on:click="dialogVisible=false,replace();">取消</el-button>
              </div>
            </div>
          </template>
        </el-dialog>
      </el-main>
    </el-container>
  </div>
</template>

<style>
.demo-form-inline .el-input {
  --el-input-width: 220px;
}

.demo-form-inline .el-select {
  --el-select-width: 220px;
}
.file-input {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  opacity: 0;
  cursor: pointer;
  z-index: 2;
}
.file-label {
  display: inline-block;
  width: 100%;
  padding: 16px;
  border: 2px dashed #dcdfe6;
  border-radius: 4px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  background-color: #f5f7fa;
  color: #606266;
  box-sizing: border-box;
}

.file-label:hover {
  border-color: #409eff;
  color: #409eff;
  background-color: #ecf5ff;
}

.file-label i {
  font-size: 24px;
  margin-bottom: 8px;
  display: block;
}

.selected-file-name {
  color: #1f2329;
  font-weight: 500;
  word-break: break-all;
  max-width: 100%;
  display: inline-block;
}

.file-hint {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
  line-height: 1.5;
}
</style>