import './assets/main.scss'
import router from "@/router/index.js"
import { createApp } from 'vue'
import App from './App.vue'
import ElementPlus from "element-plus"
import "element-plus/dist/index.css"
import { createPinia } from 'pinia'
import { createPersistedState } from 'pinia-persistedstate-plugin'

const persist=createPersistedState();
const app=createApp(App);
const pinia=createPinia();
pinia.use(persist);
app.use(pinia);
app.use(router);
app.use(ElementPlus);
app.mount('#app')
