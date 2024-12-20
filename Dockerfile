FROM amazoncorretto:23.0.1-alpine

# Create a non-root user
RUN addgroup -S mahjonguser && adduser -S mahjonguser -G mahjonguser
USER mahjonguser:mahjonguser

# ARG variables (build-time arguments)
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app

# Copy the keystore file
COPY certificates/ /app/certificates/

# ENTRYPOINT to run the Spring Boot application
ENTRYPOINT ["java","-Dspring.profiles.active=cloud","-cp","app:app/lib/*","com.oscarhkli.mahjong.score.MahjongScoreCalculatorApplication"]
