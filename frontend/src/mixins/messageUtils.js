export const messageUtils = {
  methods: {
    msg(messageCode, params, additionalText) {
      messageCode = this.$options.name + '.' + messageCode;
      let message = this.$t(messageCode);
      if (additionalText)
        message = message + additionalText;
      if (params)
        params.forEach(value => message = message.replace('{}', value));
      return message;
    }
  }
};
