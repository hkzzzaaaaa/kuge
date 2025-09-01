<script setup>
import { reactive } from 'vue'
import { ref } from 'vue'
import {userRegisterService,getCaptchaService} from "@/api/registry"
import {userloginService} from '@/api/login'
import {useRouter} from "vue-router"
import { ElMessage,ElMessageBox} from 'element-plus'
import { useTokenStore } from '@/stores/token'
const router=useRouter()
const tokenStore=useTokenStore();
const user = reactive({
  account:'',
  password:''
})
const  registerData=reactive({
  username:'',
  password1:'',
  password2:'',
  email:'',
  captcha:''
})
const checkpassword=function(rule,value,callback){
      if(value===''){
        callback(new Error("请再次确认密码"))
      }
      else if(value!==registerData.password1){
        callback(new Error("请确保两次输入的密码一致"))
      }
      else{
        callback();
      }
}
const rule={
  username:[
     {required:true,message:"请输入用户名",trigger: ['blur', 'change'] },
     {min:1,max:20,message:"长度限制为1-20位",trigger: ['blur', 'change'] }
  ],
  password1:[
     {required:true,message:"请输入密码",trigger: ['blur', 'change'] },
     {min:5,max:20,message:"长度限制为5-20位",trigger: ['blur', 'change'] }
  ],
  password2:[
    {validator:checkpassword, trigger:  ['blur', 'change']  }
  ],
  email:[
    {required:true,message:"请输入邮箱",trigger: ['blur', 'change'] },
    { type: 'email', message: '请输入正确的邮箱格式',trigger: ['blur', 'change'] }
  ],
  captcha:[
    {required:true,message:"请输入验证码",trigger: ['blur', 'change'] },
    { min: 4, max: 4, message: '验证码长度必须为4位',trigger: ['blur', 'change'] }
  ]
}
const getcaptcha=async function(){
    let result=await getCaptchaService(registerData);
    if(result.code===0){
        ElMessage({
            message:"验证码发送成功",
            type:'success'
        })
    }
    else{
         ElMessage({
            message:result.message,
            type:'error'
        })
    }
}
const register=async function(){
     let result=await userRegisterService(registerData);
     if(result.code===0){
       ElMessageBox.alert("账号为："+result.data,'注册成功')
       registerData.username=''
       registerData.password1=''
       registerData.password2=''
       registerData.email=''
       registerData.captcha=''
       ifregister.value=false
       user.account=result.data
     }
     else{
        ElMessage({
            message:'注册失败',
            type:'error'
        })
     }
}
const userlogin=async function(){
     let result=await userloginService(user);
     if(result.code===0){
        tokenStore.setToken(result.data);
        ElMessage({
            message:'登录成功',
            type:'success'
        })
       router.push('/layout')
     }
     else{
        ElMessage({
            message:'登录失败'+result.message,
            type:'error'
        })
     }
}
const misspassword=reactive({
  account:'',
  password1:'',
  password2:'',
  email:'',
  captcha:''
})
const ifregister=ref(false)
const ifmisspassword=ref(false)
</script>

<template>
    <div class="back">
        <div class="app">
            <div class="picture">
                <img class="img" src="@/assets/logo.png" alt="">
            </div>
            <div class="form" v-if="ifregister==false&&ifmisspassword==false">
                <el-form v-model="user" style="width: 80%;">
                    <div class="text1">登录</div>
                    <br>
                    <el-form-item>
                        <el-input size="large" v-model="user.account" placeholder="请输入账号"></el-input>
                    </el-form-item>
                    <el-form-item size="large">
                        <el-input v-model="user.password" placeholder="请输入密码" show-password="true"></el-input>
                    </el-form-item>
                    <div class="loginandregister">
                        <el-button type="primary" style="width:50%;" v-on:click="userlogin">
                            登录
                        </el-button>
                        <el-button type="default" style="width: 50%;" v-on:click="ifregister=true">
                            注册
                        </el-button>
                    </div>
                </el-form>
            </div>

            <div class="form" v-if="ifregister==true&&ifmisspassword==false">
                <el-form :model="registerData" style="width: 80%;"  :rules="rule">
                    <div class="text1">注册</div>
                    <br>
                    <el-form-item prop="username">
                        <el-input size="large" v-model="registerData.username" placeholder="请输入用户名"></el-input>
                    </el-form-item>
                    <el-form-item size="large" prop="password1">
                        <el-input v-model="registerData.password1" placeholder="请输入密码" show-password="true"></el-input>
                    </el-form-item>
                    <el-form-item size="large" prop="password2">
                        <el-input v-model="registerData.password2" placeholder="请再次输入密码" show-password="true"></el-input>
                    </el-form-item>
                    <el-form-item size="large" prop="email">
                        <el-input v-model="registerData.email" placeholder="请输入注册邮箱" ></el-input>
                    </el-form-item>
                    <div style="display: flex;gap: 30px;">
                    <el-form-item size="large" prop="captcha" >
                        <el-input v-model="registerData.captcha" placeholder="请输入邮箱验证码" style="width: 250px;"></el-input>
                    </el-form-item>
                    <el-button size="large" type="info" v-on:click="getcaptcha">
                            发送邮箱验证码
                    </el-button>
                    </div>
                    <div class="loginandregister">
                        <el-button type="primary" style="width:50%;" v-on:click="register">
                            注册
                        </el-button>
                        <el-button type="default" style="width: 50%;" v-on:click="ifregister=false">
                            返回
                        </el-button>
                    </div>
                </el-form>
            </div>


            <div class="form" v-if="ifregister==false&&ifmisspassword==true">
                <el-form v-model="register" style="width: 80%;">
                    <div class="text1">找回密码</div>
                    <br>
                    <el-form-item>
                        <el-input size="large" v-model="misspassword.account" placeholder="请输入账号"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-input size="large" v-model="misspassword.password1" placeholder="请输入密码" show-password="true"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-input size="large" v-model="misspassword.password2" placeholder="请再次输入密码" show-password="true"></el-input>
                    </el-form-item>
                     <div style="display: flex;gap: 30px;">
                    <el-form-item size="large">
                        <el-input v-model="misspassword.captcha" placeholder="请输入邮箱验证码" style="width: 250px;"></el-input>
                    </el-form-item>
                    <el-button size="large" type="info">
                            发送邮箱验证码
                    </el-button>
                    </div>
                    <div class="loginandregister">
                        <el-button type="primary" style="width:50%;">
                            修改密码
                        </el-button>
                        <el-button type="default" style="width: 50%;" v-on:click="ifmisspassword=false">
                            返回
                        </el-button>
                    </div>
                </el-form>
            </div>
            


        </div>
    </div>
</template>
<style>
    .back{
        height: 100vh;
        display: flex;
        justify-content: center;
        align-items: center;
        background-color:aquamarine;
    }
    .app{
        display: flex;
    }
    .picture{
        width: 550px;
        display: flex 1;
    }
    .img {
        max-width: 100%;
        max-height: 100%; 
        object-fit: contain; 
    }

    .form{
        width: 550px;
        display: flex 1;
        display: flex;
        align-items: center;
        justify-content: center;
        background-color: aliceblue;
    }
    .text1{
        font-size: 30px;
        font-weight: bold;
    }
    .rememberandmiss{
        display: flex;
        justify-content: space-between;
    }
    .loginandregister{
        display: flex;
        justify-content: center;
    }
</style>