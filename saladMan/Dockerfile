FROM eclipse-temurin:17-jdk-alpine

# Firebase 비밀키 포함
COPY src/main/resources/firebase/test-e93e5-firebase-adminsdk-fbsvc-e966fa8ac8.json /app/firebase/test-e93e5-firebase-adminsdk-fbsvc-e966fa8ac8.json
COPY ./build/libs/*SNAPSHOT.war project.war
ENTRYPOINT ["java", "-jar", "project.war"]