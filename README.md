# DD2480 Group 7, 2025, CI (Assignment #2)

This project is a continuous integration server. The server checks for commits in the repo, and automatically uses maven for building and running the tests on that commit. If everything passes, the server sends a post request for setting a checkmark on the commit, to mark it as successful. Otherwise, it sets a cross on the commit to mark it as unsuccessful. Github actions are enabled such that on each commit, there are two checkmarks which are synchronized. The server also has tests for checking that everything is working as expected. 

## Running the program

To compile the project, run in the command line (from direectory ciapp):
```bash
mvn clean package
```

To start the server, run in the command line (from directory ciapp):
```bash
java -jar target/ciapp-1.0-SNAPSHOT.jar
```
## Running the program with other config file

```bash
java -Dconfig.file=../config.properties -jar target/ciapp-1.0-SNAPSHOT.jar
```

In order for the webserver to work correctly, a private key has to be obtained from the GitHub app. This key is however in the PKCS1 format which can not be read by the program. In order to convert the key to the readable PKCS8 format, paste the following command into the console while in the directory of the downloaded private key, replacing "original_key.pem" with the name of said downloaded key.a
```console
openssl pkcs8 \
    -topk8 \
    -inform PEM \
    -outform PEM \
    -in original_key.pem \
    -out pkcs8_key.pem \
    -nocrypt
```
Some system environment variables have to defined on the server in order for the program to run correctly, these include "PRIVATE_KEY_PATH", "APP_ID", "DATABASE_PATH", and "BASE_URL". Create an ".env" file with the contents
```
PRIVATE_KEY_PATH = "Your private key file path"
APP_ID = "The GitHub app id"
DATABASE_PATH = "The database file path"
BASE_URL = "The base url for the webserver"
```

