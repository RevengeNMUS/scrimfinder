const url = `http://localhost:8080/getScrims?identifier=${window.location.pathname.split("/").at(-1)}`;

async function getTemu() {
    const fet = await fetch(url).then(async (feet) => {
        if (!feet.ok) {
            throw Error("ruh roh L bozo");
        }

        return await feet.json();
    }).catch((error) => {console.error(error)});

    let feetFarm = fet[0];

    document.getElementById("region").innerText = feetFarm.region;
    document.getElementById("identifier").innerText = feetFarm.identifier;
    document.getElementById("identifier").setAttribute("data-appstatus", feetFarm.appStatus);
    document.getElementById("date").innerText = feetFarm.endTime.substring(0, 10);
    document.getElementById("status").innerText = feetFarm.appStatus;

    var tbox = document.getElementById("teamsbox");
    let otNum = feetFarm.organizer.teamNum;
    let olink = `http://localhost:8080/team/${otNum}`;
    let boinkuslink = document.createElement('a');
    let boinkusdiv = document.createElement('div');
    boinkusdiv.innerText = `${otNum}*`;
    boinkusdiv.className = "t-num-box";
    boinkusdiv.id = "oteam";
//    boinkusdiv.setAttribute("data-subelement-boxing", "true");
    boinkuslink.setAttribute("href", olink);
    boinkuslink.appendChild(boinkusdiv);
    tbox.appendChild(boinkuslink);

    for (let i = 0; i<feetFarm.teams.length; i++) {
        let tNum = feetFarm.teams[i].teamNum;
        let link = `http://localhost:8080/team/${tNum}`;
        let boinkuslink = document.createElement('a');
        let boinkusdiv = document.createElement('div');
        boinkusdiv.innerText = tNum;
        boinkusdiv.className = "t-num-box";
        boinkusdiv.setAttribute("data-subelement-boxing", "true");
        boinkuslink.setAttribute("href", link);
        boinkuslink.appendChild(boinkusdiv);
        tbox.appendChild(boinkuslink);
    }

    document.getElementById("adder").innerText = `${feetFarm.location.address}, ${feetFarm.location.city}, ${feetFarm.location.state}`;
    document.getElementById("adderatttag").setAttribute("href", `https://maps.google.com/?ll=${feetFarm.location.latitude},${feetFarm.location.longitude}`);
}

async function startgeemap(lat, long) {
    const {InfoWindow} = await google.maps.importLibrary("maps");
    const [{ AdvancedMarkerElement }] = await Promise.all([
        google.maps.importLibrary('marker')
    ]);

    const map = mapElement.innerMap;


}

void getTemu();