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

## Deploy Instructions
MySQL port is default 3306.

run 'npm install' first and run the follow lines in path '/csye6225-spring2018'

Server: server side as RESTful architectural style, responsible for communication with client/admin side. Used JWT achieves authentication system. It is listening at http://localhost:8081/

start server as dev: npm run dev-server   ## Attention, you must run server first, then run client or admin

Client: show the front-end, It is listening at http://localhost:8080/(only admin and server running) or 8081(admin, client, server all running),

start client as dev: npm run dev-client

Admin: application management, provides sign in/sign up modules, It is listening at http://localhost:8080/ or 8081(same as above situations)

start admin as dev: npm run dev-admin

When depoly on AWS, use pm2 to package start server side and admin/client side


## Running Tests
Our test files are in the file "src/test", all the functional tests and module tests are included in this file

## CI/CD
