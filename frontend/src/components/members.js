import { toast } from 'vue3-toastify';
import { ref } from 'vue'

async function makeRequest(url, member, method, loadingMessage, successMessage, 
    errorMessage, conflictMessage) {
    const options = {
        method: method,
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(member)
    }

    const toastId = ref('')
    toastId.value = toast.loading(loadingMessage, {
        autoClose: false,
        position: toast.POSITION.BOTTOM_RIGHT
    });

    try {
        // make the request
        const response = await fetch(url, options)

        if (response.ok) {
    
            // We could technically update the previous toast
            // But it is not working as expected
            toast(successMessage, {
                type: toast.TYPE.SUCCESS,
                autoClose: 5000,
                position: toast.POSITION.BOTTOM_RIGHT
            });

            return response.status == 204 ? true : await response.json()
        } else {
            var error = errorMessage
            if (response.status == 409) {
                error = conflictMessage
            }
            toast(error, {
                type: toast.TYPE.ERROR,
                autoClose: 10000,
                position: toast.POSITION.BOTTOM_RIGHT
            });
            return false
        }

    } catch (error) {
        console.log(error);
    } finally {
        // hide toast once the request is done
        toast.remove(toastId.value)
    }
    
}

async function addMember(member) {

    // There are other ways (and maybe easier ways) of doing a post request with third-party library
    // For the sake of not using too much third-party libraries, I use the native fetch API

    return makeRequest('http://localhost:8080/api/members', member, 
        'POST', 'Ajout du membre...', 'Membre ajouté avec succès', 
        "Erreur lors de l'ajout du membre", "Un membre avec ce matricule existe déjà")
}

async function editMember(old_member, member) {
    const modified = {}
    for (const key in member) {
        if (member[key] != old_member[key]) {
            modified[key] = member[key]
        }
    }
    if (Object.keys(modified).length == 0) {
        toast('Aucune modification détectée', {
            type: toast.TYPE.INFO,
            autoClose: 5000,
            position: toast.POSITION.BOTTOM_RIGHT
        });
        return
    }
    
    return makeRequest('http://localhost:8080/api/members/' + member.id, modified,
        'PATCH', 'Modification du membre...', 'Membre modifié avec succès',
        "Erreur lors de la modification du membre", "Un conflit est survenu lors de la modification du membre")
}

async function deleteMember(member) {
    return makeRequest('http://localhost:8080/api/members/' + member.id, null,
        'DELETE', 'Suppression du membre...', 'Membre supprimé avec succès',
        "Erreur lors de la suppression du membre", "Un conflit est survenu lors de la suppression du membre")
}

export { addMember, editMember, deleteMember }