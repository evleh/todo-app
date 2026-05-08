import {createRouter, createWebHistory} from 'vue-router';

import LoginPage from "./components/LoginPage.vue";
import RegistrationPage from "./components/RegistrationPage.vue";

const routes = [
    { path: '/', name: 'login',  component: LoginPage },
    { path: '/register', name: 'register', component: RegistrationPage },
]

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: routes,
});

export default router;