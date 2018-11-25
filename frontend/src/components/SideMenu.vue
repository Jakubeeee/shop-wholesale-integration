<template>
  <div>
    <aside class="menu">
      <p class="menu-label bold-text grayed-text">
        {{msg('integrationMenuGroup')}}
      </p>
      <ul class="menu-list">
        <li>
          <a href="#/dashboard" :class="{ 'is-active': activePage === 'homePage' }" class="bold-text">
            {{msg('dashboardMenuLink')}}
          </a>
        </li>
        <li>
          <a href="#/tasks" :class="{ 'is-active': activePage === 'tasksPage' }" class="bold-text">
            {{msg('tasksMenuLink')}}
          </a>
        </li>
        <li>
          <a href="#/logs" :class="{ 'is-active': activePage === 'logsPage' }" class="bold-text">
            {{msg('logsMenuLink')}}
          </a>
        </li>
      </ul>
    </aside>
  </div>
</template>

<script>
  import {mapGetters} from 'vuex'
  import {messageUtils} from '../mixins/messageUtils'

  export default {
    name: "sideMenu",
    data() {
      return {}
    },
    mixins: [messageUtils],
    computed: {
      ...mapGetters({
        activePage: 'activePage'
      })
    }
  }
</script>

<style lang="scss" scoped>
  @import '../assets/styles/custom_colors.scss';

  a {
    color: $black_color;
  }

  a:hover, a.is-active {
    background-color: transparent;
    color: $blue-color;
  }

</style>
