## Instructions

TODO: Review the instrucitons to clearly report what it works and not - 11 DEc 2025 !

Use locally JDK 21 !

Compile the project and run `quarkus:dev`

```shell
mvn quarkus:dev -Dquarkus.args="test-project/demo-spring-boot-todo-app dev.snowdrop.openrewrite.MatchConditions --jar=org.openrewrite:rewrite-java:8.68.1,dev.snowdrop:openrewrite-recipes:1.0.0-SNAPSHOT"
```

> *WARNING*
> We don't have to pass at the moment the option `--jar` as there is an issue to load a snapshot jar using Eclipse Aether

If you prefer, you can use the uber jar file with the following recipes:

## Autoformat the java code - OK

```shell
java -jar target/quarkus-app/quarkus-run.jar \
  test-project/simple \
  org.openrewrite.java.format.AutoFormat
```

## Test the recipes to find annotations - NOK
```shell
java -jar target/quarkus-app/quarkus-run.jar \
  test-project/demo-spring-boot-todo-app \
  dev.snowdrop.openrewrite.MatchConditions
```

See the [rewrite](test-project/demo-spring-boot-todo-app/rewrite.yml) file definition
  
## Test the recipes to find annotations - NOK
```shell
java -jar "target/quarkus-app/quarkus-run.jar,/Users/cmoullia/.m2/repository/dev/snowdrop/openrewrite-recipes/1.0.0-SNAPSHOT/openrewrite-recipes-1.0.0-SNAPSHOT.jar" \
  test-project/demo-spring-boot-todo-app \
  dev.snowdrop.openrewrite.MatchConditions
```
