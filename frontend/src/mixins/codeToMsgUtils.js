export const codeToMsgUtils = {
  methods: {
    taskCodeToMsg(taskCode, params) {
      switch (taskCode) {
        case 'DUMMY_PRODUCTS_UPDATE_TASK':
          return this.msg('dummyProductsUpdateTaskName', params);
        case 'DUMMY_PRODUCTS_UPDATE_TASK_DESC':
          return this.msg('dummyProductsUpdateTaskDescription', params);
        case 'DUMMY_GENERIC_TASK':
          return this.msg('dummyGenericTaskName', params);
        case 'DUMMY_GENERIC_TASK_DESC':
          return this.msg('dummyGenericTaskDescription', params);
        default:
          return taskCode;
      }
    },
    logCodeToMsg(logCode, params) {
      switch (logCode) {
        case 'TASKEXECSTART':
          params[0] = this.taskCodeToMsg(params[0]);
          return this.msg('startingTaskExecution', params);
        case 'DOWNPRODINFO':
          return this.msg('downloadingProductsInformation', params);
        case 'DOWNPRODAMNT':
          return this.msg('foundProductsAmount', params);
        case 'STARTMATCHPRD':
          return this.msg('productsMatchingStarted', params);
        case 'PRODNOTINDS':
          return this.msg('productNotFoundInTheDataSource', params);
        case 'PROPNOTCHNG':
          params[1] = this.logCodeToMsg(params[1]);
          return this.msg('productPropertyHasNotChange', params);
        case 'PRODUPD':
          let additionalText = '';
          for (let i = 0; i < params.length; i++) {
            if ((i - 1) > 0 && (i - 1) % 3 === 0)
              additionalText += ', {} from \'{}\' to \'{}\'';
            params[i] = this.logCodeToMsg(params[i]);
          }
          return this.msg('productPropertiesHasBeenUpdated', params, additionalText);
        case 'STOCKPROP':
          return this.msg('productStockProperty', params);
        case 'PRICEPROP':
          return this.msg('productPriceProperty', params);
        case 'EANPROP':
          return this.msg('productEanProperty', params);
        case 'UPDSUCCESS':
          return this.msg('updateFinishedSuccessfully', params);
        case 'CONNPROB':
          return this.msg('connectionProblem');
        case 'UNKNOWNPROB':
          return this.msg('unknownProblem');
        case 'TASKALRLAUNCH':
          params[0] = this.taskCodeToMsg(params[0]);
          return this.msg('taskAlreadyLaunched', params);
        case 'PROVALRINUSE':
          params[0] = this.taskCodeToMsg(params[0]);
          return this.msg('taskProviderAlreadyInUse', params);
        case 'TASKEXECSUCC':
          params[0] = this.taskCodeToMsg(params[0]);
          return this.msg('taskExecutionEndedSuccessfully', params);
        case 'TASKEXECABORT':
          params[0] = this.taskCodeToMsg(params[0]);
          return this.msg('taskExecutionAborted', params);
        default:
          return logCode;
      }
    },
    pastTaskExecutionCodeToMsg(pastTaskExecutionCode, params) {
      switch (pastTaskExecutionCode) {
        case 'DS_PRODUCT_AMOUNT':
        case 'UPDS_PRODUCT_AMOUNT':
          return this.msg('dataSourceProductAmountProperty', params);
        case 'PRODUCTS_MATCHED':
          return this.msg('productsMatchedProperty', params);
        case 'NOT_MATCHED_DS_PRODUCTS_AMOUNT':
        case 'NOT_MATCHED_UPDS_PRODUCTS_AMOUNT':
          return this.msg('dataSourceProductsUnmatchedProperty', params);
        default:
          return pastTaskExecutionCode;
      }
    },
    customCodeToMsg(customCode, params) {
      switch (customCode) {
        case 'TASKNOSCHED':
          return this.msg('taskIsNotScheduled', params);
        default:
          return customCode;
      }
    }
  }
};
