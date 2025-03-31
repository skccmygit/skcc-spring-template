# New Tech VDI Setup
>
> Since external internet access is not allowed in New Tech VDI,  
> internal repository settings are required.  
> This guide assumes that basic installation and source download are completed.

## Project Execution Process

1. If `Gradle installation location` is not separately configured, the following error occurs:
    ![img.png](images/install/new-tech-vdi-error1.png)
    Set the IntelliJ Gradle installation path. [Link](./910-install-basic-guide_en#4-gradle-installation-and-setup)
2. After setting the Gradle installation path, the following Gradle build error occurs:  
   _Maven Repository and Plugin Portal access is blocked, preventing retrieval of related plugins
   ![img_1.png](images/install/new-tech-vdi-error2.png)
3. Modify repository paths in `settings.gradle` and `build.gradle`, then refresh gradle
   - build.gradle (Apply internal Nexus service flow URL)
    ```groovy
    repositories {
        maven {
            url 'https://gdi-nexus.gdi.cloudzcp.net/repository/maven-sflowsla-group'
        }
        //mavenCentral()
    }
    ```
   - settings.gradle (Apply internal Nexus service flow URL)
    ```groovy
    // Closed Network
    pluginManagement {
     repositories {
        maven {
              url 'https://gdi-nexus.gdi.cloudzcp.net/repository/maven-sflowsla-group'
        }
     }
    }
    rootProject.name = 'skcc-spring-template'
    ```

## Reference Notes
- New Tech VDI Nexus URL: https://gdi-nexus.gdi.cloudzcp.net
- **Reason for not using maven central proxy**  
  The library (develocity-gradle-plugin:3.18.2) from the **gradle plugin portal** could not be retrieved, so it was manually uploaded to the service flow nexus.  
- Internal nexus maven central URL: https://gdi-nexus.gdi.cloudzcp.net/repository/maven-central  
- Nexus group creation will be required for future publishing needs 