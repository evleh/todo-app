import {createRouter, createWebHistory} from 'vue-router';

import LoginPage from "./components/LoginPage.vue";
import RegistrationPage from "./components/RegistrationPage.vue";
import HomePage from "./components/HomePage.vue";


const routes = [
    { path: '/', name: 'login',  component: LoginPage, meta: { requiresAuth: false }, },
    { path: '/register', name: 'register', component: RegistrationPage, meta: { requiresAuth: false }, },
    { path: '/home', name: 'home', component: HomePage, meta: { requiresAuth: true }, },
]

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: routes,
});

router.beforeEach((to, from) => {
    const isLoggedIn = localStorage.getItem("accessToken") !== null;
    if (to.meta.requiresAuth && !isLoggedIn) {
        router.push("/");
    }
})

export default router;