<template>
  <section>
    <template v-for="task in taskList">
      <card :title="taskCodeToMsg(task.code, [task.id])" collapsible="true">
        <template slot="content">
          <table id="progress-table" class="table">
            <tr>
              <td class="task-progress-cell casual-text semi-bold-text" width="5%">
                {{getCurrentProgressInPercent(task.id)}}%
              </td>
              <td class="task-progress-cell" width="95%" colspan="2">
                <progress class="progress is-success" :value="getProgressTracker(task.id).currentProgress"
                          :max="getProgressTracker(task.id).maxProgress">
                </progress>
              </td>
            </tr>
          </table>
          <div class="columns column-container">
            <div id="left-side-column" class="column is-8">
              <table class="table">
                <tr>
                  <td class="task-property-cell casual-text semi-bold-text">{{msg('taskModeProperty')}}: {{task.mode}}
                  </td>
                </tr>
                <tr>
                  <td class="task-property-cell casual-text semi-bold-text">{{msg('taskIntervalProperty')}}:
                    {{millisToMinutes(task.intervalInMillis)}} min
                  </td>
                </tr>
                <tr>
                  <td class="task-property-cell casual-text semi-bold-text">
                    {{msg('taskNextScheduledLaunchProperty')}}:
                    {{customCodeToMsg(nextScheduledTasksExecutionsMap[task.id])}}
                  </td>
                </tr>
                <tr>
                  <td class="task-property-cell casual-text semi-bold-text">{{msg('taskDescriptionProperty')}}:
                    {{taskCodeToMsg(task.code + '_DESC', [task.id])}}
                  </td>
                </tr>
              </table>
            </div>
            <div id="right-side-column" class="column is-4">
              <table id="button-table" class="table">
                <tr>
                  <td class="task-button-cell">
                    <button @click="manualLaunch(task.id)"
                            :class="setLaunchButtonClass(task.id)">
                      <span class="bold-text">{{msg('manualLaunchButtonText')}}</span>
                    </button>
                  </td>
                </tr>
                <tr>
                  <td class="task-button-cell">
                    <button class="button is-info task-button" @click="goToFilteredLogsPage(task.id)">
                      <span class="bold-text">{{msg('goToLogsButtonText')}}</span>
                    </button>
                  </td>
                </tr>
              </table>
            </div>
          </div>
        </template>
        <template slot="footer">
          <div class="columns column-container">
            <div class="column is-12">
              <card :title="msg('showLatestExecutionsCardTitle')"
                    :collapsedTitle="msg('hideLatestExecutionsCardTitle')" collapsible="true">
                <template slot="content">
                  <table class="table is-fullwidth is-striped">
                    <template
                      v-if="pastTasksExecutionsList.filter(pastTaskExecution => pastTaskExecution.taskId === task.id).length === 0">
                      <tr>
                        <td>
                          <span>{{msg('noDataCardContent')}}</span>
                        </td>
                      </tr>
                    </template>
                    <template v-else>
                      <th>{{msg('taskExecutionFinishTimeProperty')}}</th>
                      <th>{{msg('taskAdditionalInformationProperty')}}</th>
                      <tr v-for="pastTaskExecution in pastTasksExecutionsList"
                          v-if="pastTaskExecution.taskId === task.id" class="past-executions-row">
                        <td width="40%" class="casual-text semi-bold-text">{{pastTaskExecution.executionFinishTime}}
                        </td>
                        <td width="60%" class="casual-text semi-bold-text">
                          <span v-for="(value, key) in sortObject(pastTaskExecution.params)">
                            {{pastTaskExecutionCodeToMsg(key, [...value])}}<br/></span>
                        </td>
                      </tr>
                    </template>
                  </table>
                </template>
              </card>
            </div>
          </div>
        </template>
      </card>
    </template>

  </section>
</template>

<script>
  import axios from 'axios'
  import {mapGetters} from 'vuex'
  import {langUtils} from '../../mixins/langUtils'
  import {dateTimeUtils} from '../../mixins/dateTimeUtils'
  import {messageUtils} from '../../mixins/messageUtils'
  import {codeToMsgUtils} from '../../mixins/codeToMsgUtils'
  import Card from '../../components/Card.vue'

  export default {
    name: "tasksPage",
    components: {
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
          'task-button': true,
          'is-loading': taskActive === true
        }
      },
      getCurrentProgressInPercent(taskId) {
        let progressTracker = this.getProgressTracker(taskId);
        let currentProgress = progressTracker.currentProgress;
        let maxProgress = progressTracker.maxProgress;
        return (currentProgress / maxProgress * 100).toFixed(2);
      },
      getProgressTracker(taskId) {
        if (taskId in this.taskProgressMap) {
          return this.taskProgressMap[taskId];
        }
        else
          return {
            active: false,
            currentProgress: 0,
            maxProgress: 1
          };
      },
      goToFilteredLogsPage(taskId) {
        this.$store.dispatch('updateLogsPageFilterData', {'filteredTaskId': taskId})
        this.$router.push('/logs');
      }
    },
    computed: {
      ...mapGetters({
        taskList: 'taskList',
        nextScheduledTasksExecutionsMap: 'nextScheduledTasksExecutionsMap',
        taskProgressMap: 'taskProgressMap',
        pastTasksExecutionsList: 'pastTasksExecutionsList'
      })
    }
  }
</script>

<style lang="scss" scoped>
  @import '../../assets/styles/custom_colors.scss';

  $border-style: 0.065rem solid $light-grey-color;

  table tr td:not(.past-executions-cell) {
    border: none;
  }

  #progress-table {
    margin-bottom: 0.5rem;
  }

  .task-progress-cell {
    padding: 0.3rem 0.5rem;
  }

  .column-container {
    margin: 0;
  }

  .column, .nested-cell {
    padding: 0;
  }

  #left-side-column {
    margin: 0;
    border-top: $border-style;
    border-right: $border-style;
    padding: 0;
  }

  #right-side-column {
    margin: 0;
    border-top: $border-style;
    padding: 0;
  }

  .task-property-cell {
    padding: 0.3rem 0.5rem;
  }

  #button-table {
    margin: 0.5rem 0;
  }

  .task-button-cell {
    padding: 0.5rem 0;
    text-align: center;
  }

  .task-button {
    width: 60%;
  }

  .past-executions-row:not(:last-child) {
    border-bottom: $border-style;
  }

</style>
