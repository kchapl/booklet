// See https://developers.google.com/identity/sign-in/web
function onSignIn(googleUser) {// Useful data for your client-side scripts:
    const profile = googleUser.getBasicProfile();
    console.log("ID: " + profile.getId()); // Don't send this directly to your server!
    console.log('Full Name: ' + profile.getName());
    console.log('Given Name: ' + profile.getGivenName());
    console.log('Family Name: ' + profile.getFamilyName());
    console.log("Image URL: " + profile.getImageUrl());
    console.log("Email: " + profile.getEmail());
    // The ID token you need to pass to your backend:
    const id_token = googleUser.getAuthResponse().id_token;
    console.log("ID Token: " + id_token);
}
