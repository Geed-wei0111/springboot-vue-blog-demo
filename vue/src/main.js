import { createApp, ref } from 'vue'
import ElementPlus from 'element-plus';
import 'element-plus/lib/theme-chalk/index.css';

import App from './App.vue'
import router from './router'
import store from './store'
import '@/assets/css/global.css'

//解决element-plus无法显示中文问题
import zhLocale from 'element-plus/lib/locale/lang/zh-cn'
ElementPlus.useLang = (app, ref, locale) => {
    const template = (str, option) => {
        if (!str || !option) return str
        return str.replace(/\{(\w+)\}/g, (_, key) => {
            return option[key]
        })
    };

    // 注入全局属性,子组件都能通过inject获取
    app.provide('ElLocaleInjection', {
        lang: ref(locale.name),
        locale: ref(locale),
        t: (...args) => {
            const [path, option] = args;
            let value;
            const array = path.split('.');
            let current = locale;
            for (let i = 0, j = array.length; i < j; i++) {
                const property = array[i];
                value = current[property];
                if (i === j - 1) return template(value, option);
                if (!value) return '';
                current = value
            }
        },
    })
};
const app = createApp(App);
ElementPlus.useLang(app, ref, zhLocale);

app.use(store).use(router).use(ElementPlus).mount('#app');