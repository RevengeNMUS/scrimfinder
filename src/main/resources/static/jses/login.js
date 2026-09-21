function authGoogle() {
    window.location.href = "oauth2/authorization/google";
}

function authGit() {
    window.location.href = "oauth2/authorization/github";
}

function authDisc() {
    window.location.href = "oauth2/authorization/discord";
}
//todo add disc auth you chudngus :sob;

function backback() {
    history.back();
}

window.authDisc = authDisc;
window.authGit = authGit;
window.authGoogle = authGoogle;

window.backback = backback;