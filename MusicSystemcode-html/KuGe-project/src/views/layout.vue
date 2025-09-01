<script lang="ts" setup>
import { ArrowDown,Star,VideoPlay,Plus } from '@element-plus/icons-vue'
import { Search } from '@element-plus/icons-vue'
import {useRouter} from "vue-router"
import { ref,watch,nextTick} from 'vue'
import { reactive } from 'vue'
import { getInformationService } from '@/api/information'
import { ElMessage } from 'element-plus'
import { findMusic } from '@/api/music'
import type { ComponentSize } from 'element-plus'
import { AddHistoryMusicService } from '@/api/History'
import {putFavouriteService } from '@/api/music'
import { userlogoutService } from '@/api/login'
import { useTokenStore } from '@/stores/token'
const currentPage = ref(1)
const pageSize = ref(10)
const pagesize=ref('');
const size = ref<ComponentSize>('default')
const tableData=ref([])
const ifsearch=ref(false)
const router=useRouter()
const audioRef = ref(null);
const currentMusicUrl = ref('');
const playmusic=function(music_url,rowdata){
  handlePlay(music_url,rowdata);
}
const handlePlay =async (musicUrl,rowdata) => {
    currentMusicUrl.value = musicUrl;
    await AddHistoryMusicService(rowdata);
};
watch(currentMusicUrl, (newValue) => {
  if (newValue && audioRef.value) {
    nextTick(() => {
      audioRef.value.play().catch((error) => {
        console.error('播放失败:', error);
      });
    });
  }
});
import {
  CollectionTag,
  House,
  Menu as IconMenu,
  PieChart,
} from '@element-plus/icons-vue'
const userinformation=reactive({
    user_id:'',
    user_name:'',
    user_account:'',
    user_password:'',
    user_email:'',
    user_avatar:'',
    gender:'',
    birthday:'',
    vip:'',
    province:'',
    city:'',
    signature:''
})
const handleSizeChange = (val: number) => {
  pageSize.value = val
  currentPage.value=1
  findMusicServiceImpl();
}
const handleCurrentChange = (val: number) => {
  currentPage.value = val
  findMusicServiceImpl();
}
const handleOpen = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}
const handleClose = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}
const getautherlayout=function(){
  router.push('/autherlayout')
}
const getfavourite=function(){
  ifsearch.value=false;
  router.push('/favourite')
}
const getmusicbase=function(){
    ifsearch.value=false;
    router.push('/musicbase')
}
const gethistory=function(){
    ifsearch.value=false;
    router.push('/history')
}
const getinformation=async function(){ 
    let result=await getInformationService();
    if(result.code===0){
      Object.assign(userinformation, result.data);
      router.push({
            path: '/information',
            query: {userInfo: JSON.stringify(userinformation)}
          });
    }
    else{
      ElMessage({
            message:'查询失败',
            type:'error'
        })
  }
}
const findMusicServiceImpl=async function(){
    let parms={
    pageNum:currentPage.value,
    pagesize:pageSize.value,
    music_name:search.value
  }
let result=await findMusic(parms);
  tableData.value=result.data.items;
  pagesize.value=result.data.total;
  ifsearch.value=true;
}
const circleUrl=ref("https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png")
const search = ref('')
const putFavourite=async function(music_id){
  console.log(music_id);
  let result=await putFavouriteService(music_id)
  if(result.code===0){
     ElMessage({
            message:'操作成功',
            type:'success'
        })
  }
  else{
     ElMessage({
            message:'操作失败',
            type:'error'
        })
  }
}
const logout=async function(){
  router.push('/')
  let result=await userlogoutService(); 
  if(result.code===0){
    const tokenStore = useTokenStore()
     tokenStore.removeToken()
     ElMessage({
            message:'成功退出',
            type:'success'
        })
  }
  else{
     ElMessage({
            message:'退出失败',
            type:'error'
        })
  }
}
getmusicbase();
</script>
<template>
    <div class="common-layout">
        <el-container>
            <el-aside width="200px" style="background-color: #F0F7FF;height: 100vh;">
                    <h2 class="mb-2" style="text-align: center;">酷哥音乐</h2>
                    <el-menu
                        default-active="2"
                        class="el-menu-vertical-demo"
                        @open="handleOpen"
                        @close="handleClose"
                        always
                        style="background-color: #F0F7FF;"
                        >
                        <el-menu-item index="1" style="height:80px;" v-on:click="getmusicbase">
                           <el-icon><House /></el-icon>
                           <span >排行榜</span>
                        </el-menu-item>
                        <el-menu-item index="2" style="height:80px;"  v-on:click="getfavourite">
                            <el-icon><CollectionTag /></el-icon>
                            <span >我的收藏</span>
                        </el-menu-item>
                        <el-menu-item index="3" style="height:80px;" v-on:click="gethistory">
                            <el-icon><PieChart /></el-icon>
                            <span>最近播放</span>
                        </el-menu-item>
                    </el-menu>
            </el-aside>
            <el-container>
                    <el-header style="background-color:  #F0F7FF;">
                        <div class="header">
                            <div>
                                <el-input v-model="search" style="width: 400px;height: 50px;margin-top:5px;" placeholder="Please input" size="default" />
                                <el-button :icon="Search" circle size="large" style="margin-left: 10px;" v-on:click="findMusicServiceImpl"/>
                            </div>
                            <audio controls ref="audioRef" :src="currentMusicUrl" loop>
                                 
                            </audio>
                            <el-dropdown>
                                <span class="el-dropdown-link" style="margin-top: 5px;font-size: large;">
                                <el-avatar :size="50" :src="circleUrl" />
                                <el-icon class="el-icon--right">
                                    <arrow-down />
                                </el-icon>
                                </span>
                                <template #dropdown>
                                <el-dropdown-menu>
                                    <el-dropdown-item v-on:click="getinformation">个人资料</el-dropdown-item>
                                    <el-dropdown-item v-on:click="getautherlayout">音乐人</el-dropdown-item>
                                    <el-dropdown-item v-on:click="logout">退出登录</el-dropdown-item>
                                </el-dropdown-menu>
                                </template>
                            </el-dropdown>
                        </div>
                    </el-header>
                <el-main>
                  <div class="search-result-container">
                    <router-view @play-song="handlePlay" v-if="ifsearch===false"></router-view>
                    <div class="table-wrapper" v-if="ifsearch===true">
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
                          <el-button :icon="VideoPlay" circle size="small" @click="handlePlay(scope.row.music_url, scope.row)" />
                          <el-button :icon="Star" circle size="small" @click="putFavourite(scope.row.music_id)" />
                          <el-button :icon="Plus" circle size="small" />
                        </template>
                      </el-table-column>
                      </el-table>
                    </div>
                    <div class="pagination-container" v-if="ifsearch===true">
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
                  </div>
                </el-main>
            </el-container>
        </el-container>
    </div>
</template>
<style>
.search-result-container {
  width: 100%;
  min-height: 100%;
  display: flex;
  flex-direction: column;
}
.table-wrapper {
  flex: 1;
}
.pagination-container {
  display: flex;
  justify-content: end;
  margin-top: auto; 
  padding: 20px 0; 
  border-top: 1px solid #f0f0f0; 
}
.header {
  display: flex;
  justify-content: space-between;
}
.example-showcase .el-dropdown-link {
  cursor: pointer;
  color: var(--el-color-primary);
  display: flex;
  align-items: center;
}
</style>