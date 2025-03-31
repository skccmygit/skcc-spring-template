# File Processing Guide
> This guide explains file upload and download functionality.
> For uploads, there are `policies` that manage features such as `size, extension, and database storage` according to the policy.
> It is recommended to extend policy items according to the project's requirements.

---

## Table of Contents
1. [Main Features](#1-main-features)
   - [1.1 Upload](#11-upload)
   - [1.2 Download](#12-download)
2. [Environment Configuration](#2-environment-configuration)
   - [2.1 filePolicy-{profile}.yml](#21-filepolicy-profileyml)
     - [Upload Policy Properties Reference](#upload-policy-properties-reference)
     - [2.2 application-{profile}.yml](#22-application-profileyml)
3. [Reference Notes](#3-reference-notes)

---

## 1. Main Features

### 1.1 Upload
Single and multiple file uploads are supported.
The **policy name** is required for uploads. The policy determines the upload path and whether to store the file in the database.

| Method | Path              | Description              | Notes |
|--------|------------------|--------------------------|-------|
| POST   | /upload          | Uploads a single file    |       |
| POST   | /upload/multiple | Uploads multiple files   |       |

### 1.2 Download
Downloads can be performed using either the `complete file path` or `database-stored file ID`.
The `FileDownloadRequest` object is used for downloads.

| Method | Path       | Description           | Notes |
|--------|------------|----------------------|-------|
| POST   | /download  | Downloads a file      |       |

## 2. Environment Configuration

### 2.1 filePolicy-{profile}.yml 
Define policy properties by declaring desired policy keys from the 3rd depth.
Note that the following content is mapped directly to the `UploadPolices.java` file.

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

#### **Upload Policy Properties Reference**

| Property           | Description           | Type          | Notes              |
|-------------------|----------------------|---------------|-------------------|
| maxFileSize       | Maximum file size    | Number        | 5242880 # 5MB    |
| uploadDir         | Storage path        | String        |                   |
| allowedExtensions | Allowed extensions   | String Array  | Array format      |
| saveDb            | Database storage    | boolean       | true, false      |

### 2.2 application-{profile}.yml
- Import external `file environment file` using the following content in application-{profile}.yml:
  ```yaml
  spring:
    config:
      import: classpath:filePolicy-dev.yml
  ```

## 3. Reference Notes

- `FileModel` was defined to avoid confusion with Java's internal File class.
- `FileCreate` contains internal business logic. Therefore, if there are changes in file creation (upload) related business logic, only the logic within this object needs to be modified.
- `File Size Limitation`    
  File size is limited during upload. The policy's size limit applies to individual files. If you want to limit the total size of multiple files, modify the FileService logic.   
  Additionally, the total file size is related to Spring Framework memory. In this guide, the SpringFramework servlet environment is configured for approximately 100MB.
  ```yaml
  spring:
    servlet:
      multipart:
      max-file-size: 100MB
      max-request-size: 105MB # Maximum size of total request (files + data)
  ``` 