import axios from 'axios'
import { ElMessageBox } from 'element-plus'
import router from "../src/router";

const request = axios.create({
    baseURL: '/api',  // 注意！！ 这里是全局统一加上了 '/api' 前缀，也就是说所有接口都会加上'/api'前缀在，页面里面写接口的时候就不要加 '/api'了，否则会出现2个'/api'，类似 '/api/api/user'这样的报错，切记！！！
    timeout: 5000
})

// request 拦截器
// 可以自请求发送前对请求做一些处理
// 比如统一加token，对请求参数统一加密
request.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json;charset=utf-8';

    // 设置请求头
    let user = localStorage.getItem("user");
    let ip = localStorage.getItem("ip");
    if (user){
        user = JSON.parse(user);
        config.headers['token'] = user.token;
    }
    if (ip){
        config.headers['ip'] = ip;
    }

    return config
}, error => {
    return Promise.reject(error)
});
let tokenOut = false;
// response 拦截器
// 可以在接口响应后统一处理结果
request.interceptors.response.use(
    response => {
        let res = response.data;
        // 如果是返回的文件
        if (response.config.responseType === 'blob') {
            return res
        }
        // 兼容服务端返回的字符串数据
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res
        }
        //401时提示信息
        if (res.code === "401" && !tokenOut){//加上判断，防止触发多个弹窗，影响体验
            tokenOut = true;
            if (tokenOut){
                ElMessageBox.alert(res.msg, 'WARNING', {
                    confirmButtonText: '确定',
                    callback:action=>{
                        if (action === "confirm"){
                            tokenOut = false;
                            localStorage.removeItem("user");
                            router.push('/login');//token验证失败跳转到登录界面
                        }
                    }
                })
            }
        }
        return res;
    },
    error => {
        console.log('err' + error) // for debug
        return Promise.reject(error)
    }
)


export default request

