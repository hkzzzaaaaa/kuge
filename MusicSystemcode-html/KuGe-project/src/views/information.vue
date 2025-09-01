<script lang="ts" setup>
import { ref,reactive,onMounted} from 'vue'
import { ElMessage } from 'element-plus'
import type { UploadProps } from 'element-plus'
import { useRoute } from 'vue-router';
import {useRouter} from 'vue-router';
import { updateform,updatesign,updatepassword,updateimage,payCodeService} from '@/api/information';
const route = useRoute(); 
const router=useRouter();
const dialogVisible=ref(false);
const dialogVisible2=ref(false);
const num = ref(1);
const returnmain=function(){
    router.push('/layout');
}
const replasenum=function(){
  num.value=1;
}
const pay=function(){
  dialogVisible2.value=true;
  dialogVisible.value=false;
}
const userpassword=reactive({
  password:'',
  newpassword1:'',
  newpassword2:''
})
const userinformation = reactive({
    user_id: '',
    user_name: '',
    user_account: '',
    user_password: '',
    user_email: '',
    user_avatar: '',
    gender: '',
    birthday: '',
    vip: '',
    province: '',
    city: '',
    signature: ''
});
 onMounted(() => {
    try {
        const userInfoParam = route.query.userInfo;
        if (userInfoParam) {
            const info = JSON.parse(userInfoParam as string);
            Object.assign(userinformation, info);
        }
    } catch (error) {
        console.error('解析用户信息失败:', error);
        // 可以添加错误提示或默认值
    }
  });
const imageUrl = ref('')
const value1 = ref('')
const handleAvatarSuccess: UploadProps['onSuccess'] = (
  response,
  uploadFile
) => {
  imageUrl.value = URL.createObjectURL(uploadFile.raw!)
}

const updateinformation=async function(){
   let result=await updateform(userinformation)
   if(result.code===0){
      ElMessage({
        message:"信息修改成功",
        type:"success"
      })
   }else{
      ElMessage({
        message:"信息修改失败",
        type:"error"
      })
   }
}
const updatesignature=async function(){
   let result=await updatesign(userinformation)
   if(result.code===0){
      ElMessage({
        message:"信息修改成功",
        type:"success"
      })
   }else{
      ElMessage({
        message:"信息修改失败",
        type:"error"
      })
   }
}
const updatepass=async function(){
  if(userpassword.newpassword1===userpassword.newpassword2){
     let result=await updatepassword(userpassword)
  if(result.code===0){
      ElMessage({
        message:"信息修改成功",
        type:"success"
      })
      userpassword.password=''
      userpassword.newpassword1=''
      userpassword.newpassword2=''
   }else{
      ElMessage({
        message:"信息修改失败",
        type:"error"
      })
      userpassword.password=''
      userpassword.newpassword1=''
      userpassword.newpassword2=''
   }
  }
  else{
    ElMessage({
        message:"请输入相同的密码",
        type:"error"
      })
      userpassword.password=''
      userpassword.newpassword1=''
      userpassword.newpassword2=''
  }
}
const updateimages=async function(){
    if(imageUrl.value===''){
      ElMessage({
        message:"请选择图片",
        type:"error"
      })
    }
    else{
      let result=await updateimage(imageUrl);
      if(result.code===0){
        ElMessage({
        message:"头像修改成功",
        type:"success"
      })
      }
      else{
        ElMessage({
        message:"头像修改失败",
        type:"error"
      })
      }
    }
}
const getCode=async function(){
   let parms={
      payaccount:num.value*30,
      openmounth:num.value
    }
    let result= await payCodeService(parms);
    if(result.code===0){
      const qrCodeUrl = result.data
      const fullUrl=`http://localhost:8080${qrCodeUrl}`;
      console.log(qrCodeUrl);
      window.open(fullUrl);
    }
    else{
       ElMessage({
        message:"二维码获取失败",
        type:"error"
      })
    }
}
</script>

