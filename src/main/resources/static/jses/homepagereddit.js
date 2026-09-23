async function redir() {
    const sillybilly = await fetch("authcheck");
    const sillierbilly =  await sillybilly.json();
    const silliestbilly = sillierbilly.authed;

    if (silliestbilly === true) {
        window.location.href = "/dashboard";
    } else {
        window.location.href = "/homepage";
    }
}

void redir();