<!--=========================TEMPLATE=========================-->
<template>
  <div>
    <div class="container">
      <div class="content">
        <div class="field">
          <div class="level">
            <label class="checkbox">
              <input id="errorCheckbox" type="checkbox" v-model="errorChecked"/>
              {{msg('showErrorsCheckboxLabel')}}
            </label>
            <label class="checkbox">
              <input id="warnCheckbox" type="checkbox" v-model="warnChecked"/>
              {{msg('showWarningsCheckboxLabel')}}
            </label>
            <label class="checkbox">
              <input id="updateCheckbox" type="checkbox" v-model="updateChecked"/>
              {{msg('showUpdatesCheckboxLabel')}}
            </label>
            <label class="checkbox">
              <input id="infoCheckbox" type="checkbox" v-model="infoChecked"/>
              {{msg('showInfosCheckboxLabel')}}
            </label>
            <label class="checkbox">
              <input id="debugCheckbox" type="checkbox" v-model="debugChecked"/>
              {{msg('showDebugsCheckboxLabel')}}
            </label>
            <div class="field has-addons">
              <div class="control level-right">
                <a class="button is-danger is-outlined" @click="clearLogs">
                  <span class="button-text">{{msg('clearLogsButtonText')}}</span>
                  <b-icon icon="delete"></b-icon>
                </a>
              </div>
            </div>
          </div>
          <ul id="logbox">
            <li v-for="log in filteredLogList">
              <span style="color:blue; font-size: small;">({{log.time}})</span>
              <span :class="setLogClass(log.type)"
                    style="text-transform: uppercase">{{log.type}}:</span>
              <span :class="setLogClass(log.type)">{{logCodeToMsg(log.code, log.params)}}</span>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>
<!--=======================TEMPLATE END=======================-->

<!--==========================SCRIPT==========================-->
<script>
  import axios from 'axios'
  import {mapGetters} from 'vuex'
  import {messageUtils} from '../../mixins/messageUtils'
  import {logsUtils} from '../../mixins/logsUtils'

  export default {
    name: "logsPage",
    data() {
      return {
        errorChecked: true,
        warnChecked: true,
        updateChecked: true,
        infoChecked: true,
        debugChecked: false,
      }
    },
    mixins: [messageUtils, logsUtils],
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
        logList: 'logList'
      }),
      filteredLogList() {
        return this.logList.filter(log => this.isTypeEnabled(log.type)).sort((a,b) => {
          return new Date(b.time) - new Date(a.time);
        }).slice(1,2500);
      }
    }
  }
</script>
<!--=========================SCRIPT END========================-->

<!--===========================STYLE===========================-->
<style scoped>
  .container {
    width: 100%;
  }

  .checkbox {
    color: #0F1D38;
    font-size: 14px;
    font-weight: 700;
  }

  .errorLog {
    color: #ff413e;
    font-weight: bold;
    font-size: small;
  }

  .warnLog {
    color: #ffba41;
    font-weight: bold;
    font-size: small;
  }

  .updateLog {
    color: #64ff17;
    font-weight: bold;
    font-size: small;
  }

  .infoLog {
    color: #1b3aff;
    font-weight: bold;
    font-size: small;
  }

  .debugLog {
    color: grey;
    font-weight: bold;
    font-size: small;
  }

  .button-text {
    font-size: 14px;
    font-weight: 700;
  }
</style>
<!--=========================STYLE END=========================-->
