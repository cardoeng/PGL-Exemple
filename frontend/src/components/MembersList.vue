<script setup>
import { onMounted, ref } from 'vue'
import MemberVue from './MemberVue.vue'
import { toast } from 'vue3-toastify';
import { addMember, editMember, deleteMember } from './members.js'

const members = ref([])
const refreshing = ref(false)
const addMemberModal = ref(null)
const editMemberModal = ref(null)

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

function _addMember(member) {
  members.value.push(member)
}

function _editMember(member) {
  // There are only a few members, so we can afford to do this
  const index = members.value.findIndex(m => m.id === member.id);
  members.value[index] = member;
  editMemberModal.value.hide();
}

function _deleteMember(member) {
  const index = members.value.findIndex(m => m.id === member.id);
  members.value.splice(index, 1);
}

function _editMemberRequest(member) {
  const index = members.value.findIndex(m => m.id === member.id);
  return editMember(members.value[index], member);
}

function _deleteMemberRequest(member) {
  if (deleteMember(member)) {
    _deleteMember(member);
  }
}

function openAddModal() {
  addMemberModal.value.show();
}

function openEditModal(member) {
  editMemberModal.value.show(member);
}

</script>

<template>
  <!-- The modal -->
  <MemberVue modalId="addMemberModal" @success="_addMember" title="Ajouter un membre" 
  :makeRequest="addMember" ref="addMemberModal"/>
  <MemberVue modalId="editMemberModal" @success="_editMember" title="Modifier un membre"
  :makeRequest="_editMemberRequest" blockId=true ref="editMemberModal"/>
  <div class="py-4">
    <div class="btn-group" role="group">
      <button type="button" class="btn btn-info btn-lg" @click="openAddModal">
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
          <th>Actions</th>
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
          <td>
            <div class="btn-group" role="group">
              <button type="button" class="btn btn-info btn-sm" @click="openEditModal(member)">
                Modifier
              </button>
              <button type="button" class="btn btn-danger btn-sm" @click="_deleteMemberRequest(member)">
                Supprimer
              </button>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
  <div v-else class="spinner-border" role="status">
    <span class="visually-hidden">Loading...</span>
  </div>
</template>
