package net.sghill.jenkins.rewrite

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.openrewrite.java.Assertions.mavenProject
import org.openrewrite.java.Assertions.srcMainResources
import org.openrewrite.maven.Assertions.pomXml
import org.openrewrite.test.RecipeSpec
import org.openrewrite.test.RewriteTest
import org.openrewrite.test.SourceSpecs.other
import org.openrewrite.test.SourceSpecs.text

@Disabled // TODO port to rewrite 8
class CreateIndexJellyTest : RewriteTest {
    override fun defaults(spec: RecipeSpec) {
        spec.recipe(CreateIndexJelly())
    }

    @Test
    fun indexJellyAlreadyExists() = rewriteRun(
        other("peanut butter and...") {
            spec -> spec.path("src/main/resources/index.jelly")
        }
    )

    @Test
    fun createIndexJelly() = rewriteRun(
        mavenProject("plugin",
            pomXml("""
                <project>
                    <parent>
                        <groupId>org.jenkins-ci.plugins</groupId>
                        <artifactId>plugin</artifactId>
                        <version>4.40</version>
                    </parent>
                    <artifactId>my-plugin</artifactId>
                    <description>This is my plugin's description</description>
                    <version>0.1</version>
                    <repositories>
                        <repository>
                            <id>repo.jenkins-ci.org</id>
                            <url>https://repo.jenkins-ci.org/public/</url>
                        </repository>
                    </repositories>
                </project>
            """),
            srcMainResources(
                text(null, """
                    <?jelly escape-by-default='true'?>
                    <div>
                    This is my plugin's description
                    </div>
                """) {
                    spec -> spec.path("index.jelly")
                }
            )
        )
    )
    @Test
    fun createIndexJellyEmptyDescription() = rewriteRun(
        mavenProject("plugin",
            pomXml("""
                <project>
                    <parent>
                        <groupId>org.jenkins-ci.plugins</groupId>
                        <artifactId>plugin</artifactId>
                        <version>4.40</version>
                    </parent>
                    <artifactId>my-plugin</artifactId>
                    <description/>
                    <version>0.1</version>
                    <repositories>
                        <repository>
                            <id>repo.jenkins-ci.org</id>
                            <url>https://repo.jenkins-ci.org/public/</url>
                        </repository>
                    </repositories>
                </project>
            """),
            srcMainResources(
                text(null, """
                    <?jelly escape-by-default='true'?>
                    <div>
                    my-plugin
                    </div>
                """) {
                    spec -> spec.path("index.jelly")
                }
            )
        )
    )

    @Test
    fun createIndexJellyNoDescription() = rewriteRun(
        mavenProject("plugin",
            pomXml("""
                <project>
                    <parent>
                        <groupId>org.jenkins-ci.plugins</groupId>
                        <artifactId>plugin</artifactId>
                        <version>4.40</version>
                    </parent>
                    <artifactId>my-plugin</artifactId>
                    <version>0.1</version>
                    <repositories>
                        <repository>
                            <id>repo.jenkins-ci.org</id>
                            <url>https://repo.jenkins-ci.org/public/</url>
                        </repository>
                    </repositories>
                </project>
            """),
            srcMainResources(
                text(null, """
                    <?jelly escape-by-default='true'?>
                    <div>
                    my-plugin
                    </div>
                """) {
                    spec -> spec.path("index.jelly")
                }
            )
        )
    )
}