## API documentation
The API documentation can be found at [index.html](https://dd2480-2025-group7.github.io/ci/). 

You can also regenerate the API documentation yourself. To generate the API documentation move to the ciapp directory and run.
```bash
mvn javadoc:javadoc
```
The generated file can be found in ciapp - target - site - index.html. 

## Statement of Contributions

Each person has contributed to the project by creating issues, particpating in assigning issues among the group, writing code and creating pull requests, and reviewing other group members' pull requests. Reviews have been divided among the group members in a way that everyone has reviewed a fair share of the PRs and in a way that all group members have a solid knowledge of the codebase. A lot of the issues in this project was solved with pair programming and thus a lot of the commits have several contributors. We all spent rougly the same amount of time working on the project. 

### Vilhelm Prytz
- [#11 feat: implement basic Maven project](https://github.com/dd2480-2025-group7/ci/pull/11)
- [#13 feat: add Dockerfile and docker-compose.yml](https://github.com/dd2480-2025-group7/ci/pull/13)
- [#18 feat: initial project structure and some work on methods](https://github.com/dd2480-2025-group7/ci/pull/18)
- [#20 feat: cleanup and methods in StoreBuildResult.java](https://github.com/dd2480-2025-group7/ci/pull/20)
- [#21 ci: add GitHub actions workflow along our CI server](https://github.com/dd2480-2025-group7/ci/pull/21)
- [#29 feat: allow users of programs to specify allowed repos dynamically](https://github.com/dd2480-2025-group7/ci/pull/29)
- [#30 feat: create simple bash script that mocks webhook request](https://github.com/dd2480-2025-group7/ci/pull/30)
- [#34 feat: Implement GitHub API requests to change commit status](https://github.com/dd2480-2025-group7/ci/pull/34)
- [#44 fix: add private-key.pem in docker-compose.yml](https://github.com/dd2480-2025-group7/ci/pull/44)
- [#53 fix: add Maven to Dockerfile during runtime](https://github.com/dd2480-2025-group7/ci/pull/53)
- [#55 bug: exit the program if required environment variables are not set](https://github.com/dd2480-2025-group7/ci/pull/55)
- [#57 bug: remove premature Git.shutdown() and move it to shutdown hook](https://github.com/dd2480-2025-group7/ci/pull/57)
- [#59 test: add tests for WebServer.java class](https://github.com/dd2480-2025-group7/ci/pull/59)
- [#63 test: add tests for runMavenTests in ProjectTest.java](https://github.com/dd2480-2025-group7/ci/pull/63)
- [#68 feat: store past builds in database and display them on index with link to logs](https://github.com/dd2480-2025-group7/ci/pull/68)

### Elin Fransholm
- [#18 feat: initial project structure and some work on methods](https://github.com/dd2480-2025-group7/ci/pull/18)
- [#20 feat: cleanup and methods in StoreBuildResult.java](https://github.com/dd2480-2025-group7/ci/pull/20)
- [#35 feat: restructured code in project.java](https://github.com/dd2480-2025-group7/ci/pull/35)
- [#58 feat: add tests for cloneRepo](https://github.com/dd2480-2025-group7/ci/pull/58)
- [#61 feat: add two tests for deleteRepo](https://github.com/dd2480-2025-group7/ci/pull/61)
- [#62 fix: remove unnecessary new instance of project in testCloneRepo_false](https://github.com/dd2480-2025-group7/ci/pull/62)
- [#69 fix: error handling](https://github.com/dd2480-2025-group7/ci/pull/69)

### Linda Nycander
- [#20 feat: cleanup and methods in StoreBuildResult.java](https://github.com/dd2480-2025-group7/ci/pull/20)
- [#28 feat: add functionality for deleting cloned git repos](https://github.com/dd2480-2025-group7/ci/pull/28)
- [#39 doc: add and fix javadoc comments](https://github.com/dd2480-2025-group7/ci/pull/39)
- [#58 feat: add tests for cloneRepo](https://github.com/dd2480-2025-group7/ci/pull/58)
- [#61 feat: add two tests for deleteRepo](https://github.com/dd2480-2025-group7/ci/pull/61)
- [#62 fix: remove unnecessary new instance of project in testCloneRepo_false](https://github.com/dd2480-2025-group7/ci/pull/62)
- [#69 fix: error handling](https://github.com/dd2480-2025-group7/ci/pull/69)

### Philip Ågren-Jahnsson
- [#18 feat: initial project structure and some work on methods](https://github.com/dd2480-2025-group7/ci/pull/18)
- [#20 feat: cleanup and methods in StoreBuildResult.java](https://github.com/dd2480-2025-group7/ci/pull/20)
- [#34 feat: Implement GitHub API requests to change commit status](https://github.com/dd2480-2025-group7/ci/pull/34)
- [#51 fix: Changed private-key.pem to be read from getenv()](https://github.com/dd2480-2025-group7/ci/pull/51)
- [#65 feat: Add TokenGetterTest.java](https://github.com/dd2480-2025-group7/ci/pull/65)
- [#66 feat: Add vscode configuration](https://github.com/dd2480-2025-group7/ci/pull/66)
- [#72 feat: Summary of checkruns](https://github.com/dd2480-2025-group7/ci/pull/72)

## SEMAT (Team assessment)

According to the checklist in [Essence standard](https://www.omg.org/spec/Essence/1.2/PDF) on p.52 we are in the "collaborating state". The team now know and trust each other. We all work effectively together and have a common goal that we want to achieve. 

To reach the next state "performing", the group needs to minimize the need for backtracking. We don't want to waste our time and work. Currently there are still some small needs for going back to already written code and correcting some small errors. The team has already reached some of the goals of this state, like working without outside help and continously meeting our commitments. 

## Motivation for P+

We believe that the group has fulfilled the requirements for P+.

- Nearly all commits are linked to an issue that describes the feature / commit.
- The group has worked well togehter in a proactive and creative way. Each group member is proud of the work that has been done.
- We keep the history of past builds in a database. This way the history is not lost when rebooting the server. [LINK TO BUILD LOG LIST]