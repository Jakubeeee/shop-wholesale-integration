<template>
  <div id="app">
    <navbar/>
    <router-view/>
  </div>
</template>

<script>
  import axios from "axios/index";
  import Navbar from './components/Navbar.vue';

  export default {
    name: 'App',
    components: {Navbar},
    async beforeCreate() {
      this.$i18n.locale = this.$store.getters.language;
      this.$store.dispatch('launchListener', 'NEXT_SCHEDULED_TASKS_EXECUTIONS');
      this.$store.dispatch('launchListener', 'TASKS_LOGS');
      this.$store.dispatch('launchListener', 'TASKS_PROGRESS');
      this.$store.dispatch('launchListener', 'PAST_TASKS_EXECUTIONS');
      let groupedRequests = () => {
        axios.get('/activeUser').then((response) => {
          this.$store.dispatch('registerActiveUser', response.data);
        });
        axios.get('/tasks').then((response) => {
          this.$store.dispatch('registerTasks', response.data);
        });
      };
      await groupedRequests();
      groupedRequests = () => {
        axios.get('/nextScheduledTasksExecutions').then((response) => {
          this.$store.dispatch('registerNextScheduledTasksExecutions', response.data);
        });
        axios.get('/pastTasksExecutions').then((response) => {
          this.$store.dispatch('registerPastTasksExecutions', response.data);
        });
      };
      await groupedRequests();
      groupedRequests = () => {
        axios.get('/tasksProgress').then((response) => {
          this.$store.dispatch('registerTasksProgress', response.data);
        });
        axios.get('/tasksLogs').then((response) => {
          this.$store.dispatch('registerTasksLogs', response.data);
        });
      };
      groupedRequests();
    }
  }
</script>

<style lang="scss">
  @import 'assets/styles/text_styles.scss';
  @import 'assets/styles/components_styles.scss';

  html, body {
    height: 100%;
    background: #ecf0f3;
    font-family: 'Open Sans', serif;
  }

  .snackbar.is-bottom-left {
    align-self: start;
  }

</style>
