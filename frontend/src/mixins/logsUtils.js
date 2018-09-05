export const logsUtils = {
  methods: {
    logCodeToMsg(logCode, params) {
      switch(logCode){
        case 'PREPUPD':
          return this.msg('preparingForUpdate');
        case 'LOOKFORPROD':
          return this.msg('searchingForProducts');
        case 'THREADERR':
          return this.msg('threadErrorOccurred');
        case 'PRODNOTINSHP':
          return this.msg('productNotFoundInTheShop', params);
        case 'STCKNOTCHNG':
          return this.msg('productStockHasNotChange', params);
        case 'PRODUPD':
          return this.msg('productStockHasBeenUpdated', params);
        case 'UPDSUCCESS':
          return this.msg('updateFinishedSuccessfully', params);
        case 'AUTHTOKENEXPRD':
          return this.msg('authTokenExpired');
        case 'CONNPROB':
          return this.msg('connectionProblem');
      }
    }
  }
};
