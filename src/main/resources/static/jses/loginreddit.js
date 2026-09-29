async function redir() {
    const sillybilly = await fetch("new-user-check");
    const sillierbilly =  await sillybilly.json();
    const silliestbilly = sillierbilly.authed;

    if (!silliestbilly) {
        window.location.href = "/homepage";
    }

    const userbilly = sillierbilly.newuser;
    if (userbilly) {
        window.location.href = "/onboarding"
    } else {
        window.location.href = "/dashboard"
    }
}

void redir();