package net.sghill.jenkins.rewrite;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.*;
import org.openrewrite.marker.SearchResult;
import org.openrewrite.semver.Semver;
import org.openrewrite.semver.VersionComparator;
import org.openrewrite.xml.XmlVisitor;
import org.openrewrite.xml.tree.Xml;

/**
 * Determines if this project is a Jenkins plugin by checking if the POM
 * has a managed version of jenkins-core.
 */
@Value
@EqualsAndHashCode(callSuper = false)
public class IsJenkinsPlugin extends Recipe {
    @Option(displayName = "Jenkins version",
            description = "The value of the `<jenkins.version>` property.",
            example = "[1,)")
    String version;

    @Override
    public String getDisplayName() {
        return "Is the project a Jenkins plugin?";
    }

    @Override
    public String getDescription() {
        return "Checks if the project is a Jenkins plugin by the presence of a managed version of jenkins-core";
    }

    @Override
    public Validated validate() {
        Validated validated = super.validate();
        //noinspection ConstantConditions
        if (version != null) {
            validated = validated.or(Semver.validate(version, null));
        }
        return validated;
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        Validated<VersionComparator> versionValidation = Semver.validate(version, null);
        if (versionValidation.isValid()) {
            VersionComparator versionComparator = versionValidation.getValue();
            if (versionComparator != null) {
                return new XmlVisitor<ExecutionContext>() {
                    @Override
                    public Xml visitDocument(Xml.Document document, ExecutionContext executionContext) {
                        String jenkinsVersion = Jenkins.isJenkinsPluginPom(document);
                        if (jenkinsVersion != null && versionComparator.isValid(null, jenkinsVersion) &&
                                !document.getMarkers().findFirst(SearchResult.class).isPresent()) {
                            return SearchResult.found(document, jenkinsVersion);
                        }
                        return document;
                    }
                };
            }
        }

        return TreeVisitor.noop();
    }
}
