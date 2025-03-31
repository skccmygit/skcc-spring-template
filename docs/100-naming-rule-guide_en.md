# Java Naming Rule Guide
> Consistent Naming Rules are crucial for improving readability and maintainability in Java application development.
> This guide defines standards for Naming Rules to help enhance code quality during collaboration.

---
## Table of Contents
1. [Naming Conventions](#1-naming-conventions)  
   1.1 [Basic Naming Rules](#11-basic-naming-rules)  
   1.2 [Special Abbreviation Rules](#12-special-abbreviation-rules)

2. [Java Naming Rules](#2-java-naming-rules)  
   2.1 [Package Naming Rules](#21-package-naming-rules)  
   2.2 [Method Naming Rules](#22-method-naming-rules)  
   2.3 [Variable Naming Rules](#23-variable-naming-rules)  
   2.4 [Constant Naming Rules](#24-constant-naming-rules)

3. [Additional Recommendations](#3-additional-recommendations)

--- 
## **1. Naming Conventions**

### 1.1 Basic Naming Rules
- **Use Understandable Full English Descriptions**: Names should clearly indicate their role.
- **Consistently follow CamelCase or Snake_Case conventions**.
- **When combining two words**: Capitalize the first letter of the second word.  
  Example: `getCodeList`, `toModelWithChild`
- **Follow widely used forms for initials and abbreviations**.  
  Example: `HTML`, `URL`, `DAO` remain in uppercase.
- **Length should not exceed 30 characters**:
    - Class, interface, method, variable, constant, and file names should be 30 characters or less for readability.
- Do not use Java reserved words (`final`, `class`, `void`, etc.).

---

### 1.2 Special Abbreviation Rules
- Common abbreviations (`URL`, `HTML`, etc.) remain in uppercase.
- When special abbreviations are mixed between words, follow regular CamelCase.  
  Example: `parseHTML`, `encodeURL`

---

## **2. Java Naming Rules**

### 2.1 Package Naming Rules
- Package names must use **lowercase** only.
- Package structure should be hierarchically designed to clearly show modularization.  
  Example: `com.project.module.config`
- Each word should be 2-15 characters long in lowercase English.
- Separate business domain and technical areas for detailed design.

#### **Package Level Structure Example**

| Level  | Name             | Description                     |
|--------|------------------|---------------------------------|
| 1      | app              | Basic Application Structure     |
| 1      | common           | Common Modules (Utils, Domain)  |
| 1      | business         | Business Logic Area            |
| 2      | dto              | Application DTO                |
| 2      | service          | Service Logic Area             |
| 2      | domain           | Domain Models                  |
| 2      | repository       | Data Access Layer              |
| 3      | jpa              | JPA Implementation Module      |
| 3      | mybatis          | MyBatis Implementation Module  |
| 2      | controller       | Web Controller Related         |

---

### 2.2 Method Naming Rules
- Methods **describe actions** within a class.
- Generally start with a **verb** and use **CamelCase**.
- Recommend using `verb + noun` form to convey clear meaning.

| Method Name     | Description                    |
|-----------------|--------------------------------|
| `getCode`       | `Code Entity` Detail Query     |
| `getCodeList`   | List Query Method             |
| `validUser`     | User Validation Method         |
| `isAvailable`   | Used for Status/Property Check |
| `hasPermission` | Permission Check Method        |

---

### 2.3 Variable Naming Rules
- Variable names should **avoid abbreviations** and clearly convey meaning.
- Use **CamelCase** and start with lowercase.
- Do not use underscore(_) or special characters (e.g., `$`) at the beginning.

| Example Variable Name | Description                |
|----------------------|----------------------------|
| `userName`           | User Name                  |
| `userId`             | User ID                    |
| `totalCount`         | Total Count                |
| `isLoggedIn`         | Login Status Flag          |

---

### 2.4 Constant Naming Rules
- Constants must be declared as `static final`.
- Use **UPPER_SNAKE_CASE** with underscores(`_`) between words.
- Do not start with underscore(_) or special characters($).

| Example Constant Name    | Description                    |
|-------------------------|--------------------------------|
| `DEFAULT_USER_ROLE`     | Default User Role              |
| `MAX_PAGE_SIZE`         | Maximum Page Size              |
| `DATE_FORMAT_YYYYMMDD`  | Date Format (YYYY-MM-DD)       |

---

## **3. Additional Recommendations**

- **Clearly convey purpose in names**: Method, variable, and class names should clearly indicate their purpose and what they do.
- **Choose words based on purpose**:
    - **For actions**: Use words like `get`, `find`, `update` as starting words.
    - **For status checks**: Use words like `is`, `has`, `validate`.
- **Maintain consistency**: Variables and methods with the same role should follow the same naming rules globally.
- **Use meaningful abbreviations only**: When abbreviations are necessary, use only those well-known within the team.

--- 