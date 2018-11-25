<template>
  <div class="container">
    <div class="level">
      <label class="checkbox">
        <input id="errorCheckbox" type="checkbox" v-model="errorChecked"/>
        <span class="bold-text">{{msg('showErrorsCheckboxLabel')}}</span>
      </label>
      <label class="checkbox">
        <input id="warnCheckbox" type="checkbox" v-model="warnChecked"/>
        <span class="bold-text">{{msg('showWarningsCheckboxLabel')}}</span>
      </label>
      <label class="checkbox">
        <input id="updateCheckbox" type="checkbox" v-model="updateChecked"/>
        <span class="bold-text">{{msg('showUpdatesCheckboxLabel')}}</span>
      </label>
      <label class="checkbox">
        <input id="infoCheckbox" type="checkbox" v-model="infoChecked"/>
        <span class="bold-text">{{msg('showInfosCheckboxLabel')}}</span>
      </label>
      <label class="checkbox">
        <input id="debugCheckbox" type="checkbox" v-model="debugChecked"/>
        <span class="bold-text">{{msg('showDebugsCheckboxLabel')}}</span>
      </label>
    </div>
    <div class="level">
      <b-dropdown v-model="filteredTaskId">
        <a class="button is-info is-outlined" slot="trigger">
          <span class="bold-text">{{msg('showLogsForTheTaskButtonName')}}</span>
          <b-icon icon="menu-down"></b-icon>
        </a>
        <b-dropdown-item :value=0>{{msg('showLogsForAllTasksDropdownItem')}}</b-dropdown-item>
        <template v-for="task in taskList">
          <b-dropdown-item :value=task.id>{{taskCodeToMsg(task.code, [task.id])}}</b-dropdown-item>
        </template>
      </b-dropdown>
      <a class="button is-danger is-outlined" @click="clearLogs">
        <span class="bold-text">{{msg('clearLogsButtonText')}}</span>
        <b-icon icon="delete"></b-icon>
      </a>
    </div>
    <div class="level">
      <span class="bold-text blue-text">{{msg('taskForWhichLogsAreFiltered', [filteredTaskName])}}</span>
    </div>
    <b-pagination id="pagination" :total="logsAmount" :current.sync="currentPage"
                  :per-page="100"></b-pagination>
    <ul>
      <li v-for="log in slicedFilteredLogList">
        <span class="casual-text semi-bold-text blue-text">({{truncateToSeconds(log.time)}})</span>
        <span :class="setLogClass(log.type)">{{log.type}}:</span>
        <span :class="setLogClass(log.type)"> (ID: {{log.taskId}})</span>
        <span :class="setLogClass(log.type)">{{logCodeToMsg(log.code, log.params)}}</span>
      </li>
    </ul>
    <b-pagination id="pagination" :total="logsAmount" :current.sync="currentPage"
                  :per-page="100"></b-pagination>
  </div>
</template>

<script>
  import axios from 'axios'
  import {mapGetters} from 'vuex'
  import {dateTimeUtils} from '../../mixins/dateTimeUtils'
  import {messageUtils} from '../../mixins/messageUtils'
  import {codeToMsgUtils} from '../../mixins/codeToMsgUtils'

  export default {
    name: "logsPage",
    mixins: [dateTimeUtils, messageUtils, codeToMsgUtils],
    methods: {
      setLogClass(type) {
        return {
          'errorLog': type === 'ERROR',
          'warnLog': type === 'WARN',
          'updateLog': type === 'UPDATE',
          'infoLog': type === 'INFO',
          'debugLog': type === 'DEBUG',
        }
      },
      isTypeEnabled(type) {
        if (type === 'ERROR') {
          return this.errorChecked
        }
        else if (type === 'WARN') {
          return this.warnChecked
        }
        else if (type === 'UPDATE') {
          return this.updateChecked
        }
        else if (type === 'INFO') {
          return this.infoChecked
        }
        else if (type === 'DEBUG') {
          return this.debugChecked
        }
      },
      clearLogs() {
        axios.post('clearLogs');
      }
    },
    computed: {
      ...mapGetters({
        taskList: 'taskList',
        taskLogList: 'taskLogList',
        filterData: 'logsPageFilterData'
      }),
      filteredTaskName() {
        if (this.filteredTaskId === 0)
          return this.msg('showLogsForAllTasksDropdownItem');
        let filteredTask = this.taskList.filter((task) => task.id === this.filteredTaskId)[0];
        return this.taskCodeToMsg(filteredTask.code, [filteredTask.id]);
      },
      logsAmount() {
        return this.filteredLogList.length;
      },
      slicedFilteredLogList() {
        return this.filteredLogList.slice(this.currentPage * 100 - 100, this.currentPage * 100 - 1);
      },
      filteredLogList() {
        return this.taskLogList.filter(log => this.isTypeEnabled(log.type))
          .filter(log => this.filteredTaskId === 0 || log.taskId === this.filteredTaskId)
          .sort((a, b) => {
            return new Date(b.time) - new Date(a.time);
          });
      },
      errorChecked: {
        set(errorChecked) {
          this.$store.dispatch('updateLogsPageFilterData', {'errorChecked': errorChecked})
        },
        get() {
          return this.filterData.errorChecked;
        }
      },
      warnChecked: {
        set(warnChecked) {
          this.$store.dispatch('updateLogsPageFilterData', {'warnChecked': warnChecked})
        },
        get() {
          return this.filterData.warnChecked;
        }
      },
      updateChecked: {
        set(updateChecked) {
          this.$store.dispatch('updateLogsPageFilterData', {'updateChecked': updateChecked})
        },
        get() {
          return this.filterData.updateChecked;
        }
      },
      infoChecked: {
        set(infoChecked) {
          this.$store.dispatch('updateLogsPageFilterData', {'infoChecked': infoChecked})
        },
        get() {
          return this.filterData.infoChecked;
        }
      },
      debugChecked: {
        set(debugChecked) {
          this.$store.dispatch('updateLogsPageFilterData', {'debugChecked': debugChecked})
        },
        get() {
          return this.filterData.debugChecked;
        }
      },
      filteredTaskId: {
        set(filteredTaskId) {
          this.$store.dispatch('updateLogsPageFilterData', {'filteredTaskId': filteredTaskId})
        },
        get() {
          return this.filterData.filteredTaskId;
        }
      },
      currentPage: {
        set(currentPage) {
          this.$store.dispatch('updateLogsPageFilterData', {'currentPage': currentPage})
        },
        get() {
          return this.filterData.currentPage;
        }
      }
    }
  }
</script>

<style lang="scss" scoped>
  @import '../../assets/styles/logs_styles.scss';

  .container {
    width: 100%;
  }

  #pagination {
    margin: 1rem 0;
  }

</style>
