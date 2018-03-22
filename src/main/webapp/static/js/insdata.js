
function retrieveUser() {
    $.getJSON("/api/users/me/profiles", function(data) {
        var profile = data.profiles[0];

        $('.user-profile-avatar img').attr('src', profile.igProfile.pictureUrl);
        $(".user-profile-name").text(profile.igProfile.name);
    });
}
