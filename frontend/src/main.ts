import { createApp } from 'vue'
import './style.css'
import PrimeVue from 'primevue/config';
import Aura from '@primeuix/themes/aura';
import ToastService from 'primevue/toastservice';
import App from './App.vue'
import router from './router';
import 'primeicons/primeicons.css'
import Tooltip from 'primevue/tooltip';

createApp(App)
    .use(PrimeVue, {
        theme: {
            preset: Aura
        }
    })
    .use(router)
    .use(ToastService)
    .directive('tooltip', Tooltip)
    .mount('#app')