<script setup>
import {useRouter} from "vue-router"
const router=useRouter()
const getlayout=function(){
    router.push('/layout')
}
import { userlogoutService } from '@/api/login'
import { useTokenStore } from '@/stores/token'
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
const getmusicpublic=function(){
    router.push('/musicpublic');
}
getmusicpublic();
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
                        :router
                        >
                        <el-menu-item index="/musicpublic" style="height:80px;">
                            <el-icon><CollectionTag /></el-icon>
                            <span>歌曲管理</span>
                        </el-menu-item>

                    </el-menu>
            </el-aside>
            <el-container>
                    <el-header style="background-color:  #F0F7FF;">
                        <div class="header">
                            <div>
                            </div>
                            <el-dropdown>
                                <span class="el-dropdown-link" style="margin-top: 5px;font-size: large;">
                                <el-avatar :size="50" :src="circleUrl" />
                                <el-icon class="el-icon--right">
                                    <arrow-down />
                                </el-icon>
                                </span>
                                <template #dropdown>
                                <el-dropdown-menu>
                                    <el-dropdown-item v-on:click="getlayout">主页面</el-dropdown-item>
                                    <el-dropdown-item v-on:click="logout">退出登录</el-dropdown-item>
                                </el-dropdown-menu>
                                </template>
                            </el-dropdown>
                        </div>
                    </el-header>
                <el-main>
                   <router-view></router-view>
                </el-main>
            </el-container>
        </el-container>
    </div>
</template>
<style>
    .header{
        display: flex;
        justify-content:space-between
    }
    .example-showcase .el-dropdown-link {
        cursor: pointer;
        color: var(--el-color-primary);
        display: flex;
        align-items: center;
    }
</style>
