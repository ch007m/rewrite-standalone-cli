/*
 * Copyright 2020 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.snowdrop.openrewrite.cli;

import org.apache.maven.repository.internal.DefaultArtifactDescriptorReader;
import org.apache.maven.repository.internal.DefaultVersionRangeResolver;
import org.apache.maven.repository.internal.DefaultVersionResolver;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class for resolving Maven artifacts from GAV (Group:Artifact:Version) coordinates.
 * Supports downloading artifacts from Maven repositories and caching them locally.
 */
public class MavenArtifactResolver {

    // Pattern to match GAV coordinates: group:artifact:version
    private static final Pattern GAV_PATTERN = Pattern.compile("^[a-zA-Z0-9_.\\-]+:[a-zA-Z0-9_.\\-]+:[a-zA-Z0-9_.\\-]+(?:-[a-zA-Z0-9_.\\-]+)*$");

    private final RepositorySystem repositorySystem;
    private final DefaultRepositorySystemSession session;
    private final List<RemoteRepository> repositories;

    public MavenArtifactResolver() {
        this.repositorySystem = createRepositorySystem();
        this.session = createRepositorySystemSession();
        this.repositories = createRepositories();
    }

    /**
     * Determines if a string represents a Maven GAV coordinate or a file path.
     *
     * @param jarPathOrCoordinate the string to check
     * @return true if it's a GAV coordinate, false if it's a file path
     */
    public static boolean isGavCoordinate(String jarPathOrCoordinate) {
        if (jarPathOrCoordinate == null || jarPathOrCoordinate.trim().isEmpty()) {
            return false;
        }

        // If it contains file separators, it's likely a file path
        if (jarPathOrCoordinate.contains("/") || jarPathOrCoordinate.contains("\\")) {
            return false;
        }

        // If it ends with .jar, it's likely a file path
        if (jarPathOrCoordinate.toLowerCase().endsWith(".jar")) {
            return false;
        }

        // Check if it matches the GAV pattern (group:artifact:version)
        return GAV_PATTERN.matcher(jarPathOrCoordinate.trim()).matches();
    }

    /**
     * Resolves a list of jar paths or GAV coordinates to actual file paths.
     *
     * @param jarPathsOrCoordinates list of file paths or GAV coordinates
     * @return list of resolved file paths
     * @throws ArtifactResolutionException if artifact resolution fails
     */
    public List<Path> resolveArtifacts(List<String> jarPathsOrCoordinates) throws ArtifactResolutionException {
        List<Path> resolvedPaths = new ArrayList<>();

        for (String jarPathOrCoordinate : jarPathsOrCoordinates) {
            if (isGavCoordinate(jarPathOrCoordinate)) {
                System.out.println("Resolving Maven artifact: " + jarPathOrCoordinate);
                Path resolvedPath = resolveArtifact(jarPathOrCoordinate);
                resolvedPaths.add(resolvedPath);
                System.out.println("Resolved to: " + resolvedPath);
            } else {
                // It's a file path, use as-is
                resolvedPaths.add(Paths.get(jarPathOrCoordinate));
            }
        }

        return resolvedPaths;
    }

    /**
     * Resolves a single Maven GAV coordinate to a local file path.
     *
     * @param gavCoordinate the GAV coordinate (group:artifact:version)
     * @return the path to the resolved jar file
     * @throws ArtifactResolutionException if artifact resolution fails
     */
    public Path resolveArtifact(String gavCoordinate) throws ArtifactResolutionException {
        String[] parts = gavCoordinate.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid GAV coordinate: " + gavCoordinate +
                ". Expected format: group:artifact:version");
        }

        String groupId = parts[0];
        String artifactId = parts[1];
        String version = parts[2];

        Artifact artifact = new DefaultArtifact(groupId, artifactId, "jar", version);
        ArtifactRequest request = new ArtifactRequest(artifact, repositories, null);

        // session.setOffline(true);
        ArtifactResult result = repositorySystem.resolveArtifact(session, request);
        return result.getArtifact().getFile().toPath();
    }

    /**
     * Resolves artifacts with their transitive dependencies.
     *
     * @param gavCoordinates list of GAV coordinates
     * @return list of resolved file paths including transitive dependencies
     * @throws DependencyResolutionException if dependency resolution fails
     */
    public List<Path> resolveArtifactsWithDependencies(List<String> gavCoordinates) throws DependencyResolutionException {
        List<Dependency> dependencies = new ArrayList<>();

        for (String gavCoordinate : gavCoordinates) {
            if (isGavCoordinate(gavCoordinate)) {
                String[] parts = gavCoordinate.split(":");
                if (parts.length == 3) {
                    String groupId = parts[0];
                    String artifactId = parts[1];
                    String version = parts[2];

                    Artifact artifact = new DefaultArtifact(groupId, artifactId, "jar", version);
                    dependencies.add(new Dependency(artifact, "compile"));
                }
            }
        }

        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setDependencies(dependencies);
        collectRequest.setRepositories(repositories);

        DependencyRequest dependencyRequest = new DependencyRequest(collectRequest, null);
        DependencyResult dependencyResult = repositorySystem.resolveDependencies(session, dependencyRequest);

        List<Path> resolvedPaths = new ArrayList<>();
        for (ArtifactResult artifactResult : dependencyResult.getArtifactResults()) {
            resolvedPaths.add(artifactResult.getArtifact().getFile().toPath());
        }

        return resolvedPaths;
    }

    private RepositorySystem createRepositorySystem() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();

        // Add repository connector and transporter factories
        locator.addService(org.eclipse.aether.impl.ArtifactDescriptorReader.class, DefaultArtifactDescriptorReader.class);
        locator.addService(org.eclipse.aether.impl.VersionResolver.class, DefaultVersionResolver.class);
        locator.addService(org.eclipse.aether.impl.VersionRangeResolver.class, DefaultVersionRangeResolver.class);

        // Your existing transport and connector factories are also needed
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);

        // Set error handling for missing services
        locator.setErrorHandler(new DefaultServiceLocator.ErrorHandler() {
            @Override
            public void serviceCreationFailed(Class<?> type, Class<?> impl, Throwable exception) {
                System.err.println("Service creation failed for " + type.getName() + " with implementation " + impl.getName() + ": " + exception.getMessage());
                exception.printStackTrace();
            }
        });

        return locator.getService(RepositorySystem.class);
    }

    private DefaultRepositorySystemSession createRepositorySystemSession() {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

        // Set up local repository
        String userHome = System.getProperty("user.home");
        LocalRepository localRepo = new LocalRepository(userHome + "/.m2/repository");
        session.setLocalRepositoryManager(repositorySystem.newLocalRepositoryManager(session, localRepo));

        return session;
    }

    private List<RemoteRepository> createRepositories() {
        return Arrays.asList(
            new RemoteRepository.Builder("central", "default", "https://repo1.maven.org/maven2/").build(),
            new RemoteRepository.Builder("sonatype-snapshots", "default", "https://oss.sonatype.org/content/repositories/snapshots/").build()
        );
    }
}