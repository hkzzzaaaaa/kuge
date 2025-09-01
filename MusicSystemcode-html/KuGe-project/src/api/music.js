import request from "@/utils/request"
export const addMusicService=function(formdata){
    return request.post("/music/addmusic",formdata);
}
export const findmusicpublicService=function(params){

return request.get("/music/findmusicpublic",{params:params})
}
export const findMusicByNameService=function(params){
return request.get("/music/findMusicByName",{params:params})
}
export const findMusicBaseService=function(params){
    return request.get("/music/findMusicBaseService",{params:params})
}
export const findMusic=function(params){
    return request.get("/music/findMusic",{params:params})
}
export const putFavouriteService=function(music_id){
    return request.put(`/music/putFavourite?music_id=${music_id}`);
}
export const getFavouriteService=function(params){
    return request.get("/music/getFavourite",{params:params})
}
export const deleteFavouriteService=function(params){
    const parms=new URLSearchParams();
    parms.append("music_name",params.music_name);
    parms.append("user_name",params.user_name);
    console.log(params.music_name);
    return request.put(`/music/deleteFavourite?${parms.toString()}`)
}