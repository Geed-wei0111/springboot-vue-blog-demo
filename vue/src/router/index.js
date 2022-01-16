import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login'
import Layout from "../layout/Layout";
import Register from "../views/Register";
import Person from "../views/Person";
import Test from "../views/Test";
import UploadTest from "../views/UploadTest";
import News from "../views/News";

const routes = [
  {
    path: '/',
    name: 'Layout',
    component: Layout,
    redirect: "/login",
    children:[
      {
        path: 'home',
        name: 'Home',
        component: Home
      },
      {
        path: 'person',
        name: 'Person',
        component: Person,
      },
      {
        path: 'test',
        name: 'Test',
        component: Test,
      },
      {
        path: 'uploadTest',
        name: 'UploadTest',
        component: UploadTest,
      },
      {
        path: 'news',
        name: 'News',
        component: News,
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
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
