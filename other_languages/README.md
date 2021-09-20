# Installing the Fleet Engine generated client for Node.js / TypeScript

Some operating systems have problems building the Fleet Engine generated client
from the [googleapis](https://github.com/googleapis/googleapis) repository.
As an alternative, the generated output is provided under
maps-fleetengine-v1-nodejs.

Clone the java-fleetengine-auth repository:
```
git clone https://github.com/googlemaps/java-fleetengine-auth.git
```

Navigate to your project directory and run the following:
```
cp -r /{root-git-directory}/java-fleetengine-auth/other_languages/maps-fleetengine-v1-nodejs/ .

&#35; Ensures that the generated source code is built before installing the module to your application
cd maps-fleetengine-v1-nodejs
npm install
cd ..

&#35; Install the dependencies
npm install maps-fleetengine-v1-nodejs
npm install google-auth-library

&#35; Needed to run sample code
npm install dotenv
```

Sample code:

```
const fleetengine = require("@googlemaps/fleetengine");
const { GoogleAuth } = require('google-auth-library');
require('dotenv').config();

const token = process.env.FLEETENGINE_TOKEN;
const project_id = 'project-id';

async function main() {
    let auth = new GoogleAuth();
    auth.cachedCredential = new AuthorizationHeaderProvider();
    const vehicleClient = new fleetengine.VehicleServiceClient({
        auth: auth
    });

    let response = vehicleClient.listVehicles({parent: `providers/${project_id}`});
    console.log(response);
}

class AuthorizationHeaderProvider {
    getRequestMetadata(url, callback) {
        callback(null, {'authorization': `Bearer ${token}`});
    }
}

main().catch(console.error);
```
