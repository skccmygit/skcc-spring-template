# SK Internal Network Additional Settings
>
> In the internal network environment, while internet access is allowed, there may be certificate trust issues with `*.gradle.org`.  
> Therefore, you need to add the internal ROOT certificate to the default Java Home's certificate store (cacert).

## Project Gradle Build Error

![error-cert](images/install/intranet-error-cert.png)

### Solution
>
> Add SK Internal ROOT Certificate

- Navigate to the SK ROOT certificate file directory (sk.crt file in the project's docs/ssl folder)
- Add the certificate using the keytool command

```bash
keytool -import -trustcacerts -file 'certificate_filename' -keystore $env:JAVA_HOME/lib/security/cacerts -storepass changeit -alias 'certificate_alias'
```

- Execution screen
![cert-import](images/install/intranet-error-cert-1.png)

- After restarting IntelliJ, click on gradle build to download the related jar files.

## Reference Notes

### Extracting SK ROOT Certificate Using OpenSSL

- You can extract all SSL chain certificates using the openssl command.

```bash
openssl s_client -connect host:port -showcerts
# openssl s_client -connect plugins.gradle.org:443 -showcerts
```

- Extract the SK certificate portion from the above result and create a CRT file.
![cert-export-by-openssl](images/install/intranet-error-appendix-1.png) 