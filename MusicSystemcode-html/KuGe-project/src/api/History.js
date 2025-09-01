import request from "@/utils/request"
export const AddHistoryMusicService=function(rowdata){
    return request.put('History/add',rowdata);
}
export const GetHistoryMusicService=function(params){
    return request.get('History/get',{params:params});
}