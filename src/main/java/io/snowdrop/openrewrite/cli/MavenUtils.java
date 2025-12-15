package io.snowdrop.openrewrite.cli;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.*;
import org.jboss.logging.Logger;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MavenUtils {
    private static final Logger logger = Logger.getLogger(MavenUtils.class);

    private ModelBuilder modelBuilder;

    private ModelBuildingResult buildModel(File pomPath) {
        RepositoryModelResolver repositoryModelResolver = new RepositoryModelResolver();
        DefaultModelBuildingRequest req = new DefaultModelBuildingRequest();
        req.setProcessPlugins(false);
        req.setPomFile(pomPath);
        req.setValidationLevel(ModelBuildingRequest.VALIDATION_LEVEL_MINIMAL);
        req.setSystemProperties(System.getProperties());
        req.setLocationTracking(true);
        req.setModelResolver(repositoryModelResolver);

        ModelBuildingResult result = null;
        try {
            return modelBuilder.build(req);
        } catch (Exception e) {
            logger.error("Could not build effective model: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public Model setupProject(File pomFile) {
        modelBuilder = new DefaultModelBuilderFactory().newInstance();
        ModelBuildingResult modelBuilding = buildModel(pomFile);
        return modelBuilding.getEffectiveModel();
    }

    public static Set<Artifact> convertDependenciesToArtifacts(List<Dependency> dependencies) {
        return dependencies.stream()
            .map(dep -> {
                return new DefaultArtifact(dep.getGroupId(),dep.getArtifactId(),dep.getVersion(),dep.getScope(),dep.getType(),dep.getClassifier(),new DefaultArtifactHandler(dep.getType()));
            })
            .filter(java.util.Objects::nonNull)
            .collect(Collectors.toSet());
    }

}
