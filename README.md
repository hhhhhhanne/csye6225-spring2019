# CSYE 6225 - Spring 2019

## Team Information

| Name | NEU ID | Email Address |
| --- | --- | --- |
| Xiaohan Zhao | 001825212| zhao.xiaoh@husky.neu.edu |
| Changsi Liu | 001831955 | liu.changsi@husky.neu.edu |
| Ang Li | 001820694 | li.ang2@husky.neu.edu |
| Junjie He | 001893762 | he.jun@husky.neu.edu |
| | | |

## Technology Stack
- Programming Language: Java 1.8
- Web Framework: Springboot 2.1.2.RELEASE
- Database: MySql
- Database Connection Pool: DruidDataSource
- Data Persistence Framework: myBatis
- IDE: IntelliJ
- Plugins: Lombok
- Version Control: Git
- Project Management: Maven
- Test Tool: Postman
- Development Environment: Ubuntu

## Build Instructions
Clone the repository into a local repository

Use Maven to build:
<code>$ mvn clean install</code>

run the application by executing:
<code>$ java -jar target/csye6225-0.0.1-SNAPSHOT.jar</code>

The server will be run at http://localhost:8081/, test can be done using Postman.

## Deploy Instructions
MySQL port is default 3306.

Server: server side as RESTful architectural style.As a default, it is listening at http://localhost:8081/


## Running Tests
Our test files are in the file "src/test", all the functional tests and module tests are included in this file.

## CI/CD
