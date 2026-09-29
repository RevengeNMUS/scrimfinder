async function setitupsetitsetitup () {
    const sillybilly = await fetch("authEmail");
    const sillierbilly =  await sillybilly.json();
    const silliestbilly = sillierbilly.email;

    document.getElementById("email").setAttribute("value", silliestbilly)
}

async function redir() {
    const sillybilly = await fetch("new-user-check");
    const sillierbilly =  await sillybilly.json();
    const silliestbilly = sillierbilly.authed;

    if (!silliestbilly) {
        window.location.href = "/homepage";
    }

    const userbilly = sillierbilly.newuser;
    if (!userbilly) {
        window.location.href = "/dashboard"
    }
}

void redir();

void setitupsetitsetitup();

const form = document.getElementById('search-form');

form.addEventListener('submit', (event) => {
    event.preventDefault();

    console.log("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE SUBMITTED");

    const formData = new FormData(form);

    const tNum = formData.get('tNum');
    const tName = formData.get('tName');
    const region = formData.get('region');
    const email = formData.get('email');

    const url = 'http://localhost:8080/createUser';
    const data = {
        tNum: formData.get('tNum'),
        tName: formData.get('tName'),
        region: formData.get('region'),
        email: formData.get('email')
    };

    var token = document.querySelector("meta[name='_csrf']").getAttribute("content");
//    var headere = document.querySelector("meta[name='_csrf_header']").getAttribute("content");

    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': token
        },
        body: JSON.stringify(data)
    }).then(r => {console.log(`response recieved: ${r}`); window.location.href = "/homepage-redir";});
});