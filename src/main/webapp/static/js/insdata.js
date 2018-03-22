
function retrieveUser() {
    $.getJSON("/api/users/me/profiles", function(data) {
        var profile = data.profiles[0];

        $(".data-userFullName").text(profile.igProfile.name);
    });
}
