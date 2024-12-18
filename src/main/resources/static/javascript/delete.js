async function deleteUser(modal, id) {
    let oneUser = await userFetch.findOneUser(id);

    if (!oneUser.ok) {
        console.error(`Ошибка: Пользователь с id ${id} не найден.`);
        modal.find('.modal-body').html('<p class="text-danger">Пользователь не найден или произошла ошибка.</p>');
        return;
    }

    let user = await oneUser.json();

    modal.find('.modal-title').html('Delete user');

    let deleteButton = `<button style="font-size: 20px;" class="btn btn-danger" id="deleteButton">Delete</button>`;
    let closeButton = `<button style="font-size: 20px;" type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>`;
    modal.find('.modal-footer').append(deleteButton);
    modal.find('.modal-footer').append(closeButton);

    let rolesOptions = Array.isArray(user.roles)
        ? user.roles.map(role => `<option>${role.role.substr(5)}</option>`).join("")
        : "<option>Нет ролей</option>";

    let bodyForm = `
    <form class="form-group text-center" id="deleteUser" style="font-size: 20px;">
        <div class="form-group">
            <label style="font-size: 20px; font-weight: bold" for="userId" class="col-form-label">ID</label>
            <input style="font-size: 20px;" type="text" class="form-control username" id="userId" value="${user.id}" readonly>
        </div>

        <div class="form-group">
            <label style="font-size: 20px; font-weight: bold" for="username" class="col-form-label">Username</label>
            <input style="font-size: 20px;" type="text" class="form-control username" id="username" value="${user.username}" readonly>
        </div>

        <div class="form-group">
            <label style="font-size: 20px; font-weight: bold" for="age" class="com-form-label">Age</label>
            <input style="font-size: 20px;" type="number" class="form-control" id="age" value="${user.age}" readonly>
            <div class="invalid-feedback">Age cannot be empty</div>
        </div>

        <div class="form-group">
            <label style="font-size: 20px; font-weight: bold" for="email" class="com-form-label">Email</label>
            <input style="font-size: 20px;" type="text" class="form-control" id="email" value="${user.email}" readonly>
        </div>

        <div class="form-group">
            <label for="roles" class="com-form-label">Role:</label>
            <select style="text-align: center" id="roles" class="form-control select" size="2" name="roles" style="max-height: 100px" disabled>
                ${rolesOptions}
            </select>
        </div>
    </form>
`;
    modal.find('.modal-body').append(bodyForm);

    $("#deleteButton").off('click').on('click', async () => {
        const response = await userFetch.deleteUser(id);

        if (response.ok) {
            await getUsers();
            modal.modal('hide');
        } else {
            let body = await response.json();
            let alert = `<div class="alert alert-danger alert-dismissible fade show col-12" role="alert" id="messageError">
                            ${body.info || "Произошла ошибка при удалении пользователя."}
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>`;
            modal.find('.modal-body').prepend(alert);
        }
    });
}
