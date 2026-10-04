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
            <div class="nametag" data-inner-container-boxing="true" >
                ${oscrims[i].identifier}
            </div>
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
            <div class="nametag" data-inner-container-boxing="true" >
                ${ascrims[i].identifier}
            </div>
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
}

void geebyegeegun();