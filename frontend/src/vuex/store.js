import Vue from 'vue'
import Vuex from 'vuex'
import axios from "axios/index";
import SockJS from 'sockjs-client'
import Stomp from 'webstomp-client'
import router from '../router/index'
import i18n from '../language/i18n'
import createPersistedState from 'vuex-persistedstate'

Vue.use(Vuex);

const state = {
  authenticated: false,
  activeUser: '',
  activePage: '',
  language: 'en',
  resetEmailCooldownActive: false,
  changePasswordCooldownActive: false,
  taskList: [],
  nextScheduledTasksExecutionsMap: {},
  taskLogList: [],
  taskProgressMap: {},
  pastTasksExecutionsList: [],
  logsPageFilterData: {
    errorChecked: true,
    warnChecked: true,
    updateChecked: true,
    infoChecked: true,
    debugChecked: false,
    filteredTaskId: 0,
    currentPage: 1
  }
};

const getters = {
  authenticated: state => state.authenticated,
  activeUser: state => state.activeUser,
  activePage: state => state.activePage,
  language: state => state.language,
  resetEmailCooldownActive: state => state.resetEmailCooldownActive,
  changePasswordCooldownActive: state => state.changePasswordCooldownActive,
  taskList: state => state.taskList,
  nextScheduledTasksExecutionsMap: state => state.nextScheduledTasksExecutionsMap,
  taskLogList: state => state.taskLogList,
  taskProgressMap: state => state.taskProgressMap,
  pastTasksExecutionsList: state => state.pastTasksExecutionsList,
  logsPageFilterData: state => state.logsPageFilterData
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
  SET_TASK_LIST(state, newValue) {
    state.taskList = newValue;
  },
  ADD_TO_NEXT_SCHEDULED_TASK_EXECUTIONS_MAP(state, payload) {
    state.nextScheduledTasksExecutionsMap[Object.keys(payload)[0]] = Object.values(payload)[0];
  },
  SET_NEXT_SCHEDULED_TASK_EXECUTIONS_MAP(state, newValue) {
    state.nextScheduledTasksExecutionsMap = newValue;
  },
  ADD_TO_TASK_LOGS_LIST(state, newValue) {
    state.taskLogList.push(...newValue);
  },
  SET_TASK_LOGS_LIST(state, newValue) {
    state.taskLogList = newValue;
  },
  SET_TASK_PROGRESS_MAP(state, newValue) {
    state.taskProgressMap = newValue;
  },
  ADD_TO_PAST_TASK_EXECUTIONS_LIST(state, newValue) {
    state.pastTasksExecutionsList.push(...newValue);
  },
  SET_PAST_TASK_EXECUTIONS_LIST(state, newValue) {
    state.pastTasksExecutionsList = newValue;
  },
  UPDATE_LOGS_PAGE_FILTER_DATA_MAP(state, payload) {
    state.logsPageFilterData[Object.keys(payload)[0]] = Object.values(payload)[0];
  },
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
  registerActiveUser: (context, activeUser) => {
    context.commit('SET_ACTIVE_USER', activeUser);
  },
  registerPageChange: (context, pageName) => {
    context.commit('SET_ACTIVE_PAGE', pageName);
  },
  changeLanguage: (context, locale) => {
    context.commit('SET_LANGUAGE', locale);
    i18n.locale = locale;
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
  registerTasks: (context, taskList) => {
    context.commit('SET_TASK_LIST', taskList);
  },
  registerNextScheduledTasksExecutions: (context, nextScheduledTasksExecutionsMap) => {
    context.commit('SET_NEXT_SCHEDULED_TASK_EXECUTIONS_MAP', nextScheduledTasksExecutionsMap);
  },
  registerTasksLogs: (context, taskLogsList) => {
    context.commit('SET_TASK_LOGS_LIST', taskLogsList);
  },
  registerTasksProgress: (context, taskProgressMap) => {
    context.commit('SET_TASK_PROGRESS_MAP', taskProgressMap);
  },
  registerPastTasksExecutions: (context, pastTasksExecutionsList) => {
    context.commit('SET_PAST_TASK_EXECUTIONS_LIST', pastTasksExecutionsList);
  },
  launchListener: (context, listenerCode) => {
    let stompClient = Stomp.over(new SockJS('/websocket-endpoint'));
    stompClient.debug = () => {
    };
    let listenerConfig = helpers.getListenerConfig(listenerCode);
    stompClient.connect({}, () => {
      stompClient.subscribe('/topic/' + listenerConfig.backendEndpoint, (message) => {
        let body = JSON.parse(message.body);
        if (body.action === 'SWAP_STATE')
          context.commit(listenerConfig.setListMutationName, body.payload);
        else if (body.action === 'ADD_TO_STATE')
          context.commit(listenerConfig.addToListMutationName, body.payload);
      })
    });
  },
  updateLogsPageFilterData: (context, payload) => {
    context.commit('UPDATE_LOGS_PAGE_FILTER_DATA_MAP', payload);
  },
};

const helpers = {
  getListenerConfig: listenerCode => {
    switch (listenerCode) {
      case 'NEXT_SCHEDULED_TASKS_EXECUTIONS':
        return {
          backendEndpoint: 'nextScheduledTasksExecutions',
          setListMutationName: 'SET_NEXT_SCHEDULED_TASK_EXECUTIONS_MAP',
          addToListMutationName: 'ADD_TO_NEXT_SCHEDULED_TASK_EXECUTIONS_MAP'
        };
      case 'TASKS_LOGS':
        return {
          backendEndpoint: 'tasksLogs',
          setListMutationName: 'SET_TASK_LOGS_LIST',
          addToListMutationName: 'ADD_TO_TASK_LOGS_LIST'
        };
      case 'TASKS_PROGRESS':
        return {
          backendEndpoint: 'tasksProgress',
          setListMutationName: 'SET_TASK_PROGRESS_MAP',
          addToListMutationName: ''
        };
      case 'PAST_TASKS_EXECUTIONS':
        return {
          backendEndpoint: 'pastTasksExecutions',
          setListMutationName: 'SET_PAST_TASK_EXECUTIONS_LIST',
          addToListMutationName: 'ADD_TO_PAST_TASK_EXECUTIONS_LIST'
        };
      default:
        return {};
    }
  }
};

const store = new Vuex.Store({
  state,
  getters,
  actions,
  mutations,
  plugins: [createPersistedState(
    {paths: ['language']}
  )]
});

export default store;
