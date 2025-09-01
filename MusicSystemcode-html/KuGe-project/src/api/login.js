import request from "@/utils/request"
export const userloginService = function(user){
    const parms=new URLSearchParams();
     for(let key in user){
        parms.append(key,user[key]);
    }
    return request.post("/login/getlogin",parms,{skipAuth: true})
}
export const userlogoutService=function(){
    return request.get('/login/logout')
}