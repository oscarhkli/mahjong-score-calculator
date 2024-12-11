mvn clean package -DskipTests
mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
docker build -t oscarhkli/mahjong-score-calculator .
