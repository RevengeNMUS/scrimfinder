const mapElement = document.querySelector('gmp-map');
const url = `http://localhost:8080/getScrims${location.search}`;

function buttonClicked(id) {
    var button = document.getElementById(id);
    if(button.getAttribute("aria-selected") === "false"){
        button.setAttribute("aria-selected", "true");
    } else {
        button.setAttribute("aria-selected", "false");
    }
}


async function initGeeMapelectricbugaloo() {
    try {
        const response = await fetch('http://localhost:8080/gmapApi');
        const jsonresp = await response.json();
        const yummykey = jsonresp.url;
        console.log(yummykey);

        (g=>{var h,a,k,p="The Google Maps JavaScript API",c="google",l="importLibrary",q="__ib__",m=document,b=window;b=b[c]||(b[c]={});var d=b.maps||(b.maps={}),r=new Set,e=new URLSearchParams,u=()=>h||(h=new Promise(async(f,n)=>{await (a=m.createElement("script"));e.set("libraries",[...r]+"");for(k in g)e.set(k.replace(/[A-Z]/g,t=>"_"+t[0].toLowerCase()),g[k]);e.set("callback",c+".maps."+q);a.src=`https://maps.${c}apis.com/maps/api/js?`+e;d[q]=f;a.onerror=()=>h=n(Error(p+" could not load."));a.nonce=m.querySelector("script[nonce]")?.nonce||"";m.head.append(a)}));d[l]?console.warn(p+" only loads once. Ignoring:",g):d[l]=(f,...n)=>r.add(f)&&u().then(()=>d[l](f,...n))})({
            key: yummykey,
            v: "weekly"
        });

        void initNutsAndBolts();
        void initRubberRoom();
        void initGeemap();

    } catch (error) {
        console.error("Failed to initialize Google Maps:", error);
    }
}

function initNutsAndBolts() {
    const mparam = new URLSearchParams(window.location.search);

    if(mparam.has('region')) {
        const region = mparam.get('region');
        const regionTag = document.getElementById(region);
        regionTag.setAttribute("selected", "selected");
    }

    if(mparam.has('identifier')) {
        const identifie = mparam.get('identifier');
        const scrimsearcher = document.getElementById("scrimsearcher");
        scrimsearcher.setAttribute("content", identifie);
    }

    if(mparam.has('appStatus')){
        const appStatus = mparam.get('appStatus');
        if(appStatus === "OPEN") {
            const button = document.getElementById("openfilterbutton");
            button.setAttribute("aria-selected", "true");
        }
    }
}

async function initRubberRoom() {
    try {
        var scontainer = document.getElementsByClassName("scrims-container")[0];
        const fet = await fetch(url);

        if (!fet.ok) {
            throw new Error(`HTTP error! Status: ${fet.status}`);
        }

        const scrims = await fet.json();
        console.log(scrims);

        for(let i = 0; i<scrims.length; i++) {
            var scrim = scrims[i];
            let sillybilly = document.createElement('div');
            sillybilly.className = 'scrim-box';
            sillybilly.id = `scrim-box-${scrim.identifier}`;
            sillybilly.innerHTML =
                `<div id="status" class="${scrim.appStatus}label">${scrim.appStatus}</div>
                <a href="/scrim/${scrim.identifier}" target="_blank"> <div id="identifier">${scrim.identifier}</div></a>
                <a href="/team/${scrim.organizer.teamNum}" target="_blank"> <div id="orgTeam">Org Team: ${scrim.organizer.teamNum}</div></a>
                <div id="teams">Teams: ${scrims[i].teams.length}/${scrims[i].size}</div>
                <div id="region">Region: ${scrim.region}</div>
                <div id="city">City: ${scrim.location.city}, ${scrim.location.state}</div>
                <div id="date">
                    <div id="dateTag">
                        Date:
                    </div>
                    <div id="dateTime">
                        ${scrim.endTime.substring(0, 10)}
                    </div>
                </div>`;
            scontainer.appendChild(sillybilly);
        }
    } catch (error) {//TODO ADDMINATIONS ADDIMIN ANIMATION SFAA
        console.error("fahhh DDDDDD: ", error);
    }
}

