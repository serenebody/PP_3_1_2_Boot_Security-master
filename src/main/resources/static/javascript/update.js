async function updateUser(modal, id) {
    let oneUser = await userFetch.findOneUser(id);
    let user = await oneUser.json();

    modal.find('.modal-title').html('Edit user');

    let editButton = `<button style="font-size: 20px;" class="btn btn-primary" id="editButton">Edit</button>`;
    let closeButton = `<button style="font-size: 20px;" type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>`;
    modal.find('.modal-footer').append(editButton);
    modal.find('.modal-footer').append(closeButton);

    let bodyForm = `
        <form class="form-group text-center" id="updateUser">
            <div class="form-group">
                <label style="font-size: 20px; font-weight: bold" for="id" class="col-form-label">ID</label>
                <input style="font-size: 20px;" type="text" class="form-control username" id="id" value="${user.id}" readonly>
            </div>
            <div class="form-group">
                <label style="font-size: 20px; font-weight: bold" for="username" class="col-form-label">Username</label>
                <input style="font-size: 20px;" type="text" class="form-control username" id="username" value="${user.username}">
            </div>
            <div class="form-group">
                <label style="font-size: 20px; font-weight: bold" for="age" class="com-form-label">Age</label>
                <input style="font-size: 20px;" type="number" class="form-control" id="age" value="${user.age}">
            </div>
            <div class="form-group">
                <label style="font-size: 20px; font-weight: bold" for="email" class="com-form-label">Email</label>
                <input style="font-size: 20px;" type="text" class="form-control" id="email" value="${user.email}">
            </div>
            <div class="form-group">
                <label style="font-size: 20px; font-weight: bold" for="password" class="com-form-label">Password</label>
                <input style="font-size: 20px;" type="password" class="form-control" id="password" value="${user.password}">
            </div>
            <div class="form-group">
                <label style="font-size: 20px; font-weight: bold" for="roles" class="com-form-label">Roles</label>
                <select multiple id="roles" size="2" class="form-control" style="max-height: 100px; text-align: center">
                    <option value="ROLE_USER" ${user.roles.includes("ROLE_USER") ? "selected" : ""}>USER</option>
                    <option value="ROLE_ADMIN" ${user.roles.includes("ROLE_ADMIN") ? "selected" : ""}>ADMIN</option>
                </select>
            </div>
        </form>
    `;
    modal.find('.modal-body').html(bodyForm);

    $("#editButton").on('click', async () => {
        // Получение выбранных ролей
        let checkedRoles = Array.from(document.querySelector('#roles').options)
            .filter(option => option.selected)
            .map(option => option.value);

        let id = modal.find("#id").val().trim();
        let username = modal.find("#username").val().trim();
        let password = modal.find("#password").val().trim();
        let email = modal.find("#email").val().trim();
        let age = modal.find("#age").val().trim();

        let data = {
            id: id,
            username: username,
            password: password,
            email: email,
            age: age,
            roles: checkedRoles
        };

        const response = await fetch(`/api/admin/update-user/${id}?roleNames=${checkedRoles.join(',')}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            await getUsers();
            modal.modal('hide');
        } else {
            let body = await response.json();
            let alert = `<div class="alert alert-danger alert-dismissible fade show col-12" role="alert" id="messageError">
                            ${body.info}
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>`;
            modal.find('.modal-body').prepend(alert);
        }
    });
}



