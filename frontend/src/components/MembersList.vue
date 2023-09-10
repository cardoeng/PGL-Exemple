<script setup>
import { onMounted, ref } from 'vue'
import MemberVue from './MemberVue.vue'
import { getMembers, addMember, editMember, deleteMember } from './members.js'

const members = ref([])
const refreshing = ref(false)
const addMemberModal = ref(null)
const editMemberModal = ref(null)

/**
 * Refresh the list of members
 */
async function refresh() {
  refreshing.value = true

  const data = await getMembers();
  members.value = data;

  refreshing.value = false
}

onMounted(() => {
  refresh();
})

/**
 * Parse a date to a 'Month Year' format, e.g. 'January 2021'
 * Set the language to French
 * @param {string | number | Date} date The date to parse
 */
function parseDate(date) {
  var d = new Date(date);
  // return formatted date month and year
  return d.toLocaleDateString('fr-FR', { year: 'numeric', month: 'long' });
}

/**
 * Add a member to the list
 * @param {*} member The member to add
 */
function _addMember(member) {
  members.value.push(member)
}

/**
 * Edit a member in the list and show the modal for editing it
 * @param {*} member The member to edit
 */
function _editMember(member) {
  // There are only a few members, so we can afford to do this
  const index = members.value.findIndex(m => m.id === member.id);
  members.value[index] = member;
  editMemberModal.value.hide();
}

/**
 * Delete a member in the list
 * @param {*} member The member to delete
 */
function _deleteMember(member) {
  // We can afford to do this as we suppose there are not a lot of members
  const index = members.value.findIndex(m => m.id === member.id);
  members.value.splice(index, 1);
}

/**
 * Make the request to edit a member
 * @param {*} member The member edited (suppose its ID did not change)
 */
function _editMemberRequest(member) {
  if (member == true) {
    return // no change
  }
  // We can afford to do this as we suppose there are not a lot of members
  const index = members.value.findIndex(m => m.id === member.id);
  return editMember(members.value[index], member);
}

/**
 * Make the request to delete a member
 * @param {*} member The member to delete
 */
function _deleteMemberRequest(member) {
  if (deleteMember(member)) {
    _deleteMember(member);
  }
}

/**
 * Open the modal for adding a member
 */
function openAddModal() {
  addMemberModal.value.show();
}

/**
 * Open the modal for editing a member
 * @param {*} member The member to edit
 */
function openEditModal(member) {
  editMemberModal.value.show(member);
}

</script>

<template>
  <!-- The modal -->
  <!-- We could only use one and call method to motify the title, ...
  For the sake of the example and simplicity, this is not done here -->
  <MemberVue modalId="addMemberModal" @success="_addMember" title="Ajouter un membre" :makeRequest="addMember"
    ref="addMemberModal" />
  <MemberVue modalId="editMemberModal" @success="_editMember" title="Modifier un membre" :makeRequest="_editMemberRequest"
    blockId=true ref="editMemberModal" />

  <!-- The actions button (refresh and add a member) -->
  <div class="py-4">
    <div class="btn-group" role="group">
      <button type="button" class="btn btn-info btn-lg" @click="openAddModal">
        Ajouter un membre
      </button>
      <!-- Call refresh in members list-->
      <button type="button" class="btn btn-info btn-lg" @click="refresh" :disabled="refreshing">
        Rafraîchir
      </button>
    </div>
  </div>

  <!-- The list of members if we are not refreshing -->
  <div v-if="!refreshing">
    <!-- A table with some bootstrap class for style -->
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
          <td>{{ member.firstName }}</td>
          <td>{{ member.lastName }}</td>
          <td>{{ member.email }}</td>
          <td>{{ member.status }}</td>
          <td>{{ parseDate(member.beginDate) }}</td>
          <td>{{ member.endDate == null ? null : parseDate(member.endDate) }}</td>
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
  <!-- Else, show a spinner or loading if the spinner cannot be shown -->
  <div v-else class="spinner-border" role="status">
    <span class="visually-hidden">Loading...</span>
  </div>
</template>
