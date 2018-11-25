<template>
  <nav class="navbar is-white">
    <div class="container">
      <div class="navbar-brand">
        <a class="navbar-item brand-text bold-text" href="#/dashboard">
          {{ msg('webAppButtonText') }}
        </a>
        <div class="navbar-burger burger" @click="toggleBurger" :class="{'is-active': burgerActive}"
             data-target="navMenu">
          <span></span>
          <span></span>
          <span></span>
        </div>
      </div>
      <div id="navMenu" class="navbar-menu" :class="{'is-active': burgerActive}">
        <div class="navbar-start"></div>

        <div class="navbar-item" v-if="!authenticated && !burgerActive">
          <a class="button is-success is-outlined navbar-item" href="#/login">
            <span class="bold-text">{{ msg('loginButtonText') }}</span>
            <b-icon icon="login"></b-icon>
          </a>
        </div>

        <div class="navbar-item" v-if="authenticated  && !burgerActive">
          <a class="button is-danger is-outlined navbar-item" @click="logout">
            <span class="bold-text">{{ msg('logoutButtonText') }}</span>
            <b-icon icon="logout"></b-icon>
          </a>
        </div>

        <b-dropdown class="navbar-item" v-if="!burgerActive">
          <a class="button is-info is-outlined" slot="trigger">
            <span class="bold-text">{{ msg('chooseLanguageDropdownText') }}</span>
            <b-icon icon="menu-down"></b-icon>
          </a>
          <b-dropdown-item @click="changeLanguage('pl')">{{ msg('polishLanguageChoice') }}</b-dropdown-item>
          <b-dropdown-item @click="changeLanguage('en')">{{ msg('englishLanguageChoice') }}</b-dropdown-item>
        </b-dropdown>

      </div>
    </div>
  </nav>
</template>

<script>
  import {mapGetters} from 'vuex'
  import {messageUtils} from '../mixins/messageUtils'

  export default {
    name: "navbar",
    data() {
      return {
        burgerActive: false
      }
    },
    mixins: [messageUtils],
    methods: {
      toggleBurger() {
        this.burgerActive = !this.burgerActive;
      },
      changeLanguage(locale) {
        this.$store.dispatch('changeLanguage', locale);
      },
      logout() {
        this.$store.dispatch('logout');
      }
    },
    computed: {
      ...mapGetters({
        authenticated: 'authenticated'
      }),
    }
  }
</script>

<style lang="scss" scoped>
  @import '../assets/styles/custom_colors.scss';

  .navbar {
    border-top: 0.25rem solid $blue-color;
    margin-bottom: 1rem;
  }

  .navbar-item {
    padding: 0.5rem;
  }

</style>
