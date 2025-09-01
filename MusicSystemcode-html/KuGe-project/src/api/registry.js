import request from "@/utils/request"
// export function registryService(registerData){
//     return request.post("/login/registry",registerData);
// }

// @Parm注解时使用该格式提交
export const userRegisterService =(registerData)=>{
    const parms=new URLSearchParams();
    for(let key in registerData){
        parms.append(key,registerData[key]);
    }
    return request.post("/login/registry",parms,{skipAuth: true})
}
export const getCaptchaService=function(registerData){
     const params = new URLSearchParams();
    params.append("email", registerData.email);
    return request.post("/login/getcaptcha", params,{skipAuth: true});
}