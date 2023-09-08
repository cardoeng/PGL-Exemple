<script setup>

import { onMounted, ref } from 'vue'
import Datepicker from 'vuejs3-datepicker';
import { Modal } from 'bootstrap';


const props = defineProps({
    modalId: {
        type: String,
        required: true
    },
    makeRequest: {
        type: Function,
        required: true
    },
    title: {
        type: String,
        required: true
    },
    blockId: {
        required: false,
        default: false
    }

})

const emit = defineEmits(['success'])

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

async function submit() {
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
    const data = await props.makeRequest(member)
    if (data) {
        emit('success', data)
        reset()
        _hide()
    }
    block.value = false
}

function _show(member) {
    if (member) {
        matricule.value = member.id
        firstName.value = member.firstName
        lastName.value = member.lastName
        email.value = member.email.split('@')[0]
        status.value = member.status
        beginDate.value = member.beginDate
        endDate.value = member.endDate
        // We suppose that the member is valid by default
        // So we remove every is-invalid class
        memberForm.value.querySelectorAll('.is-invalid').forEach(e => {
            e.classList.remove('is-invalid')
        })
        validForm.value = true
    }
    modalObject.show()
}

function _hide() {
    modalObject.hide()
}

function _matriculeCheck(e) {
    if (matricule.value <= 0) {
        // add .is-invalid class to the input
        e.target.classList.add('is-invalid')
    } else {
        // remove .is-invalid class from the input
        e.target.classList.remove('is-invalid')
    }
    _checkForm()
}

function _notEmptyCheck(e) {
    if (e.target && e.target.value == '') {
        // add .is-invalid class to the input
        e.target.classList.add('is-invalid')
    } else {
        // remove .is-invalid class from the input
        e.target.classList.remove('is-invalid')
    }
    _checkForm()
}

function _emailCheck(e) {
    if (e.target && (e.target.value == '' || e.target.value.includes('@'))) {
        // the @ is automatically added when sending the request
        // add .is-invalid class to the input
        e.target.classList.add('is-invalid')
    } else {
        // remove .is-invalid class from the input
        e.target.classList.remove('is-invalid')
    }
    _checkForm()
}

function _beginDateCheck(d) {
    // query the input
    const control = document.querySelector('#beginDate')
    if (d == null) {
        // add .is-invalid class to the input
        control.classList.add('is-invalid')
    } else {
        // remove .is-invalid class from the input
        control.classList.remove('is-invalid')
    }
    _checkForm()
}

function _checkForm() {
    validForm.value = memberForm.value.checkValidity()
}

defineExpose({
    show: _show,
    hide: _hide
})

</script>

<template>
    <div :id="props.modalId" class="modal fade" tabindex="-1" ref="modalElement">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">{{ props.title }}</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                
                <div class="modal-body">
                    <form ref="memberForm">
                        <div class="mb-2">
                            <label for="matricule" class="form-label">Matricule*</label>
                            <input type="number" id="matricule" class="form-control is-invalid" @input="_matriculeCheck"
                            placeholder="Matricule" v-model="matricule" required :disabled="block || props.blockId"/>
                        </div>
                        <div class="mb-2">
                            <label for="firstName" class="form-label">Prénom*</label>
                            <input type="text" id="firstName" class="form-control is-invalid" @input="_notEmptyCheck"
                            placeholder="Prénom" v-model="firstName" required :disabled="block"/>
                        </div>
                        <div class="mb-2">
                            <label for="lastName" class="form-label">Nom*</label>
                            <input type="text" id="lastName" class="form-control is-invalid" @input="_notEmptyCheck" 
                            placeholder="Nom" v-model="lastName" required :disabled="block"/>
                        </div>
                        <div class="mb-2">
                            <label for="email" class="form-label">Email*</label>
                            <div class="input-group">
                                <input type="text" id="email" class="form-control is-invalid" @input="_emailCheck"
                                placeholder="Email" v-model="email" required :disabled="block"/>
                                <div class="input-group-append">
                                    <span class="input-group-text" id="basic-addon2">@umons.ac.be</span>
                                </div>
                            </div>
                        </div>
                        <div class="mb-2">
                            <label for="status" class="form-label">Status*</label>
                            <select id="status" class="form-control is-invalid" @input="_notEmptyCheck" 
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
                            <datepicker id="beginDate" v-model="beginDate" class="is-invalid" @selected="_beginDateCheck"
                            :typeable="true" format="yyyy-MM-dd" required :disabled="block"/>
                        </div>
                        <div class="mb-2">
                            <label for="endDate" class="form-label">Date de fin</label>
                            <br>
                            <datepicker id="endDate" v-model="endDate" :typeable="true" format="yyyy-MM-dd"
                            :disabled="block"/>
                        </div>

                        <label>* Champs obligatoires</label>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" @click="submit" :disabled="block || !validForm">Confirmer</button>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                </div>
            </div>
        </div>
    </div>
</template>