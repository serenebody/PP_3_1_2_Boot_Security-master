async function createUser() {
    $('#addUser').click(async () => {
        let addUserForm = $('#addForm');
        let username = addUserForm.find('#usernameCreate').val().trim();
        let password = addUserForm.find('#passwordCreate').val().trim();
        let email = addUserForm.find('#emailCreate').val().trim();
        let age = addUserForm.find('#ageCreate').val().trim();

        let checkedRoles = () => {
            return Array.from(document.querySelector('#rolesCreate').options)
                .filter(option => option.selected)
                .map(option => option.value);
        };

        let data = {
            username: username,
            password: password,
            email: email,
            age: age
        };

        const response = await fetch(`/api/admin/user-add?roleNames=${checkedRoles().join(',')}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            await getUsers();
            addUserForm.find('#usernameCreate').val('');
            addUserForm.find('#passwordCreate').val('');
            addUserForm.find('#emailCreate').val('');
            addUserForm.find('#ageCreate').val('');
            $('#rolesCreate').val([]);

            let alert = `<div class="alert alert-success alert-dismissible fade show col-12" role="alert" id="successMessage">
                         Пользователь успешно создан!
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>`;
            addUserForm.prepend(alert);
            $('.nav-tabs a[href="#adminTable"]').tab('show');
        } else {
            let body = await response.json();
            let alert = `<div class="alert alert-danger alert-dismissible fade show col-12" role="alert" id="messageError">
                            ${body.info}
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>`;
            addUserForm.prepend(alert);
        }
    });
}
