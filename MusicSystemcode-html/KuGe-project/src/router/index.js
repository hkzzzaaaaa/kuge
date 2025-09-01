import {createRouter,createWebHistory} from 'vue-router'
import login from '@/views/login.vue'
import layout from '@/views/layout.vue'
import autherlayout from '@/views/autherlayout.vue';
import information from '@/views/information.vue';
import musicpublic from '@/views/musicpublic.vue';
import musicbase from '@/views/musicbase.vue';
import favourite from '@/views/favourite.vue';
import history from '@/views/history.vue';
import { useTokenStore } from '@/stores/token';
const routes=[
    {
        path:'/',
        redirect:'/login'
    },
    {
        path:'/login',
        component:login,
        meta: { requiresAuth: false } 
    },
    {
        path:'/layout',
        component:layout,
        meta: { requiresAuth: true },
        children:[
            {
                path:'/musicbase',
                component:musicbase,
                meta: { requiresAuth: true }
            },
            {
                path:'/favourite',
                component:favourite,
                meta: { requiresAuth: true }
            },
            {
                path:'/history',
                component:history,
                meta: { requiresAuth: true }
            }
        ]
    },
    {
        path:'/autherlayout',
        component:autherlayout,
        meta: { requiresAuth: true },
        children:[
            {
                path:'/musicpublic',
                component:musicpublic,
                meta: { requiresAuth: true }
            }
        ]
    },
    {
        path:'/information',
        component:information,
        meta: { requiresAuth: true }
    }
]
const router=createRouter({
    history:createWebHistory(),
    routes:routes
})
router.beforeEach((to,next) => {
    const tokenStore = useTokenStore();
    // 如果目标路由需要登录（meta.requiresAuth 为 true）
    if (to.meta.requiresAuth) {
        // 检查 Token 是否存在
        if (tokenStore.token) {
            next(); // 已登录，放行
        } else {
            // 未登录，跳转到登录页，并记录原路径
            next({
                path: '/login',
                query: { redirect: to.fullPath } // 保存原路径，登录后可跳转回
            });
        }
    } else {
        // 无需登录的页面，直接放行
        next();
    }
});
export default router