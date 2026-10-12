var filled = false;
var place;

async function geebyegeemap() {
    const response = await fetch('http://localhost:8080/gmapApi');
    const jsonresp = await response.json();
    const yummykey = jsonresp.url;
    console.log(yummykey);

    (g=>{var h,a,k,p="The Google Maps JavaScript API",c="google",l="importLibrary",q="__ib__",m=document,b=window;b=b[c]||(b[c]={});var d=b.maps||(b.maps={}),r=new Set,e=new URLSearchParams,u=()=>h||(h=new Promise(async(f,n)=>{await (a=m.createElement("script"));e.set("libraries",[...r]+"");for(k in g)e.set(k.replace(/[A-Z]/g,t=>"_"+t[0].toLowerCase()),g[k]);e.set("callback",c+".maps."+q);a.src=`https://maps.${c}apis.com/maps/api/js?`+e;d[q]=f;a.onerror=()=>h=n(Error(p+" could not load."));a.nonce=m.querySelector("script[nonce]")?.nonce||"";m.head.append(a)}));d[l]?console.warn(p+" only loads once. Ignoring:",g):d[l]=(f,...n)=>r.add(f)&&u().then(()=>d[l](f,...n))})({
        key: "AIzaSyBndwE5U3xdTC9dochw9KrTgdbObujYH8E",
        v: "weekly"
    });
}

async function geebyegeegun() {
    const elementofsurprise = document.getElementById("evil-container");

    const team = await fetch("getUserInfo");
    const orgsjon = await team.json();
    const jonathan = orgsjon.user;

    console.log(jonathan);

    const oscrims = jonathan.organizedScrimmages;
    console.log(oscrims);
    if (oscrims && oscrims.length > 0) {
        for (let i = 0; i < oscrims.length; i++) {

            var jorban = document.createElement('div')
            jorban.setAttribute("class", "box")
            jorban.setAttribute("data-appstatus", "OPEN")

            jorban.innerHTML=`
            <a href="scrim/${oscrims[i].identifier}">
                <div class="nametag" data-inner-container-boxing="true">
                    ${oscrims[i].identifier}
                </div>
            </a>
            <div class="waw">
                <button class="edityummer" data-beegbutton="true">
                    edit
                </button>
                <button class="deleteyummer" data-beegbutton="true">
                    delete
                </button>
                <button class="shareyummer" data-beegbutton="true">
                    share
                </button>
            </div>
        `;

            elementofsurprise.appendChild(jorban);
        }
    }

    const ascrims = jonathan.activeScrimmages;
    console.log(ascrims);
    if (ascrims && ascrims.length > 0) {
        for (let i = 0; i < ascrims.length; i++) {

            var jorban = document.createElement('div')
            jorban.setAttribute("class", "box")
            jorban.setAttribute("data-appstatus", "CLOSED")

            jorban.innerHTML=`
            <a href="scrim/${ascrims[i].identifier}">
                <div class="nametag" data-inner-container-boxing="true">
                    ${ascrims[i].identifier}
                </div>
            </a>
            <div class="waw">
                <button class="deleteyummer" data-beegbutton="true">
                    delete
                </button>
                <button class="shareyummer" data-beegbutton="true">
                    share
                </button>
            </div>
        `;

            elementofsurprise.appendChild(jorban);
        }
    }
}

async function stuffystuffy(date, num, place, reg, status) {
    const url = `http://localhost:8080/createScrim`;

    let placestuff = place.addressComponents;

    let snum0 = '';
    let road0 = '';
    let city0 = '';
    let state0 = '';
    let country0 = '';
    let lat0 = place.location.lat();
    let long0 = place.location.lng();

    placestuff.forEach(placeitem => {
        if (placeitem.types) {
            if (placeitem.types[0] === "street_number") {
                snum0 = placeitem.shortText;
            }
            if (placeitem.types[0] === "route") {
                road0 = placeitem.shortText;
            }
            if (placeitem.types[0] === "locality") {
                city0 = placeitem.shortText;
            }
            if (placeitem.types[0] === "administrative_area_level_1") {
                state0 = placeitem.shortText;
            }
            if (placeitem.types[0] === "country") {
                country0 = placeitem.shortText;
            }
        }
    });

    var token = document.querySelector("meta[name='_csrf']").getAttribute("content");

    const bodeh =
        {
            region: reg,
            appStatus: status,
            size: num,
            startTime: date,

            location: {
                longitude: long0,
                latitude: lat0,
                address: snum0 + " " + road0,
                city: city0,
                state: state0,
                country: country0
            }
        }

    await fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': "application/json",
            'X-CSRF-TOKEN': token
        },
        body: JSON.stringify(bodeh)
    }).then(r => {console.log(`response recieved: ${r}`); window.location.reload()});

    window.alert("Info Changed! Reload :0");
    window.location.reload();
}

async function setupdialog() {
    const speaker = document.getElementById("dialog");
    const scrimmy = document.getElementById("createScrim");
    const greb = document.getElementById("greb");
    const form = document.getElementById("beegform");

    scrimmy.addEventListener("click", action => {
        speaker.showModal();
    });

    // Request needed libraries.
    const { PlaceAutocompleteElement } =
        await google.maps.importLibrary('places');
    // Create the input HTML element, and append it.
    const placeAutocomplete = new PlaceAutocompleteElement();
    placeAutocomplete.id = "guac";
    document.getElementById("fancyone").appendChild(placeAutocomplete);

    placeAutocomplete.addEventListener('gmp-select', async ({ placePrediction }) => {
        filled = true;

        place = placePrediction.toPlace();

        await place.fetchFields({ fields: ['displayName', 'formattedAddress', 'location', 'addressComponents'] });

        console.log(place.addressComponents);

    });

    placeAutocomplete.addEventListener('input', async ({ placePrediction }) => {
        filled = false;
    });

    form.addEventListener("submit", function(e) {
        e.preventDefault();

        console.info(document.getElementById("storttime").value);


        if (!filled) {
            window.alert("no.");
            event.preventDefault();
        } else {
            stuffystuffy(
                document.getElementById("storttime").value,
                document.getElementById("sizer").value,
                place,
                document.getElementById("regiob").value,
                document.getElementById("stat").value);
            document.getElementById("beegform").submit();
        }
    });
}

void geebyegeemap();
void setupdialog();
void geebyegeegun();