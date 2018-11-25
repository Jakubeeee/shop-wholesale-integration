<template>
  <div class="tile is-parent">
    <article class="tile is-child box component">
      <p class="title medium-text bold-text">{{content}}</p>
      <p class="subtitle small-text semi-bold-text grayed-text">{{subtitle}}</p>
    </article>
  </div>
</template>

<script>
  export default {
    name: "info-tile",
    props: ['content', 'subtitle']
  }
</script>

<style lang="scss" scoped>
  @import '../assets/styles/text_styles.scss';

  .is-child {
    text-align: left;
  }

</style>
