/**
 * Main configuration file of the vue-i18n internationalization plugin
 */
import Vue from 'vue'
import VueI18n from 'vue-i18n'

Vue.use(VueI18n);

export default new VueI18n({
  locale: '',
  messages: {
    'en': require('./messages_en_importer.js'),
    'pl': require('./messages_pl_importer.js')
  }
});
