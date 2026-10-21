const url = `http://localhost:8080/getUserInfo`;
var tNum;

async function getTemu() {
    const fet = await fetch(url).then(async (feet) => {
        if (!feet.ok) {
            throw Error("ruh roh L bozo");
        }

        return await feet.json();
    }).catch((error) => {console.error(error);});

    let feetFarm = fet.user;
    tNum = feetFarm.teamNum;

    document.getElementById("TNUM").innerText = feetFarm.teamNum;
    document.getElementById("nam").setAttribute("value", feetFarm.teamName);
    document.getElementById("contact").setAttribute("value", feetFarm.email);
    document.getElementById(feetFarm.region).setAttribute("selected", "true");
}

async function scatmansworld() {
    var token = document.querySelector("meta[name='_csrf']").getAttribute("content");

    const bodeh =
        {
            email: document.getElementById("contact").value,
            region: document.getElementById("reg").value,
            name: document.getElementById("nam").value
        }

    await fetch(`http://localhost:8080/modifyTeam/${tNum}`, {
        method: 'PUT',
        headers: {
            'Content-Type': "application/json",
            'X-CSRF-TOKEN': token
        },
        body: JSON.stringify(bodeh)
    }).then(r => {console.log(`response recieved: ${r}`);});
}

function initGeeMap() {
    void getTemu();
    document.getElementById("submit").addEventListener("click", action =>
    {
        scatmansworld();
        window.alert("Info Changed! Reload :0");
        window.location.reload();
    }
    );
}

void initGeeMap();