$(document).ready(function () {
    $("#datepicker").datetimepicker({
        footer: true,
        uiLibrary: "bootstrap5",
        format: "yyyy-dd-mm HH:MM",
        datepicker: {
            disableDates: function (date) {
                // allow for today
                const currentDate = new Date().setHours(0, 0, 0, 0);
                return date.setHours(0, 0, 0, 0) >= currentDate ? true : false;
            },
        },
    });

    // Activate tooltip
    $('[data-toggle="tooltip"]').tooltip();

    // Select/Deselect checkboxes
    var checkbox = $('table tbody input[type="checkbox"]');
    $("#selectAll").click(function () {
        if (this.checked) {
            checkbox.each(function () {
                this.checked = true;
            });
        } else {
            checkbox.each(function () {
                this.checked = false;
            });
        }
    });
    checkbox.click(function () {
        if (!this.checked) {
            $("#selectAll").prop("checked", false);
        }
    });
});

let id = 0;

$(document).on("show.bs.modal", "#editScooterModal", function (e) {
    let btnClicked = $(e.relatedTarget);
    id = btnClicked.data("attribute-id"); // id of clicked button
    $("input[name=id]").val(id);
});

$(document).on("show.bs.modal", "#deleteScooterModal", function (e) {
    let btnClicked = $(e.relatedTarget);
    id = btnClicked.data("attribute-id"); // id of clicked button
    $("input[name=id]").val(id);
    console.log(id);
});

$(document).on("show.bs.modal", "#editBikeModal", function (e) {
    let btnClicked = $(e.relatedTarget);
    id = btnClicked.data("attribute-id"); // id of clicked button
    $("input[name=id]").val(id);
});

$(document).on("show.bs.modal", "#deleteBikeModal", function (e) {
    let btnClicked = $(e.relatedTarget);
    id = btnClicked.data("attribute-id"); // id of clicked button
    $("input[name=id]").val(id);
    console.log(id);
});

$(document).on("show.bs.modal", "#editCarsModal", function (e) {
    let btnClicked = $(e.relatedTarget);
    id = btnClicked.data("attribute-id"); // id of clicked button
    $("input[name=id]").val(id);
});

$(document).on("show.bs.modal", "#deleteCarsModal", function (e) {
    let btnClicked = $(e.relatedTarget);
    id = btnClicked.data("attribute-id"); // id of clicked button
    $("input[name=id]").val(id);
    console.log(id);
});

$(document).on("show.bs.modal", "#editPartnerModal", function (e) {
    let btnClicked = $(e.relatedTarget);
    id = btnClicked.data("attribute-id"); // id of clicked button
    $("input[name=id]").val(id);
});

$(document).on("show.bs.modal", "#deletePartnerModal", function (e) {
    let btnClicked = $(e.relatedTarget);
    id = btnClicked.data("attribute-id"); // id of clicked button
    $("input[name=id]").val(id);
    console.log(id);
});

$(document).on("show.bs.modal", "#editPlanModal", function (e) {
    let btnClicked = $(e.relatedTarget);
    id = btnClicked.data("attribute-id"); // id of clicked button
    $("input[name=id]").val(id);
});

$(document).on("show.bs.modal", "#deletePlanModal", function (e) {
    let btnClicked = $(e.relatedTarget);
    id = btnClicked.data("attribute-id"); // id of clicked button
    $("input[name=id]").val(id);
    console.log(id);
});

$(document).on("show.bs.modal", "#editRentModal", function (e) {
    let btnClicked = $(e.relatedTarget);
    id = btnClicked.data("attribute-id"); // id of clicked button
    $("input[name=id]").val(id);
});

$(document).on("show.bs.modal", "#deleteRentModal", function (e) {
    let btnClicked = $(e.relatedTarget);
    id = btnClicked.data("attribute-id"); // id of clicked button
    $("input[name=id]").val(id);
    console.log(id);
});

$(document).on("show.bs.modal", "#editScooterModal", function (e) {
    let btnClicked = $(e.relatedTarget);
    id = btnClicked.data("attribute-id"); // id of clicked button
    $("input[name=id]").val(id);
});

$(document).on("show.bs.modal", "#deleteScooterModal", function (e) {
    let btnClicked = $(e.relatedTarget);
    id = btnClicked.data("attribute-id"); // id of clicked button
    $("input[name=id]").val(id);
    console.log(id);
});

$(document).on("show.bs.modal", "#editUserModal", function (e) {
    let btnClicked = $(e.relatedTarget);
    id = btnClicked.data("attribute-id"); // id of clicked button
    $("input[name=id]").val(id);
});

$(document).on("show.bs.modal", "#deleteUserModal", function (e) {
    let btnClicked = $(e.relatedTarget);
    id = btnClicked.data("attribute-id"); // id of clicked button
    $("input[name=id]").val(id);
    console.log(id);
});
