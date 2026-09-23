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

    startgeemap(feetFarm.location.latitude,feetFarm.location.longitude);
}

async function startgeemap(lat, long) {
    const [{ AdvancedMarkerElement }] = await Promise.all([
        google.maps.importLibrary('marker')
    ]);

    const mapElement = document.querySelector('gmp-map');

    const map = mapElement.innerMap;

    mapElement.setAttribute("center", `${lat},${long}`)

    new AdvancedMarkerElement({
        title: 'Scim :0',
        map: map, //todo genuinely ohw the hel are we doing join scrims
        position: {lat: lat, lng: long},
        gmpClickable: true
    })
}

async function initGeeMap() {
    try {
        const response = await fetch('http://localhost:8080/gmapApi');
        const jsonresp = await response.json();
        const yummykey = jsonresp.url;
        console.log(yummykey);

        (g=>{var h,a,k,p="The Google Maps JavaScript API",c="google",l="importLibrary",q="__ib__",m=document,b=window;b=b[c]||(b[c]={});var d=b.maps||(b.maps={}),r=new Set,e=new URLSearchParams,u=()=>h||(h=new Promise(async(f,n)=>{await (a=m.createElement("script"));e.set("libraries",[...r]+"");for(k in g)e.set(k.replace(/[A-Z]/g,t=>"_"+t[0].toLowerCase()),g[k]);e.set("callback",c+".maps."+q);a.src=`https://maps.${c}apis.com/maps/api/js?`+e;d[q]=f;a.onerror=()=>h=n(Error(p+" could not load."));a.nonce=m.querySelector("script[nonce]")?.nonce||"";m.head.append(a)}));d[l]?console.warn(p+" only loads once. Ignoring:",g):d[l]=(f,...n)=>r.add(f)&&u().then(()=>d[l](f,...n))})({
            key: yummykey,
            v: "weekly"
        });

        void getTemu(); //todo make decider page that checks if user logged in and direct to homepage OR OR OR dashboard
    } catch (error) {
        console.error("Failed to initialize Google Maps:", error);
    }
}

void initGeeMap();