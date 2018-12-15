export const codeToMsgUtils = {
  data() {
    return {
      taskCodeMapper: require('../language/code-mappers/taskCodeMapper.json'),
      logCodeMapper: require('../language/code-mappers/logCodeMapper.json'),
    }
  },
  methods: {
    taskCodeToMsg(taskCode, paramsValues) {
      let i18nCode = this.taskCodeMapper[taskCode];
      return i18nCode !== undefined ? this.msg(i18nCode, paramsValues) : taskCode;
    },
    logCodeToMsg(logCode, logParams, dynamicParts) {
      let i18nCode = this.logCodeMapper[logCode];
      let preparedParamsValues = this.prepareParamsValues(logParams);
      let additionalText = this.prepareDynamicText(i18nCode, dynamicParts);
      return i18nCode !== undefined ? this.msg(i18nCode, preparedParamsValues, additionalText) : logCode;

    },
    prepareParamsValues(params) {
      if (_.isEmpty(params)) return [];
      let preparedParamsValues = [];
      Object.values(params).forEach(param => {
        preparedParamsValues.push(this.translateParamValue(param))
      });
      return preparedParamsValues;
    },
    translateParamValue(param) {
      let translatedParamValue = param.value;
      if (this.shouldBeTranslated(param)) {
        translatedParamValue = this.taskCodeToMsg(translatedParamValue);
        translatedParamValue = this.logCodeToMsg(translatedParamValue);
      }
      return translatedParamValue;
    },
    shouldBeTranslated(param) {
      return param.type === 'CODE';
    },
    prepareDynamicText(i18nCode, dynamicParts) {
      if (dynamicParts === 0) return '';
      let additionalText = '';
      let dynamicPartI18nCode = i18nCode + 'DynamicPart';
      for (let i = 1; i <= dynamicParts; i++)
        additionalText += this.msg(dynamicPartI18nCode);
      return additionalText
    }
  }
};
