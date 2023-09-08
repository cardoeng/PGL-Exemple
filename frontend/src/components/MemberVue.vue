<script setup>

import { computed, onMounted, ref, reactive } from 'vue'
import Datepicker from 'vuejs3-datepicker';
import { Modal } from 'bootstrap';
import VueDatePicker from '@vuepic/vue-datepicker';
import { useVuelidate } from '@vuelidate/core'
import { required, email } from '@vuelidate/validators'


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

const formState = reactive({
    id: '',
    firstName: '',
    lastName: '',
    email: '',
    status: '',
    beginDate: '',
    endDate: ''
})

const mailCheck = (value) => {
    // automatically added
    return ! value.includes('@')
}

const rules = computed(() => ({
    id: {
        required,
        min: 1
    },
    firstName: {
        required
    },
    lastName: {
        required
    },
    email: {
        required, mailCheck
    },
    status: {
        required
    },
    beginDate: {
        required
    },
}))

const v$ = useVuelidate(rules, formState)
const validForm = computed(() => v$.value.$invalid === false)

const modalElement = ref(null)
const memberForm = ref(null)
const beginDatePicker = ref(null)
let modalObject = null

onMounted(() => {
    modalObject = new Modal(modalElement.value)
})

function reset() {
    formState.id = 0
    formState.firstName = ''
    formState.lastName = ''
    formState.email = ''
    formState.status = ''
    formState.beginDate = ''
    formState.endDate = ''
    block.value = false
}

async function submit() {
    block.value = true
    const member = formState
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
        formState.id = member.id
        formState.firstName = member.firstName
        formState.lastName = member.lastName
        formState.email = member.email
        formState.status = member.status
        formState.beginDate = member.beginDate
        formState.endDate = member.endDate


        // We suppose that the member is valid by default
        // So we remove every is-invalid class
        memberForm.value.querySelectorAll('.is-invalid').forEach(e => {
            e.classList.remove('is-invalid')
        })
    }
    modalObject.show()
}

function _hide() {
    modalObject.hide()
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
                    <form ref="memberForm" id="memberForm" @submit.prevent="submit">
                        <div class="mb-2">
                            <label for="matricule" class="form-label">Matricule*</label>
                            <input type="number" id="matricule" class="form-control"
                            :class="(v$.id.$invalid) ? 'is-invalid' : 'is-valid'"
                            placeholder="Matricule" v-model="formState.id" :disabled="block || props.blockId"/>
                            <div class="invalid-feedback">Le matricule doit être positif</div>
                        </div>
                        <div class="mb-2">
                            <label for="firstName" class="form-label">Prénom*</label>
                            <input type="text" id="firstName" class="form-control"
                            :class="(v$.firstName.$invalid) ? 'is-invalid' : 'is-valid'"
                            placeholder="Prénom" v-model="formState.firstName" :disabled="block"/>
                        </div>
                        <div class="mb-2">
                            <label for="lastName" class="form-label">Nom*</label>
                            <input type="text" id="lastName" class="form-control"
                            :class="(v$.lastName.$invalid) ? 'is-invalid' : 'is-valid'"
                            placeholder="Nom" v-model="formState.lastName" :disabled="block"/>
                        </div>
                        <div class="mb-2">
                            <label for="email" class="form-label">Email*</label>
                            <div class="input-group">
                                <input type="text" id="email" class="form-control"
                                :class="(v$.email.$invalid) ? 'is-invalid' : 'is-valid'"
                                placeholder="Email" v-model="formState.email" :disabled="block"/>
                                <div class="input-group-append">
                                    <span class="input-group-text" id="basic-addon2">@umons.ac.be</span>
                                </div>
                            </div>
                        </div>
                        <div class="mb-2">
                            <label for="status" class="form-label">Status*</label>
                            <select id="status" class="form-control"
                            :class="(v$.status.$invalid) ? 'is-invalid' : 'is-valid'"
                            v-model="formState.status" required :disabled="block">
                                <option value="Professor">Professeur</option>
                                <option value="Postdoc">Postdoc</option>
                                <option value="PhD">Doctorant</option>
                                <option value="Assistant">Assistant</option>
                            </select>
                        </div>
                        <div class="mb-2">
                            <label for="beginDate" class="form-label">Date de début*</label>
                            <br>
                            <VueDatePicker id="beginDate" ref="beginDatePicker" 
                            v-model="formState.beginDate" class="form-control"
                            :class="(v$.beginDate.$invalid) ? 'is-invalid' : 'is-valid'"
                            format="yyyy-MM-dd" :disabled="block" required />
                        </div>
                        <div class="mb-2">
                            <label for="endDate" class="form-label">Date de fin</label>
                            <br>
                            <VueDatePicker id="endDate"
                            v-model="formState.endDate" class="form-control"
                            format="yyyy-MM-dd" :disabled="block" />
                        </div>

                        <label>* Champs obligatoires</label>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary" @click="submit" :disabled="block || !validForm">Confirmer</button>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                </div>
            </div>
        </div>
    </div>
</template>