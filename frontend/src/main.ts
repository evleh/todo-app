import { createApp } from 'vue'
import './style.css'
import PrimeVue from 'primevue/config';
import Aura from '@primeuix/themes/aura';
import ToastService from 'primevue/toastservice';
import App from './App.vue'
import router from './router';

createApp(App)
    .use(PrimeVue, {
        theme: {
            preset: Aura
        }
    })
    .use(router)
    .use(ToastService)
    .mount('#app')