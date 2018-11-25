export const langUtils = {
  methods: {
    nvl(nullableObject, reserveObject) {
      return nullableObject !== null ? nullableObject : reserveObject;
    },
    sortObject(object) {
      return Object.keys(object).sort().reduce((result, key) => {
        result[key] = object[key];
        return result;
      }, {});
    }
  }
};
