# accounts-service
Wrapper around [Keycloak](https://www.keycloak.org/). Provides 0Auth2 custom authentication using keycloak api to minimize direct access to keycloak server.
1. First you need to ensure that keycloak server is running. run `docker compose up -d` to run keycloak and other servce that this application depends on.
2. Create a realm and client with all the neccessary roles and permissions. to learn more about keycloak please refer to the following [docs](https://www.keycloak.org/documentation)
3. Enusre all the required environment variables are privided before your start the application.

