# Multi-stage build para otimização
FROM eclipse-temurin:24-jdk-alpine AS builder

WORKDIR /app

# Copiar arquivos de configuração do Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Dar permissão de execução ao Maven Wrapper
RUN chmod +x ./mvnw

# Baixar dependências (cache layer)
RUN ./mvnw dependency:go-offline

# Copiar código fonte
COPY src ./src

# Construir aplicação
RUN ./mvnw clean package -DskipTests

# Estágio final - Runtime
FROM eclipse-temurin:24-jre-alpine

WORKDIR /app

# Criar usuário não-root para segurança
RUN addgroup -g 1001 -S appgroup && \
    adduser -S appuser -u 1001 -G appgroup

# Instalar dependências necessárias
RUN apk add --no-cache tzdata

# Configurar timezone
ENV TZ=America/Sao_Paulo

# Copiar JAR da aplicação
COPY --from=builder /app/target/*.jar app.jar

# Alterar propriedade dos arquivos
RUN chown -R appuser:appgroup /app

# Mudar para usuário não-root
USER appuser

# Expor porta
EXPOSE 8080

# Configurações de JVM otimizadas
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseStringDeduplication"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Comando para executar a aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]