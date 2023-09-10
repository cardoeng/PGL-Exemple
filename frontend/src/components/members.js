import { toast } from 'vue3-toastify';
import { ref } from 'vue'

/**
 * Make a request to the server
 * @param {String} url The URL
 * @param {*} member The member to send
 * @param {String} method The method (POST, PATCH, DELETE)
 * @param {String | null} loadingMessage The message to show while loading
 * @param {String | null} successMessage The message to show on success
 * @param {String | null} errorMessage The message to show on error
 * @param {String | null} conflictMessage The message to show on conflict
 * @returns 
 */
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

/**
 * 
 * @returns The list of members
 */
async function getMembers() {
    try {
      /* Add a toast (the notification shown on top of the page) */
      const response = await toast.promise(fetch('/api/members'), {
        pending: 'Chargement...',
        error: 'Erreur du chargement de la liste des membres'
      }, {
        position: toast.POSITION.BOTTOM_RIGHT
      });
      /* Parse the data from JSON to a dictionary / map */
      const data = await response.json();
      
      /* Set the value of our list */
      return data
    } catch (error) {
      console.log(error);
    }
    return null;

}

/**
 * Add a member
 * @param {*} member  The member to add
 * @returns The member added or false if an error occurred
 */
async function addMember(member) {

    // There are other ways (and maybe easier ways) of doing a post request with third-party library
    // For the sake of not using too much third-party libraries, I use the native fetch API

    return makeRequest('/api/members', member, 
        'POST', 'Ajout du membre...', 'Membre ajouté avec succès', 
        "Erreur lors de l'ajout du membre", "Un membre avec ce matricule existe déjà")
}

/**
 * Edit a member
 * @param {*} old_member The old member
 * @param {*} member The new member
 * @returns The member edited or false if an error occurred
 */
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
        return true
    }
    
    return makeRequest('/api/members/' + member.id, modified,
        'PATCH', 'Modification du membre...', 'Membre modifié avec succès',
        "Erreur lors de la modification du membre", "Un conflit est survenu lors de la modification du membre")
}

/**
 * Delete a member
 * @param {*} member The member to delete
 * @returns True if the member was deleted, false otherwise
 */
async function deleteMember(member) {
    return makeRequest('/api/members/' + member.id, null,
        'DELETE', 'Suppression du membre...', 'Membre supprimé avec succès',
        "Erreur lors de la suppression du membre", "Un conflit est survenu lors de la suppression du membre")
}

export { getMembers, addMember, editMember, deleteMember }