import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import HomeView from '../views/HomeView.vue'
import GroupsView from '../views/GroupsView.vue'
import AuditView from '../views/AuditView.vue'
import ProjectsTasksView from '../views/ProjectsTasksView.vue'
import WeeklyReportsView from '../views/WeeklyReportsView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: LoginView },
    { path: '/', component: HomeView },
    { path: '/groups', component: GroupsView },
    { path: '/projects', component: ProjectsTasksView },
    { path: '/weekly', component: WeeklyReportsView },
    { path: '/audit', component: AuditView }
  ]
})

router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  if (!token && to.path !== '/login') {
    return '/login'
  }
})

export default router
