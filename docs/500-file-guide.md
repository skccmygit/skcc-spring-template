# 파일 처리가이드
> 본 가이드는 파일을 업로드 및 다운로드 기능에 대해서 설명합니다.
> 업로드의 경우 `정책`이 존재하며 정책에 따라 `크기,확장자,db저장여부` 기능을 관리합니다.  
> 프로젝트의 성격에 맞게 정책 항목은 확장하는 것을 권장합니다.

---
## 목차
1. [주요기능](#1-주요기능)
   - [1.1 업로드](#11-업로드)
   - [1.2 다운로드](#12-다운로드)
2. [환경설정](#2-환경설정-)
   - [2.1 filePolicy-{profile}.yml](#21-filepolicy-profileyml)
     - [업로드정책 속성 참고](#업로드정책-속성은-아래-표를-참고)
     - [2.2 application-{profile}.yml](#22-application-profileyml)
3. [참고사항](#3-참고사항)
---
## 1. 주요기능
### 1.1 업로드
단건 업로드 및 다건 업로드를 이용할 수 있습니다.
업로드시 **정책명**은 필수 입니다. 정책에 따라 해당 파일을 업로드할 경로 및 DB 저장여부를 판단 합니다.

| Method | 경로               | 설명             | 기타 |
|--------|------------------|----------------|----|
| POST   | /upload          | 단건파일을 업로드 합니다. |    |
| POST   | /upload/multiple | 다건파일을 업로드 합니다. |    |

### 1.2 다운로드
다운로드는 `파일전체경로`, `DB 저장 파일 ID` 로 다운로드할 수 있습니다.
다운로드시 FileDownloadRequest 객체를 활용합니다.  

| Method | 경로        | 설명            | 기타 |
|--------|-----------|---------------|----|
| POST   | /download | 파일을 다운로드 합니다. |    |

## 2. 환경설정 
### 2.1 filePolicy-{profile}.yml 
3 Depth 부터 원하는 정책Key를 선언하여 정책 속성들을 정의하면 됩니다.
참고로, 아래 내용이 `UploadPolices.java` 파일에 그대로 맵핑됩니다.

  ```yaml
  file:
    uploadPolices:
      temp:
        maxFileSize: 5242880 # 5MB
        uploadDir: "D:/dev/uploads/temp/{date:yyyyMMdd}"
        allowedExtensions: ["jpg","png","txt"]
        save-db: false
      document:
        maxFileSize: 10485760 # 10MB
        uploadDir: "D:/dev/uploads/documents/{date:yyyy}"
        allowedExtensions: ["pdf", "txt"]
        save-db: true
  ```

#### **업로드정책 속성은 아래 표를 참고**

| 속성                | 속성명                 | 타입           | 기타            |
|-------------------|---------------------|--------------|---------------|
| maxFileSize       | 최대 업로드 파일 크기        | Number       | 5242880 # 5MB |
| uploadDir         | 저장 경로               | String       |
| allowedExtensions | ["jpg","png","txt"] | String Array | 배열 형태로 저장     |
| saveDb            | Database 저장 여부      | boolean      | true, false   |


### 2.2 application-{profile}.yml
- application-{profile}.yml 내부의 아래 내용으로 외부의 `파일환경파일`을 import 합니다.
  ```yaml
  spring:
    config:
      import: classpath:filePolicy-dev.yml
  ```
## 3. 참고사항

- `FileModel`의 경우 File로 할 경우 java 내부의 File 클래스와 혼동이 있어 정의하였습니다.
- `FileCreate`의 경우 내부에 비즈니스 로직을 넣었습니다. 따라서, 파일생성(업로드) 관련 비즈니스 변경이 있을경우 해당 객체 내 로직만 수정하면 됩니다.
- `파일 크기 제한`    
  업로드 시, 파일 크기를 제한합니다. 정책의 크기 제한은 파일개별당 크기 입니다. 만약 다건 크기를 제한하고 싶다면 FileService 로직을 수정하면됩니다.   
또한 전체 파일 크기의 경우 Spring Framework 메모리와 연관성이 있습니다. 본 가이드에서는 약 100MB 정도로 SpringFramework 서블릿 환경 설정을 해놓았습니다. 
  ```yaml
  spring:
    servlet:
      multipart:
      max-file-size: 100MB
      max-request-size: 105MB # 전체 요청(파일 + 데이터)의 최대 크기
  ```
