import Vue from 'vue'
import Router from 'vue-router'
import Main from '@/components/Main'
import WxChat from '@/components/wxChat'
import Chatroom from '@/components/Chatroom'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Main',
      component: Main
    },
    {
      path: '/wx',
      name: 'WxChat',
      component: WxChat
    },
    {
      path: '/chatroom',
      name: 'Chatroom',
      component: Chatroom
    }
  ]
})
