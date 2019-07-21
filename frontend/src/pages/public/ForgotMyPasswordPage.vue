<template>
  <section class="hero is-success">
    <div class="hero-body">
      <div class="container has-text-centered">
        <div class="column is-4 is-offset-4">
          <h3 class="title title big-text bold-text dark-grayed-text">{{ msg('header') }}</h3>
          <div class="box">
            <b-field :type="$v.email.$error ? 'is-danger' : 'text'">
              <b-input size="is-large" type="text" v-model.trim="email" :placeholder="msg('emailPlaceholder')"
                       icon="email" @input="$v.email.$touch()" autofocus="">
              </b-input>
            </b-field>
            <b-field class="help is-danger">
              <span v-if="!$v.email.required && $v.email.$dirty">
                {{ msg('fieldRequiredError') }}
              </span>
              <span v-else-if="!$v.email.email && $v.email.$dirty">
                {{ msg('invalidEmailError') }}
              </span>
            </b-field>
            <p class="control">
              <button @click="sendPasswordResetToken()"
                      class="button is-block is-info is-large is-fullwidth">
                <span class="medium-text bold-text">{{ msg('sendButtonText') }}</span>
              </button>
            </p>
          </div>
          <p class="small-text bold-text dark-grayed-text">
            <a href="#/login">{{ msg('loginLink') }}</a> &nbsp;·&nbsp;
          </p>
        </div>
      </div>
    </div>
    <b-loading :active.sync="isLoading"></b-loading>
  </section>
</template>

<script>
  import axios from 'axios'
  import {messageUtils} from '../../mixins/messageUtils'
  import {email, required} from 'vuelidate/lib/validators'
  import {validationUtils} from '../../mixins/validationUtils'
  import {notificationUtils} from '../../mixins/notificationUtils'
  import {mapGetters} from 'vuex'

  export default {
    name: "forgotMyPasswordPage",
    data() {
      return {
        email: '',
        isLoading: false,
      }
    },
    mixins: [messageUtils, validationUtils, notificationUtils],
    methods: {
      sendPasswordResetToken() {
        if (this.formInvalid()) {
          this.dangerSnackbar(this.msg('invalidEmailNotification'));
          return
        }
        if (this.cooldownActive) {
          this.dangerSnackbar(this.msg('emailCooldownActiveNotification'));
          return;
        }
        this.isLoading = true;
        axios('/forgot-my-password', {
          method: "post",
          data: this.email,
          headers: {
            'Content-type': 'text/plain',
            'Accept-language': this.language
          },
        }).then(() => {
            this.isLoading = false;
            this.successSnackbar(this.msg('emailSentNotification'), 6000);
            this.$store.dispatch('beginResetEmailCooldown');
          }
        ).catch(() => {
            this.isLoading = false;
            this.dangerSnackbar(this.msg('emailNotFoundNotification'));
          }
        );
      }
    },
    computed: {
      ...mapGetters({
        language: 'language',
        cooldownActive: 'resetEmailCooldownActive'
      }),
    },
    validations: {
      email: {
        required,
        email
      }
    }
  }
</script>

<style lang="scss" scoped>

  .hero.is-success {
    background: #ecf0f3;
  }

  .box {
    margin-top: 2rem;
  }

</style>
<!--=========================STYLE END=========================-->
