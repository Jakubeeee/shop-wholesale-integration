import Vue from 'vue'
import Router from 'vue-router'
import store from '../vuex/store'
import LoginPage from '@/pages/public/LoginPage.vue'
import ForgotMyPasswordPage from '@/pages/public/ForgotMyPasswordPage.vue'
import ChangePasswordPage from '@/pages/public/ChangePasswordPage.vue'
import ContentParentPage from '@/pages/protected/ContentParentPage.vue'
import DashboardPage from '@/pages/protected/DashboardPage.vue'
import LogsPage from '@/pages/protected/LogsPage.vue'
import TasksPage from '@/pages/protected/TasksPage.vue'

Vue.use(Router);

const router = new Router({
  routes: [
    {
      path: '/login',
      name: 'loginPage',
      component: LoginPage,
      meta: {protected: false}
    },
    {
      path: '/forgot-my-password',
      name: 'forgotMyPasswordPage',
      component: ForgotMyPasswordPage,
      meta: {protected: false}
    },
    {
      path: '/change-password',
      name: 'changePasswordPage',
      component: ChangePasswordPage,
      meta: {protected: false}
    },
    {
      path: '/',
      name: 'contentParentPage',
      component: ContentParentPage,
      meta: {protected: true},
      children: [
        {
          path: '',
          redirect: '/dashboard',
        },
        {
          path: '/dashboard',
          name: 'dashboardPage',
          component: DashboardPage,
          meta: {protected: true}
        },
        {
          path: '/logs',
          name: 'logsPage',
          component: LogsPage,
          meta: {protected: true}
        },
        {
          path: '/tasks',
          name: 'tasksPage',
          component: TasksPage,
          meta: {protected: true}
        }
      ]
    }
  ]
});

router.beforeEach((to, from, next) => {
  store.dispatch('checkAuthenticated').then(() => {
    if (to.meta.protected) {
      if (store.getters.authenticated)
        next();
      else
        next('/login');
    }
    else {
      if (store.getters.authenticated)
        next('/dashboard');
      else
        next();
    }
  })
});

router.afterEach((to) => {
  store.dispatch('registerPageChange', to.name);
});

export default router;
