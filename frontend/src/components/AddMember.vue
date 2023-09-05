<script setup>

import { onMounted, ref } from 'vue'
import Datepicker from 'vuejs3-datepicker';

const props = defineProps({
    modalId: {
        type: String,
        required: true
    }
})

const matricule = ref(0)
const firstName = ref('')
const lastName = ref('')
const email = ref('')
const status = ref('')
const beginDate = ref('')
const endDate = ref('')

function addMember() {
    const member = {
        "id": matricule.value,
        "firstName": firstName.value,
        "lastName": lastName.value,
        "email": email.value,
        "status": status.value,
        "beginDate": beginDate.value,
        "endDate": endDate.value == "" ? null : endDate.value
    }
    const options = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(member)
    }
    console.log(options)
    // There are other ways (and maybe easier) of doing a post with third-party library
    // For the sake of simplicity, we use the native fetch API
    fetch('http://localhost:8080/api/members', options)
        .then(response => response.json())
        .then(data => {
            console.log(data)
            $('#addMemberModal').modal('hide')
        })
        .catch(error => console.log(error))

}
</script>

<template>
    <div :id="props.modalId" class="modal fade" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Ajouter un membre</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                
                <div class="modal-body">
                    <form>
                        <div class="mb-2">
                            <label for="matricule" class="form-label">Matricule*</label>
                            <input type="number" id="matricule" class="form-control" placeholder="Matricule" v-model="matricule"/>
                        </div>
                        <div class="mb-2">
                            <label for="firstName" class="form-label">Prénom*</label>
                            <input type="text" id="firstName" class="form-control" placeholder="Prénom" v-model="firstName" />
                        </div>
                        <div class="mb-2">
                            <label for="lastName" class="form-label">Nom*</label>
                            <input type="text" id="lastName" class="form-control" placeholder="Nom" v-model="lastName" />
                        </div>
                        <div class="mb-2">
                            <label for="email" class="form-label">Email*</label>
                            <div class="input-group">
                                <input type="text" id="email" class="form-control" placeholder="Email" v-model="email" />
                                <div class="input-group-append">
                                    <span class="input-group-text" id="basic-addon2">@umons.ac.be</span>
                                </div>
                            </div>
                        </div>
                        <div class="mb-2">
                            <label for="status" class="form-label">Status*</label>
                            <select id="status" class="form-control" v-model="status">
                                <option value="Professor">Professeur</option>
                                <option value="Postdoc">Postdoc</option>
                                <option value="PhD">Doctorant</option>
                                <option value="Assistant">Assistant</option>
                            </select>
                        </div>
                        <div class="mb-2">
                            <label for="beginDate" class="form-label">Date de début*</label>
                            <br>
                            <datepicker id="beginDate" v-model="beginDate" :typeable="true" format="yyyy-MM-dd"/>
                        </div>
                        <div class="mb-2">
                            <label for="endDate" class="form-label">Date de fin</label>
                            <br>
                            <datepicker id="endDate" v-model="endDate" :typeable="true" format="yyyy-mm-dd"/>
                        </div>

                        <label>* Champs obligatoires</label>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" @click="addMember">Ajouter</button>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                </div>
            </div>
        </div>
    </div>
</template>