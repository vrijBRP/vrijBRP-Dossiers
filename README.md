# vrijBRP Dossiers

API used to create, update and search dossiers in vrijBRP.
These API's are based on OpenAPI 3.0 standard.

## License
Copyright &copy; 2021 Procura BV. \
Licensed under the [EUPL](https://github.com/vrijBRP/vrijBRP/blob/master/LICENSE.md)

## Getting started
_Committing this code to GitHub is a first step towards an open source BRP._ \
_This application could have maven dependencies that might not be publicly available at this moment._\
_It also depends on several components that will become open source later._

### Build

#### Build requirements
- Java 11
- Maven 3
- [Java Code formatting](https://github.com/vrijBRP/vrijBRP/blob/master/CONTRIBUTING.md)
- Eclipse code formatter (**Intellij**)
- Lombok plugin (**optional**)

#### Build commands
`mvn clean package` \
Create a run configuration to start `nl.procura.burgerzaken.dossiers.DossiersApplication`

### Run the application

#### Run requirements
- running vrijBRP Balie instance

#### Configuration
Create an `application.properties` file in the project root, e.g.:
```properties
server.port=8081
procura.gbasource.url=http://<server>:<port>/personen/rest
procura.gbasource.username=<vrijbrp-balie username>
procura.gbasource.password=<vrijbrp-balie password>
procura.personrecordsource.url=http://<server>:<port>/personen-ws/rest
procura.personrecordsource.username=<vrijbrp-balie-ws username>
procura.personrecordsource.password=<vrijbrp-balie-ws password>
# logging
logging.level.root=INFO
```

*'personen' is a module in project 'vrijbrp-balie'*

#### PostgreSQL configuration
1. Create database `dossiers`

#### Create API client
Run InitClient class with 4 parameters:
1. id: unique ID to be used in token request
2. scope: API scope, possible values: api, admin
3. customer: customer description
4. application: application description

InitClient returns a secret.

#### Authentication
A detailed description of the authentication flow can be found in `application/src/main/resources/static/public/authentication.pdf`.

In short, execute the following request with the basic authentication of `id` and `secret`:
```http request
POST /oauth/token HTTP/1.1
Host: localhost
Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
Content-Type: application/x-www-form-urlencoded

grant_type=client_credentials&scope=api
```

The resulting access token can be used as Bearer token in following requests.

## Docker image
A docker image will become available publically in the near future.
