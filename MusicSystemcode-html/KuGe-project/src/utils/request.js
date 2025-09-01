import axios from "axios";
const baseURL="/api";
const instant=axios.create({baseURL});
import { useTokenStore } from "@/stores/token";
instant.interceptors.request.use(
    (config)=>{
        if (config.skipAuth) {
            return config;
        }
        const tokenStore=useTokenStore();
        if(tokenStore.token){
            config.headers.Authorization=tokenStore.token
        }
        return config; 
    },
    err=>{
        Promise.reject(err)
    }
)
instant.interceptors.response.use(
    result=>{
        return result.data;
    },
    err=>{
        console.log("服务错误",err);
        return Promise.reject(err);
    }
)
export default instant