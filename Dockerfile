FROM ubuntu:lastest AS build

RUN apt-get update
RUN apt-get install openjdk-17-jdk -y
# copia todos os arquivos da pasta pro docker
COPY . .

# istala o maven e builda o app
RUN apt-get install maven -y
RUN mvn clean install

# expoem a porta 8080
EXPOSE 8080
# copia o arquivo do build pra pasta app.jar no docker
COPY --from=build /target/todolist-1.0.0.jar app.jar

ENTRYPOINT [ "java", "-jar", "app.jar" ]