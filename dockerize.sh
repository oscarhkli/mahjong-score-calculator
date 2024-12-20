mvn clean package -DskipTests
mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
docker build --platform linux/amd64 -t oscarhkli/mahjong-score-calculator .
