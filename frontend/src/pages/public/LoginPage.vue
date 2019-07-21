<template>
  <section class="hero is-success">
    <div class="hero-body">
      <div class="container has-text-centered">
        <div class="column is-4 is-offset-4">
          <h3 class="title big-text bold-text dark-grayed-text">{{ msg('header') }}</h3>
          <div class="box">
            <b-field>
              <b-input size="is-large" type="text" v-model="credentials.username"
                       :placeholder="msg('usernamePlaceholder')" icon="account" autofocus="">
              </b-input>
            </b-field>
            <b-field>
              <b-input size="is-large" type="password" v-model="credentials.password"
                       :placeholder="msg('passwordPlaceholder')" icon="key-variant">
              </b-input>
            </b-field>
            <button @click="login" class="button is-block is-info is-large is-fullwidth">
              <span class="medium-text bold-text">{{ msg('loginButtonText') }}</span>
            </button>
          </div>
          <p class="small-text bold-text dark-grayed-text">
            <a href="#/forgot-my-password">{{ msg('forgotMyPasswordLink') }}</a>
          </p>
        </div>
      </div>
    </div>
    <b-loading :active.sync="isLoading"></b-loading>
  </section>
</template>

<script>
  import {messageUtils} from '../../mixins/messageUtils'
  import {notificationUtils} from '../../mixins/notificationUtils'

  export default {
    name: "loginPage",
    data() {
      return {
        credentials: {
          username: '',
          password: ''
        },
        isLoading: false
      }
    },
    mixins: [messageUtils, notificationUtils],
    methods: {
      login() {
        this.isLoading = true;
        this.$store.dispatch('login', this.credentials).then(() => {
          this.isLoading = false;
          if (!this.$store.getters.authenticated) {
            this.dangerSnackbar(this.msg('invalidUsernameOrPasswordNotification'));
            this.credentials.password = '';
          }
        });
      }
    }
  }
</script>

<style lang="scss" scoped>
  @import '../../assets/styles/text_styles.scss';

  .hero.is-success {
    background: #ecf0f3;
  }

  .box {
    margin-top: 2rem;
  }

</style>
