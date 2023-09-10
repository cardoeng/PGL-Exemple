<script setup>

import { computed, onMounted, ref, reactive } from 'vue'
import { Modal } from 'bootstrap';
import VueDatePicker from '@vuepic/vue-datepicker';
import { useVuelidate } from '@vuelidate/core'
import { required } from '@vuelidate/validators'

/* We declare the list of attributes that we should (or could) 
receive from the parents */

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

/* We declare we can emit a 'success' at some point */

const emit = defineEmits(['success'])

/* If the entire form should be block or not */

const block = ref(false)

/* The current state of the form (the data it represents) */

const formState = reactive({
    id: '',
    firstName: '',
    lastName: '',
    email: '',
    status: '',
    beginDate: '',
    endDate: ''
})

/* Simple function to check if the email is what we want
(we automatically add @umons.ac.be, so no @ should be in the email) */

const mailCheck = (value) => {
    // automatically added
    return ! value.includes('@')
}

/* The rules that each part of our form should respect */

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

/* Initialize Vuelidate for validating the form (making sure
the rules are respected) */
const v$ = useVuelidate(rules, formState)
const validForm = computed(() => v$.value.$invalid === false)

/* A few elements of our form */
const modalElement = ref(null)
const memberForm = ref(null)
const beginDatePicker = ref(null)
let modalObject = null

onMounted(() => {
    modalObject = new Modal(modalElement.value)
})

/**
 * Reset the form to default value
 */
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

/**
 * Submit the form
 */
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

/**
 * Show the form with the default values or with the values of the given member
 * @param {*} member The defaults value to show (optional)
 */
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
    } else {
        reset()
    }
    modalObject.show()
}

/**
 * Hide the form
 */
function _hide() {
    modalObject.hide()
}

/* Define what methods are exposed */

defineExpose({
    show: _show,
    hide: _hide
})

</script>

<!-- HTML part -->

<template>
    <!-- Declare a basic modal with bootstrap -->
    <div :id="props.modalId" class="modal fade" tabindex="-1" ref="modalElement">
        <div class="modal-dialog">
            <div class="modal-content">
                <!-- The header (title) of our modal -->
                <div class="modal-header">
                    <h5 class="modal-title">{{ props.title }}</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                
                <!-- The content of our modal (i.e. our form) -->
                <div class="modal-body">
                    <!-- Declare a form calling the submit method when the form
                    should be submitted -->
                    <form ref="memberForm" id="memberForm" @submit.prevent="submit">
                        <div class="mb-2">
                            <!-- Declare a label and a input for the id
                            Add a class 'is-invalid' (bootstrap class) if Vuelidate detects that 
                            this input does not follow the rules we set for it (required and > 0) -->
                            <label for="matricule" class="form-label">Matricule*</label>
                            <input type="number" id="matricule" class="form-control"
                            :class="(v$.id.$invalid) ? 'is-invalid' : 'is-valid'"
                            placeholder="Matricule" v-model="formState.id" :disabled="block || props.blockId"/>
                            <!-- Note the v-model attribute linking the value of this input to `formState.id` -->
                            <!-- Feedback (text shown) if the input is invalid (has the 'is-invalid' class) -->
                            <div class="invalid-feedback">Le matricule doit être positif (> 0)</div>
                        </div>
                        <!-- The following always has the same template -->
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
                                <!-- Show that we add @umons.ac.be to the input -->
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
                <!-- The footer of the form (in our cases, confirm and cancel) -->
                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary" @click="submit" :disabled="block || !validForm">Confirmer</button>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                </div>
            </div>
        </div>
    </div>
</template>