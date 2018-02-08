new Vue({
    el: '#app',
    data: {
        logList: [],
        filteredLogList: [],
        errorChecked: true,
        warnChecked: true,
        updateChecked: true,
        infoChecked: true,
        debugChecked: false,
        currentProgress: 0,
        maxProgress: 1,
        isUpdating: 0
    },
    methods: {
        setAsyncTasks() {
            setInterval(() => {
                this.getLogs();
                this.getProgress();
            }, 2000);
        },
        getLogs() {
            axios.get('/logs')
                .then(response => {
                    this.logList = response.data;
                });
            this.filterLogList();
        },
        getProgress() {
            axios.get('/progress')
                .then(response => {
                    this.maxProgress = response.data.maxProgress;
                    this.currentProgress = response.data.currentProgress;
                    this.isUpdating = response.data.isUpdating;
                });
        },
        filterLogList() {
            this.filteredLogList = this.logList.filter(log => {
                return this.isTypeEnabled(log.type);
            })
        },
        isTypeEnabled(type) {
            if (type === 'ERROR') {
                return this.errorChecked
            }
            else if (type === 'WARN') {
                return this.warnChecked
            }
            else if (type === 'UPDATE') {
                return this.updateChecked
            }
            else if (type === 'INFO') {
                return this.infoChecked
            }
            else if (type === 'DEBUG') {
                return this.debugChecked
            }
        },
        //TODO something wrong here
//            getCheckboxStateFromSession() {
//                this.errorChecked = this.nvl(sessionStorage.getItem("errorChecked"), true);
//                this.warnChecked = this.nvl(sessionStorage.getItem("warnChecked"), true);
//                this.updateChecked = this.nvl(sessionStorage.getItem("updateChecked"), true);
//                this.infoChecked = this.nvl(sessionStorage.getItem("infoChecked"), true);
//                this.debugChecked = this.nvl(sessionStorage.getItem("debugChecked"), false);
//            },
        setCheckboxStateInSession() {
            sessionStorage.setItem("errorChecked", this.errorChecked);
            sessionStorage.setItem("warnChecked", this.warnChecked);
            sessionStorage.setItem("updateChecked", this.updateChecked);
            sessionStorage.setItem("infoChecked", this.infoChecked);
            sessionStorage.setItem("debugChecked", this.debugChecked);
        },
        setLogClass(type) {
            return {
                'errorLog': type === 'ERROR',
                'warnLog': type === 'WARN',
                'updateLog': type === 'UPDATE',
                'infoLog': type === 'INFO',
                'debugLog': type === 'DEBUG',
            }
        },
        setUpdateButtonClass() {
            return {
                'button': true,
                'is-success': true,
                'is-loading': this.isUpdating === 1,
            }
        },
        nvl(checkedValue, reserveValue) {
            if (checkedValue === null)
                return reserveValue;
            return checkedValue;
        }
    },
    watch: {
        errorChecked() {
            this.filterLogList();
            this.setCheckboxStateInSession();
        },
        warnChecked() {
            this.filterLogList();
            this.setCheckboxStateInSession();
        },
        updateChecked() {
            this.filterLogList();
            this.setCheckboxStateInSession();
        },
        infoChecked() {
            this.filterLogList();
            this.setCheckboxStateInSession();
        },
        debugChecked() {
            this.filterLogList();
            this.setCheckboxStateInSession();
        },
    },
    created() {
        this.setAsyncTasks();
    },
    mounted() {
        //this.getCheckboxStateFromSession(); //TODO something wrong here
        this.getLogs();
        this.getProgress();
    },
})