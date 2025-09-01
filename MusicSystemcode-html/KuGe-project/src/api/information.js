import request from "@/utils/request"
export const getInformationService=function(){
    return request.get('/information/getinfo')
}
export const updateform=function(userinformation){
     const parms=new URLSearchParams();
     for(let key in userinformation){
        parms.append(key,userinformation[key]);
    }
    return request.post("/information/updateinform",parms)
}
export const updatesign=function(userinformation){
    return request.post("/information/updatesign",{signature: userinformation.signature})
}
export const updatepassword=function(userpassword){
     const parms=new URLSearchParams();
     for(let key in userpassword){
        parms.append(key,userpassword[key]);
    }
    return request.post("/information/updatepassword",parms)
}
export const updateimage=function(imageUrl){
     const parms=new URLSearchParams();
     params.append('imageUrl', imageUrl);
    return request.post("/information/updateimage",parms)
}
export const payCodeService=function(params){
    return request.get("/pay/getCode",{params:params})
}