<template>
  <div class="page-container">
    <div class="page-title">
      <el-text style="font-size: 26px; font-weight: 600;">个人中心</el-text>
            <el-button 
              type="primary" 
              class="btn-action btn-return"
              @click="returnmain"
            >
              返回主页面
            </el-button>
    </div>
    <div class="main-content">
      <div class="left-section">
        <div class="card-wrapper">
          <div class="form-block">
            <el-text class="block-title">个性签名</el-text>
            <el-input
              v-model="userinformation.signature"
              class="custom-textarea"
              :rows="5"
              type="textarea"
              :maxlength="50" 
              show-word-limit
              placeholder="请输入个性签名（最多50字）"
            />
            <el-button 
              type="primary" 
              class="btn-action"
              @click="updatesignature"
            >
              保存
            </el-button>
          </div>
          <div class="form-block password-block">
            <el-text class="block-title">修改密码</el-text>
            <el-form class="form-content">
              <el-form-item>
                <el-input 
                  size="large" 
                  v-model="userpassword.password" 
                  placeholder="请输入原密码" 
                  show-password
                  class="custom-input"
                ></el-input>
              </el-form-item>
              <el-form-item>
                <el-input 
                  size="large" 
                  v-model="userpassword.newpassword1" 
                  placeholder="请输入新密码" 
                  show-password
                  class="custom-input"
                ></el-input>
              </el-form-item>
              <el-form-item>
                <el-input 
                  size="large" 
                  v-model="userpassword.newpassword2" 
                  placeholder="请再次输入新密码" 
                  show-password
                  class="custom-input"
                ></el-input>
              </el-form-item>
            </el-form>
            <el-button 
              type="primary" 
              class="btn-action"
              @click="updatepass"
            >
              保存
            </el-button>
          </div>        
        </div>
      </div>
      <div class="right-section">
        <div class="card-wrapper">
          <div class="profile-header">
            <el-text class="header-title">个人资料</el-text>
            <div class="info-item">
              <el-text class="info-label">账号:</el-text>
              <el-text class="info-value">{{ userinformation.user_account}}</el-text>
            </div>
            <div class="info-item">
              <el-text class="info-label">酷哥id:</el-text>
              <el-text class="info-value">{{ userinformation.user_id}}</el-text>
            </div>
          </div>
          <div class="form-block">
            <el-form class="form-content">
              <el-form-item class="form-item">  
                <el-text class="form-label">名称:</el-text>
                <el-input 
                  size="large" 
                  v-model="userinformation.user_name"
                  class="custom-input"
                ></el-input>
              </el-form-item>
              
              <el-form-item class="form-item">  
                <el-text class="form-label">性别:</el-text>
                <el-radio-group v-model="userinformation.gender" class="radio-group">
                  <el-radio :value="0" class="radio-option">男</el-radio>
                  <el-radio :value="1" class="radio-option">女</el-radio>
                  <el-radio :value="2" class="radio-option">保密</el-radio>
                </el-radio-group>
              </el-form-item>
              
              <el-form-item class="form-item">
                <el-text class="form-label">生日:</el-text>
                <div class="birth-content">
                  <el-text class="current-birth">{{ userinformation.birthday}}</el-text>
                  <el-date-picker
                    v-model="value1"
                    type="date"
                    class="custom-input date-picker"
                    placeholder="选择生日"
                    size="large" 
                  />
                </div>
              </el-form-item>
              
              <el-form-item class="form-item location-item">
                <el-text class="form-label">省份:</el-text>
                <el-input 
                  size="large" 
                  v-model="userinformation.province"
                  class="custom-input short-input"
                  placeholder="省份"
                ></el-input>
                <div style="width: 100%;"></div>
                <br></br>
                <el-text class="form-label">城市:</el-text>
                <el-input 
                  size="large" 
                  v-model="userinformation.city"
                  class="custom-input short-input"
                  placeholder="城市"
                ></el-input>
              </el-form-item>
            </el-form>
            
            <el-button 
              type="primary" 
              class="btn-action"
              @click="updateinformation"
            >
              保存
            </el-button>
          </div>
          <div class="form-block vip-block">
            <el-text class="form-label">vip到期：</el-text>
            <el-text class="vip-date">{{ userinformation.vip }}</el-text>
            <el-button 
              type="primary" 
              class="btn-action btn-vip"
              @click="dialogVisible=true"
            >
              购买会员
            </el-button>
          </div>
        </div>
      </div>
    </div>
    <el-dialog
      v-model="dialogVisible"
      title="购买会员"
      width="500px"
      class="custom-dialog"
    >
      <div class="dialog-body">
        <div class="dialog-item">
          <el-text class="dialog-label">购买月数：</el-text>
          <el-input-number 
            v-model="num" 
            :min="1" 
            :max="36" 
            class="number-input"
            controls-position="right"
          />
        </div>
        
        <div class="dialog-item">
          <el-text class="dialog-label">总价格：</el-text>
          <el-text class="price-text">{{ num * 30 }}元</el-text>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button 
            type="primary" 
            class="dialog-btn"
            @click="pay"
          >
            下单
          </el-button>
          <el-button 
            class="dialog-btn dialog-btn-cancel"
            @click="replasenum"
          >
            重置
          </el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog
      v-model="dialogVisible2"
      title="订单"
      width="500px"
      class="custom-dialog"
    >
      <div class="dialog-body">
        <div class="dialog-item">
          <el-text class="dialog-label">购买月数：</el-text>
          <el-text class="dialog-value">{{ num }}</el-text>
        </div>
        
        <div class="dialog-item">
          <el-text class="dialog-label">总价格：</el-text>
          <el-text class="price-text">{{ num * 30 }}元</el-text>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button 
            type="primary" 
            class="dialog-btn"
            @click="getCode"
          >
            支付
          </el-button>
          <el-button 
            class="dialog-btn dialog-btn-cancel"
          >
            已支付
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>


<style scoped>
.page-container {
  min-height: 100vh;
  background-color: #f7f8fa;
  padding: 30px 50px;
  box-sizing: border-box;
}

