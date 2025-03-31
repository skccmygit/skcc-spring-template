# Source Download and Execution
>
> This is a guide for downloading and executing the SpringBoot Template source.  
> This guide assumes that all pre-installation and settings are completed.  
> (Instructions are based on Intellij Community version)

## Source Download
>
> Repository address may change.

1. Create an arbitrary directory to store the source (example: C:\\workspace)  
![Create Directory](images/source/source-down-1.png)  

2. Right-click inside the directory folder and click `Open Git bash here`  
![Run GIT-Bash](images/source/source-down-2.png)

3. git clone
   - git repo address: <https://github.com/skccmygit/skcc-spring-template.git>  
  **Address may change.**  
  ![Git-Clone-Source](images/source/source-git-clone.png)

## Source Execution
>
> Instructions are based on Intellij Community version.

1. Run Intellij
2. File - Open - Select the downloaded source directory  
   ![IDE-Open-Project](images/source/source-ide-selec.png)
3. When opening the Project, Gradle automatically recognizes and performs `automatic library download`  
   **If separate Nexus Repository settings** are needed, configure repo URLs in `build.gradle` and `settings.gradle`
   - build.gradle (Internal gdi configuration)

      ```gradle
      repositories {
         // For closed networks, use internal Nexus
         maven {
             url 'https://gdi-nexus.gdi.cloudzcp.net/repository/maven-central'
         }
         //mavenCentral()
      }
      ```

   - settings.gradle (Internal gdi configuration)

      ```gradle
      // Closed Network
      pluginManagement {
         repositories {
            maven {
                  url 'https://gdi-nexus.gdi.cloudzcp.net/repository/maven-central'
            }
         }
      }
      rootProject.name = 'skcc-spring-template'
      ```

4. If **JDK settings are not configured**, set JDK in project settings (File-Project-Structure or shortcut [Ctrl+Alt+Shift+S])
   ![IDE-SET-JDK](images/source/source-ide-jdk.png)

5. Navigate to Spring Main Class source → Click run → Click `modify Run Configuration`
   ![Source Execution](images/source/source-exe.png)

6. Click `Modify Options`
   ![Run Configuration](images/source/source-ide-run-config.png)

7. Click `Add VM options`  
   ![ADD-VM](images/source/source-ide-add-vm.png)

8. Enter `-Dspring.profiles.active=local` (for local environment execution, enter environment-specific variables)  
   ![ADD-VM-VALUE](images/source/source-ide-add-vm-value.png)

9. Run `Application` (triangle at the top)
   ![img.png](images/source/source-ide-execute.png)  
   Execution Result
   ![img_1.png](images/source/source-ide-execute-result.png)
   ![img_1.png](images/source/source-ide-execute-result2.png) 