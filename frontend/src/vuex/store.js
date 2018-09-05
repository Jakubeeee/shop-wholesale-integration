import Vue from 'vue'
import Vuex from 'vuex'
import axios from "axios/index";
import SockJS from 'sockjs-client'
import Stomp from 'webstomp-client'
import router from '../router/index'
import i18n from '../language/lang'
import createPersistedState from 'vuex-persistedstate'

Vue.use(Vuex);

const state = {
  authenticated: false,
  activeUser: '',
  activePage: '',
  language: 'pl',
  resetEmailCooldownActive: false,
  changePasswordCooldownActive: false,
  logList: [],
  testMessages: [],
  currentProgress: 0,
  maxProgress: 1,
  updating: 0
};

const getters = {
  authenticated: state => state.authenticated,
  activeUser: state => state.activeUser,
  activePage: state => state.activePage,
  language: state => state.language,
  resetEmailCooldownActive: state => state.resetEmailCooldownActive,
  changePasswordCooldownActive: state => state.changePasswordCooldownActive,
  logList: state => state.logList,
  testMessages: state => state.testMessages,
  currentProgress: state => state.currentProgress,
  maxProgress: state => state.maxProgress,
  updating: state => state.updating
};

const mutations = {
  SET_AUTHENTICATED(state, newValue) {
    state.authenticated = newValue;
  },
  SET_ACTIVE_USER(state, newValue) {
    state.activeUser = newValue;
  },
  SET_ACTIVE_PAGE(state, newValue) {
    state.activePage = newValue;
  },
  SET_LANGUAGE(state, newValue) {
    state.language = newValue;
  },
  SET_RESET_EMAIL_COOLDOWN_ACTIVE(state, newValue) {
    state.resetEmailCooldownActive = newValue;
  },
  SET_CHANGE_PASSWORD_COOLDOWN_ACTIVE(state, newValue) {
    state.changePasswordCooldownActive = newValue;
  },
  SET_LOG_LIST(state, newValue) {
    state.logList = newValue;
  },
  SET_TEST_MESSAGES(state, newValue) {
    state.testMessages = newValue;
  },
  SET_CURRENT_PROGRESS(state, newValue) {
    state.currentProgress = newValue;
  },
  SET_MAX_PROGRESS(state, newValue) {
    state.maxProgress = newValue;
  },
  SET_UPDATING(state, newValue) {
    state.updating = newValue;
  }
};

const actions = {
  login: async (context, credentials) => {
    let params = 'password=' + credentials.password + '&username=' + credentials.username;
    await axios('/login', {
      method: "post",
      data: params,
      withCredentials: true
    }).then(() => {
      context.commit('SET_AUTHENTICATED', true);
      router.push({path: '/dashboard'});
      axios.get('/activeUser').then((response) => {
        context.commit('SET_ACTIVE_USER', response.data);
      })
    }).catch(() => {
    })
  },
  logout: async (context) => {
    await axios.post('/logout').then(() => {
        context.commit('SET_AUTHENTICATED', false);
        context.commit('SET_ACTIVE_USER', '');
        router.push({path: '/login'});
      }
    )
  },
  checkAuthenticated: async (context) => {
    await axios.get('/isAuthenticated').then((response) => {
      context.commit('SET_AUTHENTICATED', response.data)
    })
  },
  registerPageChange: (context, pageName) => {
    context.commit('SET_ACTIVE_PAGE', pageName);
  },
  changeLanguage: (context, locale) => {
    context.commit('SET_LANGUAGE', locale);
    i18n.locale = locale;
    axios('/changeLocale', {
      method: "post",
      data: getters.language,
      headers: {
        'Content-type': 'text/plain'
      }
    })
  },
  beginResetEmailCooldown: (context) => {
    context.commit('SET_RESET_EMAIL_COOLDOWN_ACTIVE', true);
    setTimeout(() => {
      context.commit('SET_RESET_EMAIL_COOLDOWN_ACTIVE', false);
    }, 1000 * 60);
  },
  beginPasswordChangeCooldown: (context) => {
    context.commit('SET_CHANGE_PASSWORD_COOLDOWN_ACTIVE', true);
    setTimeout(() => {
      context.commit('SET_CHANGE_PASSWORD_COOLDOWN_ACTIVE', false);
    }, 10000 * 60);
  },
  launchLogsListener: (context) => {
    this.socket = new SockJS('/websocket-endpoint');
    this.stompClient = Stomp.over(this.socket);
    this.stompClient.debug = () => {};
    this.stompClient.connect({}, () => {
      this.stompClient.subscribe('/topic/logs', (message) => {
        context.commit('SET_LOG_LIST', JSON.parse(message.body));
      })
    })
  },
  launchProgressListener: (context) => {
    this.socket = new SockJS('/websocket-endpoint');
    this.stompClient = Stomp.over(this.socket);
    this.stompClient.debug = () => {};
    this.stompClient.connect({}, () => {
      this.stompClient.subscribe('/topic/progress', (message) => {
        context.commit('SET_CURRENT_PROGRESS', JSON.parse(message.body).currentProgress);
        context.commit('SET_MAX_PROGRESS', JSON.parse(message.body).maxProgress);
        context.commit('SET_UPDATING', JSON.parse(message.body).isUpdating);
      })
    })
  }
};

const store = new Vuex.Store({
  state,
  getters,
  actions,
  mutations,
  plugins: [createPersistedState()]
});

export default store;