.page-title {
  margin-bottom: 30px;
  color: #1d2129;
  padding-bottom: 15px;
  border-bottom: 1px solid #e5e6eb;
  display: flex;
  justify-content: space-between;
}

.main-content {
  display: flex;
  gap: 30px;
  width: 100%;
}

.left-section, .right-section {
  flex: 1;
  min-width: 450px;
}
.card-wrapper {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  padding: 30px;
  min-height: calc(100vh - 140px);
  box-sizing: border-box;
}
.form-block {
  margin-bottom: 35px;
  padding-bottom: 25px;
  border-bottom: 1px solid #f2f3f5;
}

.form-block:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.password-block {
  margin-top: 20px;
}

.return-block {
  margin-top: 20px;
  display: flex;
  justify-content: flex-start;
  align-items: center;
}
.block-title {
  font-size: 18px;
  font-weight: 500;
  color: #1d2129;
  margin-bottom: 15px;
  display: block;
}
.custom-textarea {
  width: 100%;
  border-radius: 8px;
  border-color: #dcdfe6;
  transition: all 0.2s;
  resize: none;
}

.custom-textarea:focus,
.custom-input:focus {
  border-color: #4096ff;
  box-shadow: 0 0 0 3px rgba(64, 150, 255, 0.1);
  outline: none;
}

.custom-input {
  width: 100%;
  border-radius: 8px;
  border-color: #dcdfe6;
  transition: all 0.2s;
}
.btn-action {
  width: 180px;
  height: 40px;
  border-radius: 8px;
  margin-top: 15px;
  font-size: 14px;
  transition: all 0.2s;
}

.btn-action:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 150, 255, 0.2);
}

.btn-return {
  background-color: #67c23a;
  border-color: #67c23a;
}

.btn-return:hover {
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.2);
}

.btn-vip {
  background-color: #ff9a2e;
  border-color: #ff9a2e;
  margin-top: 0;
  margin-left: 15px;
}

.btn-vip:hover {
  box-shadow: 0 4px 12px rgba(255, 154, 46, 0.2);
}
.form-content {
  width: 100%;
}

.form-item {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.form-label {
  font-size: 18px;
  color: #4e5969;
  margin-right: 15px;
  min-width: 70px;
}
.radio-group {
  display: flex;
  gap: 25px;
}

.radio-option {
  padding: 5px 10px;
  border-radius: 6px;
  transition: background-color 0.2s;
}

.radio-option:hover {
  background-color: #f2f3f5;
}
.birth-content {
  flex: 1;
}

.current-birth {
  display: block;
  margin-bottom: 8px;
  color: #86909c;
  font-size: 14px;
}

.date-picker {
  width: 100%;
}
.location-item {
  align-items: center;
  gap: 10px;
}

.short-input {
  width: 45%;
}

.separator {
  color: #c9cdD4;
  font-size: 18px;
}
.profile-header {
  margin-bottom: 30px;
}

.header-title {
  font-size: 22px;
  font-weight: 600;
  color: #1d2129;
  margin-bottom: 20px;
  display: block;
}

.info-item {
  margin-bottom: 15px;
}

.info-label {
  font-size: 16px;
  color: #4e5969;
  margin-right: 10px;
}

.info-value {
  font-size: 16px;
  color: #1d2129;
}
.vip-block {
  margin-top: 20px;
  display: flex;
  align-items: center;
}

.vip-date {
  font-size: 18px;
  color: #ff7d00;
  font-weight: 500;
}
.custom-dialog {
  border-radius: 12px;
  overflow: hidden;
}

.dialog-body {
  padding: 25px;
}

.dialog-item {
  margin-bottom: 25px;
  display: flex;
  align-items: center;
}

.dialog-label {
  font-size: 16px;
  color: #4e5969;
  width: 100px;
}

.number-input {
  width: 180px;
  border-radius: 6px;
}

.price-text {
  font-size: 18px;
  color: #f53f3f;
  font-weight: 500;
}

.dialog-value {
  font-size: 16px;
  color: #1d2129;
}
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  padding: 15px 25px;
  border-top: 1px solid #f2f3f5;
}

.dialog-btn {
  width: 100px;
  border-radius: 6px;
}

.dialog-btn-cancel {
  background-color: #f2f3f5;
  color: #4e5969;
  border-color: #e5e6eb;
}

.dialog-btn-cancel:hover {
  background-color: #e8eaec;
  color: #1d2129;
}
@media (max-width: 992px) {
  .main-content {
    flex-direction: column;
  }
  
  .left-section, .right-section {
    min-width: 100%;
    margin-bottom: 30px;
  }
  
  .card-wrapper {
    min-height: auto;
  }
}

@media (max-width: 576px) {
  .page-container {
    padding: 20px 15px;
  }
  
  .form-item {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .form-label {
    margin-bottom: 8px;
  }
  
  .location-item {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .short-input {
    width: 100%;
    margin-bottom: 10px;
  }
  
  .separator {
    display: none;
  }
  
  .radio-group {
    flex-wrap: wrap;
  }
  
  .btn-action {
    width: 100%;
  }
}
</style>