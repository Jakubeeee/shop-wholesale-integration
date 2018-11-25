<template>
  <div class="section card-parent">
    <template v-if="collapsible === 'true'">
      <b-collapse :open="false" class="card component">
        <div slot="trigger" slot-scope="props" class="card-header">
          <p class="card-header-title small-text semi-bold-text grayed-text">
            <template v-if="!props.open || collapsedTitle === ''">{{title}}</template>
            <template v-else>{{collapsedTitle}}</template>
          </p>
          <a class="card-header-icon">
            <b-icon
              :icon="props.open ? 'menu-down' : 'menu-up'">
            </b-icon>
          </a>
        </div>
        <div class="card-table" :style="cardTableStyle">
          <div class="content casual-text semi-bold-text">
            <slot name="content"></slot>
          </div>
        </div>
        <footer class="card-footer" style="display:block">
          <slot name="footer"></slot>
        </footer>
      </b-collapse>
    </template>
    <template v-else>
      <div class="card events-card component">
        <header class="card-header">
          <p class="card-header-title small-text semi-bold-text grayed-text">
            {{title}}
          </p>
        </header>
        <div class="card-table" :style="cardTableStyle">
          <div class="content casual-text semi-bold-text">
            <slot name="content"></slot>
          </div>
        </div>
        <footer class="card-footer" style="display:block">
          <slot name="footer"></slot>
        </footer>
      </div>
    </template>
  </div>
</template>

<script>
  export default {
    name: "card",
    props: {
      collapsible: {
        default: 'false',
      },
      scrollable: {
        default: 'false'
      },
      height: {
        default: null,
        type: Number
      },
      title: {
        default: ''
      },
      collapsedTitle: {
        default: ''
      }
    },
    data() {
      return {
        cardTableStyle: {
          'min-height': this.height === null ? 'auto' : this.height + 'rem',
          'max-height': this.height === null ? 'auto' : this.height + 'rem',
          'overflow-y': this.scrollable === 'true' ? 'scroll' : 'unset'
        }
      }
    },
  }
</script>

<style lang="scss" scoped>
  @import '../assets/styles/custom_colors.scss';

  .card-parent {
    margin: 0.75rem 0;
    padding: 0;

    .card-parent {
      margin: 0;
    }
  }

  .component .component {
    margin: 0.3rem 0;
    border: none;
    box-shadow: none;
    border-radius: 0;
  }

  .card-header {
    box-shadow: none;
  }

  .card-header-title {
    margin: 0;
    padding: 0.5rem 0.75rem;
  }

  .card-footer .card-header-title {
    padding: 0 0.75rem;
  }

  .card-footer:empty {
    border: none;
  }

</style>
