# --- Estágio de Build ---
# Usa uma imagem com o JDK completo e o Maven para compilar o projeto
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /build

# Copia apenas os arquivos necessários para o build, aproveitando o cache do Docker
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Baixa as dependências primeiro
RUN ./mvnw dependency:go-offline -B

# Copia o código-fonte e compila o projeto
COPY src src
RUN ./mvnw package -DskipTests

# --- Estágio Final ---
# Usa uma imagem JRE mínima para rodar a aplicação
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copia apenas o JAR compilado do estágio de build
COPY --from=builder /build/target/quarkus-app/ /app/

# Expõe a porta e define o comando para rodar a aplicação
EXPOSE 8080
CMD ["java", "-jar", "/app/quarkus-run.jar"]
