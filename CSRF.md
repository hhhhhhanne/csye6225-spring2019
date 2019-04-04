Why CSRF can not be implemented?
Firstly all our requests are cross-site. CSRF will check tokens from post requests, but we cannot generate legal tokens from Postman as we need to first register a user to get a legal token. The registration process need post request.
So, we fall into a loop and we can never get a legal token.
