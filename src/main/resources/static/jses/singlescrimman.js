async function sususamongs() {
    //addeth the schmorgus borgus yummy yummy loading from scrim info abt app status being open und teams und size limit :0
    const sillyboy = location.pathname.split("/").pop();
    const omeingodt = await fetch(`http://localhost:8080/getScrims?identifier=${sillyboy}`)
    const susus = await omeingodt.json().then(value => {return value[0]});

    const astat = document.getElementById("appstatus");
    astat.setAttribute("data-appstatus", susus.appStatus);

    const sizzler = document.getElementById("sizee");
    sizzler.setAttribute("value", susus.size);

    document.getElementById("appstatus").addEventListener("click", apstatupdat);
}

function apstatupdat() {
    const astat = document.getElementById("appstatus");

    if (astat.matches('[data-appstatus="OPEN"]')) {
        astat.setAttribute("data-appstatus", "CLOSED");
    } else {
        if (astat.matches('[data-appstatus="CLOSED"]')) {
            astat.setAttribute("data-appstatus", "OPEN");
        }
    }
}

function applysus() {
    const goonbyesubmit = document.getElementById("removeteamsubmit");
    goonbyesubmit.addEventListener("click", goonbyeTeam);

    const sizzsubmit = document.getElementById("sizelimitsubmit");
    sizzsubmit.addEventListener("click", saveSizz);

    const submitappstatus = document.getElementById("appstatussubmit");
    submitappstatus.addEventListener("click", saveStat);
}

async function saveStat() {
    const sillyboy = location.pathname.split("/").pop();

    const astat = document.getElementById("appstatus");
    const gorb = astat.matches('[data-appstatus="OPEN"]')

    let smorg = "";
    if (gorb) {
        smorg = "OPEN";
    } else {
        smorg = "CLOSED"
    }
    const url = `http://localhost:8080/modifyScrim/${sillyboy}?appStatus=${smorg}`;

    var token = document.querySelector("meta[name='_csrf']").getAttribute("content");
//    var headere = document.querySelector("meta[name='_csrf_header']").getAttribute("content");

    await fetch(url, {
        method: 'PUT',
        headers: {
            'X-CSRF-TOKEN': token
        },
    }).then(r => {console.log(`response recieved: ${r}`);});

    window.alert("Info Changed! Reload :0");
    window.location.reload();
}

async function saveSizz() {
    const sillyboy = location.pathname.split("/").pop();

    const mmmm = document.getElementById("sizee").value;

    const url = `http://localhost:8080/modifyScrim/${sillyboy}?size=${mmmm}`;

    var token = document.querySelector("meta[name='_csrf']").getAttribute("content");
//    var headere = document.querySelector("meta[name='_csrf_header']").getAttribute("content");

    await fetch(url, {
        method: 'PUT',
        headers: {
            'X-CSRF-TOKEN': token
        },
    }).then(r => {console.log(`response recieved: ${r}`); window.location.reload()});

    window.alert("Info Changed! Reload :0");
    window.location.reload();
}

async function goonbyeTeam() {
    const sillyboy = location.pathname.split("/").pop();

    const mmmm = document.getElementById("numero").value;

    const url = `http://localhost:8080/teamLeaveScrim/${mmmm}?scrimID=${sillyboy}`;

    var token = document.querySelector("meta[name='_csrf']").getAttribute("content");
//    var headere = document.querySelector("meta[name='_csrf_header']").getAttribute("content");

    await fetch(url, {
        method: 'PUT',
        headers: {
            'X-CSRF-TOKEN': token
        },
    }).then(r => {console.log(`response recieved: ${r}`); window.location.reload()});

    window.alert("Info Changed! Reload :0");
    window.location.reload();
}

//wawwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww

void sususamongs();
void applysus()