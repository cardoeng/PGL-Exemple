<script setup>
import { onMounted, ref } from 'vue'
import AddMember from './AddMember.vue'
import { toast } from 'vue3-toastify';

const members = ref([])
const refreshing = ref(false)
const addMemberModal = ref(null)

async function refresh() {
  refreshing.value = true
  // wait a bit for simulation purpose

  try {
    const response = await toast.promise(fetch('http://localhost:8080/api/members'), {
      pending: 'Chargement...',
      error: 'Erreur du chargement de la liste des membres'
    }, {
      position: toast.POSITION.BOTTOM_RIGHT
    });
    const data = await response.json();
    members.value = data;
  } catch (error) {
    console.log(error);
  }
  
  refreshing.value = false
}

onMounted(() => {
  refresh();
})

function parseDate(date) {
  var d = new Date(date);
  // return formatted date month and year
  return d.toLocaleDateString('fr-FR', { year: 'numeric', month: 'long' });
}

function addMember(member) {
  members.value.push(member)
}

function openModal() {
  addMemberModal.value.show();
}

</script>

<template>
  <!-- The modal -->
  <AddMember modalId="addMemberModal" @memberAdded="addMember" ref="addMemberModal"/>
  <div class="py-4">
    <div class="btn-group" role="group">
      <button type="button" class="btn btn-info btn-lg" @click="openModal">
       Ajouter un membre
      </button>
      <!-- Call refresh in members list-->
      <button type="button" class="btn btn-info btn-lg"
        @click="refresh" :disabled="refreshing">
          Rafraîchir
        </button>
    </div>
  </div>

  <div v-if="!refreshing">
    <table class="table table-bordered table-striped-columns">
      <thead>
        <tr>
          <th>Prénom</th>
          <th>Nom</th>
          <th>Email</th>
          <th>Status</th>
          <th>Date de début</th>
          <th>Date de fin</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="member in members">
          <td>{{member.firstName}}</td>
          <td>{{member.lastName}}</td>
          <td>{{member.email}}</td>
          <td>{{member.status}}</td>
          <td>{{parseDate(member.beginDate)}}</td>
          <td>{{member.endDate == null ? null : parseDate(member.endDate)}}</td>
        </tr>
      </tbody>
    </table>
  </div>
  <div v-else class="spinner-border" role="status">
    <span class="visually-hidden">Loading...</span>
  </div>
</template>
