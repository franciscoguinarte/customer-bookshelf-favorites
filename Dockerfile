# Estágio Único: Execução da Aplicação
# Usamos uma imagem base leve e confiável do Eclipse Temurin, apenas com o Java Runtime.
FROM eclipse-temurin:17-jre-jammy

# Define o diretório de trabalho.
WORKDIR /app

# Copia o arquivo .jar pré-construído da pasta target local para a imagem.
# O projeto deve ser compilado localmente com `mvn clean package` antes de construir a imagem.
COPY target/*.jar app.jar

# Expõe a porta em que a aplicação Spring Boot roda.
EXPOSE 8080

# Define o comando para executar a aplicação quando o contêiner iniciar.
ENTRYPOINT ["java", "-jar", "app.jar"]