FROM amazoncorretto:23.0.1-alpine
RUN addgroup -S mahjonguser && adduser -S mahjonguser -G mahjonguser
USER mahjonguser:mahjonguser
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-Dspring.profiles.active=cloud","-cp","app:app/lib/*","com.oscarhkli.mahjong.score.MahjongScoreCalculatorApplication"]
