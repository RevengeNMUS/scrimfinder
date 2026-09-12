const mapElement = document.querySelector('gmp-map');

async function init() {
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

    const url = 'http://localhost:8080/getScrims';

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
                boxbox.className = "closeTags";
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
                    <a href="http://localhost:8080/findScrim/${scrims[i].identifier}" target="_blank" style="color: dodgerblue; text-decoration: none;font-weight: 500; font-family: Arial, ui-rounded;">
                        <div id="identifier" style="font-size: medium;font-weight: 700;color: var(--fly);">
                            ${scrims[i].identifier}
                        </div>
                    </a>
                    <a href="/team/${scrims[i].organizer.teamNum}" target="_blank" style="color: dodgerblue; text-decoration: none;font-weight: 500; font-family: Arial, ui-rounded;">
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

void init();
