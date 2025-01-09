function onSignIn(googleUser: gapi.auth2.GoogleUser) {
    const profile = googleUser.getBasicProfile();
    console.log("ID: " + profile.getId());
    console.log('Full Name: ' + profile.getName());
    console.log('Given Name: ' + profile.getGivenName());
    console.log('Family Name: ' + profile.getFamilyName());
    console.log("Image URL: " + profile.getImageUrl());
    console.log("Email: " + profile.getEmail());
    const id_token = googleUser.getAuthResponse().id_token;
    console.log("ID Token: " + id_token);

    // Send the ID token and user ID to the backend for user authentication
    fetch('/api/authenticate', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ id_token: id_token, user_id: profile.getId() })
    })
    .then(response => response.json())
    .then(data => {
        console.log('User authenticated:', data);
        // Store the user ID and ID token in local storage
        localStorage.setItem('user_id', profile.getId());
        localStorage.setItem('id_token', id_token);
    })
    .catch(error => {
        console.error('Error authenticating user:', error);
    });
}
