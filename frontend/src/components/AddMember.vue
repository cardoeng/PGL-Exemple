<script setup>

import { onMounted, ref } from 'vue'
import Datepicker from 'vuejs3-datepicker';
import { toast } from 'vue3-toastify';
import { Modal } from 'bootstrap';


const props = defineProps({
    modalId: {
        type: String,
        required: true
    }
})

const emit = defineEmits(['memberAdded'])

const block = ref(false)
const validForm = ref(false)
const matricule = ref(-1)
const firstName = ref(null)
const lastName = ref('')
const email = ref('')
const status = ref('')
const beginDate = ref('')
const endDate = ref('')
const modalElement = ref(null)
const memberForm = ref(null)
let modalObject = null

onMounted(() => {
    modalObject = new Modal(modalElement.value)
})

async function addMember() {
    block.value = true
    const member = {
        "id": matricule.value,
        "firstName": firstName.value,
        "lastName": lastName.value,
        "email": email.value + "@umons.ac.be",
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

    // There are other ways (and maybe easier) of doing a post with third-party library
    // For the sake of simplicity, we use the native fetch API



    try {
        const toastId = ref('')
        toastId.value = toast.loading('Ajout du membre...', {
            autoClose: false,
            position: toast.POSITION.BOTTOM_RIGHT
        });
        // make the request
        const response = await fetch('http://localhost:8080/api/members', options)
        // hide toast once the request is done
        toast.remove(toastId.value)
        if (response.ok) {
            const data = await response.json();
            // emit member added event
            emit('memberAdded', data)
            // hide the modal
            reset()
            _hide()
            // We could technically update the previous toast
            // But it is not working as expected
            toast('Membre ajouté avec succès', {
                type: toast.TYPE.SUCCESS,
                autoClose: 5000,
                position: toast.POSITION.BOTTOM_RIGHT
            });
        } else {
            var error = "Erreur lors de l'ajout du membre"
            if (response.status == 409) {
                error = 'Un membre avec ce matricule existe déjà'
            }
            toast(error, {
                type: toast.TYPE.ERROR,
                autoClose: 10000,
                position: toast.POSITION.BOTTOM_RIGHT
            });
        }
    } catch (error) {
        toast.remove(toastId.value)
        console.log(error);
    }
    block.value = false
}

function reset() {
    matricule.value = 0
    firstName.value = ''
    lastName.value = ''
    email.value = ''
    status.value = ''
    beginDate.value = ''
    endDate.value = ''
    block.value = false
}

function _show() {
    modalObject.show()
}

function _hide() {
    modalObject.hide()
}

function matriculeCheck(e) {
    if (matricule.value <= 0) {
        // add .is-invalid class to the input
        e.target.classList.add('is-invalid')
    } else {
        // remove .is-invalid class from the input
        e.target.classList.remove('is-invalid')
    }
    checkForm()
}

function notEmptyCheck(e) {
    if (e.target && e.target.value == '') {
        // add .is-invalid class to the input
        e.target.classList.add('is-invalid')
    } else {
        // remove .is-invalid class from the input
        e.target.classList.remove('is-invalid')
    }
    checkForm()
}

function checkForm() {
    validForm.value = memberForm.value.checkValidity()
}

defineExpose({
    show: _show
})

</script>

<template>
    <div :id="props.modalId" class="modal fade" tabindex="-1" ref="modalElement">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Ajouter un membre</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                
                <div class="modal-body">
                    <form ref="memberForm">
                        <div class="mb-2">
                            <label for="matricule" class="form-label">Matricule*</label>
                            <input type="number" id="matricule" class="form-control is-invalid" @input="matriculeCheck"
                            placeholder="Matricule" v-model="matricule" required :disabled="block"/>
                        </div>
                        <div class="mb-2">
                            <label for="firstName" class="form-label">Prénom*</label>
                            <input type="text" id="firstName" class="form-control is-invalid" @input="notEmptyCheck"
                            placeholder="Prénom" v-model="firstName" required :disabled="block"/>
                        </div>
                        <div class="mb-2">
                            <label for="lastName" class="form-label">Nom*</label>
                            <input type="text" id="lastName" class="form-control is-invalid" @input="notEmptyCheck" 
                            placeholder="Nom" v-model="lastName" required :disabled="block"/>
                        </div>
                        <div class="mb-2">
                            <label for="email" class="form-label">Email*</label>
                            <div class="input-group">
                                <input type="text" id="email" class="form-control is-invalid" @input="notEmptyCheck"
                                placeholder="Email" v-model="email" required :disabled="block"/>
                                <div class="input-group-append">
                                    <span class="input-group-text" id="basic-addon2">@umons.ac.be</span>
                                </div>
                            </div>
                        </div>
                        <div class="mb-2">
                            <label for="status" class="form-label">Status*</label>
                            <select id="status" class="form-control is-invalid" @input="notEmptyCheck" 
                            v-model="status" required :disabled="block">
                                <option value="Professor">Professeur</option>
                                <option value="Postdoc">Postdoc</option>
                                <option value="PhD">Doctorant</option>
                                <option value="Assistant">Assistant</option>
                            </select>
                        </div>
                        <div class="mb-2">
                            <label for="beginDate" class="form-label">Date de début*</label>
                            <br>
                            <datepicker id="beginDate" v-model="beginDate" class="is-invalid" @input="notEmptyCheck"
                            :typeable="true" format="yyyy-MM-dd" required :disabled="block"/>
                        </div>
                        <div class="mb-2">
                            <label for="endDate" class="form-label">Date de fin</label>
                            <br>
                            <datepicker id="endDate" v-model="endDate" :typeable="true" format="yyyy-mm-dd"
                            :disabled="block"/>
                        </div>

                        <label>* Champs obligatoires</label>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" @click="addMember" :disabled="block || !validForm">Ajouter</button>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                </div>
            </div>
        </div>
    </div>
</template>