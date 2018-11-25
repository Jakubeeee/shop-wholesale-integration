export const dateTimeUtils = {
  methods: {
    truncateToSeconds(dateTime) {
      return dateTime.slice(0, dateTime.lastIndexOf("."));
    },
    millisToMinutes(millis){
      return millis / 60000;
    },
    dateToString(date) {
      let dateFormat = require('dateformat');
      try {
        return dateFormat(date, "dd-mm-yyyy HH:MM:ss");
      } catch (error) {
        return null;
      }
    }
  }
};
