// See https://developers.google.com/identity/gsi/web/guides/migration#redirect-mode
function handleCredentialResponse(credentialResponse) {// Useful data for your client-side scripts:
    console.log("select_by: " + credentialResponse.select_by);
    // The ID token you need to pass to your backend:
    const id_token = credentialResponse.credential;
    console.log("ID Token: " + id_token); // to be verified on server
}
