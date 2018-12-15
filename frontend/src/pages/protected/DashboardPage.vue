<template>
  <div>
    <section class="hero is-info welcome is-small component">
      <div class="hero-body">
        <div class="container">
          <h1 class="title white-hsl-text">
            {{msg('heroText', [activeUser])}}
          </h1>
        </div>
      </div>
    </section>
    <section class="info-tiles">
      <div class="tile is-ancestor has-text-centered">
        <template v-for="infoTileData in infoTilesData">
          <info-tile :content="infoTileData.value" :subtitle="infoTileData.title"></info-tile>
        </template>
      </div>
    </section>
    <card :title="msg('tasksCardTitle')">
      <template slot="content">
        <table class="table is-fullwidth is-striped">
          <tr v-for="task in taskList">
            <td width="25%">
              {{taskCodeToMsg(task.code, [task.id])}}
            </td>
            <td width="15%" style="vertical-align:middle">
              <button @click="manualLaunch(task.id)" :class="setLaunchButtonClass(task.id)">
                <span class="bold-text">{{msg("manualLaunchButtonText")}}</span>
              </button>
            </td>
            <td width="60%" style="vertical-align:middle">
              <progress class="progress is-success"
                        :value="getProgressTracker(task.id).currentProgress"
                        :max="getProgressTracker(task.id).maxProgress">
              </progress>
            </td>
          </tr>
        </table>
      </template>
      <template slot="footer">
        <a href="#/tasks" class="card-footer-item small-text semi-bold-text">{{msg('viewAllCardFooter')}}</a>
      </template>
    </card>
    <card :title="msg('lastLogsCardTitle')">
      <template slot="content">
        <table class="table is-fullwidth is-striped">
          <tr v-for="log in filteredLogList">
            <td width="25%" class="casual-text semi-bold-text blue-text">{{truncateToSeconds(log.time)}}</td>
            <td width="65%" :class="setLogClass(log.type)">
              (ID: {{log.taskId}}) {{logCodeToMsg(log.code, log.params, log.dynamicParts)}}
            </td>
            <td width="10%" :class="setLogClass(log.type)" style="text-transform: uppercase">{{log.type}}</td>
          </tr>
        </table>
      </template>
      <template slot="footer">
        <a href="#/logs" class="card-footer-item small-text semi-bold-text">{{msg('viewAllCardFooter')}}</a>
      </template>
    </card>
  </div>
</template>

<script>
  import axios from 'axios'
  import {mapGetters} from 'vuex'
  import InfoTile from '../../components/InfoTile.vue'
  import Card from '../../components/Card.vue'
  import {langUtils} from '../../mixins/langUtils'
  import {dateTimeUtils} from '../../mixins/dateTimeUtils'
  import {messageUtils} from '../../mixins/messageUtils'
  import {codeToMsgUtils} from '../../mixins/codeToMsgUtils'

  export default {
    name: "dashboardPage",
    components: {
      'info-tile': InfoTile,
      'card': Card
    },
    mixins: [langUtils, dateTimeUtils, messageUtils, codeToMsgUtils],
    methods: {
      manualLaunch(taskId) {
        axios('/launchTaskManually', {
          method: "post",
          data: taskId,
          headers: {
            'Content-type': 'text/plain'
          }
        })
      },
      setLaunchButtonClass(taskId) {
        let taskActive = this.getProgressTracker(taskId).active;
        return {
          'button': true,
          'is-success': true,
          'is-loading': taskActive === true,
        }
      },
      setLogClass(type) {
        return {
          'errorLog': type === 'ERROR',
          'warnLog': type === 'WARN',
          'updateLog': type === 'UPDATE',
          'infoLog': type === 'INFO',
          'debugLog': type === 'DEBUG',
        }
      },
      getProgressTracker(taskId) {
        if (taskId in this.taskProgressMap)
          return this.taskProgressMap[taskId];
        else
          return {
            active: false,
            currentProgress: 0,
            maxProgress: 1
          };
      }
    },
    computed: {
      ...mapGetters({
        activeUser: 'activeUser',
        taskList: 'taskList',
        nextScheduledTasksExecutionsMap: 'nextScheduledTasksExecutionsMap',
        taskLogList: 'taskLogList',
        taskProgressMap: 'taskProgressMap',
        pastTasksExecutionsList: 'pastTasksExecutionsList'
      }),
      infoTilesData() {
        let activeTasksAmountTileValue = this.taskList.filter((task) => task.scheduledable === true).length;
        let totalLoggedErrorsTileValue = this.taskLogList.filter(log => log.type === 'ERROR').length;
        let lastTaskEndedTileValue = this.nvl(this.dateToString(new Date(Math.max.apply(null, this.pastTasksExecutionsList.map(
          pastTaskExecution => new Date(pastTaskExecution.executionFinishTime))))), '-');
        let datesArray = [];
        Object.keys(this.nextScheduledTasksExecutionsMap).forEach(
          key => {
            if (this.nextScheduledTasksExecutionsMap[key] !== 'TASKNOSCHED')
              datesArray.push(new Date(this.nextScheduledTasksExecutionsMap[key]))
          });
        let nextTaskStartsTileValue = this.dateToString(Math.min.apply(null, datesArray));
        return {
          'activeTasksAmountTile': {
            value: activeTasksAmountTileValue,
            title: this.msg('activeTasksAmountTile'),
          },
          'totalLoggedErrorsTile': {
            value: totalLoggedErrorsTileValue,
            title: this.msg('totalLoggedErrorsTile'),
          },
          'lastTaskEndedTile': {
            value: lastTaskEndedTileValue,
            title: this.msg('lastTaskEndedTile'),
          },
          'nextTaskStartsTile': {
            value: nextTaskStartsTileValue,
            title: this.msg('nextTaskStartsTile'),
          }
        };
      },
      filteredLogList() {
        return this.taskLogList.filter(log => log.type !== 'DEBUG').sort((a, b) => {
          return new Date(b.time) - new Date(a.time);
        }).slice(0, 15);
      }
    }
  }
</script>

<style lang="scss" scoped>
  @import '../../assets/styles/logs_styles.scss';

  .info-tiles {
    margin: 0.75rem 0;
  }

  .hero {
    background: -webkit-linear-gradient(to right, #3273dc, #36d1dc);
    background: linear-gradient(to right, #3273dc, #36d1dc);
  }

</style>
