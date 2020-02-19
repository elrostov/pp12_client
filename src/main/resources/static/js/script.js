jQuery(function ($) {

    $.ajax({
        url: '/api/admin',
        type: 'GET',
        contentType: "application/json;charset=UTF-8",
        dataType: 'json', //тип данных, ожидаемый в качестве ответа от сервера
        success: function (data) { //функция в success сработает при получении ответа от сервера с кодом 200 - ОК
            data.forEach(function (element) {
                addTableRow(element);
            })
        },
    });

    $('#registrationButton').click(function (e) { //e - это объект события, которое сработало. В нашем случае - нажатие на кнопку в форме регистрации

        e.preventDefault(); // Убираем у события действие по-умолчанию - отправку данных на сервер.

        $('#ajaxRegistrationDiv').html('<h4>Registering new user...</h4>').fadeIn(1000, function () {
            var createObject = {};
            createObject["username"] = $("#usernameInput").val();
            createObject["password"] = $("#passwordInput").val();
            createObject["email"] = $("#emailInput").val();
            createObject["roles"] = $("#rolesInput").val();

            $.ajax({
                url: '/api/admin',
                type: 'POST',
                contentType: "application/json;charset=UTF-8",
                data: JSON.stringify(createObject), //тип данных, передаваемых на сервер
                dataType: 'json', //тип данных, ожидаемый в качестве ответа от сервера
                context: document.getElementById('ajaxRegistrationDiv'), //задает содержимое переменной this
                success: function (data) { //функция в success сработает при получении ответа от сервера с кодом 200 - ОК
                    $(this).fadeOut(1000, function () {
                        $(this).toggleClass('alert-primary alert-success');
                        $(this).find('h4').attr('class', 'alert-heading').text('New user registered!');
                        $(this).append(`<hr><h5>User ${data.username}</h5><p>id: ${data.id}</p><p>email: ${data.email}</p><p>roles: ${data.roles}</p>`);
                        $(this).fadeIn(1000)
                            .delay(2000)
                            .fadeOut(1000, function () {
                                $("#registrationForm").trigger("reset");
                                // $(this).tabs({ active: -1 });
                                // $('#usersTableLink').click();
                                // $("#userstable").show();
                            });
                    });
                    addTableRow(data);
                },
                error: function () {
                    alert("Error!");
                }
            });
        })
    });

    $('#tbody').on('click', '.delete-row', function () {
        var el = this;
        var id = this.id.slice(this.id.lastIndexOf('-') + 1);
        $.ajax({
            url: '/api/admin/' + id,
            type: 'DELETE',
            contentType: "application/json;charset=UTF-8",
            success: function () {
                // Remove row from HTML Table
                $(el).closest('tr').css('background', 'lightcoral');
                $(el).closest('tr').fadeOut(500, function () {
                    $(this).remove();
                });
            },
            error: function () {
                alert("Error!");
            }
        });
    });

    $('#tbody').on('click', '.edit-user', function () {
        var id = this.id.slice(this.id.lastIndexOf('-') + 1);
        $('#modal-input-id-disabled').attr('value', id);
        $('#modal-input-username-disabled').attr('value', $('#username-' + id).text());
        $('#modal-input-password').attr('value', "");
        $('#modal-input-email').attr('value', $('#userEmail-' + id).text());
        var userRow = $("[id=" + id + "]");
        var rolesList = ["ADMIN", "USER"];
        var userRoles = userRow.find('#userRoles-' + id).text();
        $('#modal-input-roles').empty();
        rolesList.forEach(function (value) {
            if (userRoles.includes(value)) {
                $('#modal-input-roles').append('<option id="option"' + value + ' value="' + value + '" selected>' + value + '</option>')
            } else {
                $('#modal-input-roles').append('<option id="option"' + value + ' value="' + value + '">' + value + '</option>')
            }
        });
    });

    $('#updateUser').on('click', function () {
        var updateObject = {};
        updateObject["id"] = $("#modal-input-id-disabled").val();
        updateObject["username"] = $("#modal-input-username-disabled").val();
        updateObject["password"] = $("#modal-input-password").val();
        updateObject["email"] = $("#modal-input-email").val();
        updateObject["roles"] = $("#modal-input-roles").val();

        $.ajax({
            url: '/api/admin',
            type: 'PUT',
            contentType: "application/json;charset=UTF-8",
            data: JSON.stringify(updateObject), //тип данных, передаваемых на сервер
            dataType: 'json', //тип данных, ожидаемый в качестве ответа от сервера
            success: function (data) {
                var id = data.id;
                var name = data.username;
                var email = data.email;
                var roles = data.roles;

                $('#userId-' + id).text(id);
                $('#username-' + id).text(name);
                $('#userEmail-' + id).text(email);
                $('#userRoles-' + id).text(roles);

                $('#modalWindow .closeBtn').click();
            },
            error: function () {
                alert("Error!");
            }
        })
    })
});

function addTableRow(element) {
    var id = element.id;
    var name = element.username;
    var email = element.email;
    var roles = element.roles;
    var markup = `<tr id="${id}">
                        <td id="userId-${id}">${id}</td>
                        <td id="userRoles-${id}">${roles}</td>
                        <td id="username-${id}">${name}</td>
                        <td id="userEmail-${id}">${email}</td>
                        <td><button type="button" class="btn btn-info edit-user" data-toggle="modal" data-target="#modalWindow" id="editButton-${id}">Edit</button></td>
                        <td><button type="button" class="btn btn-info delete-row" id="deleteButton-${id}">Delete</button></td>
                  </tr>`;
    $('#tbody').append(markup);
}