import axios from 'axios'
import {req} from 'vuelidate/lib/validators/common'

export default async function (value) {
  if (!req(value)) return true;
  let emailUnique = true;
  await axios('/is-email-unique', {
    method: "post",
    data: value,
    headers: {'Content-type': 'text/plain'}
  }).then((response) => {
    emailUnique = response.data;
  });
  return emailUnique;
};