async function initGeemap() {
    const {InfoWindow} = await google.maps.importLibrary("maps");
    const [{ AdvancedMarkerElement }] = await Promise.all([
        google.maps.importLibrary('marker')
    ]);

    // Get the inner map.
    const innerMap = mapElement.innerMap;

    // Set map options.
    innerMap.setOptions({
        mapTypeControl: true,
    });

    try {
        const fet = await fetch(url);

        if (!fet.ok) {
            throw new Error(`HTTP error! Status: ${fet.status}`);
        }

        const scrims = await fet.json();
        console.log(scrims);

        for(let i = 0; i<scrims.length; i++) {
            console.log("\t", {lat: Number(scrims[i].location.latitude), lng: Number(scrims[i].location.longitude)});

            const marker = new AdvancedMarkerElement({
                map: innerMap,
                position: {lat: Number(scrims[i].location.latitude), lng: Number(scrims[i].location.longitude)},
//              title: scrims[i].applicationStatus.toString()
                gmpClickable: true,
                anchorTop: "-50%",
            });

            const boxbox = document.createElement('div');
            boxbox.textContent = scrims[i].appStatus;
            let eeatbacon;

            if (scrims[i].appStatus === "OPEN") {
                boxbox.className = "openTags";
                eeatbacon = "--open-status";
            } else {
                boxbox.className = "closedTags";
                eeatbacon = "--closed-status";
            }

            marker.append(boxbox);
            const infoWindow = new InfoWindow();

            marker.addEventListener('gmp-click', () => {
                infoWindow.close();
                infoWindow.setContent(`
                <div class = "scrim-box" style="margin: 10px; padding: 5px; padding-right: 10px; display: flex;flex-direction: column;background-color: var(--inner-box-background);border:5px solid var(--inner-box-border);box-shadow: 5px 5px var(--inner-box-border);border-radius: 10px;">
                    <div id="status" style="font-size: x-small;font-weight:900;color: var(${eeatbacon});text-align: left;margin: 2px 2px 2px 5px;font-family: Arial, ui-rounded;">
                        ${scrims[i].appStatus}
                    </div>
                    <a href="http://localhost:8080/scrim/${scrims[i].identifier}" target="_blank" style="color: dodgerblue; text-decoration: none;font-weight: 500; font-family: Arial, ui-rounded;">
                        <div id="identifier" style="font-size: medium;font-weight: 700;color: var(--fly);">
                            ${scrims[i].identifier}
                        </div>
                    </a>
                    <a href="/team/${scrims[i].organizer.teamNum}" target="_blank style="color: dodgerblue; text-decoration: none;font-weight: 500; font-family: Arial, ui-rounded;">
                        <div id="orgTeam" style="font-weight: 600;color: var(--non-header-text);font-size: small;text-align: center;">
                            Org Team: ${scrims[i].organizer.teamNum}"
                        </div>
                    </a>
                    <div id="teams" style = "font-weight: 600;color: var(--non-header-text);font-size: small;text-align: left; margin: 2px 2px 2px 5px;font-family: Arial, ui-rounded;">
                        Teams: ${scrims[i].teams.length}/${scrims[i].size}
                    </div>
                    <div id="region" style = "font-weight: 600;color: var(--non-header-text);font-size: small;text-align: left;margin: 2px 2px 2px 5px;font-family: Arial, ui-rounded;">
                        Region: ${scrims[i].region}
                    </div>
                    <div id="city" style = "font-weight: 600;color: var(--non-header-text);font-size: small;text-align: left;margin: 2px 2px 2px 5px;font-weight: 500;font-family: Arial, ui-rounded;">
                        City: ${scrims[i].location.city}, ${scrims[i].location.state}
                    </div>
                    <div id="date" style="margin: 2px 2px 2px 5px;font-weight: 500;font-family: Arial, ui-rounded;">
                        <div id="dateTag" style="color: var(--non-header-text);font-size: x-small;font-weight: 500;">
                            Date:
                        </div>
                        <div id="dateTime" style="color: var(--non-header-text);font-size: small;font-weight: 600;">
                            ${scrims[i].endTime.substring(0, 10)}
                        </div>
                    </div>
                </div>`);
                infoWindow.open({map: innerMap, anchor: marker});
            });
        }
    } catch (error) {
        console.error("fahhh: ", error);
    }
}

function igotthescript() {
    const ariaopenclose = document.getElementById("openfilterbutton").ariaSelected;
    let openclose = "";
    let searchstring = document.getElementById("scrimsearcher").value;
    let regionstring = document.getElementById("regions").value;
    let hasBefore = false;

    if(ariaopenclose === "true") {
        openclose = "appStatus=OPEN";
        hasBefore = true;
    }

    if (!(regionstring === "")) {
        if(hasBefore) {
            regionstring = `&region=${regionstring}`;
        } else {
            regionstring = `region=${regionstring}`;
        }
        hasBefore = true;
    }

    if (!(searchstring === "")) {
        if(hasBefore) {
            searchstring=`&identifier=${searchstring}`;
        } else {
            searchstring=`identifier=${searchstring}`;
        }
    }

    window.location.href = `/searchScrims?${openclose}${regionstring}${searchstring}`;
}

window.buttonClicked = buttonClicked;
window.igotthescript = igotthescript;

void initGeeMapelectricbugaloo();