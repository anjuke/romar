cd $(dirname $0)/
cd ..
ROMAR_HOME=`pwd`

java -cp \
    $ROMAR_HOME/romar-core-1.1.0-jar-with-dependencies.jar:$ROMAR_HOME/lib/hadoop-core-0.20.204.0.jar:$ROMAR_HOME/conf \
    org.apache.hadoop.util.RunJar \
    $ROMAR_HOME/romar-core-1.1.0-jar-with-dependencies.jar  \
    org.apache.mahout.cf.taste.hadoop.similarity.item.ItemSimilarityJob \
    $@
