*Paula Soriano Sánchez (843710)*
# Lab 1 Git Race -- Project Report

## 1. Description of Changes
The several changes which have been made in this proyect are described in this section.

### 1.1. Customize the Whitelabel Error Page
The default Spring Boot Whitelabel Error Page was replaced with a custom, responsive template named `error.html`, implemented with Thymeleaf. The HTML and CSS design of this page was created by ChatGPT in order to speed up the process of design and stored in the `src/main/resources/templates` directory so that Spring Boot automatically serves it when a non-existent route is requested. A Kotlin test validates the implementation by sending a request to the invalid URL: `http://localhost:$port`, confirming a 404 status and checking that the response contains the distinctive title and header of the new page.

To validate this behavior, a Kotlin integration test named `in case of error it returns the error page created` was generated using Spring Boot’s testing framework. The test starts the application on a random port, sends an HTTP GET request to a path that does not exist, and includes an `Accept: text/html` header to request an HTML response. It then asserts that the returned status is 404 Not Found, that the Content-Type begins with `text/html`, and that the body of the response contains the custom `<title>` and `<h1>` elements defined in `error.html`, ensuring the Thymeleaf template is correctly rendered.

### 1.2. Implement `/time` endpoint
It consists on a simple Spring Boot web service written in Kotlin that provides the current date and time.
A data class (TimeDTO) represents the time information, while a service layer (TimeService) implements a TimeProvider interface to supply the current LocalDateTime.
A REST controller exposes the endpoint /time, returning the time data as JSON.
The test implemented is named `time returns current time` and uses TestRestTemplate to verify that the `/time` endpoint returns a successful HTTP response with a JSON body containing the time field.


### 1.3. Enable HTTP/2 and SSL with a PKCS12 keystore
The Spring Boot application was configured to support HTTPS and HTTP/2 using a self-signed certificate. A private key and a public certificate were generated, then combined into a PKCS12 keystore, which was placed in the project's resources directory. The application’s configuration was updated to enable SSL and HTTP/2 on port 8443, specifying the keystore and password.

The SSL setup was tested using a browser, where HTTPS connections were established despite browser warnings due to the self-signed certificate. HTTP/2 protocol was confirmed by inspecting network requests in the browser’s developer tools.

However, attempts to test HTTP/2 using Windows’ default curl failed because the installed version did not support HTTP/2 (see the error in section *4.2.3. Bug Fixes and Guidance*).


## 2. Technical Decisions
Several technical decisions were made during the project:

- **Testing strategy**: Integration tests were prioritized to validate the http status of the request as well as the behaviour of the endpoint.

- **Use of WSL to work with OpenSSL**: Instead of installing OpenSSL directly on Windows, which can be more complex and prone to compatibility issues, WSL was used to run OpenSSL in a Linux-like environment. This simplified the generation of SSL certificates and avoided problems with native Windows installations

## 3. Learning Outcomes
Through the completion of this lab, several technical and methodological lessons were learned:

- **Certificate management**: Understand and practice generating, configuring, and debugging SSL certificates.

- **Testing practices**: Strengthened knowledge in writing integration tests with TestRestTemplate, managing request headers and validating responses with different Content-Types.

- **Debugging cross-platform issues**: Learned how to identify and resolve environment-specific problems, such as PowerShell curl aliasing and OpenSSL configuration errors.

## 4. AI Disclosure
### 4.1. AI Tools Used
The main AI tool used in this project was ChatGPT (OpenAI) and it was employed for generating HTML and CSS templates for the custom error page, providing debugging suggestions and explanations for common Spring Boot issues, and refining the documentation to improve clarity, conciseness and professionalism.

### 4.2. AI-Assisted Work
This section describes the specific parts of the project where AI assistance was used.

#### 4.2.1. Development of a customize Error Page
To speed up the process of designing a custom error page, ChatGPT was used to develop a custom, responsive template named `error.html`, using Thymeleaf.
This provided a styled 404 page that could be directly integrated into the Spring Boot application.

#### 4.2.2. Documentation
It was also used to rephrase and refine documentation to make it more clear, structured, and professional. The AI helped ensure that descriptions of functionalities, configurations, and test cases are comprehensive and easily understandable by external reviewers.

#### 4.2.3. Bug Fixes and Guidance
In addition to the previous point, ChatGPT has been used in order to provide help with several bugs that have appeared during development.

- **Missing Accept: text/html header**
The problem was that the server returned a default representation (not in HTML format), so the test could not reliably check for HTML content.
The solution was to send the proper header using `TestRestTemplate.exchange` with an `HttpEntity` that sets `accept = listOf(MediaType.TEXT_HTML)` applied to headers.

- **Content-Type includes a charset**
The assertion `assertThat(response.headers.contentType).isEqualTo(MediaType.TEXT_HTML)` failed because Spring Boot returns `Content-Type: text/html;charset=UTF-8`, while `MediaType.TEXT_HTML` does not include the charset.
The solution was to relax the check to ignore the charset as the following: `assertThat(response.headers.contentType?.toString()).startsWith("text/html")`.

- **Posible null in Content-Type**
The problem was that the tests named `in case of error it returns the error page created` and `time returns current time` could fail if the response does not include a `Content-Type`, as 404, 204, or redirects do not always include a Content-Type.
The solution was to treat contentType as nullable in Kotlin (?.) to avoid exceptions. This has been applied in the following lines: `assertThat(response.headers.contentType?.toString()).startsWith("text/html")` and `assertThat(response.headers.contentType?.toString()).startsWith("application/json")`.

- **Error generating the certificate with OpenSSL in WSL**
The error that appeared in WSL was the following: `Error checking x509 extension section EXT when using <( … )`.
The cause was that OpenSSL could not read the extension section with process substitution.
The solution was to create a configuration file `localhost.cnf` with `[req]`, `[dn]`, and `[EXT]` sections, and run OpenSSL using `-config localhost.cnf -extensions EXT`. The file `localhost.cnf` is located in the `src/main/resources` directory. 

- **Curl alias issue in PowerShell**
The problem was that appeared an error when running `curl -H ...`, which was: `CannotConvertArgumentNoMessage`.
It was because PowerShell aliases curl to Invoke-WebRequest, which does not accept Linux-style flags.
For that reason, the solution was to use `curl.exe` to call the real Windows version of curl.

- **Curl does not support HTTP/2 on some versions of Windows**
Trying to test if the SSL setup worked, this error appeared in the windows terminal: `the installed libcurl version does not support this`. This means that the default Windows curl does not include HTTP/2 support.
The solution was to do the verification of HTTP/2 using browser developer tools.

#### 4.2.3. Percentage of AI-assisted vs. original work
AI suggestions have been adapted to match project-specific configurations.
Moreover, AI-generated documentation was refined to align with report style and structure requirements, carefully reviewing every part to ensure it accurately reflects the original intentions of the developer and says exactly what she decided to convey from the beginning.

#### 4.2.4. Modifications made to AI-generated code
Approximately 30% of the work was AI-assisted and 70% was original work.
AI was primarily used for accelerating repetitive tasks such as generating HTML templates, providing debugging hints, and improving documentation style.

### 4.3. Original Work
The majority of the project code. While AI provided guidance and templates, the logic, structure, and debugging decisions reflect the developer’s own understanding and implementation.