<template>
  <section class="hero is-success">
    <div class="hero-body">
      <div class="container has-text-centered">
        <div class="column is-4 is-offset-4">
          <h3 class="title title big-text bold-text dark-grayed-text">{{ msg('header') }}</h3>
          <div class="box">
            <b-field :label="msg('passwordLabel')" :type="$v.credentials.password.$error ? 'is-danger' : 'text'">
              <b-input size="is-large" type="password" v-model="credentials.password" icon="key-variant" autofocus=""
                       @input="$v.credentials.passwordConfirm.$touch(); $v.credentials.password.$touch()">
              </b-input>
            </b-field>
            <b-field class="help is-danger">
              <span v-if="!$v.credentials.password.validPassword && $v.credentials.password.$dirty">
                {{ msg('passwordRegexUnmatchedError',
                [$v.credentials.password.$params.validPassword.min,
                $v.credentials.password.$params.validPassword.max]) }}
              </span>
            </b-field>
            <b-field :label="msg('passwordConfirmLabel')"
                     :type="$v.credentials.passwordConfirm.$error ? 'is-danger' : 'text'">
              <b-input size="is-large" type="password" v-model="credentials.passwordConfirm" icon="key-variant"
                       @input="$v.credentials.passwordConfirm.$touch(); $v.credentials.password.$touch()">
              </b-input>
            </b-field>
            <b-field class="help is-danger">
              <span v-if="!$v.credentials.passwordConfirm.required && $v.credentials.passwordConfirm.$dirty">
                {{ msg('fieldRequiredError') }}
              </span>
              <span v-else-if="!$v.credentials.passwordConfirm.sameAsPassword && $v.credentials.passwordConfirm.$dirty">
                {{ msg('passwordDifferenceError') }}
              </span>
            </b-field>
            <button @click="changePassword" class="button is-block is-info is-large is-fullwidth">
              <span class="medium-text bold-text">{{ msg('changePasswordButtonText') }}</span>
            </button>
          </div>
        </div>
      </div>
    </div>
    <b-loading :active.sync="isLoading"></b-loading>
  </section>
</template>

<script>
  import axios from 'axios'
  import {messageUtils} from '../../mixins/messageUtils'
  import {required, sameAs} from 'vuelidate/lib/validators'
  import validPassword from '../../validation/validPassword'
  import {validationUtils} from '../../mixins/validationUtils'
  import {notificationUtils} from '../../mixins/notificationUtils'
  import {mapGetters} from 'vuex'

  export default {
    name: "changePasswordPage",
    data() {
      return {
        credentials: {
          userId: '',
          resetToken: '',
          password: '',
          passwordConfirm: ''
        },
        isLoading: false
      }
    },
    mixins: [messageUtils, validationUtils, notificationUtils],
    methods: {
      changePassword() {
        if (this.formInvalid()) {
          this.dangerSnackbar(this.msg('invalidFormNotification'));
          this.credentials.password = '';
          this.credentials.passwordConfirm = '';
          return
        }
        if (this.cooldownActive) {
          this.dangerSnackbar(this.msg('changeCooldownActiveNotification'));
          return;
        }
        this.isLoading = true;
        axios('/changePassword', {
          method: "post",
          data: JSON.stringify(this.credentials),
          withCredentials: true,
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
          },
        }).then(() => {
          this.isLoading = false;
          this.successSnackbar(this.msg('passwordChangedNotification'), 6000);
          this.$store.dispatch('beginPasswordChangeCooldown');
          this.$router.push('/login');
        }).catch(() => {
          this.isLoading = false;
          this.dangerSnackbar(this.msg('changePasswordErrorNotification'));
          this.credentials.password = '';
          this.credentials.passwordConfirm = '';
        })
      }
    },
    computed: {
      ...mapGetters({
        cooldownActive: 'changePasswordCooldownActive'
      }),
    },
    validations: {
      credentials: {
        password: {
          validPassword: validPassword(8, 25)
        },
        passwordConfirm: {
          required,
          sameAsPassword: sameAs('password')
        }
      }
    },
    created() {
      this.credentials.userId = this.$route.query.id;
      this.credentials.resetToken = this.$route.query.token;
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
