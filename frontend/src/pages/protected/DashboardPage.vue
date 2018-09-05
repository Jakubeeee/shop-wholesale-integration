<!--=========================TEMPLATE=========================-->
<template>
  <div>
    <section class="hero is-info welcome is-small">
      <div class="hero-body">
        <div class="container">
          <h1 class="title">
            {{msg('heroText', [activeUser])}}
          </h1>
        </div>
      </div>
    </section>
    <section class="info-tiles">
      <div class="tile is-ancestor has-text-centered">
        <template v-for="infoTile in infoTiles">
          <info-tile :content="infoTile.value" :subtitle="msg(infoTile.title)"></info-tile>
        </template>
      </div>
    </section>
    <dashboard-card :title="msg('operationsCardTitle')">
      <template slot="content">
        <table class="table is-fullwidth is-striped">
          <tbody>
          <tr>
            <td width="15%">
              <button @click="manualUpdate" :class="setUpdateButtonClass()">{{msg("manualUpdateButtonText")}}</button>
            </td>
            <td width="85%" style="vertical-align:middle">
              <progress class="progress is-success" :value="currentProgress" :max="maxProgress"></progress>
            </td>
          </tr>
          </tbody>
        </table>
      </template>
    </dashboard-card>
    <dashboard-card :title="msg('lastLogsCardTitle')">
      <template slot="content">
        <table class="table is-fullwidth is-striped">
          <tbody>
          <tr v-for="log in filteredLogList">
            <td width="25%" style="color:blue; font-size: small;">{{log.time}}</td>
            <td width="65%" :class="setLogClass(log.type)">{{logCodeToMsg(log.code, log.params)}}</td>
            <td width="10%" :class="setLogClass(log.type)" style="text-transform: uppercase">{{log.type}}</td>
          </tr>
          </tbody>
        </table>
      </template>
      <template slot="footer">
        <a href="#/logs" class="card-footer-item">{{msg('viewAllCardFooter')}}</a>
      </template>
    </dashboard-card>
  </div>
</template>

<!--=======================TEMPLATE END=======================-->

<!--==========================SCRIPT==========================-->
<script>
  import axios from 'axios'
  import {mapGetters} from 'vuex'
  import InfoTile from '../../components/InfoTile.vue'
  import DashboardCard from '../../components/DashboardCard.vue'
  import {messageUtils} from '../../mixins/messageUtils'
  import {logsUtils} from '../../mixins/logsUtils'


  export default {
    name: "dashboardPage",
    components: {
      'info-tile': InfoTile,
      'dashboard-card': DashboardCard
    },
    mixins: [messageUtils, logsUtils],
    data() {
      return {
        infoTiles: [
          {
            value: '-',
            title: 'lastUpdateEndedTile',
            type: 'LAST_UPDATE',
          },
          {
            value: '-',
            title: 'productsUpdatedInLastUpdateTile',
            type: 'PRODUCTS_UPDATED',
          },
          {
            value: '-',
            title: 'nextUpdateStartsTile',
            type: 'NEXT_UPDATE',
          },
          {
            value: '-',
            title: 'updateIntervalTile',
            type: 'UPDATE_INTERVAL',
          }
        ]
      }
    },
    methods: {
      manualUpdate() {
        axios.post('/updateProducts')
      }
      ,
      setUpdateButtonClass() {
        return {
          'button': true,
          'is-success': true,
          'is-loading': this.updating === 1,
        }
      }
      ,
      updateInfoTilesData() {
        axios.get('/additionalInfo').then((response) => {
          Object.keys(response.data).forEach((key) => {
            if (response.data.hasOwnProperty(key)) {
              let infoTileData = this.infoTiles.filter((infoTile) => infoTile.type === response.data[key].type)[0];
              let index = this.infoTiles.indexOf(infoTileData);
              infoTileData.value = response.data[key].information;
              this.infoTiles[index] = infoTileData;
            }
          });
        });
      }
      ,
      setLogClass(type) {
        return {
          'errorLog': type === 'ERROR',
          'warnLog': type === 'WARN',
          'updateLog': type === 'UPDATE',
          'infoLog': type === 'INFO',
          'debugLog': type === 'DEBUG',
        }
      }
    },
    computed: {
      ...mapGetters({
        activeUser: 'activeUser',
        currentProgress: 'currentProgress',
        maxProgress: 'maxProgress',
        updating: 'updating',
        logList: 'logList'
      }),
      filteredLogList() {
        return this.logList.filter(log => log.type !== 'DEBUG').sort((a,b) => {
          return new Date(b.time) - new Date(a.time);
        }).slice(0,25);
      }
    },
    watch: {
      updating(newValue) {
        // update info tiles at the end of product update
        if (newValue === 0)
          this.updateInfoTilesData();
      }
    },
    created() {
      this.updateInfoTilesData();
    }
  }
</script>
<!--=========================SCRIPT END========================-->

<!--===========================STYLE===========================-->
<style scoped>

  .info-tiles {
    margin: 1rem 0;
  }

  .hero.welcome.is-info {
    background: #36D1DC;
    background: -webkit-linear-gradient(to right, #5B86E5, #36D1DC);
    background: linear-gradient(to right, #5B86E5, #36D1DC);
  }

  .hero.welcome .title, .hero.welcome {
    color: hsl(192, 17%, 99%);
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
</style>
<!--=========================STYLE END=========================-->
