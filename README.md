## Instructions

```shell
mvn quarkus:dev -Dquarkus.args="test-project/demo-spring-boot-todo-app dev.snowdrop.openrewrite.MatchConditions --jar=org.openrewrite:rewrite-java:8.62.4,dev.snowdrop:openrewrite-recipes:1.0.0-SNAPSHOT"
```

or using jar

```shell
java -jar target/quarkus-app/quarkus-run.jar \
  test-project/simple \
  org.openrewrite.java.format.AutoFormat
 
java -jar target/quarkus-app/quarkus-run.jar \
  test-project/demo-spring-boot-todo-app \
  dev.snowdrop.openrewrite.MatchConditions
  
java -jar "target/quarkus-app/quarkus-run.jar,/Users/cmoullia/.m2/repository/dev/snowdrop/openrewrite-recipes/1.0.0-SNAPSHOT/openrewrite-recipes-1.0.0-SNAPSHOT.jar" \
  test-project/demo-spring-boot-todo-app \
  dev.snowdrop.openrewrite.MatchConditions
```

**REMARK**: We don't need to pass at the moment the option `--jar` as there is an issue to load the snapshot jar