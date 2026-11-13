import { ElMessageBox } from 'element-plus'
import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login'
import Layout from "../layout/Layout"
import Register from "../views/Register"
import Blog from "../views/blog"
import Index from "../views/Index"
import store from "../store"
import Home from "../views/Home"
import BlogSearch from "../views/front/BlogSearch"
import BlogWrite from "../views/front/blogWrite"
import MyFavorite from "../views/front/myFavorite"
import MyBlogs from "../views/front/myBlogs"
import MyVisit from "../views/front/myVisit"
import Resource from "../views/front/Resource"

const routes = [
  {
    path: '/',
    name: 'Layout',
    component: Layout,
    redirect: "/index",
    children:[
      {
        path: 'home',
        name: '首页',
        component: Home
      },
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/blog',
    name: 'Blog',
    component:Blog
  },
  {
    path: '/index',
    name: 'Index',
    component:Index
  },
  {
    path: '/blogSearch',
    name: 'BlogSearch',
    component:BlogSearch,
    meta:{
      keepAlive:true,
    }
  },
  {
    path: '/blogWrite',
    name: 'BlogWrite',
    component:BlogWrite
  },
  {
    path: '/myFavorite',
    name: 'MyFavorite',
    component: MyFavorite
  },
  {
    path: '/myBlogs',
    name: 'MyBlogs',
    component: MyBlogs
  },
  {
    path: '/myVisit',
    name: 'MyVisit',
    component: MyVisit
  },
  {
    path: '/resource',
    name: 'Resource',
    component: Resource
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

//刷新页面时，重置路由
activeRouter();
function activeRouter() {
  const userStr = localStorage.getItem("user");
  if (userStr) {
    const user = JSON.parse(userStr);
    if (!user.permissions)
      return;
    let root = {
      path: '/',
      name: 'Layout',
      component: Layout,
      redirect: "/index",
      children: [

      ]
    };
    user.permissions.forEach(p => {
      let obj = {
        path: p.path,
        name: p.name,
        component: ()=>import("@/views/"+p.name),
      };
      root.children.push(obj);
    });
    if (router) {
      router.addRoute(root);
    }
  }
};

//声明一个白名单
const whiteRouter = ['/login', '/register', "/blog", "/index", "/blogSearch", "/blogWrite", "/myFavorite", "/myBlogs", "/myVisit", "/resource"];

router.beforeEach((to, from, next) => {
  //这些页面所有人可以访问，无需阻拦
  if (whiteRouter.includes(to.path)) {
    next();
    return
  }
  let user = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {};
  if (!user.permissions || !user.permissions.length) {
    //没有任何权限，说明该用户没有登录
    next('/login');
  } else if (!user.permissions.find(p => {
    if (!p.path.includes("/")) {
      p.path = '/' + p.path;
    }
    return p.path === to.path;
  })) {
    //登录了但是权限里没有这个页面，禁止访问
    ElMessageBox.alert("没有这个页面", 'WARNING', {
      confirmButtonText: '确定',
    });
    next('/login');
  } else {
    //面包屑相关
    //路由守卫监听到变化后，将要跳转的页面name和path保存到本地存储中
    localStorage.setItem("currentPathName", to.name);
    localStorage.setItem("currentPath", to.path);
    //提交保存，调用setPath
    store.commit("setPath");
    //放行通过该函数，否则会卡住，函数内容不用管
    next();
  }
});

export default router